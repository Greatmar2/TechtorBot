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
import java.util.concurrent.TimeUnit;

import emoji4j.EmojiUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import takeshi.db.controllers.CPoll;
import takeshi.db.model.OPoll;
import takeshi.guildsettings.GSetting;
import takeshi.main.DiscordBot;
import takeshi.util.Misc;

/**
 * The type Poll handler.
 */
public class PollHandler {
	// {guild-id, {message-id, end}}
	private final Map<Long, Map<Long, Timestamp>> listeners;
	private final DiscordBot bot;
//	private boolean lock = false;

	/**
	 * Instantiates a new Poll handler.
	 *
	 * @param bot the bot
	 */
	public PollHandler(DiscordBot bot) {
		this.bot = bot;
		listeners = new ConcurrentHashMap<>();
	}

	/**
	 * Add message.
	 *
	 * @param guildId   the guild id
	 * @param messageId the message id
	 * @param time      the time
	 */
	public synchronized void addMessage(long guildId, long messageId, Timestamp time) {
		if (!listeners.containsKey(guildId)) {
			listeners.put(guildId, new ConcurrentHashMap<>());
		}
		if (!listeners.get(guildId).containsKey(messageId)) {
			listeners.get(guildId).put(messageId, time);
		}
	}

	/**
	 * Init guild boolean.
	 *
	 * @param guildId     the guild id
	 * @param forceReload the force reload
	 * @return the boolean
	 */
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

	/**
	 * Init guilds.
	 */
	public void initGuilds() {
		List<Guild> guilds = bot.getJda().getGuilds();

		for (Guild guild : guilds) {
			List<OPoll> polls = CPoll.getMessagesForGuild(guild.getIdLong());
			for (OPoll poll : polls) {
				if (poll.messageId <= 0) {
					continue;
				}
				addMessage(guild.getIdLong(), poll.messageId, poll.messageExpire);

				// When initializing for the first time, start all the timers
				if (bot != null) {
					long waitTime = poll.messageExpire.getTime() - (new Date()).getTime() + 1000;
					if (waitTime >= 0) {
						bot.schedule(new Runnable() {
							@Override
							public void run() {
								bot.pollHandler.checkPoll(poll, guild, -1);
							}
						}, waitTime, TimeUnit.MILLISECONDS);
					} else {
						bot.pollHandler.checkPoll(poll, guild, -1);
					}
				}
			}
		}
	}

	/**
	 * Remove guild.
	 *
	 * @param guildId the guild id
	 */
	public synchronized void removeGuild(long guildId) {
		if (listeners.containsKey(guildId)) {
			listeners.remove(guildId);
		}
	}

	/**
	 * Check polls.
	 *
	 * @param bot the bot
	 */
	public void checkPolls(DiscordBot bot) {
		List<Guild> guilds = bot.getJda().getGuilds();
		for (Guild guild : guilds) {
			checkPolls(guild);
		}
	}

	/**
	 * Check polls.
	 *
	 * @param guild the guild
	 */
	public void checkPolls(Guild guild) {
		checkPolls(guild, -1);
	}

	/**
	 * Check polls.
	 *
	 * @param guild           the guild
	 * @param forceCancelChan the force cancel chan
	 */
	public void checkPolls(Guild guild, long forceCancelChan) {
//		System.out.println("[DEBUG] Checking polls for guild " + guild.getName());
		long guildId = guild.getIdLong();
		initGuild(guildId, false);
		if (listeners.containsKey(guildId)) {
//			System.out.println("[DEBUG] Found poll(s) guild");
			// Look through polls for expired ones
			List<OPoll> polls = CPoll.getMessagesForGuild(guildId);
			for (OPoll poll : polls) {
				if (poll.id <= 0) {
					continue;
				}
				checkPoll(poll, guild, forceCancelChan);
			}
		}

	}

	/**
	 * Check poll.
	 *
	 * @param poll            the poll
	 * @param guild           the guild
	 * @param forceCancelChan the force cancel chan
	 */
	public void checkPoll(OPoll poll, Guild guild, long forceCancelChan) {
		TextChannel tchan = guild.getTextChannelById(poll.channelId);
		boolean debug = GuildSettings.getBoolFor(tchan, GSetting.DEBUG);
		if (debug) {
			tchan.sendMessage(String.format("[DEBUG] Checking poll in %s", tchan.getAsMention())).queue();
		}
		// If the message has expired
		if (poll.messageExpire.before(new Date()) || forceCancelChan == poll.channelId) {
			if (poll.channelId > 0 && poll.messageId > 0) {
				if (tchan != null && tchan.canTalk()) {
					// Count messages then delete them
					Message message = tchan.retrieveMessageById(poll.messageId).complete();
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
						if (reaction.retrieveUsers().complete().contains(guild.getJDA().getSelfUser())) {
							voteCounts[val] = reaction.getCount();
							val++;
						}
//						}
//						if (emoji.length() == 3 && Character.isDigit(emoji.charAt(1))) {
//							int val = Integer.parseInt(emoji.charAt(1) + "");
//							if (val >= 1 && val <= 9) {
//							}
//						}
					}
					List<MessageEmbed> embeds = message.getEmbeds();
					if (embeds.size() > 0) {
						if (debug) {
							tchan.sendMessage(
									String.format("[DEBUG] Counted reactions for %s, found %s", embeds.get(0).getDescription(), Misc.concat(voteCounts)))
									.queue();
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
						String footer = "Vote ended";
						if (poll.single) {
							footer = "Single-choice poll | " + footer;
						}
						emResults.setFooter(footer, null);

						// Send poll results
						tchan.sendMessage(emResults.build()).queue();

						// Delete the poll
						CPoll.delete(poll);
						message.delete().queue();
					} else {
						if (debug) {
							tchan.sendMessage(String.format("[DEBUG] No embeds found on message:\n%s", message.getContentRaw())).queue();
						}
					}
				}
			}
		}
	}

	/**
	 * Handle reaction boolean.
	 *
	 * @param guild    the guild
	 * @param message  the message
	 * @param channel  the channel
	 * @param user     the user
	 * @param reaction the reaction
	 * @param adding   the adding
	 * @return the boolean
	 */
	public boolean handleReaction(Guild guild, long message, TextChannel channel, User user, ReactionEmote reaction, boolean adding) {
		boolean ret = false;
		if (adding) {
			long guildId = guild.getIdLong();
			initGuild(guildId, false);
			if (listeners.containsKey(guildId) && listeners.get(guildId).containsKey(message)) {
				OPoll poll = CPoll.findBy(guildId, message);
				ret = true;
				if (!poll.single || poll.id <= 0) {
					return ret;
				}
				Message mess = channel.retrieveMessageById(message).complete();
				List<MessageReaction> reactions = mess.getReactions();
				for (MessageReaction messageReaction : reactions) {
//						boolean debug = GuildSettings.getBoolFor(channel, GSetting.DEBUG);
//						if (debug) {
//							channel.sendMessage(String.format("[DEBUG] Single responses: checking `%s` vs `%s`", messageReaction.getReactionEmote(), reaction))
//									.queue();
//						}
					if (!messageReaction.getReactionEmote().equals(reaction)) {
						messageReaction.removeReaction(user).queue();
					}
				}
			}
		}
		return ret;
	}
}
