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

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import takeshi.command.meta.AbstractCommand;
import takeshi.main.BotConfig;
import takeshi.main.DiscordBot;
import takeshi.permission.SimpleRank;
import takeshi.templates.Templates;
import takeshi.util.DisUtil;

/**
 * leaves the guild
 */
public class LeaveGuildCommand extends AbstractCommand {
	/**
	 * Instantiates a new Leave guild command.
	 */
	public LeaveGuildCommand() {
		super();
	}

	@Override
	public String getDescription() {
		return "leaves guild :(";
	}

	@Override
	public String getCommand() {
		return "leaveguild";
	}

	@Override
	public String[] getUsage() {
		return new String[] { "leaveguild     //leaves the guild" };
	}

	@Override
	public String[] getAliases() {
		return new String[] {};
	}

	@Override
	public String stringExecute(DiscordBot bot, String[] args, MessageChannel channel, User author, Message inputMessage) {
		boolean shouldLeave = false;
		Guild guild = ((TextChannel) channel).getGuild();
		SimpleRank rank = bot.security.getSimpleRank(author, channel);
		if (!rank.isAtLeast(SimpleRank.GUILD_ADMIN)) {
			return Templates.no_permission.formatGuild(channel);
		}
		if (rank.isAtLeast(SimpleRank.BOT_ADMIN) && args.length >= 1 && args[0].matches("^\\d{10,}$")) {
			guild = channel.getJDA().getGuildById(args[0]);
			if (guild == null) {
				return Templates.config.cant_find_guild.formatGuild(channel);
			}
			if (args.length == 1) {
				return "Are you sure? Type `" + DisUtil.getCommandPrefix(channel) + "leaveguild " + args[0] + " confirm` to leave _" + guild.getName() + "_";
			}
			if (args[1].equals("confirm")) {
				shouldLeave = true;
			}
		}
		if (args.length == 0) {
			return "Are you sure? Type `" + DisUtil.getCommandPrefix(channel) + "leaveguild confirm` to leave";
		}
		if (args[0].equals("confirm")) {
			shouldLeave = true;
		}
		if (shouldLeave) {
			final Guild finalGuild = guild;
			bot.getJda().getGuildById(BotConfig.BOT_GUILD_ID).getDefaultChannel().createInvite().setMaxAge(0).setMaxUses(5).queue(invite -> {
				bot.out.sendAsyncMessage(bot.getDefaultChannel(finalGuild),
						"I have been ordered to leave. Goodbye.\nVisit here for queries: " + invite.getUrl(), message -> {
							bot.queue.add(finalGuild.leave());
						});
			});
			return "";
		}
		return ":face_palm: I expected you to know how to use it";
	}
}