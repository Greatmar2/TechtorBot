/*
 * Copyright 2017 github.com/kaaz and 2019 Greatmar2
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

package takeshi.util;

import com.google.api.client.repackaged.com.google.common.base.Joiner;
import emoji4j.EmojiUtils;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import takeshi.db.controllers.CGuild;
import takeshi.db.model.OGuild;
import takeshi.guildsettings.DefaultGuildSettings;
import takeshi.guildsettings.GSetting;
import takeshi.handler.GuildSettings;
import takeshi.main.BotConfig;
import takeshi.main.BotContainer;
import takeshi.main.DiscordBot;
import takeshi.main.Launcher;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilities for discord objects
 */
public class DisUtil {
	private static final Pattern mentionUserPattern = Pattern.compile("<@!?([0-9]{8,})>");
	private static final Pattern channelPattern = Pattern.compile("<#!?([0-9]{4,})>");
	private static final Pattern rolePattern = Pattern.compile("<@&([0-9]{4,})>");
	private static final Pattern anyMention = Pattern.compile("<[@#][&!]?([0-9]{4,})>");
	private static final Pattern discordId = Pattern.compile("(\\d{9,})");

	/**
	 * find a text channel by name
	 *
	 * @param guild       the guild to search in
	 * @param channelName the channel to search for
	 * @return TextChannel || null
	 */
	public static TextChannel findChannel(Guild guild, String channelName) {
		for (TextChannel channel : guild.getTextChannels()) {
			if (channel.getName().equalsIgnoreCase(channelName)) {
				return channel;
			}
		}
		return null;
	}

	/**
	 * Extract id string.
	 *
	 * @param id the id
	 * @return the string
	 */
	public static String extractId(String id) {
		Matcher matcher = discordId.matcher(id);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}

	/**
	 * check if a string has any mention
	 *
	 * @param search the text to match
	 * @return contains mention?
	 */
	public static boolean hasMention(String search) {
		return anyMention.matcher(search).matches();
	}

	/**
	 * Has permission boolean.
	 *
	 * @param channel    the channel
	 * @param user       the user
	 * @param permission the permission
	 * @return the boolean
	 */
	public static boolean hasPermission(MessageChannel channel, User user, Permission permission) {
		if (channel == null) {
			return false;
		}
		switch (channel.getType()) {
			case PRIVATE:
				return true;
			case TEXT:
				TextChannel textChannel = (TextChannel) channel;
				return PermissionUtil.checkPermission(textChannel, textChannel.getGuild().getMember(user), permission);
			default:
				return false;
		}
	}

	/**
	 * find a voice channel by name
	 *
	 * @param guild       the guild to search in
	 * @param channelName the channel to search for
	 * @return VoiceChannel || null
	 */
	public static VoiceChannel findVoiceChannel(Guild guild, String channelName) {
		for (VoiceChannel channel : guild.getVoiceChannels()) {
			if (channel.getName().equalsIgnoreCase(channelName)) {
				return channel;
			}
		}
		return null;
	}

	/**
	 * Gets the first channel in a guild where the bot has permission to write
	 *
	 * @param guild the guild to search in
	 * @return TextChannel || null
	 */
	public static TextChannel findFirstWriteableChannel(Guild guild) {
		for (TextChannel channel : guild.getTextChannels()) {
			if (channel.canTalk()) {
				return channel;
			}
		}
		return null;
	}

	/**
	 * Search for a guild
	 *
	 * @param searchArg text to look for with i-prefix searches for internal                  guild-id if it consists of at least 10 decimals, it will                  assume its a discord-id
	 * @param container the container to look in
	 * @return Guild || null
	 */
	public static Guild findGuildBy(String searchArg, BotContainer container) {
		if (searchArg.matches("i\\d+")) {
			OGuild rec = CGuild.findById(Integer.parseInt(searchArg.substring(1)));
			if (rec.id > 0) {
				return container.getShardFor(rec.discord_id).getJda().getGuildById(Long.toString(rec.discord_id));
			}
		} else if (searchArg.matches("^\\d{10,}$")) {
			return container.getShardFor(searchArg).getJda().getGuildById(searchArg);
		}
		return null;
	}

