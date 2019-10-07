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

package takeshi.command.fun;

import org.apache.commons.lang3.StringEscapeUtils;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import takeshi.command.meta.AbstractCommand;
import takeshi.handler.CommandHandler;
import takeshi.main.DiscordBot;
import takeshi.templates.Templates;

/**
 * !joke gives you a random pun <@user>
 */
public class PunCommand extends AbstractCommand {
	/**
	 * Instantiates a new Pun command.
	 */
	public PunCommand() {
		super();
	}

	@Override
	public String getDescription() {
		return "An attempt to be punny";
	}

	@Override
	public String getCommand() {
		return "pun";
	}

	@Override
	public String[] getUsage() {
		return new String[] {};
	}

	@Override
	public String[] getAliases() {
		return new String[] {};
	}

	@Override
	public String stringExecute(DiscordBot bot, String[] args, MessageChannel channel, User author, Message inputMessage) {
		bot.out.sendAsyncMessage(channel, Templates.command.joke_wait.formatGuild(channel), message -> {
			String puntxt = "";
			puntxt = CommandHandler.getCommand("reddit").stringExecute(bot, new String[] { "puns" }, channel, author, null);
			if (puntxt != null && !puntxt.isEmpty()) {
				bot.out.editAsync(message, StringEscapeUtils.unescapeHtml4(puntxt.replace(author.getName(), "<@" + author.getId() + ">")));
			} else {
				bot.out.editAsync(message, Templates.command.joke_not_today.formatGuild(channel));
			}
		});
		return "";
	}
}