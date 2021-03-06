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

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import takeshi.command.meta.AbstractCommand;
import takeshi.handler.CommandHandler;
import takeshi.main.DiscordBot;

/**
 * !joke gives you a random chuck norris joke with chuck norris replaced by
 * <@user>
 */
public class GeckoCommand extends AbstractCommand {
	/**
	 * Instantiates a new Gecko command.
	 */
	public GeckoCommand() {
		super();
	}

	@Override
	public String getDescription() {
		return "Gecko gifs from giphy";
	}

	@Override
	public String getCommand() {
		return "gecko";
	}

	@Override
	public String[] getUsage() {
		return new String[] { "gecko         //shows random gecko gif" };
	}

	@Override
	public String[] getAliases() {
		return new String[] { "gek", "geck" };
	}

	@Override
	public String stringExecute(DiscordBot bot, String[] args, MessageChannel channel, User author, Message inputMessage) {
		return CommandHandler.getCommand("gif").stringExecute(bot, new String[] { "gecko" }, channel, author, inputMessage);
	}
}