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

package takeshi.command.administrative;

import java.util.List;

import emoji4j.EmojiUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import takeshi.command.meta.AbstractCommand;
import takeshi.command.meta.CommandVisibility;
import takeshi.core.Logger;
import takeshi.db.controllers.CReactionRole;
import takeshi.db.model.OReactionRoleKey;
import takeshi.db.model.OReactionRoleMessage;
import takeshi.main.DiscordBot;
import takeshi.templates.Templates;
import takeshi.util.DisUtil;
import takeshi.util.Emojibet;
import takeshi.util.Misc;

/**
 * give and take away roles with reactions rather than typing
 */
public class ReactionRoleCommand extends AbstractCommand {
	/**
	 * Instantiates a new Reaction role command.
	 */
	public ReactionRoleCommand() {
		super();
	}

	@Override
	public String getDescription() {
		return "Adds and removes roles from users based on reactions from a message\n\n"
				+ "You save messages/reactions to keys to make maintaining them a little easier.";
	}

	@Override
	public String getCommand() {
		return "reactionrole";
	}

	@Override
	public String[] getUsage() {
		return new String[] { "rr //overview of all the configured keys", "rr add <key> <emote> <role> //adds a reaction with role to the message",
				"rr remove <key> <emote>     //removes emote reaction from key", "rr delete <key>             //deletes the set",
				"rr message <key> <message>  //updates the message", "rr display <key> [channel]  //displays the message in this channel",
				"                            //or in the channel you specified" };
	}

	@Override
	public CommandVisibility getVisibility() {
		return CommandVisibility.PUBLIC;
	}

	@Override
	public String[] getAliases() {
		return new String[] { "rr" };
	}

