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

import java.util.Arrays;
import java.util.List;

import com.google.api.client.repackaged.com.google.common.base.Joiner;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import takeshi.command.meta.AbstractCommand;
import takeshi.command.meta.CommandVisibility;
import takeshi.core.Logger;
import takeshi.db.controllers.CGuild;
import takeshi.db.controllers.CModerationCase;
import takeshi.db.controllers.CUser;
import takeshi.db.model.OModerationCase;
import takeshi.guildsettings.GSetting;
import takeshi.handler.GuildSettings;
import takeshi.main.DiscordBot;
import takeshi.permission.SimpleRank;
import takeshi.templates.Templates;
import takeshi.util.DisUtil;
import takeshi.util.Misc;
import takeshi.util.QuickEmbedBuilder;

public class CaseCommand extends AbstractCommand {
	public CaseCommand() {
		super();
	}

	@Override
	public String getDescription() {
		return "Moderate the mod-cases";
	}

	@Override
	public String getCommand() {
		return "case";
	}

	@Override
	public String[] getUsage() {
		return new String[] { "case reason <id> <message>  //sets/modifies the reason of a case",
				"case reason last <message>  //sets/modified the reason of the last added case by you",
				"case user <name/user-id/mention> //shows a list of cases for this user or cases created by this moderator",
				"case page <page number> <name/user-id/mention> //cases will be split into pages of 10", "case show <case-id>       //shows case",
				"case dismiss <case-id>    //Permanently dismisses the specified case" };
	}

	@Override
	public CommandVisibility getVisibility() {
		return CommandVisibility.PUBLIC;
	}

	@Override
	public String[] getAliases() {
		return new String[] {};
	}

	@Override
	public String simpleExecute(DiscordBot bot, String[] args, MessageChannel mChannel, User author, Message inputMessage) {
		TextChannel channel = (TextChannel) mChannel;
		SimpleRank rank = bot.security.getSimpleRank(author, channel);
		Guild guild = channel.getGuild();
		if (!rank.isAtLeast(SimpleRank.GUILD_ADMIN)) {
			return Templates.no_permission.formatGuild(channel);
		}
		if (args.length > 0) {
			switch (args[0].toLowerCase()) {
			case "reason":
				if (args.length < 3) {
					return Templates.invalid_use.formatGuild(channel);
				}
				return editReason(bot, guild, guild.getMember(author), channel, args[1], Misc.joinStrings(args, 2));
			case "page": // Show a specified page of the mod actions involving a user
				try {
					int page = Integer.parseInt(args[1]);
					if (page > 0) {
						return displayCases(Joiner.on(" ").join(Arrays.copyOfRange(args, 2, args.length)), guild, channel, page);
					}
				} catch (NumberFormatException e) {
					Logger.warn(e.getMessage(), e.getStackTrace());
					return Templates.invalid_use.formatGuild(channel);
				}
			case "user": // Show the latest 10 mod actions involving a user
				return displayCases(Joiner.on(" ").join(Arrays.copyOfRange(args, 1, args.length)), guild, channel, 1);
			case "show": // Show detail about a specific mod action
				try {
					OModerationCase c = CModerationCase.findById(Integer.parseInt(args[1]));
					if (c.guildId != CGuild.getCachedId(guild.getIdLong())) {
						return Templates.command.case_not_found.formatGuild(guild.getIdLong(), c.id);
					}

					EmbedBuilder caseMessage = new EmbedBuilder();

					caseMessage.setTitle("Case " + c.id);
					caseMessage.setColor(c.punishment.getColor());

					// Build description of <moderator> <verb> <user>
					String desc = "";
					User moderator = bot.getJda().getUserById(CUser.findById(c.moderatorId).discord_id);
					if (moderator != null) {
						caseMessage.setAuthor(moderator.getName(), null, moderator.getAvatarUrl());
						String curName = moderator.getName() + "#" + moderator.getDiscriminator();
						desc += " " + curName;
//						if (!curName.equalsIgnoreCase(c.moderatorName.trim())) {
//							desc += " (previously " + c.moderatorName + ")";
//						}
					} else {
						desc += " " + c.moderatorName;
					}
					desc += " " + c.punishment.getVerb();
					User user = bot.getJda().getUserById(CUser.findById(c.userId).discord_id);
					if (user != null) {
						caseMessage.setThumbnail(user.getAvatarUrl());
						String curName = user.getName() + "#" + user.getDiscriminator();
						desc += " " + curName;
//						if (!curName.equalsIgnoreCase(c.userName.trim())) {
//							desc += " (previously " + c.userName + ")";
//						}
					} else {
						desc += " " + c.userName;
					}
					caseMessage.setDescription(desc + ".");

					caseMessage.addField("Reason", c.reason, true);
					caseMessage.addField("At", c.createdAt.toString(), true);
					caseMessage.addField("Dismissed", (c.active == 0) + "", true);

					channel.sendMessage(caseMessage.build()).queue();

					return "";
				} catch (NumberFormatException e) {
					Logger.warn(e.getMessage(), e.getStackTrace());
				}
			case "dismiss":
				try {
					OModerationCase c = CModerationCase.findById(Integer.parseInt(args[1]));
					String caseTitle = String.format("case %s, where %s %s %s", c.id, c.moderatorName, c.punishment.getVerb(), c.userName);
					if (args.length >= 3 && args[2].equalsIgnoreCase("confirm")) {
						c.active = 0;
						CModerationCase.update(c);
						return "Dismissed " + caseTitle + ".";
					}
					String prefix = GuildSettings.get(guild).getOrDefault(GSetting.COMMAND_PREFIX);
					return String.format(
							"Are you sure you want to dismiss %s?\nType `%scase dismiss %s confirm` to dismiss this case. This action cannot be undone.\nIt will no longer appear in `%scase user`, but can be found by its ID with `%scase show`.",
							caseTitle, prefix, args[1], prefix, prefix);
				} catch (NumberFormatException e) {
					Logger.warn(e.getMessage(), e.getStackTrace());
				}
			}
		}
		return Templates.invalid_use.formatGuild(channel);

	}

