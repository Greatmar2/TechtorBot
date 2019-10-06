/*
 * Copyright 2019 github.com/greatmar2
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

package takeshi.command.bot_administration;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import takeshi.command.meta.AbstractCommand;
import takeshi.main.DiscordBot;
import takeshi.permission.SimpleRank;
import takeshi.templates.Templates;
import takeshi.util.DisUtil;

/**
 * !botstatus changes the bot status (the playing game, or streaming)
 */
public class ListGuildsCommand extends AbstractCommand {
	public ListGuildsCommand() {
		super();
	}

	@Override
	public String getDescription() {
		return "List the guilds (servers) that I am in.";
	}

	@Override
	public String getCommand() {
		return "listguilds";
	}

	@Override
	public String[] getUsage() {
		return new String[] { "listguilds                      //lists the guilds" };
	}

	@Override
	public String[] getAliases() {
		return new String[] { "listservers" };
	}

	@Override
	public MessageBuilder execute(DiscordBot bot, String[] args, MessageChannel channel, User author, Message inputMessage) {
		SimpleRank rank = bot.security.getSimpleRank(author);
		if (!rank.isAtLeast(SimpleRank.BOT_ADMIN)) {
			return new MessageBuilder(Templates.no_permission.formatGuild(channel));
		}
		return DisUtil.getGuildList(bot);
	}
}