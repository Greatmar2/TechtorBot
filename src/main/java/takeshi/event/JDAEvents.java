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

package takeshi.event;

import emoji4j.EmojiUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.DisconnectEvent;
import net.dv8tion.jda.api.events.ReconnectedEvent;
import net.dv8tion.jda.api.events.ResumedEvent;
import net.dv8tion.jda.api.events.StatusChangeEvent;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateActivityOrderEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import takeshi.db.controllers.CBotEvent;
import takeshi.db.controllers.CGuild;
import takeshi.db.controllers.CGuildMember;
import takeshi.db.controllers.CUser;
import takeshi.db.model.OGuild;
import takeshi.db.model.OGuildMember;
import takeshi.db.model.OUser;
import takeshi.guildsettings.GSetting;
import takeshi.handler.GuildSettings;
import takeshi.handler.MusicPlayerHandler;
import takeshi.main.BotConfig;
import takeshi.main.DiscordBot;
import takeshi.main.GuildCheckResult;
import takeshi.main.Launcher;
import takeshi.role.RoleRankings;
import takeshi.service.BotStatusService;
import takeshi.templates.Template;
import takeshi.templates.Templates;
import takeshi.util.DisUtil;

import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created on 12-10-2016
 */
public class JDAEvents extends ListenerAdapter {
	private final DiscordBot discordBot;

	/**
	 * Instantiates a new Jda events.
	 *
	 * @param bot the bot
	 */
	public JDAEvents(DiscordBot bot) {
		this.discordBot = bot;
	}

	@Override
	public void onDisconnect(DisconnectEvent event) {
		DiscordBot.LOGGER.info("[event] DISCONNECTED! ");
	}

	@Override
	public void onStatusChange(StatusChangeEvent event) {
		discordBot.getContainer().reportStatus(event.getJDA().getShardInfo() != null ? event.getJDA().getShardInfo().getShardId() : 0, event.getOldStatus(),
				event.getNewStatus());
//		discordBot.pollHandler.checkPolls(discordBot);
	}

	@Override
	public void onResume(ResumedEvent event) {
	}

	@Override
	public void onReconnect(ReconnectedEvent event) {
		discordBot.getContainer().reportError(String.format("[RECONNECT] \\#%02d with a different JDA", discordBot.getShardId()));
	}

	@Override
	public void onGuildJoin(GuildJoinEvent event) {
		Guild guild = event.getGuild();
		User owner = guild.getOwner().getUser();
		OUser user = CUser.findBy(owner.getIdLong());
		user.discord_id = owner.getId();
		user.name = EmojiUtils.shortCodify(owner.getName());
		CUser.update(user);
		OGuild dbGuild = CGuild.findBy(guild.getId());
		dbGuild.discord_id = guild.getIdLong();
		dbGuild.name = EmojiUtils.shortCodify(guild.getName());
		dbGuild.owner = user.id;
		if (dbGuild.id == 0) {
			CGuild.insert(dbGuild);
		}
		if (dbGuild.isBanned()) {
			discordBot.queue.add(guild.leave());
			return;
		}
		discordBot.loadGuild(guild);
		String cmdPre = GuildSettings.get(guild).getOrDefault(GSetting.COMMAND_PREFIX);
		String joinEventMessage = "Guild joined. "; // This message will be displayed in the bot's home guild to notify of actions
		// in other guilds.
		GuildCheckResult guildCheck = discordBot.security.checkGuild(guild);

		if (dbGuild.active != 1) {
			String message = "Thanks for adding me to your guild!\n" + "To see what I can do you can type the command `" + cmdPre + "help`.\n"
					+ "Most of my features are opt-in, which means that you'll have to enable them first. Admins can use `" + cmdPre
					+ "config` to change my settings.\n"
					+ "Most commands have a help portion which can be accessed by typing help after the command; For instance: `" + cmdPre + "skip help`\n\n"
					+ "If you need help or would like to give feedback, feel free to let me know on `" + cmdPre + "discord`";
//					+ "If you need help or would like to give feedback, feel free to let me know on either `" + cmdPre + "discord` or `" + cmdPre + "github`";
			switch (guildCheck) {
				case TEST_GUILD:
					message += "\n\n:Warning: The guild has been categorized as a test guild. This means that I might leave this guild when the next cleanup happens.\n"
							+ "If this is not a test guild feel free to join my `" + cmdPre + "discord` and ask to have your guild added to the whitelist!";
					joinEventMessage += ":warning: Test guild! Will leave at next cleanup.";
					break;
				case BOT_GUILD:
					message += "\n\n:Warning: :robot: Too many bots here, I'm leaving!\n"
							+ "If your guild is not a collection of bots and you actually plan on using me join my `" + cmdPre
							+ "discord` and ask to have your guild added to the whitelist!";
					joinEventMessage += ":robot: Bot guild! Leaving.";
					break;
				case SMALL:
				case OWNER_TOO_NEW:
				case OKE:
				default:
					joinEventMessage += ":inbox_tray: Normal Guild.";
					break;
			}
			TextChannel outChannel = null;
			for (TextChannel channel : guild.getTextChannels()) {
				if (channel.canTalk()) {
					outChannel = channel;
					break;
				}
			}
			CBotEvent.insert(":house:", ":white_check_mark:", String.format(":id: %s | :hash: %s | :busts_in_silhouette: %s | %s", guild.getId(), dbGuild.id,
					guild.getMembers().size(), EmojiUtils.shortCodify(guild.getName())).replace("@", "@\u200B"));
			discordBot.getContainer().guildJoined();
			Launcher.log("bot joins guild", "bot", "guild-join", "guild-id", guild.getId(), "guild-name", guild.getName());
			if (outChannel != null) {
				discordBot.out.sendAsyncMessage(outChannel, message, null);
			} else {
				discordBot.out.sendPrivateMessage(owner, message);
			}
			if (guildCheck.equals(GuildCheckResult.BOT_GUILD)) {
				discordBot.queue.add(guild.leave());
			}
			dbGuild.active = 1;
		}
		CGuild.update(dbGuild);
		DiscordBot.LOGGER.info("[event] JOINED SERVER! " + guild.getName());
		discordBot.sendStatsToDiscordPw();
		discordBot.getContainer().sendStatsToDiscordlistNet();
		for (Member member : event.getGuild().getMembers()) {
			User guildUser = member.getUser();
			int userId = CUser.getCachedId(guildUser.getIdLong(), guildUser.getName());
			OGuildMember guildMember = CGuildMember.findBy(dbGuild.id, userId);
			guildMember.joinDate = new Timestamp(member.getTimeJoined().toInstant().toEpochMilli());
			CGuildMember.insertOrUpdate(guildMember);
		}
		if (joinEventMessage.length() > 0) {
			DisUtil.notifyOfEvent(discordBot, guild, joinEventMessage);
		}

	}