	private String editReason(DiscordBot bot, Guild guild, Member moderator, MessageChannel feedbackChannel, String caseId, String reason) {
		OModerationCase oCase;
		if (caseId.equalsIgnoreCase("last")) {
			oCase = CModerationCase.findLastFor(CGuild.getCachedId(guild.getIdLong()), CUser.getCachedId(moderator.getUser().getIdLong()));
		} else {
			oCase = CModerationCase.findById(Misc.parseInt(caseId, -1));
		}
		if (oCase.id == 0 || oCase.guildId != CGuild.getCachedId(guild.getIdLong())) {
			return Templates.command.case_not_found.formatGuild(guild.getIdLong(), oCase.id);
		}
		oCase.reason = reason;
		CModerationCase.update(oCase);
		try {
			TextChannel channel = guild.getTextChannelById(GuildSettings.get(guild).getOrDefault(GSetting.BOT_CHANNEL));
			if (channel == null) {
				return Templates.config.modlog_not_found.formatGuild(guild.getIdLong());
			}
			bot.queue.add(channel.getMessageById(oCase.messageId), msg -> {
				if (msg != null) {
					bot.queue.add(msg.editMessage(new MessageBuilder().setEmbed(CModerationCase.buildCase(guild, oCase)).build()));
				} else {
					bot.queue.add(feedbackChannel.sendMessage(Templates.command.case_reason_modified.formatGuild(channel)));
				}
			});
		} catch (NumberFormatException e) {
			Logger.warn(e.getMessage(), e.getStackTrace());
		}

		return Templates.command.case_reason_modified.formatGuild(guild.getIdLong());
	}

	private String displayCases(String userSearch, Guild guild, TextChannel channel, int page) {
		User targetUser = DisUtil.findUser(channel, userSearch);
		if (targetUser == null) {
			return Templates.config.cant_find_user.formatGuild(guild.getIdLong(), userSearch);
		}
		List<OModerationCase> cases = CModerationCase.findUserCases(guild, targetUser);
		EmbedBuilder caseMessage = new EmbedBuilder();
		caseMessage.setAuthor("Cases involving " + targetUser.getName(), null, targetUser.getAvatarUrl());
		caseMessage.setColor(QuickEmbedBuilder.WARN_COL);
		int pages = -Math.floorDiv(cases.size(), -10);
		if (pages == 0) {
			caseMessage.setDescription("No cases were found involving " + targetUser.getName() + ".");
		} else {
			page = Math.min(page, pages);
			int pageStart = ((page - 1) * 10) + 1;
			int pageEnd = Math.min(cases.size(), page * 10);
			for (int i = pageStart; i <= pageEnd; i++) {
				OModerationCase c = cases.get(i - 1);
				String fieldName = String.format("%s: %s %s %s", c.id, c.moderatorName, c.punishment.getVerb(), c.userName);
				String fieldValue = String.format("Reason: %s\nAt: %s", c.reason, c.createdAt);

				caseMessage.addField(fieldName, fieldValue, false);
			}
			if (pages > 1) {
				caseMessage.setFooter(String.format("Page %s of %s | Use %scase page <page number> <user>", page, pages,
						GuildSettings.get(guild).getOrDefault(GSetting.COMMAND_PREFIX)), null);
			}
		}

		channel.sendMessage(caseMessage.build()).queue();

		return "";
	}
}