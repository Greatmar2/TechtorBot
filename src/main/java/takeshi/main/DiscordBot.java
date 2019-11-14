/*
 * Copyright 2017 github.com/kaaz
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

package takeshi.main;

import com.mashape.unirest.http.Unirest;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.MessageBuilder.SplitPolicy;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import takeshi.command.bot_administration.ReplyCommand;
import takeshi.db.controllers.CBanks;
import takeshi.db.controllers.CGuild;
import takeshi.event.JDAEventManager;
import takeshi.event.JDAEvents;
import takeshi.guildsettings.GSetting;
import takeshi.handler.*;
import takeshi.handler.discord.RestQueue;
import takeshi.permission.SimpleRank;
import takeshi.role.RoleRankings;
import takeshi.templates.Templates;
import takeshi.util.DisUtil;
import takeshi.util.Misc;

import javax.security.auth.login.LoginException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The type Discord bot.
 */
public class DiscordBot {

	/**
	 * The constant LOGGER.
	 */
	public static final Logger LOGGER = LogManager.getLogger(DiscordBot.class);
	/**
	 * The Startup time stamp.
	 */
	public final long startupTimeStamp;
	/**
	 * The Queue.
	 */
	public final RestQueue queue;
	private final int totShards;
	private final ScheduledExecutorService scheduler;
	private final AtomicReference<JDA> jda;
	/**
	 * The Mention me.
	 */
	public String mentionMe;
	/**
	 * The Mention me alias.
	 */
	public String mentionMeAlias;
	/**
	 * The Chat bot handler.
	 */
	public ChatBotHandler chatBotHandler = null;
	/**
	 * The Security.
	 */
	public SecurityHandler security = null;
	/**
	 * The Out.
	 */
	public OutgoingContentHandler out = null;
	/**
	 * The Music reaction handler.
	 */
	public MusicReactionHandler musicReactionHandler = null;
	/**
	 * The Raffle handler.
	 */
	public RaffleHandler raffleHandler = null;
	/**
	 * The Poll handler.
	 */
	public PollHandler pollHandler = null;
	/**
	 * The Role reaction handler.
	 */
	public RoleReactionHandler roleReactionHandler = null;
	/**
	 * The Auto role handler.
	 */
	public AutoRoleHandler autoRoleHandler = null;
	/**
	 * The Command reaction handler.
	 */
	public CommandReactionHandler commandReactionHandler = null;
	/**
	 * The Game handler.
	 */
	public GameHandler gameHandler = null;
	/**
	 * The Reply listeners.
	 */
	public List<ReplyCommand.ReplyListener> replyListeners = new ArrayList<>();
	/**
	 * The Last forward.
	 */
	public MessageChannel lastForward = null;
	private AutoReplyHandler autoReplyhandler;
	private volatile boolean isReady = false;
	private int shardId;
	private BotContainer container;