	/**
	 * Helper for {@link DisUtil#findGuildBy(String, BotContainer)} validates if the
	 * input could be converted to a guild
	 *
	 * @param searchArg text to validate
	 * @return is valid input?
	 */
	public static boolean matchesGuildSearch(String searchArg) {
		return searchArg != null && (searchArg.matches("i\\d+") || searchArg.matches("^\\d{10,}$"));
	}

	/**
	 * Checks if the string contains a mention for a role
	 *
	 * @param input string to check for mentions
	 * @return found a mention
	 */
	public static boolean isUserMention(String input) {
		return mentionUserPattern.matcher(input).find();
	}

	/**
	 * Is role mention boolean.
	 *
	 * @param input the input
	 * @return the boolean
	 */
	public static boolean isRoleMention(String input) {
		return rolePattern.matcher(input).find();
	}

	/**
	 * helper method to see if a guild uses the economy module
	 *
	 * @param channel channel to check
	 * @return use economy?
	 */
	public static boolean useEconomy(GuildChannel channel) {
		return channel instanceof TextChannel
				&& GuildSettings.getBoolFor(((TextChannel) channel), GSetting.MODULE_ECONOMY);
	}

	/**
	 * Replaces tags with a variable
	 *
	 * @param input    the message to replace tags in
	 * @param user     user info for user related tags
	 * @param channel  channel/guild info
	 * @param userArgs the user args
	 * @return formatted string
	 */
	public static String replaceTags(String input, User user, MessageChannel channel, String[] userArgs) {
		Guild guild = null;
		if (channel instanceof TextChannel) {
			guild = ((TextChannel) channel).getGuild();
		}
		String output = input.replace("\\%", "\u0013");
		output = output.replace("%user%", user.getName()).replace("%user-mention%", user.getAsMention())
				.replace("%user-id%", user.getId())
				.replace("%nick%",
						guild != null && guild.isMember(user) ? guild.getMember(user).getEffectiveName()
								: user.getName())
				.replace("%discrim%", user.getDiscriminator())
				.replace("%guild%", (guild == null) ? "Private" : guild.getName())
				.replace("%guild-id%", (guild == null) ? "0" : guild.getId())
				.replace("%guild-users%", (guild == null) ? "0" : guild.getMembers().size() + "")
				.replace("%channel%", (guild == null) ? "Private" : channel.getName())
				.replace("%channel-id%", (guild == null) ? "0" : channel.getId())
				.replace("%channel-mention%", (guild == null) ? "Private" : ((TextChannel) channel).getAsMention());
		if (guild == null) {
			return output.replace("\u0013", "%");
		}
		if (userArgs != null && output.contains("%arg")) {
			String allArgs = Joiner.on(" ").join(userArgs);
			output = output.replace("%args%", allArgs);
			for (int i = 0; i < userArgs.length; i++) {
				output = output.replace("%arg" + (i + 1) + "%", userArgs[i]);
			}
		}
		int ind;
		Random rng = new Random();
		while ((ind = output.indexOf("%rand-user%")) != -1) {
			output = output.substring(0, ind)
					+ guild.getMembers().get(rng.nextInt(guild.getMembers().size())).getEffectiveName()
					+ output.substring(ind + 11);
		}

		if (output.contains("%rand-user-online%")) {
			List<Member> onlines = new ArrayList<>();
			guild.getMembers().stream().filter((u) -> (u.getOnlineStatus().equals(OnlineStatus.ONLINE)))
					.forEach(onlines::add);
			while ((ind = output.indexOf("%rand-user-online%")) != -1)
				output = output.substring(0, ind) + onlines.get(rng.nextInt(onlines.size())).getEffectiveName()
						+ output.substring(ind + 18);
		}
		return output.replace("\u0013", "%");
	}

