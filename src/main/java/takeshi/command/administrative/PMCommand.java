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

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import takeshi.command.meta.AbstractCommand;
import takeshi.main.DiscordBot;
import takeshi.permission.SimpleRank;
import takeshi.templates.Templates;
import takeshi.util.DisUtil;

/**
 * !pm
 * make the bot pm someone
 */
public class PMCommand extends AbstractCommand {
	/**
	 * Instantiates a new Pm command.
	 */
	public PMCommand() {
		super();
	}

	@Override
	public String getDescription() {
		return "Send a message to user";
	}

	@Override
	public String getCommand() {
		return "pm";
	}

	@Override
	public String[] getUsage() {
		return new String[] {"pm <@user> <message..>"};
	}

	@Override
	public String[] getAliases() {
		return new String[] {};
	}

	@Override
	public String stringExecute(DiscordBot bot, String[] args, MessageChannel channel, User author, Message inputMessage) {
		SimpleRank rank = bot.security.getSimpleRank(author, channel);
		if (!rank.isAtLeast(SimpleRank.USER)) {
			return Templates.no_permission.formatGuild(channel);
		}
		if (args.length > 1) {
			User targetUser = null;
			int startIndex = 1;
			if (args[0].toLowerCase().matches("anon") && rank.isAtLeast(SimpleRank.BOT_ADMIN)) {
				targetUser = DisUtil.findUser((TextChannel) channel, args[1]);
				startIndex = 2;
			} else {
				targetUser = DisUtil.findUser((TextChannel) channel, args[0]);
			}

			if (targetUser != null && !targetUser.getId().equals(channel.getJDA().getSelfUser().getId())) {
				String message = "";
				for (int i = startIndex; i < args.length; i++) {
					message += " " + args[i];
				}
				if (startIndex == 1) {
					message = "You got a message from " + author.getAsMention() + ": " + message;
				}
				bot.out.sendPrivateMessage(targetUser, message);
				return Templates.command.pm_success.formatGuild(channel);
			} else {
				return Templates.command.pm_cant_find_user.formatGuild(channel);
			}
		}
		return Templates.invalid_use.formatGuild(channel);
	}
}