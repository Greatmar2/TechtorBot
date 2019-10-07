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

package takeshi.command.administrative.modactions;

import com.google.api.client.repackaged.com.google.common.base.Joiner;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import takeshi.command.meta.AbstractCommand;
import takeshi.command.meta.CommandVisibility;
import takeshi.db.controllers.CModerationCase;
import takeshi.db.model.OModerationCase;
import takeshi.main.DiscordBot;
import takeshi.templates.Templates;
import takeshi.util.DisUtil;

/**
 * The type Abstract mod action command.
 */
abstract public class AbstractModActionCommand extends AbstractCommand {
	/**
	 * Instantiates a new Abstract mod action command.
	 */
	public AbstractModActionCommand() {
		super();
	}

	@Override
	public String[] getUsage() {
		return new String[] { String.format("%s <user>     //%s user from guild", getCommand(), getPunishType().getDescription()), };
	}

	/**
	 * Gets punish type.
	 *
	 * @return the punish type
	 */
	protected abstract OModerationCase.PunishType getPunishType();

	/**
	 * Gets required permission.
	 *
	 * @return the required permission
	 */
	protected abstract Permission getRequiredPermission();

	@Override
	public CommandVisibility getVisibility() {
		return CommandVisibility.PUBLIC;
	}

	/**
	 * Punish boolean.
	 *
	 * @param bot    the bot
	 * @param guild  the guild
	 * @param member the member
	 * @return the boolean
	 */
	protected abstract boolean punish(DiscordBot bot, Guild guild, Member member);

	@Override
	public String stringExecute(DiscordBot bot, String[] args, MessageChannel channel, User author, Message inputMessage) {
		TextChannel chan = (TextChannel) channel;
		Guild guild = chan.getGuild();
		if (getRequiredPermission() != null) {
			if (!PermissionUtil.checkPermission(guild.getMember(author), getRequiredPermission())) {
				return Templates.no_permission.formatGuild(channel);
			}
			if (!PermissionUtil.checkPermission(guild.getSelfMember(), getRequiredPermission())) {
				return Templates.permission_missing.formatGuild(channel, getRequiredPermission().name());
			}
		}
		if (args.length == 0) {
			return Templates.command.modaction_empty.formatGuild(channel, getPunishType().getKeyword().toLowerCase());
		}
		User targetUser = DisUtil.findUser(chan, Joiner.on(" ").join(args));
		if (targetUser == null) {
			return Templates.config.cant_find_user.formatGuild(channel, Joiner.on(" ").join(args));
		}
		if (targetUser.getId().equals(guild.getSelfMember().getUser().getId())) {
			return Templates.command.modaction_not_self.formatGuild(channel, getPunishType().getKeyword().toLowerCase());
		}
		if (!PermissionUtil.canInteract(guild.getSelfMember(), guild.getMember(targetUser))) {
			return "I can't act on that user. Make sure I am higher-ranked than all their roles.";
//			return Templates.permission_missing.formatGuild(channel, targetUser);
		}
		if (!punish(bot, guild, guild.getMember(targetUser))) {
			return Templates.command.modaction_failed.formatGuild(channel, getPunishType().getKeyword().toLowerCase(), targetUser);
		}
		int caseId = CModerationCase.insert(guild, targetUser, author, getPunishType(), null);
		TextChannel modlogChannel = bot.getModlogChannel(guild.getIdLong());
		if (modlogChannel != null) {
			bot.queue.add(modlogChannel.sendMessage(CModerationCase.buildCase(guild, caseId)), message -> {
				OModerationCase modCase = CModerationCase.findById(caseId);
				modCase.messageId = message.getId();
				CModerationCase.update(modCase);
			});
		}
		return Templates.command.modaction_success.formatGuild(channel, targetUser, getPunishType().getVerb().toLowerCase());
	}
}