	@Override
	public String stringExecute(DiscordBot bot, String[] args, MessageChannel channel, User author, Message inputMessage) {
		TextChannel t = (TextChannel) channel;
		Guild guild = t.getGuild();
		if (!PermissionUtil.checkPermission(guild.getSelfMember(), Permission.MANAGE_ROLES)) {
			return Templates.permission_missing.formatGuild(channel, "manage_roles");
		}
		if (args.length == 0) {
			List<OReactionRoleKey> list = CReactionRole.getKeysForGuild(guild.getIdLong());
			String result = "";
			if (list.isEmpty()) {
				return "No keys are configured";
			}
			for (OReactionRoleKey key : list) {
				result += key.messageKey + "\n";
			}
			return "All configured keys: \n" + result;
		}
		switch (args[0].toLowerCase()) {
		case "add":// eg. !rr add <key> <emote> <role>
			if (args.length >= 4) {
//				System.out.println(args[2]);
				List<Role> mentionedRoles = inputMessage.getMentionedRoles();
				Role role;
				// Try to find the role for the rr command
				if (mentionedRoles.isEmpty()) {
					String roleName = "";
					for (int i = 3; i < args.length; i++) {
						roleName += " " + args[i];
					}
					role = DisUtil.findRole(guild, roleName);
				} else {
					role = mentionedRoles.get(0);
				}
				if (role == null) {
					return "Role not found. Make sure the role name contains the words you're using, or that you're @mentioning the role.";
				}
				// Try to get the bot's role
//				Role botRole = DisUtil.findRole(guild, bot.getUserName());
//				// If it can't find a role matching the name of the bot, then manually search
//				// the server for one
//				if (botRole == null) {
//					List<Role> serverRoles = guild.getRoles();
//					for (Role serverRole : serverRoles) {
//						List<Member> members = guild.getMembersWithRoles(serverRole);
//						if (members.size() != 1) {
//							continue;
//						}
//						if (members.get(0).getUser().getIdLong() == bot.getJda().getSelfUser().getIdLong()) {
//							role = serverRole;
//							break;
//						}
//					}
//				}
				// Make sure the bot's role can interact with (manage) the target role
//				if (botRole != null && !botRole.canInteract(role)) {
				if (!PermissionUtil.canInteract(guild.getSelfMember(), role)) {
					return "I may not manage this role. Make sure my role is higher-listed than it.";
				}
				OReactionRoleKey key = CReactionRole.findOrCreate(guild.getIdLong(), args[1]);
				String emoteId = "";
				boolean isNormalEmote = false;
				if (!DisUtil.isEmote(bot, args[2])) {
					if (args[2].matches("([\\u20a0-\\u32ff\\ud83c\\udc00-\\ud83d\\udeff\\udbb9\\udce5-\\udbb9\\udcee])")) {
						// System.out.println(EmojiUtils.hexHtmlify(args[2]) + " " +
						// EmojiUtils.shortCodify(args[2]));

//						emoteId = EmojiUtils.htmlify(args[2]);
//						isNormalEmote = true;

						return "JDA doesn't like this emote.";
					} else {
						return "No emote found";
					}
				}
				if (!isNormalEmote) {
					isNormalEmote = EmojiUtils.isEmoji(args[2]);
					if (isNormalEmote) {
						emoteId = EmojiUtils.shortCodify(args[2]);
					} else if (emoteId.length() == 0) {
						emoteId = Misc.getGuildEmoteId(args[2]);
						if (bot.getJda().getEmoteById(emoteId) == null) {
							return "Can't find guild-emote";
						}
					}
				}
				try {
					String ret = String.format("Adding to key `%s` the reaction %s with role `%s`", args[1], DisUtil.emoteToDisplay(bot, args[2]),
							role.getName());
					CReactionRole.addReaction(key.id, emoteId, isNormalEmote, role.getIdLong());
					return ret;
				} catch (NullPointerException ex) {
					return "Could not find guild (server) emote! Make sure I am in the server that the emote came from.";
				}
			}
			return "Invalid usage! See help for more info";
		case "remove":// eg. !rr remove key <emote>
			if (args.length >= 3) {
//				System.out.println(args[2]);
				OReactionRoleKey key = CReactionRole.findOrCreate(guild.getIdLong(), args[1]);
				String emoteId = "";
//				int emoteType = -1;
				if (!DisUtil.isEmote(bot, args[2])) {
					if (Emojibet.isEmoji(args[2])) {
//						emoteId = EmojiUtils.htmlify(args[2]);
//						emoteType = 2;
						return "JDA doesn't like this emote.";
					} else {
						return "No emote found";
					}
				} else if (EmojiUtils.isEmoji(args[2])) {
//					emoteType = 0;
					emoteId = EmojiUtils.shortCodify(args[2]);
				} else {
//					emoteType = 1;
					emoteId = Misc.getGuildEmoteId(args[2]);
					if (bot.getJda().getEmoteById(emoteId) == null) {
						return "Can't find guild-emote";
					}
				}
				try {
					String ret = String.format("Removing the reaction %s from key `%s`", DisUtil.emoteToDisplay(bot, args[2]), args[1]);
					CReactionRole.removeReaction(key.id, emoteId);
					return ret;
				} catch (NullPointerException ex) {
					return "Could not find guild (server) emote! Make sure I am in the server that the emote came from.";
				}
			}
			return "Invalid usage! See help for more info";
		case "delete":
			if (args.length >= 2) {
				OReactionRoleKey key = CReactionRole.findBy(guild.getIdLong(), args[1]);
				if (key.messageKey.length() > 0) {
					if (key.channelId > 0 && key.messageId > 0) {
						TextChannel tchan = ((TextChannel) channel).getGuild().getTextChannelById(key.channelId);
						if (tchan != null && tchan.canTalk()) {
							tchan.deleteMessageById(key.messageId).queue();
						}
					}
					CReactionRole.delete(key);
					return String.format("Deleting reaction role key `%s`", key.messageKey);
				} else {
					return String.format("There's no key matching `%s`!", args[1]);
				}
			}
			return "Invalid usage! See help for more info";
		case "message":
		case "msg":
		case "text":// eg. !rr message key <newtext>
			if (args.length >= 2) {
				OReactionRoleKey key = CReactionRole.findBy(guild.getIdLong(), args[1]);
				if (key.id == 0) {
					return String.format("Key `%s` doesn't exist", args[1]);
				}
				key.message = Misc.joinStrings(args, 2);
				if (key.message.length() > 1500) {
					key.message = key.message.substring(0, 1500);
				}
				CReactionRole.update(key);
				updateText(t, key, author);
				return String.format("Text for %s updated!", args[1]);
			}
			return "Invalid usage! See help for more info";
		case "display":// spams the message here
			if (args.length < 2) {
				return "Invalid usage! See help for more info";
			}
			OReactionRoleKey key = CReactionRole.findBy(guild.getIdLong(), args[1]);
			if (key.id == 0) {
				return String.format("Key `%s` not found!", args[1]);
			}
			if (args.length == 3) {
				if (DisUtil.isChannelMention(args[2])) {
					t = ((TextChannel) channel).getGuild().getTextChannelById(DisUtil.extractId(args[2]));
					if (t == null) {
						return Templates.config.cant_talk_in_channel.formatGuild(channel, args[2]);
					}
				}
			}
			displayMessage(bot, t, key, author);
			return "";
		default:
			return "Unknown action! See help for what I can do.";
		}

	}