	/**
	 * Attempts to find a user in a channel, first look for account name then for
	 * nickname
	 *
	 * @param channel    the channel to look in
	 * @param searchText the name to look for
	 * @return IUser | null
	 */
	public static Member findUserIn(TextChannel channel, String searchText) {
		List<Member> users = channel.getGuild().getMembers();
		List<Member> potential = new ArrayList<>();
		int smallestDiffIndex = 0, smallestDiff = -1;
		for (Member u : users) {
			String nick = u.getEffectiveName();
			if (nick.equalsIgnoreCase(searchText)) {
				return u;
			}
			if (nick.toLowerCase().contains(searchText)) {
				potential.add(u);
				int d = Math.abs(nick.length() - searchText.length());
				if (d < smallestDiff || smallestDiff == -1) {
					smallestDiff = d;
					smallestDiffIndex = potential.size() - 1;
				}
			}
		}
		if (!potential.isEmpty()) {
			return potential.get(smallestDiffIndex);
		}
		return null;
	}

	/**
	 * Attempts to find a user from mention, if that fails see
	 * {@link DisUtil#findUserIn(TextChannel, String)}
	 *
	 * @param channel    the channel context
	 * @param searchText the search argument
	 * @return user || null
	 */
	public static User findUser(TextChannel channel, String searchText) {
		if (DisUtil.isUserMention(searchText)) {
			return channel.getJDA().getUserById(DisUtil.mentionToId(searchText));
		}

		try {
			return channel.getJDA().getUserById(searchText);
		} catch (NumberFormatException ignored) {

		}

		Member member = DisUtil.findUserIn(channel, searchText);
		if (member != null) {
			return member.getUser();
		}

		return null;
	}

	/**
	 * Is channel mention boolean.
	 *
	 * @param input string to check for mentions
	 * @return found a mention
	 */
	public static boolean isChannelMention(String input) {
		return channelPattern.matcher(input).matches();
	}

	/**
	 * Converts any mention to an id
	 *
	 * @param mention the mention to filter
	 * @return a stripped down version of the mention
	 */
	public static String mentionToId(String mention) {
		String id = "";
		Matcher matcher = anyMention.matcher(mention);
		if (matcher.find()) {
			id = matcher.group(1);
		}
		return id;
	}

	/**
	 * Retrieve all mentions from an input
	 *
	 * @param input text to check for mentions
	 * @return list of all found mentions
	 */
	public static List<String> getAllMentions(String input) {
		List<String> list = new ArrayList<>();
		Matcher matcher = anyMention.matcher(input);
		while (matcher.find()) {
			list.add(matcher.group(1));
		}
		return list;
	}

	/**
	 * Filters the command prefix from the string
	 *
	 * @param command the text to filter form
	 * @param channel the channel where the text came from
	 * @return text with the prefix filtered
	 */
	public static String filterPrefix(String command, MessageChannel channel) {
		String prefix = getCommandPrefix(channel);
		if (command.startsWith(prefix)) {
			return command.substring(prefix.length());
		}
		return command;
	}

	/**
	 * gets the command prefix for specified channel
	 *
	 * @param channel channel to check the prefix for
	 * @return the command prefix
	 */
	public static String getCommandPrefix(MessageChannel channel) {
		if (channel instanceof TextChannel) {
			return getCommandPrefix(((TextChannel) channel).getGuild());
		}
		return DefaultGuildSettings.getDefault(GSetting.COMMAND_PREFIX);
	}

	/**
	 * Gets command prefix.
	 *
	 * @param guild the guild
	 * @return the command prefix
	 */
	public static String getCommandPrefix(Guild guild) {
		return getCommandPrefix(guild.getIdLong());
	}

	/**
	 * Gets command prefix.
	 *
	 * @param guildId the guild id
	 * @return the command prefix
	 */
	public static String getCommandPrefix(long guildId) {
		return GuildSettings.get(guildId).getOrDefault(GSetting.COMMAND_PREFIX);
	}

	/**
	 * Gets a list of users with a certain role within a guild
	 *
	 * @param guild guild to search in
	 * @param role  the role to search for
	 * @return list of user with specified role
	 */
	public static List<Member> getUsersByRole(Guild guild, Role role) {
		return guild.getMembersWithRoles(role);
	}

