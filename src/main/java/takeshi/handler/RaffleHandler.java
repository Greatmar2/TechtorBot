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
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import emoji4j.EmojiUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import takeshi.db.controllers.CRaffle;
import takeshi.db.controllers.CRaffleBlacklist;
import takeshi.db.model.ORaffle;
import takeshi.db.model.ORaffleBlacklist;
import takeshi.guildsettings.GSetting;
import takeshi.main.DiscordBot;

/**
 * The type Raffle handler.
 */
public class RaffleHandler {
	// {guild-id, {message-id, end}}
	private final Map<Long, Map<Long, Timestamp>> listeners;
	private final DiscordBot bot;
	private final List<Long> runningRepeater = new ArrayList<>();
	/**
	 * The constant ENTRY_EMOJI.
	 */
	public static final String ENTRY_EMOJI = EmojiUtils.emojify(":inbox_tray:");
	/**
	 * The constant MAX_ENTRIES.
	 */
	public static final int MAX_ENTRIES = 99;
	/**
	 * The constant TIMESTAMP_FORMATTER.
	 */
	public static final SimpleDateFormat TIMESTAMP_FORMATTER = new SimpleDateFormat("HH:mm dd/MMM/yyyy");
//	private boolean lock = false;

	/**
	 * Instantiates a new Raffle handler.
	 *
	 * @param bot the bot
	 */
	public RaffleHandler(DiscordBot bot) {
		this.bot = bot;
		listeners = new ConcurrentHashMap<>();
	}

