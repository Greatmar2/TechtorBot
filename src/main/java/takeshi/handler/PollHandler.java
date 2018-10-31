/*
 * Copyright 2018 Greatmar2
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package takeshi.handler;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import emoji4j.EmojiUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.TextChannel;
import takeshi.db.controllers.CPoll;
import takeshi.db.model.OPoll;
import takeshi.guildsettings.GSetting;
import takeshi.main.DiscordBot;
import takeshi.util.Misc;

public class PollHandler {
	// {guild-id, {message-id, {emoji, role-id}}
	private final Map<Long, Map<Long, Timestamp>> listeners;
//	private final DiscordBot discordBot;
//	private boolean lock = false;

	public PollHandler(DiscordBot discordBot) {
//		this.discordBot = discordBot;
		listeners = new ConcurrentHashMap<>();
	}

	public synchronized void addMessage(long guildId, long messageId, Timestamp time) {
		if (!listeners.containsKey(guildId)) {
			listeners.put(guildId, new ConcurrentHashMap<>());
		}
		if (!listeners.get(guildId).containsKey(messageId)) {
			listeners.get(guildId).put(messageId, time);
		}
	}

	public synchronized boolean initGuild(long guildId, boolean forceReload) {
		if (!forceReload && listeners.containsKey(guildId)) {
			return true;
		}
		if (forceReload) {
			removeGuild(guildId);
		}
		List<OPoll> polls = CPoll.getMessagesForGuild(guildId);
		for (OPoll poll : polls) {
			if (poll.messageId <= 0) {
				continue;
			}
			addMessage(guildId, poll.messageId, poll.messageExpire);
		}

		return false;
	}

	public synchronized void removeGuild(long guildId) {
		if (listeners.containsKey(guildId)) {
			listeners.remove(guildId);
		}
	}

	public void checkPolls(DiscordBot bot) {
		List<Guild> guilds = bot.getJda().getGuilds();
		for (Guild guild : guilds) {
			checkPolls(guild);
		}
	}

	public void checkPolls(Guild guild) {
//		System.out.println("[DEBUG] Checking polls for guild " + guild.getName());
		long guildId = guild.getIdLong();
		initGuild(guildId, false);
		if (listeners.containsKey(guildId)) {
//			System.out.println("[DEBUG] Found poll(s) guild");
			// Look through polls for expired ones
			List<OPoll> polls = CPoll.getMessagesForGuild(guildId);
			for (OPoll poll : polls) {
				if (poll.id <= 0) {
					return;
				}
				TextChannel tchan = guild.getTextChannelById(poll.channelId);
				boolean debug = GuildSettings.getBoolFor(tchan, GSetting.DEBUG);
				if (debug) {
					tchan.sendMessage(String.format("[DEBUG] Checking poll in %s", tchan.getAsMention())).queue();
				}
				Timestamp expiry = poll.messageExpire;
				// If the message has expired
				if (expiry.before(new Date())) {
					if (poll.channelId > 0 && poll.messageId > 0) {
						if (tchan != null && tchan.canTalk()) {
							// Count messages then delete them
							Message message = tchan.getMessageById(poll.messageId).complete();
							if (message == null) {
								if (debug) {
									tchan.sendMessage(String.format("[DEBUG] Null message at ID:\n$s", poll.messageId)).queue();
								}
								return;
							}
							int[] voteCounts = new int[9];
							java.util.Arrays.fill(voteCounts, 0);
							List<MessageReaction> reactions = message.getReactions();
							// Count reactions
							int val = 0;
							for (MessageReaction reaction : reactions) {
								String emoji = EmojiUtils.shortCodify(reaction.getReactionEmote().getName());
								if (debug) {
									tchan.sendMessage(String.format("[DEBUG] Counting reactions: checking %s", emoji)).queue();
								}
								if (reaction.getUsers().complete().contains(guild.getJDA().getSelfUser())) {
									voteCounts[val] = reaction.getCount();
									val++;
								}
//								}
//								if (emoji.length() == 3 && Character.isDigit(emoji.charAt(1))) {
//									int val = Integer.parseInt(emoji.charAt(1) + "");
//									if (val >= 1 && val <= 9) {
//									}
//								}
							}
							List<MessageEmbed> embeds = message.getEmbeds();
							if (embeds.size() > 0) {
								if (debug) {
									tchan.sendMessage(
											String.format("[DEBUG] Counted reactions for %s, found %s", embeds.get(0).getDescription(), Misc.concat(voteCounts))).queue();
								}

								// Build poll results embed
								EmbedBuilder emResults = new EmbedBuilder(embeds.get(0));
								List<Field> voteFields = emResults.getFields();
								List<String> fieldDescs = new ArrayList<>();
								for (Field field : voteFields) {
									fieldDescs.add(field.getValue());
								}
								emResults.clearFields();
								for (int i = 0; i < Math.min(fieldDescs.size(), 9); i++) {
									if (voteCounts[i] > 0) {
										voteCounts[i]--;
										emResults.addField(voteCounts[i] + (voteCounts[i] == 1 ? " vote" : " votes"), fieldDescs.get(i), true);
									}
								}
								emResults.setFooter("Vote ended", null);

								// Send poll results
								tchan.sendMessage(emResults.build()).queue();

								// Delete the poll
								CPoll.delete(poll);
								message.delete().queue();
							} else {
								if (debug) {
									tchan.sendMessage(String.format("[DEBUG] No embeds found on message:\n$s", message.getContentRaw())).queue();
								}
							}
						}
					}
				}
			}
		}

	}
}