	@Override
	public void onGuildLeave(GuildLeaveEvent event) {
		Guild guild = event.getGuild();
		OGuild server = CGuild.findBy(guild.getId());
		server.active = 0;
		CGuild.update(server);
		discordBot.clearGuildData(guild);
		discordBot.getContainer().guildLeft();
		if (server.isBanned()) {
			return;
		}
		discordBot.sendStatsToDiscordPw();
		discordBot.getContainer().sendStatsToDiscordlistNet();
		Launcher.log("bot leaves guild", "bot", "guild-leave", "guild-id", guild.getId(), "guild-name", guild.getName());
		CBotEvent.insert(":house_abandoned:", ":fire:",
				String.format(":id: %s | :hash: %s | %s", guild.getId(), server.id, EmojiUtils.shortCodify(guild.getName()).replace("@", "@\u200B")));
		DisUtil.notifyOfEvent(discordBot, guild, "Left Guild");
	}

	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event) {
		handleReaction(event, true);
	}

	@Override
	public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
		handleReaction(event, false);
	}

	private void handleReaction(GenericMessageReactionEvent e, boolean adding) {
		if (e.getUser().isBot()) {
			if (!discordBot.security.isInteractionBot(e.getUser().getIdLong())) {
				return;
			}
		}
		if (!e.getChannel().getType().equals(ChannelType.TEXT)) {
			return;
		}
		TextChannel channel = (TextChannel) e.getChannel();
		if (discordBot.commandReactionHandler.canHandle(channel.getGuild().getIdLong(), e.getMessageIdLong())) {
			discordBot.commandReactionHandler.handle(channel, e.getMessageIdLong(), e.getUser().getIdLong(), e.getReaction(), adding);
			return;
		}
		if (!discordBot.gameHandler.executeReaction(e.getUser(), e.getChannel(), e.getReaction(), e.getMessageId())) {
			if (!discordBot.musicReactionHandler.handle(e.getMessageIdLong(), channel, e.getUser(), e.getReactionEmote(), adding)) {
				if (!discordBot.roleReactionHandler.handle(e.getMessageId(), channel, e.getUser(), e.getReactionEmote(), adding)) {
					if (!discordBot.pollHandler.handleReaction(e.getGuild(), e.getMessageIdLong(), channel, e.getUser(), e.getReactionEmote(), adding)) {
						discordBot.raffleHandler.handleReaction(e.getGuild(), e.getMessageIdLong(), channel, e.getUser(), e.getReactionEmote(), adding);
					}
				}
			}
		}
	}

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		discordBot.handleMessage(event.getGuild(), event.getChannel(), event.getAuthor(), event.getMessage());
	}

	@Override
	public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
		if (event.getAuthor().isBot()) {
			return;
		}
		discordBot.handlePrivateMessage(event.getChannel(), event.getAuthor(), event.getMessage());
	}

	@Override
	public void onGuildBan(GuildBanEvent event) {
		discordBot.logGuildEvent(event.getGuild(), "\uD83D\uDED1",
				"**" + event.getUser().getName() + "#" + event.getUser().getDiscriminator() + "** has been banned");
	}

	@Override
	public void onGuildMemberUpdateNickname(GuildMemberUpdateNicknameEvent event) {
		String message = "**" + event.getMember().getUser().getName() + "#" + event.getMember().getUser().getDiscriminator() + "** ("
				+ event.getMember().getAsMention() + ") changed nickname ";
		if (event.getOldNickname() != null) {
			message += "from _~~" + event.getOldNickname() + "~~_ ";
		}
		if (event.getNewNickname() != null) {
			message += "to **" + event.getNewNickname() + "**";
		} else {
			message += "back to normal";
		}
		discordBot.logGuildEvent(event.getGuild(), "\uD83C\uDFF7", message);
	}

	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		User user = event.getMember().getUser();
		Guild guild = event.getGuild();
		GuildSettings settings = GuildSettings.get(guild);
		OGuildMember guildMember = CGuildMember.findBy(guild.getIdLong(), user.getIdLong());
		boolean firstTime = guildMember.joinDate == null;
		guildMember.joinDate = new Timestamp(System.currentTimeMillis());
		CGuildMember.insertOrUpdate(guildMember);

		// PM owner if PM_USER_EVENTS is true
		if (settings.getBoolValue(GSetting.PM_USER_EVENTS)) {
			discordBot.out.sendPrivateMessage(guild.getOwner().getUser(),
					String.format("[user-event] **%s#%s** (" + event.getMember().getAsMention() + ") joined the guild **%s**", user.getName(),
							user.getDiscriminator(), guild.getName()),
					null);
		}
		// Log the event in the guild log channel
		discordBot.logGuildEvent(guild, "\uD83D\uDC64",
				"**" + event.getMember().getUser().getName() + "#" + event.getMember().getUser().getDiscriminator() + "** joined the guild");
		// Add auto role if one is set
		discordBot.autoRoleHandler.handle(guild, event.getMember());
		// Welcome new users if enabled
		if (settings.getBoolValue(GSetting.WELCOME_NEW_USERS)) {
			TextChannel defaultChannel = discordBot.getDefaultChannel(guild);
			if (defaultChannel != null && defaultChannel.canTalk() && !discordBot.security.isBotAdmin(user.getIdLong())) {
				Template template = firstTime ? Templates.welcome_new_user : Templates.welcome_back_user;
				discordBot.queue.add(defaultChannel.sendMessage(template.formatGuild(guild.getIdLong(), guild, user)), message -> {
					if (!"no".equals(settings.getOrDefault(GSetting.CLEANUP_MESSAGES))) {
						discordBot.schedule(() -> discordBot.out.saveDelete(message), BotConfig.DELETE_MESSAGES_AFTER * 5, TimeUnit.MILLISECONDS);
					}
				});
			} else if (defaultChannel != null && defaultChannel.canTalk() && discordBot.security.isBotAdmin(user.getIdLong())) {
				Template template = Templates.welcome_bot_admin;
				discordBot.queue.add(defaultChannel.sendMessage(template.formatGuild(guild.getIdLong(), guild, user)), message -> {
					if (!"no".equals(settings.getOrDefault(GSetting.CLEANUP_MESSAGES))) {
						discordBot.schedule(() -> discordBot.out.saveDelete(message), BotConfig.DELETE_MESSAGES_AFTER * 5, TimeUnit.MILLISECONDS);
					}
				});

			}
		}
		Launcher.log("user joins guild", "guild", "member-join", "guild-id", guild.getId(), "guild-name", guild.getName(), "user-id", user.getId(), "user-name",
				user.getName());

		if (settings.getBoolValue(GSetting.USER_TIME_RANKS) && !user.isBot()) {
			RoleRankings.assignUserRole(discordBot, guild, user);
		}
	}

	@Override
	public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
		User user = event.getMember().getUser();
		if (user.isBot()) {
			return;
		}
		Guild guild = event.getGuild();
		if (GuildSettings.get(guild).getBoolValue(GSetting.PM_USER_EVENTS)) {
			discordBot.out.sendPrivateMessage(guild.getOwner().getUser(),
					String.format("[user-event] **%s#%s** (" + event.getMember().getAsMention() + ") left the guild **%s**", user.getName(),
							user.getDiscriminator(), guild.getName()));
		}
		if (GuildSettings.get(guild).getBoolValue(GSetting.WELCOME_NEW_USERS)) {
			TextChannel defaultChannel = discordBot.getDefaultChannel(guild);
			if (defaultChannel != null && defaultChannel.canTalk()) {
				discordBot.queue.add(defaultChannel.sendMessage(Templates.message_user_leaves.formatGuild(guild.getIdLong(), user, guild)), message -> {
					if (!"no".equals(GuildSettings.get(guild.getIdLong()).getOrDefault(GSetting.CLEANUP_MESSAGES))) {
						discordBot.schedule(() -> discordBot.out.saveDelete(message), BotConfig.DELETE_MESSAGES_AFTER * 5, TimeUnit.MILLISECONDS);
					}
				});
			}
		}
		Launcher.log("user leaves guild", "guild", "member-leave", "guild-id", guild.getId(), "guild-name", guild.getName(), "user-id", user.getId(),
				"user-name", user.getName());
		OGuildMember guildMember = CGuildMember.findBy(guild.getIdLong(), user.getIdLong());
		guildMember.joinDate = new Timestamp(System.currentTimeMillis());
		CGuildMember.insertOrUpdate(guildMember);
		discordBot.logGuildEvent(guild, "\uD83C\uDFC3", "**" + user.getName() + "#" + user.getDiscriminator() + "** left the guild");
	}

	@Override
	public void onUserUpdateActivityOrder(UserUpdateActivityOrderEvent event) {
	}

	@Override
	public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
		if (event.getMember().getUser().isBot()) {
			return;
		}
		// Change status when people join voice channel
		if (event.getChannelJoined().getMembers().size() > 1) {
			event.getJDA().getPresence().setActivity(Activity.listening("your calls"));
		}
		MusicPlayerHandler player = MusicPlayerHandler.getFor(event.getGuild(), discordBot);
		if (player.isConnected()) {
			return;
		}
		String autoChannel = GuildSettings.get(event.getGuild()).getOrDefault(GSetting.MUSIC_CHANNEL_AUTO);
		if ("false".equalsIgnoreCase(autoChannel)) {
			return;
		}
		if (event.getChannelJoined().getId().equals(autoChannel) || event.getChannelJoined().getName().equalsIgnoreCase(autoChannel)) {
			player.connectTo(event.getChannelJoined());
			player.playRandomSong();
		}
	}

	@Override
	public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
		if (!event.getMember().equals(event.getGuild().getSelfMember())) {
			checkLeaving(event.getGuild(), event.getChannelLeft(), event.getMember().getUser());
			onGuildVoiceJoin(new GuildVoiceJoinEvent(event.getJDA(), 0, event.getMember()));
		} else {
			checkLeaving(event.getGuild(), event.getChannelJoined(), event.getMember().getUser());
		}
	}

	@Override
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
		checkLeaving(event.getChannelLeft().getGuild(), event.getChannelLeft(), event.getMember().getUser());

		// If no users left, change status - PUT AFTER OTHER STUFF, it returns if it
		// finds users
		List<VoiceChannel> vChannels = event.getJDA().getVoiceChannels();
		for (VoiceChannel chan : vChannels) {
			if (chan.getMembers().size() > 0) {
				return;
			}
		}
		new BotStatusService(discordBot.getContainer()).run();
	}

	private void checkLeaving(Guild guild, VoiceChannel channel, User user) {
		if (user.isBot() && !user.equals(user.getJDA().getSelfUser())) {
			return;
		}
		MusicPlayerHandler player = MusicPlayerHandler.getFor(guild, discordBot);
		if (!player.isConnected()) {
			return;
		}
		if (!player.isConnectedTo(channel)) {
			return;
		}
		player.unregisterVoteSkip(user);
		if (player.getVoteCount() >= player.getRequiredVotes()) {
			player.forceSkip();
		}
		for (Member member : guild.getAudioManager().getConnectedChannel().getMembers()) {
			if (!member.getUser().isBot()) {
				return;
			}
		}
		player.leave();
		String autoChannel = GuildSettings.get(guild).getOrDefault(GSetting.MUSIC_CHANNEL_AUTO);
		if (!"false".equalsIgnoreCase(autoChannel) && channel.getName().equalsIgnoreCase(autoChannel)) {
			return;
		}
		TextChannel musicChannel = discordBot.getMusicChannel(guild);
		if (musicChannel != null && musicChannel.canTalk()) {
			discordBot.out.sendAsyncMessage(musicChannel, Templates.music.no_one_listens_i_leave.formatGuild(guild.getIdLong()));
		}
	}
}