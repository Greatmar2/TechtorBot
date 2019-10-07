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

package takeshi.command.creator;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import takeshi.command.meta.AbstractCommand;
import takeshi.core.ExitCode;
import takeshi.main.DiscordBot;
import takeshi.main.Launcher;
import takeshi.permission.SimpleRank;
import takeshi.templates.Templates;
import takeshi.util.Emojibet;

/**
 * !exit
 * completely stops the program
 */
public class ExitCommand extends AbstractCommand {
	/**
	 * Instantiates a new Exit command.
	 */
	public ExitCommand() {
		super();
	}

	@Override
	public String getDescription() {
		return "completely shuts the bot down";
	}

	@Override
	public String getCommand() {
		return "exit";
	}

	@Override
	public String[] getUsage() {
		return new String[] {};
	}

	@Override
	public String[] getAliases() {
		return new String[] {
				"brexit"
		};
	}

	@Override
	public String stringExecute(DiscordBot bot, String[] args, MessageChannel channel, User author, Message inputMessage) {
		SimpleRank rank = bot.security.getSimpleRank(author);
		if (rank.isAtLeast(SimpleRank.SYSTEM_ADMIN)) {
			bot.out.sendAsyncMessage(channel, "I am being killed :sob: farewell world! :wave:", message -> {
				Launcher.stop(ExitCode.STOP);
			});
			return Emojibet.THUMBS_UP;
		}
		return Templates.no_permission.formatGuild(channel);
	}
}