	/**
	 * Add raffle.
	 *
	 * @param guildId   the guild id
	 * @param messageId the message id
	 * @param time      the time
	 */
	public synchronized void addRaffle(long guildId, long messageId, Timestamp time) {
		if (!listeners.containsKey(guildId)) {
			listeners.put(guildId, new ConcurrentHashMap<>());
		}
		if (!listeners.get(guildId).containsKey(messageId)) {
			if (time == null)
				time = new Timestamp(0);
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
		List<ORaffle> raffles = CRaffle.getMessagesForGuild(guildId);
		for (ORaffle raffle : raffles) {
			if (raffle.messageId <= 0) {
				continue;
			}
			addRaffle(guildId, raffle.messageId, raffle.raffleEnd);
		}

		return false;
	}

	/**
	 * Init guilds.
	 */
	public void initGuilds() {
		List<Guild> guilds = bot.getJda().getGuilds();

		for (Guild guild : guilds) {
			List<ORaffle> raffles = CRaffle.getMessagesForGuild(guild.getIdLong());
			for (ORaffle raffle : raffles) {
				if (raffle.messageId <= 0) {
					continue;
				}
				addRaffle(guild.getIdLong(), raffle.messageId, raffle.raffleEnd);

//				bot.getJda().getGuildById(396985657655951360L).getTextChannelById(505406509404913687L)
//						.sendMessage("Startup: adding raffle listener for raffle " + raffle.id).queue();

				// When initializing for the first time, start all the timers
				if (bot != null && raffle.duration > 0) {
					// Schedule normal check
					long waitTime = raffle.raffleEnd.getTime() - (new Date()).getTime() + 1000;
					if (waitTime >= 0) {
						bot.schedule(new Runnable() {
							@Override
							public void run() {
								bot.raffleHandler.checkRaffle(raffle, guild);
							}
						}, waitTime, TimeUnit.MILLISECONDS);
					} else {
						bot.raffleHandler.checkRaffle(raffle, guild);
					}
					// Schedule hourly end-check
					if (!runningRepeater.contains(guild.getIdLong())) {
						runningRepeater.add(guild.getIdLong());
						makeRepeatingRunner(guild, raffle);
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
	 * Check raffles.
	 *
	 * @param bot the bot
	 */
	public void checkRaffles(DiscordBot bot) {
		List<Guild> guilds = bot.getJda().getGuilds();
		for (Guild guild : guilds) {
			checkRaffles(guild);
		}
	}

	/**
	 * Check raffles boolean.
	 *
	 * @param guild the guild
	 * @return the boolean
	 */
	public boolean checkRaffles(Guild guild) {
		boolean allCleared = false;
//		System.out.println("[DEBUG] Checking raffles for guild " + guild.getName());
		long guildId = guild.getIdLong();
		initGuild(guildId, false);
		if (listeners.containsKey(guildId)) {
//			System.out.println("[DEBUG] Found raffle(s) guild");
			// Look through raffles for expired ones
			List<ORaffle> raffles = CRaffle.getMessagesForGuild(guildId);
			for (ORaffle raffle : raffles) {
				if (raffle.id <= 0) {
					continue;
				}
				if (checkRaffle(raffle, guild))
					raffles.remove(raffle);
			}
			if (raffles.size() == 0) {
				allCleared = true;
			}
		} else {
			allCleared = true;
		}

		return allCleared;
	}

	/**
	 * Check raffle boolean.
	 *
	 * @param raffle the raffle
	 * @param guild  the guild
	 * @return the boolean
	 */
	public boolean checkRaffle(ORaffle raffle, Guild guild) {
		boolean ended = false;
		// If the message has expired or the max entrants have been reached
		if (raffle.raffleEnd != null && raffle.raffleEnd.getTime() != 0 && raffle.raffleEnd.before(new Date())) {
			endRaffle(raffle, guild);
			ended = true;
		} else {
			Message message = guild.getTextChannelById(raffle.channelId).retrieveMessageById(raffle.messageId).complete();
			List<MessageReaction> reactions = message.getReactions();
			for (MessageReaction reaction : reactions) {
				if (reaction.getReactionEmote().getName().equals(ENTRY_EMOJI)) {
					if (reaction.getCount() > raffle.entrants) {
						endRaffle(raffle, guild);
						ended = true;
						break;
					}
				}
			}
		}
		return ended;
	}

	/**
	 * Check raffle.
	 *
	 * @param raffle   the raffle
	 * @param guild    the guild
	 * @param reaction the reaction
	 */
	public void checkRaffle(ORaffle raffle, Guild guild, MessageReaction reaction) {
		// If the message has expired or the max entrants have been reached
		if ((raffle.raffleEnd != null && raffle.raffleEnd.getTime() != 0 && raffle.raffleEnd.before(new Date()))
				|| (reaction.getReactionEmote().getName().equals(ENTRY_EMOJI) && reaction.getCount() > raffle.entrants)) {
			endRaffle(raffle, guild);
		}
	}

	/**
	 * End raffle.
	 *
	 * @param raffle the raffle
	 * @param guild  the guild
	 */
	public void endRaffle(ORaffle raffle, Guild guild) {
		TextChannel tchan = guild.getTextChannelById(raffle.channelId);
		boolean debug = GuildSettings.getBoolFor(tchan, GSetting.DEBUG);
		if (raffle.channelId > 0 && raffle.messageId > 0) {
			if (tchan != null && tchan.canTalk()) {
				// Find the raffle message
				Message message = tchan.retrieveMessageById(raffle.messageId).complete();
				if (message == null) {
					if (debug) {
						tchan.sendMessage(String.format("[DEBUG] Null message at ID:\n$s", raffle.messageId)).queue();
					}
					return;
				}
				// Find the entry reaction, get entrants, remove self, select winners
				List<MessageReaction> reactions = message.getReactions();
				List<Member> winners = new ArrayList<>();
				int numEntrants = 0;
				for (MessageReaction reaction : reactions) {
					if (!reaction.getReactionEmote().getName().equals(ENTRY_EMOJI)) {
						continue;
					}
//					reaction.removeReaction(guild.getSelfMember().getUser()).complete();
					List<User> entrants = reaction.retrieveUsers().complete();
					entrants.remove(guild.getSelfMember().getUser());
					numEntrants = entrants.size();
					List<ORaffleBlacklist> bls = CRaffleBlacklist.getForRaffle(guild.getIdLong(), raffle.id);
					for (int i = 0; i < bls.size(); i++) {
						ORaffleBlacklist bl = bls.get(i);
						if (bl.currently) {
							if (bl.raffleId == 0 && !CRaffleBlacklist.findBy(bl.guildId, bl.userId, raffle.id).currently) {
								continue;
							}
							entrants.remove(guild.getMemberById(bls.get(i).userId).getUser());
						}
					}
					for (int i = 0; i < raffle.winners && entrants.size() > 0; i++) {
						int winner = (int) (Math.random() * entrants.size());
						winners.add(guild.getMember(entrants.get(winner)));
						entrants.remove(winner);
					}
					break;
				}
				// Prepare raffle-end declaration
				MessageBuilder output = new MessageBuilder();
				if (winners.size() > 0) {
					output.setContent(guild.getMemberById(raffle.ownerId).getAsMention() + "'s raffle for **" + raffle.prize + "** has ended!\n");
					if (winners.size() == 1) {
						output.append("The winner is " + winners.get(0).getAsMention() + "!");
					} else {
						output.append("The winners are ");
						for (int i = 0; i < (winners.size() - 1); i++) {
							output.append(winners.get(i).getAsMention() + ", ");
						}
						output.append("and " + winners.get(winners.size() - 1).getAsMention() + "!");
					}
				} else {
					output.setContent("No winners found for " + guild.getMemberById(raffle.ownerId).getAsMention() + "'s raffle.");
				}
				// Send raffle-end declaration
				updateRaffle(raffle, guild, message, numEntrants, winners);
				tchan.sendMessage(output.build()).queue();

				// Update database
				if (raffle.deleteOnEnd) {
					CRaffle.delete(raffle);
				} else {
					raffle.channelId = 0L;
					raffle.messageId = 0L;
					raffle.raffleEnd = new Timestamp(0);
					CRaffle.update(raffle);
				}
			}
		}
	}

	/**
	 * Cancel raffle.
	 *
	 * @param raffle the raffle
	 * @param guild  the guild
	 */
	public void cancelRaffle(ORaffle raffle, Guild guild) {
		// Commenting this out because I want an error to be thrown if this happens
//		if(raffle.channelId == 0L || raffle.messageId == 0L) {
//			return;
//		}
		TextChannel chan = guild.getTextChannelById(raffle.channelId);
		Message mess = chan.retrieveMessageById(raffle.messageId).complete();
		mess.delete().queue();

		raffle.channelId = 0L;
		raffle.messageId = 0L;
		raffle.raffleEnd = new Timestamp(0);
		CRaffle.update(raffle);
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
		if (adding && reaction.getName().equals(ENTRY_EMOJI)) {
			long guildId = guild.getIdLong();
			initGuild(guildId, false);
			if (listeners.containsKey(guildId) && listeners.get(guildId).containsKey(message)) {
				ret = true;
				ORaffle raffle = CRaffle.findByMessage(guildId, message);

				Message mess = channel.retrieveMessageById(message).complete();
				List<MessageReaction> reactions = mess.getReactions();
				for (MessageReaction messageReaction : reactions) {
//						boolean debug = GuildSettings.getBoolFor(channel, GSetting.DEBUG);
//						if (debug) {
//							channel.sendMessage(String.format("[DEBUG] Single responses: checking `%s` vs `%s`", messageReaction.getReactionEmote(), reaction))
//									.queue();
//						}
					if (messageReaction.getReactionEmote().equals(reaction)) {
						if (raffle.id > 0 && (raffle.entrants < messageReaction.getCount()
								|| (raffle.raffleEnd != null && new Date().getTime() >= raffle.raffleEnd.getTime()))) {
							endRaffle(raffle, guild);
						}
						break;
					}
				}
			}
		}
		return ret;
	}

	/**
	 * Start raffle.
	 *
	 * @param raffle the raffle
	 * @param guild  the guild
	 */
	public void startRaffle(ORaffle raffle, Guild guild) {
		// Check for any raffles for this guild (unnecessary, the guilds are initialized
		// when the bot loads)
//		initGuild(guild.getIdLong(), false);
		// Attempt to display the raffle
		final ORaffle raf = displayRaffle(raffle, guild);
		// If the raffle is started, add it to the listeners and update the database
		// entry.
		if (raf.messageId != 0L) {
			addRaffle(raf.guildId, raf.messageId, raf.raffleEnd);
			CRaffle.update(raf);
			if (raf.duration > 0) {
				// Schedule standard end-check
				bot.schedule(new Runnable() {

					@Override
					public void run() {
						bot.raffleHandler.checkRaffle(raf, guild);

					}
				}, raf.durationUnit.toMillis(raf.duration) + 1000, TimeUnit.MILLISECONDS);
				// Schedule hourly end-check
				if (!runningRepeater.contains(guild.getIdLong())) {
					runningRepeater.add(guild.getIdLong());
					makeRepeatingRunner(guild, raf);
				}
			}
		}
	}

	/**
	 * Display raffle o raffle.
	 *
	 * @param raffle the raffle
	 * @param guild  the guild
	 * @return the o raffle
	 */
	public ORaffle displayRaffle(ORaffle raffle, Guild guild) {
		// Make sure the raffle is for the correct guild
		if (raffle.guildId != guild.getIdLong() || raffle.channelId == 0L) {
			return raffle;
		}

		// Put all necessary information about the raffle onto the embed builder
		EmbedBuilder rafEm = new EmbedBuilder();

		Member owner = guild.getMemberById(raffle.ownerId);
		rafEm.setAuthor(owner.getEffectiveName() + "'s raffle", null, owner.getUser().getAvatarUrl());
		rafEm.setColor(owner.getColor());
		rafEm.setTitle(raffle.prize);
		rafEm.setDescription(raffle.description);
		String footer = "";
		if (raffle.id != 0) {
			footer += "[" + raffle.id + "] ";
//			rafEm.setFooter("Raffle ID " + raffle.id, null);
		}
		if (raffle.duration > 0) {
			raffle.raffleEnd = new Timestamp(new Date().getTime() + raffle.durationUnit.toMillis(raffle.duration));
			footer += "Raffle ends";
//			rafEm.addField("Raffle ends", TIMESTAMP_FORMATTER.format(raffle.raffleEnd), true);
			rafEm.setTimestamp(Instant.ofEpochMilli(raffle.raffleEnd.getTime()));
		}
		if (footer.length() > 0) {
			rafEm.setFooter(footer, null);
		}
		if (raffle.entrants != 99) {
			rafEm.addField("Max entrants", raffle.entrants + "", true);
		}
		if (raffle.winners != 1) {
			rafEm.addField("Winners", raffle.winners + "", true);
		}
		rafEm.addField("React to enter", ENTRY_EMOJI, true);
		if (raffle.thumb.length() > 0) {
			rafEm.setThumbnail(raffle.thumb);
		}
		if (raffle.image.length() > 0) {
			rafEm.setImage(raffle.image);
		}

		// Display the raffle and get the message ID.
		TextChannel chan = guild.getTextChannelById(raffle.channelId);
		Message mess = chan.sendMessage(rafEm.build()).complete();
		raffle.messageId = mess.getIdLong();
		mess.addReaction(ENTRY_EMOJI).queue();

		return raffle;
	}

	/**
	 * Update raffle.
	 *
	 * @param raffle      the raffle
	 * @param guild       the guild
	 * @param message     the message
	 * @param numEntrants the num entrants
	 * @param winners     the winners
	 */
	public void updateRaffle(ORaffle raffle, Guild guild, Message message, int numEntrants, List<Member> winners) {
		if (message.getEmbeds().size() == 0) { // Check that there is an embed in the message
			return;
		}
		EmbedBuilder rafEm = new EmbedBuilder(message.getEmbeds().get(0)); // Get the raffle embed
		rafEm.clearFields();
//		List<Field> fields = rafEm.getFields(); // Get all fields
//
//		// Modify each field
//		for (int i = 0; i < fields.size(); i++) {
//			Field f = fields.get(i);
//			switch (f.getName()) {
//			case "Raffle ends":
//				fields.set(i, new Field("Raffle ended", f.getValue(), f.isInline()));
//				System.out.println(f.getValue());
//				break;
//			case "Max entrants":
//				fields.set(i, new Field("Entrants", numEntrants + "", f.isInline()));
//				break;
//			case "Winners":
//				String w = "";
//				for (Member winner : winners) {
//					w += winner.getEffectiveName() + "\n";
//				}
//				fields.set(i, new Field(f.getName(), w, f.isInline()));
//				break;
//			case "React to enter":
//				fields.remove(i);
//			}
//		}

		// Display all fields, not just the non-default ones.
//		rafEm.addField("Raffle ended", TIMESTAMP_FORMATTER.format(new Date()), true);
		rafEm.setFooter("[" + raffle.id + "] Raffle ended", null);
		rafEm.setTimestamp(Instant.now());
		rafEm.addField("Entrants", numEntrants + "", true);
		String w = "";
		for (Member winner : winners) {
			w += winner.getEffectiveName() + "\n";
		}
		rafEm.addField("Winners", w, true);

		// Update message with modified embed
		message.editMessage(rafEm.build()).queue();
	}

	private void makeRepeatingRunner(Guild guild, ORaffle raf) {
		if (runningRepeater.contains(guild.getIdLong())) {
			bot.schedule(new Runnable() {
				@Override
				public void run() {
					if (bot.raffleHandler.checkRaffles(guild)) {
						runningRepeater.remove(guild.getIdLong());
					} else {
						makeRepeatingRunner(guild, raf);
					}
				}
			}, 1L, TimeUnit.HOURS);
		}
	}
}
