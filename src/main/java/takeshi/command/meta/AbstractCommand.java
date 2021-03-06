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

package takeshi.command.meta;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import takeshi.main.DiscordBot;

/**
 * The type Abstract command.
 */
public abstract class AbstractCommand {

	private CommandCategory commandCategory = CommandCategory.UNKNOWN;

	/**
	 * Instantiates a new Abstract command.
	 */
	public AbstractCommand() {

	}

	/**
	 * A short discription of the method
	 *
	 * @return description description
	 */
	public abstract String getDescription();

	/**
	 * What should be typed to trigger this command (Without prefix)
	 *
	 * @return command command
	 */
	public abstract String getCommand();

	/**
	 * How to use the command?
	 *
	 * @return command usage
	 */
	public abstract String[] getUsage();

	/**
	 * aliases to call the command
	 *
	 * @return array of aliases
	 */
	public abstract String[] getAliases();

	/**
	 * Gets command category.
	 *
	 * @return the command category
	 */
	public final CommandCategory getCommandCategory() {
		return commandCategory;
	}

	/**
	 * The command will be set to the category matching the last part of the package
	 * name.
	 *
	 * @param newCategory category of the command
	 */
	public void setCommandCategory(CommandCategory newCategory) {
		commandCategory = newCategory;
	}

	/**
	 * where can the command be used?
	 *
	 * @return private, public, both
	 */
	public CommandVisibility getVisibility() {
		return CommandVisibility.BOTH;
	}

	/**
	 * is a command enabled? it is by default This enables/disables commands on a
	 * global scale
	 *
	 * @return command is enabled?
	 */
	public boolean isEnabled() {
		return true;
	}

	/**
	 * Whether the command can be blacklisted by guilds
	 *
	 * @return can be blacklisted?
	 */
	public boolean canBeDisabled() {
		return true;
	}

	/**
	 * Is a command listed? it is by default
	 *
	 * @return shows up in the !help list?
	 */
	public boolean isListed() {
		return true;
	}

	/**
	 * By default will call simpleExecute which returns a string. Must override to
	 * return a MessageBuilder.
	 *
	 * @param bot          the bot
	 * @param args         the args
	 * @param channel      the channel
	 * @param author       the author
	 * @param inputMessage the input message
	 * @return message builder
	 */
	public MessageBuilder execute(DiscordBot bot, String[] args, MessageChannel channel, User author, Message inputMessage) {
		return new MessageBuilder(stringExecute(bot, args, channel, author, inputMessage));
	}

	/**
	 * String execute string.
	 *
	 * @param bot          the shard where its executing on
	 * @param args         arguments for the command
	 * @param channel      channel where the command is executed
	 * @param author       who invoked the command
	 * @param inputMessage the incoming message object
	 * @return the message to output or an empty string for nothing
	 */
	public String stringExecute(DiscordBot bot, String[] args, MessageChannel channel, User author, Message inputMessage) {
		return "SimpleExecute " + this.getClass();
	};
}