	/**
	 * Instantiates a new Discord bot.
	 *
	 * @param shardId   the shard id
	 * @param numShards the num shards
	 * @param container the container
	 */
	public DiscordBot(int shardId, int numShards, BotContainer container) {
		queue = new RestQueue(this);
		scheduler = Executors.newScheduledThreadPool(1);
		jda = new AtomicReference<>();
		this.shardId = shardId;
		this.totShards = numShards;
		registerHandlers();
		setContainer(container);
		chatBotHandler = new ChatBotHandler(this);
		startupTimeStamp = System.currentTimeMillis() / 1000L;
		while (true) {
			try {
				restartJDA();
				break;
			} catch (LoginException | InterruptedException | RateLimitedException e) {
				try {
					Thread.sleep(5_000L);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
		markReady();
		container.setLastAction(shardId, System.currentTimeMillis());
	}

	/**
	 * Gets emote.
	 *
	 * @param emoteString the emote string
	 * @return the emote
	 */
	public Emote getEmote(String emoteString) {
		List<Emote> emotes = jda.get().getEmotesByName(emoteString, true);
		if (!emotes.isEmpty()) {
			return emotes.get(0);
		}

		if (Misc.parseLong(emoteString, 0) > 0) {
			return jda.get().getEmoteById(emoteString);
		}
		return null;
	}

	/**
	 * Update jda.
	 *
	 * @param jda the jda
	 */
	public void updateJda(JDA jda) {
		this.jda.compareAndSet(this.jda.get(), jda);
	}

	/**
	 * Gets jda.
	 *
	 * @return the jda
	 */
	public JDA getJda() {
		return jda.get();
	}

	/**
	 * Restart jda.
	 *
	 * @throws LoginException       the login exception
	 * @throws InterruptedException the interrupted exception
	 * @throws RateLimitedException the rate limited exception
	 */
	public void restartJDA() throws LoginException, InterruptedException, RateLimitedException {
		JDABuilder builder = new JDABuilder(AccountType.BOT).setToken(BotConfig.BOT_TOKEN);
		if (totShards > 1) {
			builder.useSharding(shardId, totShards);
		}
		builder.setBulkDeleteSplittingEnabled(false);
		builder.setEnableShutdownHook(false);
		builder.setEventManager(new JDAEventManager(this));
		System.out.println("STARTING SHARD " + shardId);
		jda.set(builder.build().awaitReady());
		jda.get().addEventListener(new JDAEvents(this));
		System.out.println("SHARD " + shardId + " IS READY ");
		//
	}

	/**
	 * Schedule the a task somewhere in the future
	 *
	 * @param task     the task
	 * @param delay    the delay
	 * @param timeUnit unit type of delay
	 */
	public void schedule(Runnable task, Long delay, TimeUnit timeUnit) {
		scheduler.schedule(task, delay, timeUnit);
	}

	/**
	 * schedule a repeating task
	 *
	 * @param task        the task
	 * @param startDelay  delay before starting the first iteration
	 * @param repeatDelay delay between consecutive executions
	 * @return the scheduled future
	 */
	public ScheduledFuture<?> scheduleRepeat(Runnable task, long startDelay, long repeatDelay) {
		return scheduler.scheduleWithFixedDelay(task, startDelay, repeatDelay, TimeUnit.MILLISECONDS);
	}

	/**
	 * Should the bot clean up after itself in specified channel?
	 *
	 * @param channel the channel to check for
	 * @return delete the message?
	 */
	public boolean shouldCleanUpMessages(MessageChannel channel) {
		String cleanupMethod = GuildSettings.getFor(channel, GSetting.CLEANUP_MESSAGES);
		String myChannel = GuildSettings.getFor(channel, GSetting.BOT_CHANNEL);
		if ("yes".equals(cleanupMethod)) {
			return true;
		} else if ("nonstandard".equals(cleanupMethod) && !channel.getName().equalsIgnoreCase(myChannel)) {
			return true;
		}
		return false;
	}

	/**
	 * Log guild event.
	 *
	 * @param guild    the guild
	 * @param category the category
	 * @param message  the message
	 */
	public void logGuildEvent(Guild guild, String category, String message) {
		TextChannel channel = getChannelFor(guild.getIdLong(), GSetting.BOT_LOGGING_CHANNEL);
		if (channel == null) {
			return;
		}
		if (!channel.canTalk()) {
			out.sendAsyncMessage(getDefaultChannel(guild), Templates.config.cant_talk_in_channel
					.format(GuildSettings.get(guild).getOrDefault(GSetting.BOT_LOGGING_CHANNEL)));
			return;
		}
		out.sendAsyncMessage(channel, String.format("%s %s", category, message));
	}

	/**
	 * Gets shard id.
	 *
	 * @return the shard id
	 */
	public int getShardId() {
		return shardId;
	}

	/**
	 * Is ready boolean.
	 *
	 * @return the boolean
	 */
	public boolean isReady() {
		return isReady;
	}

	/**
	 * Gets the default channel to output to if configured channel can't be found,
	 * return the first channel
	 *
	 * @param guild the guild to check
	 * @return default chat channel
	 */
	public synchronized TextChannel getDefaultChannel(Guild guild) {
		TextChannel defaultChannel = getChannelFor(guild.getIdLong(), GSetting.BOT_CHANNEL);
		if (defaultChannel != null) {
			return defaultChannel;
		}
		return DisUtil.findFirstWriteableChannel(guild);
	}

	/**
	 * gets the default channel to output music to
	 *
	 * @param guild guild
	 * @return default music channel
	 */
	public synchronized TextChannel getMusicChannel(Guild guild) {
		return getMusicChannel(guild.getIdLong());
	}

	/**
	 * Gets music channel.
	 *
	 * @param guildId the guild id
	 * @return the music channel
	 */
	public synchronized TextChannel getMusicChannel(long guildId) {
		Guild guild = getJda().getGuildById(guildId);
		if (guild == null) {
			return null;
		}
		TextChannel channel = getChannelFor(guildId, GSetting.MUSIC_CHANNEL);
		if (channel == null) {
			channel = getDefaultChannel(guild);
		}
		if (channel == null || !channel.canTalk()) {
			return null;
		}
		return channel;
	}

	/**
	 * Retrieves the moderation log of a guild
	 *
	 * @param guildId the guild to get the modlog-channel for
	 * @return channel || null
	 */
	public synchronized TextChannel getModlogChannel(long guildId) {
		return getChannelFor(guildId, GSetting.BOT_MODLOG_CHANNEL);
	}

	/**
	 * retrieves a channel for setting
	 *
	 * @param guildId the guild
	 * @param setting the channel setting
	 * @return A text channel Or null in case it can't be found
	 */
	private synchronized TextChannel getChannelFor(long guildId, GSetting setting) {
		Guild guild = getJda().getGuildById(guildId);
		if (guild == null) {
			return null;
		}
		String channelId = GuildSettings.get(guild.getIdLong()).getOrDefault(setting);
		if (channelId.matches("\\d{12,}")) {
			return guild.getTextChannelById(channelId);
		} else if (!channelId.isEmpty() && !"false".equals(channelId)) {
			return DisUtil.findChannel(guild, channelId);
		}
		return null;
	}

	/**
	 * Retrieves the moderation log of a guild
	 *
	 * @param guild the guild to get the modlog-channel for
	 * @return channel || null
	 */
	public synchronized TextChannel getCommandLogChannel(long guild) {
		return getChannelFor(guild, GSetting.COMMAND_LOGGING_CHANNEL);
	}

	/**
	 * Mark the shard as ready, the bot will start working once all shards are
	 * marked as ready
	 */
	public void markReady() {
		if (isReady) {
			return;
		}
		mentionMe = "<@" + this.getJda().getSelfUser().getId() + ">";
		mentionMeAlias = "<@!" + this.getJda().getSelfUser().getId() + ">";
		sendStatsToDiscordPw();
		sendStatsToDiscordbotsOrg();
		isReady = true;
		RoleRankings.fixRoles(this.getJda().getGuilds());
		container.allShardsReady();
	}

	/**
	 * Reload auto replies.
	 */
	public void reloadAutoReplies() {
		autoReplyhandler.reload();
	}

	/**
	 * Remove all cached objects for a guild
	 *
	 * @param guild the guild to clear
	 */
	public void clearGuildData(Guild guild) {
		GuildSettings.remove(guild.getIdLong());
		autoReplyhandler.removeGuild(guild.getIdLong());
		MusicPlayerHandler.removeGuild(guild);
		commandReactionHandler.removeGuild(guild.getIdLong());
	}

	/**
	 * load data for a guild
	 *
	 * @param guild guild to load for
	 */
	public void loadGuild(Guild guild) {
		int cachedId = CGuild.getCachedId(guild.getIdLong());
		CommandHandler.loadCustomCommands(cachedId);
	}

	private void registerHandlers() {
		security = new SecurityHandler();
		gameHandler = new GameHandler(this);
		out = new OutgoingContentHandler(this);
		musicReactionHandler = new MusicReactionHandler(this);
		roleReactionHandler = new RoleReactionHandler(this);
		autoRoleHandler = new AutoRoleHandler(this);
		raffleHandler = new RaffleHandler(this);
		pollHandler = new PollHandler(this);
		commandReactionHandler = new CommandReactionHandler();
		autoReplyhandler = new AutoReplyHandler(this);
	}

	/**
	 * Gets user name.
	 *
	 * @return the user name
	 */
	public String getUserName() {
		return getJda().getSelfUser().getName();
	}

	/**
	 * Sets user name.
	 *
	 * @param newName the new name
	 * @return the user name
	 */
	public boolean setUserName(String newName) {
		if (!getUserName().equals(newName)) {
			getJda().getSelfUser().getManager().setName(newName).complete();
			return true;
		}
		return false;
	}

	/**
	 * Add stream to queue.
	 *
	 * @param url   the url
	 * @param guild the guild
	 */
	public void addStreamToQueue(String url, Guild guild) {
		MusicPlayerHandler.getFor(guild, this).addStream(url);
		MusicPlayerHandler.getFor(guild, this).startPlaying();
	}

	/**
	 * Handle private message.
	 *
	 * @param channel the channel
	 * @param author  the author
	 * @param message the message
	 */
	public void handlePrivateMessage(PrivateChannel channel, User author, Message message) {
		if (security.isBanned(author)) {
			return;
		}
		if (CommandHandler.isCommand(null, message.getContentRaw(), mentionMe, mentionMeAlias)) {
			CommandHandler.process(this, channel, author, message);
		} else if (BotConfig.PRIVATE_MESSAGE_FORWARDING_ENABLED) {
			DisUtil.forwardMessage(this, message);
		} else if (BotConfig.BOT_CHATTING_ENABLED) {
			channel.sendTyping().queue();
			this.out.sendAsyncMessage(channel, this.chatBotHandler.chat(0L, message.getContentRaw(), channel), null);
		}
	}

	/**
	 * Handle message.
	 *
	 * @param guild   the guild
	 * @param channel the channel
	 * @param author  the author
	 * @param message the message
	 */
	public void handleMessage(Guild guild, TextChannel channel, User author, Message message) {
		// System.out.println(message.getContentRaw());
		if (author == null || (author.isBot() && !security.isInteractionBot(author.getIdLong()))) {
			return;
		}
		if (security.isBanned(author) || !security.getSimpleRank(author, channel).isAtLeast(SimpleRank.USER)) {
			return;
		}
		GuildSettings settings = GuildSettings.get(guild.getIdLong());
//		pollHandler.checkPolls(guild);
		// Check if bot is listening for replies on any specific channel
		for (int i = 0; i < replyListeners.size(); i++) {
			if (replyListeners.get(i).getTimeCreated().plusMinutes(BotConfig.CHANNEL_WATCH_DURATION)
					.isAfter(OffsetDateTime.now())) {
				if (channel.getIdLong() == replyListeners.get(i).getChannelID()) {
					DisUtil.forwardMessage(this, message);
				}
			} else {
				replyListeners.remove(replyListeners.get(i));
				i--;
			}
		}
		// Handle users in game mode
		if (gameHandler.isGameInput(channel, author, message.getContentRaw().toLowerCase())) {
			gameHandler.execute(author, channel, message.getContentRaw(), null);
			return;
		}
		// Handle commands
		if (CommandHandler.isCommand(channel, message.getContentRaw().trim(), mentionMe, mentionMeAlias)) {
			CommandHandler.process(this, channel, author, message);
			return;
		}
		// If autoreply is active, check for regexes that match with set autoreplies
		if (GuildSettings.getBoolFor(channel, GSetting.AUTO_REPLY)) {
			if (autoReplyhandler.autoReplied(message)) {
				return;
			}
		}
		// If Techtor's name is mentioned and bot forwarding is enabled
		if (message.getContentDisplay().toLowerCase().contains(BotConfig.BOT_NAME.toLowerCase())
				&& BotConfig.GUILD_MESSAGE_FORWARDING_ENABLED) {
			DisUtil.forwardMessage(this, message);
			return;
		} else if (BotConfig.BOT_CHATTING_ENABLED && settings.getBoolValue(GSetting.CHAT_BOT_ENABLED)
				&& channel.getId().equals(GuildSettings.get(channel.getGuild()).getOrDefault(GSetting.BOT_CHANNEL))) {
			// If bot AI chatting is enabled and forwarding is disabled
			if (PermissionUtil.checkPermission(channel, channel.getGuild().getSelfMember(), Permission.MESSAGE_WRITE)) {
				channel.sendTyping().queue();
				this.out.sendAsyncMessage(channel,
						this.chatBotHandler.chat(guild.getIdLong(), message.getContentRaw(), channel), null);
			}
		}
	}

	/**
	 * Gets container.
	 *
	 * @return the container
	 */
	public BotContainer getContainer() {
		return container;
	}

	/**
	 * Sets container.
	 *
	 * @param container the container
	 */
	public void setContainer(BotContainer container) {
		this.container = container;
	}

	/**
	 * Send stats to discord pw.
	 */
	public void sendStatsToDiscordPw() {
		if (!BotConfig.BOT_STATS_DISCORD_PW_ENABLED) {
			return;
		}
		JSONObject data = new JSONObject();
		data.put("server_count", getJda().getGuilds().size());
		if (totShards > 1) {
			data.put("shard_id", shardId);
			data.put("shard_count", totShards);
		}
		Unirest.post("https://bots.discord.pw/api/bots/" + getJda().getSelfUser().getId() + "/stats")
				.header("Authorization", BotConfig.BOT_TOKEN_BOTS_DISCORD_PW).header("Content-Type", "application/json")
				.body(data.toString()).asJsonAsync();
	}

	/**
	 * Send stats to discordbots org.
	 */
	public void sendStatsToDiscordbotsOrg() {
		if (BotConfig.BOT_TOKEN_DISCORDBOTS_ORG.length() < 10) {
			return;
		}
		JSONObject data = new JSONObject();
		data.put("server_count", getJda().getGuilds().size());
		if (totShards > 1) {
			data.put("shard_id", shardId);
			data.put("shard_count", totShards);
		}
		Unirest.post("https://discordbots.org/api/bots/" + getJda().getSelfUser().getId() + "/stats")
				.header("Authorization", BotConfig.BOT_TOKEN_DISCORDBOTS_ORG).header("Content-Type", "application/json")
				.body(data.toString()).asJsonAsync();
	}

	/**
	 * Init once.
	 */
	public void initOnce() {
		CBanks.init(getJda().getSelfUser().getIdLong(), getJda().getSelfUser().getName());
	}

	/**
	 * Will attempt to sent a message to a channel after splitting it so as to not
	 * hit the character count.
	 *
	 * @param channel the channel
	 * @param message the message
	 */
	public void sendLongMessageToChannel(MessageChannel channel, MessageBuilder message) {
		Queue<Message> messageQueue = message.buildAll(SplitPolicy.NEWLINE);
		while (messageQueue.peek() != null) {
			queue.add(channel.sendMessage(messageQueue.poll()));
		}
	}
}