	/**
	 * Checks if a user has a guild within a guild
	 *
	 * @param user       the user to check
	 * @param guild      the guild to check in
	 * @param permission the permission to check for
	 * @return permission found
	 */
	public static boolean hasPermission(User user, Guild guild, Permission permission) {
		return PermissionUtil.checkPermission(guild.getMember(user), permission);
	}

	/**
	 * attempts to find a role within a guild
	 *
	 * @param guild    the guild to search in
	 * @param roleName the role name to search for
	 * @return role or null
	 */
	public static Role findRole(Guild guild, String roleName) {
		List<Role> roles = guild.getRoles();
		Role containsRole = null;
		for (Role role : roles) {
			if (role.getName().equalsIgnoreCase(roleName)) {
				return role;
			}
			if (containsRole == null && role.getName().toLowerCase().contains(roleName.toLowerCase())) {
				containsRole = role;
			}
		}
		return containsRole;
	}

	/**
	 * Has role role.
	 *
	 * @param guild    the guild
	 * @param roleName the role name
	 * @return the role
	 */
	public static Role hasRole(Guild guild, String roleName) {
		List<Role> roles = guild.getRoles();
		Role containsRole = null;
		for (Role role : roles) {
			if (role.getName().equalsIgnoreCase(roleName)) {
				return role;
			}
			if (containsRole == null && role.getName().contains(roleName)) {
				containsRole = role;
			}
		}
		return containsRole;
	}

	/**
	 * Gets user avatar.
	 *
	 * @param user the user
	 * @return the user avatar
	 * @throws IOException the io exception
	 */
	public static BufferedImage getUserAvatar(User user) throws IOException {

		URLConnection connection = new URL(
				user.getAvatarUrl() != null ? user.getAvatarUrl() : user.getDefaultAvatarUrl()).openConnection();
		connection.setRequestProperty("User-Agent", "bot techtor-bot");
		BufferedImage profileImg;
		try {
			profileImg = ImageIO.read(connection.getInputStream());
		} catch (Exception ignored) {
			profileImg = ImageIO
					.read(Objects.requireNonNull(Launcher.class.getClassLoader().getResource("default_profile.jpg")));
		}
		return profileImg;
	}

	/**
	 * Is emote boolean.
	 *
	 * @param bot   the bot
	 * @param emote the emote
	 * @return the boolean
	 */
	public static boolean isEmote(DiscordBot bot, String emote) {
		if (EmojiUtils.isEmoji(emote) || Misc.isGuildEmote(emote)) {
			return true;
		}
		if (emote.matches("\\d+")) {
			return bot.getJda().getEmoteById(emote) != null;
		}
		return false;
	}

	/**
	 * Emote to display string.
	 *
	 * @param bot   the bot
	 * @param emote the emote
	 * @return the string
	 * @throws NullPointerException the null pointer exception
	 */
	public static String emoteToDisplay(DiscordBot bot, String emote) throws NullPointerException {
		if (EmojiUtils.isEmoji(emote)
				|| emote.matches("([\\u20a0-\\u32ff\\ud83c\\udc00-\\ud83d\\udeff\\udbb9\\udce5-\\udbb9\\udcee])")) {
			return emote;
		} else if (Misc.isGuildEmote(emote)) {
			return bot.getJda().getEmoteById(Misc.getGuildEmoteId(emote)).getAsMention();
		} else if (bot.getJda().getEmoteById(emote) != null) {
			return bot.getJda().getEmoteById(emote).getAsMention();
		}
		return "";
	}

	/**
	 * Send a message in the home guild's error log channel about the guild event
	 *
	 * @param discordBot the discord bot
	 * @param guild      the guild
	 * @param message    the message
	 */
	public static void notifyOfEvent(DiscordBot discordBot, Guild guild, String message) {
		TextChannel homeGuildErrorChannel = discordBot.getJda().getTextChannelById(BotConfig.BOT_ERROR_CHANNEL_ID);
		MessageBuilder messageBuild = new MessageBuilder("[Guild Event] ");
		messageBuild.append(getServerInfo(guild));
		messageBuild.append(": **");
		messageBuild.append(message);
		messageBuild.append("**");
		discordBot.out.sendAsyncMessage(homeGuildErrorChannel, messageBuild);
	}