	private void updateText(TextChannel channel, OReactionRoleKey key, User author) {
		if (key.messageId > 0 && key.channelId > 0) {
			TextChannel tchan = channel.getGuild().getTextChannelById(key.channelId);
			if (tchan != null && tchan.canTalk()) {
				tchan.editMessageById(String.valueOf(key.messageId), buildMessage(channel, key, CReactionRole.getReactionsForKey(key.id), author)).queue();
			}
		}
	}

	// private String buildMessage(TextChannel channel, OReactionRoleKey key,
	// List<OReactionRoleMessage> reactions) {
	// StringBuilder msg = new StringBuilder(key.message);
	// msg.append("\n Use the reactions below to give/remove the role\n");
	// for (OReactionRoleMessage reaction : reactions) {
	// msg.append(String.format("%s %s %s\n",
	// reaction.isNormalEmote ? reaction.emoji :
	// channel.getJDA().getEmoteById(reaction.emoji),
	// Emojibet.THUMBS_RIGHT, channel.getGuild().getRoleById(reaction.roleId)));
	// }
	// return msg.toString();
	// }

	private MessageEmbed buildMessage(TextChannel channel, OReactionRoleKey key, List<OReactionRoleMessage> reactions, User author) {
		EmbedBuilder emReactRole = new EmbedBuilder();
		emReactRole.setColor(Misc.randomCol());
		emReactRole.setAuthor(author.getName(), null, author.getAvatarUrl());
		emReactRole.setTitle("Role assignment: " + key.message);
//		emReactRole.setDescription("\n Use the reactions below to give/remove the role\n");
		String roles = "\n Use the reactions below to give/remove the role\n";
		for (OReactionRoleMessage reaction : reactions) {
			Role role = channel.getGuild().getRoleById(reaction.roleId);
			if (role != null) {
				try {
					roles += "\n" + (reaction.isNormalEmote ? EmojiUtils.emojify(reaction.emoji) : channel.getJDA().getEmoteById(reaction.emoji).getAsMention())
							+ " -> " + role.getName();
//					emReactRole.addField(role.getName(),
//							reaction.isNormalEmote ? EmojiUtils.emojify(reaction.emoji) : channel.getJDA().getEmoteById(reaction.emoji).getAsMention(), true);
				} catch (NullPointerException ex) {
					Logger.warn(ex.getMessage(), ex.getStackTrace());
				}
			}
		}
		emReactRole.setDescription(roles);
		return emReactRole.build();
	}

	private void displayMessage(DiscordBot bot, TextChannel channel, OReactionRoleKey key, User author) {
		if (key.channelId > 0 && key.messageId > 0) {
			TextChannel tchan = channel.getGuild().getTextChannelById(key.channelId);
			if (tchan != null && tchan.canTalk()) {
				tchan.deleteMessageById(key.messageId).queue();
			}
		}
		List<OReactionRoleMessage> reactions = CReactionRole.getReactionsForKey(key.id);
		channel.sendMessage(buildMessage(channel, key, reactions, author)).queue(message -> {
			key.messageId = message.getIdLong();
			key.channelId = channel.getIdLong();
			CReactionRole.update(key);
			bot.roleReactionHandler.initGuild(message.getGuild().getIdLong(), true);
			for (OReactionRoleMessage reaction : reactions) {
				if (reaction.isNormalEmote) {
//					channel.sendMessage(reaction.emoji + " : " + EmojiUtils.emojify(reaction.emoji)).queue();
//					message.addReaction(reaction.emoji.charAt(0) != '&' ? EmojiUtils.emojify(reaction.emoji) : reaction.emoji).queue();
					message.addReaction(EmojiUtils.emojify(reaction.emoji)).queue();
				} else {
					Emote e = message.getJDA().getEmoteById(reaction.emoji);
					if (e != null) {
						message.addReaction(e).queue();
					} else {
						Logger.warn("Can't find emoji " + reaction.emoji + "!");
					}
				}
			}
		});
	}
}