	/**
	 * Gets guild list.
	 *
	 * @param discordBot the discord bot
	 * @return the guild list
	 */
	public static MessageBuilder getGuildList(DiscordBot discordBot) {
		List<Guild> guilds = discordBot.getJda().getGuilds();
		MessageBuilder messageBuild = new MessageBuilder();
		for (Guild guild : guilds) {
			messageBuild.append(getServerInfo(guild));
			messageBuild.append("\n");
		}
		return messageBuild;
	}

	/**
	 * Returns a string with the server name, ID, owner, and number of members
	 *
	 * @param guild the guild
	 * @return server info
	 */
	public static String getServerInfo(Guild guild) {
		Member owner = guild.getOwner();

		StringBuilder strBuild = new StringBuilder(guild.getName());
		strBuild.append(" (");
		strBuild.append(guild.getId());
		strBuild.append("), owned by ");
		strBuild.append(owner.getUser().getName());
		strBuild.append("#");
		strBuild.append(owner.getUser().getDiscriminator());
		strBuild.append(" (");
		strBuild.append(owner.getAsMention());
		strBuild.append("), with ");
		strBuild.append(guild.getMembers().size());
		strBuild.append(" members.");
		return strBuild.toString();
	}

	/**
	 * Will forward the message to the bot's forwarding channel
	 *
	 * @param bot     the bot
	 * @param message the message
	 */
	public static void forwardMessage(DiscordBot bot, Message message) {
		TextChannel forwardChannel = bot.getJda().getTextChannelById(BotConfig.BOT_FORWARD_CHANNEL_ID);
		if (forwardChannel != null) {
			MessageBuilder builder = new MessageBuilder("[Message Forward] ");
			if (message.getChannelType() == ChannelType.TEXT
					&& forwardChannel.getGuild().getIdLong() != message.getGuild().getIdLong()) {
				// Handle message from a guild
				TextChannel fromChannel = (TextChannel) message.getChannel();
				User fromUser = message.getAuthor();
				builder.append("*From ");
				builder.append(fromUser.getName());
				builder.append("#");
				builder.append(fromUser.getDiscriminator());
				builder.append(" (");
				builder.append(fromUser.getAsMention());
				builder.append(") on **");
				builder.append(fromChannel.getGuild().getName());
				builder.append("**, in channel #");
				builder.append(fromChannel.getName());
				builder.append(" (");
				builder.append(fromChannel.getId());
				builder.append(")*:\n");
			} else if (message.getChannelType() == ChannelType.PRIVATE) {
				// Handle a private message
				PrivateChannel fromChannel = (PrivateChannel) message.getChannel();
				User fromUser = message.getAuthor();
				builder.append("*From ");
				builder.append(fromUser.getName());
				builder.append("#");
				builder.append(fromUser.getDiscriminator());
				builder.append(" (");
				builder.append(fromUser.getAsMention());
				builder.append("). Reply to ");
				builder.append(fromChannel.getId());
				builder.append(":*\n");
			} else {
				return;
			}
			// Add the message itself
			builder.append(message.getContentRaw());
			List<Attachment> attachments = message.getAttachments();
			if (attachments.size() > 0) {
				builder.append("\n*Attachments:*\n");
				for (Attachment attachment : attachments) {
					builder.append(attachment.getUrl());
					builder.append("\n");
				}
			}
			// Forward the message
			bot.sendLongMessageToChannel(forwardChannel, builder);
			bot.lastForward = message.getChannel();
		}
	}

	/**
	 * Checks if the bot has permission to delete a message in specified channel,
	 * then deletes it if possible
	 *
	 * @param messageToDelete the message to delete
	 */
	public static void tryDeleteMessage(Message messageToDelete) {
		if (messageToDelete.getChannelType() == ChannelType.TEXT) {
			GuildChannel channel = (GuildChannel) messageToDelete.getChannel();
			if (PermissionUtil.checkPermission(channel, channel.getGuild().getSelfMember(),
					Permission.MESSAGE_MANAGE)) {
				messageToDelete.delete().queue();
			}
		}
	}
}
