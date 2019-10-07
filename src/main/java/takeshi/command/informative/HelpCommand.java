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

package takeshi.command.informative;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import takeshi.command.meta.AbstractCommand;
import takeshi.command.meta.CommandCategory;
import takeshi.command.meta.CommandReactionListener;
import takeshi.command.meta.ICommandReactionListener;
import takeshi.guildsettings.GSetting;
import takeshi.handler.CommandHandler;
import takeshi.handler.GuildSettings;
import takeshi.main.BotConfig;
import takeshi.main.DiscordBot;
import takeshi.permission.SimpleRank;
import takeshi.templates.Templates;
import takeshi.util.DisUtil;
import takeshi.util.Emojibet;
import takeshi.util.Misc;
import takeshi.util.QuickEmbedBuilder;

/**
 * !help help function
 */
public class HelpCommand extends AbstractCommand implements ICommandReactionListener<HelpCommand.ReactionData> {
	/**
	 * Instantiates a new Help command.
	 */
	public HelpCommand() {
		super();
	}

	@Override
	public String getDescription() {
		return "An attempt to help out";
	}

	@Override
	public boolean canBeDisabled() {
		return false;
	}

	@Override
	public String getCommand() {
		return "help";
	}

	@Override
	public String[] getUsage() {
		return new String[] { "help            //shows commands grouped by categories, navigable by reactions ",
				"help full       //index of all commands, in case you don't have reactions", "help <command>  //usage for that command" };
	}

	@Override
	public String[] getAliases() {
		return new String[] { "?", "halp", "helpme", "h", "commands" };
	}

	@Override
	public String stringExecute(DiscordBot bot, String[] args, MessageChannel channel, User author, Message inputMessage) {
		return "Use full execution";
	}

	@Override
	public MessageBuilder execute(DiscordBot bot, String[] args, MessageChannel channel, User author, Message inputMessage) {
		String commandPrefix = GuildSettings.getFor(channel, GSetting.COMMAND_PREFIX);
		boolean showHelpInPM = GuildSettings.getBoolFor(channel, GSetting.HELP_IN_PM);
		if (args.length > 0 && !args[0].equals("full")) {
			AbstractCommand c = CommandHandler.getCommand(DisUtil.filterPrefix(args[0], channel));
			if (c != null) {
				EmbedBuilder ret = new EmbedBuilder();
				ret.setColor(QuickEmbedBuilder.DEFAULT_COL);
				ret.setTitle(" :information_source: " + c.getCommand());
				ArrayList<String> aliases = new ArrayList<>();
				aliases.add(commandPrefix + c.getCommand());
				for (String alias : c.getAliases()) {
					aliases.add(commandPrefix + alias);
				}
				ret.setDescription(Misc.makeTable(c.getDescription()));
				ret.addField("Accessible through", Misc.makeTable(aliases, 16, 3), false);
				// ret.addField(Emojibet.NOTEPAD + " **Description**\n",
				// Misc.makeTable(c.getDescription()), false);
				if (c.getUsage().length > 0) {
					String usages = "```php\n";
					for (String line : c.getUsage()) {
						usages += line + "\n";
					}
					usages += "```";
					ret.addField("Usages", usages, false);
				}
				return new MessageBuilder(ret);
			}
			return QuickEmbedBuilder.embedStringColor(Templates.command.help.donno.formatGuild(channel), QuickEmbedBuilder.ERROR_COL);
		}
		SimpleRank userRank = bot.security.getSimpleRank(author, channel);
		EmbedBuilder ret = new EmbedBuilder();
		ret.setColor(QuickEmbedBuilder.DEFAULT_COL);
		ret.setTitle("I know the following commands: \n\n");
		if ((args.length == 0 || !args[0].equals("full")) && channel instanceof TextChannel) {
			TextChannel textChannel = (TextChannel) channel;
			if (PermissionUtil.checkPermission(textChannel, textChannel.getGuild().getSelfMember(), Permission.MESSAGE_EMBED_LINKS,
					Permission.MESSAGE_ADD_REACTION)) {
				HashMap<CommandCategory, ArrayList<String>> map = getCommandMap(userRank);
				CommandCategory cat = CommandCategory.getFirstWithPermission(userRank);
				bot.queue.add(
						channel.sendMessage(writeFancyEmbed(writeFancyDescription(channel, cat, map.keySet()), cat.getDisplayName(),
								Misc.makeTable(map.get(cat)), writeFancyFooter(channel)).build()),
						msg -> bot.commandReactionHandler.addReactionListener(((TextChannel) channel).getGuild().getIdLong(), msg,
								getReactionListener(author.getIdLong(), new ReactionData(userRank, cat))));

				return new MessageBuilder();
			}
		}
		Field[] catFields = fieldPerCategory(getCommandMap(userRank));
		for (Field f : catFields) {
			ret.addField(f);
		}
		// ret += styleTablePerCategory(getCommandMap(userRank));
		if (showHelpInPM) {
			bot.out.sendPrivateMessage(author, ret + "for more details about a command use **" + commandPrefix + "help <command>**\n"
					+ ":exclamation: In private messages the prefix for commands is **" + BotConfig.BOT_COMMAND_PREFIX + "**");
			return QuickEmbedBuilder.embedStringColor(Templates.command.help.send_private.formatGuild(channel), QuickEmbedBuilder.WARN_COL);
		} else {
			ret.addField("Details", "for more details about a command use **" + commandPrefix + "help <command>**", false);
			return new MessageBuilder(ret);
		}

	}

	private HashMap<CommandCategory, ArrayList<String>> getCommandMap(SimpleRank userRank) {
		HashMap<CommandCategory, ArrayList<String>> commandList = new HashMap<>();
		if (userRank == null) {
			userRank = SimpleRank.USER;
		}
		AbstractCommand[] commandObjects = CommandHandler.getCommandObjects();
		for (AbstractCommand command : commandObjects) {
			if (!command.isListed() || !command.isEnabled() || !userRank.isAtLeast(command.getCommandCategory().getRankRequired())) {
				continue;
			}
			if (!commandList.containsKey(command.getCommandCategory())) {
				commandList.put(command.getCommandCategory(), new ArrayList<>());
			}
			commandList.get(command.getCommandCategory()).add(command.getCommand());
		}
		commandList.forEach((k, v) -> Collections.sort(v));
		return commandList;
	}

	private Field[] fieldPerCategory(HashMap<CommandCategory, ArrayList<String>> map) {
		CommandCategory[] keys = Arrays.stream(CommandCategory.values()).filter(category -> map.containsKey(category)).toArray(CommandCategory[]::new);
		Field[] fields = new Field[keys.length];
		for (int i = 0; i < keys.length; i++) {
			fields[i] = new Field(keys[i].getEmoticon() + " " + keys[i].getDisplayName(), Misc.makeTable(map.get(keys[i])), false);
		}
		return fields;
	}

	private String styleTablePerCategory(HashMap<CommandCategory, ArrayList<String>> map) {
		StringBuilder table = new StringBuilder();
		for (CommandCategory category : CommandCategory.values()) {
			if (map.containsKey(category)) {
				table.append(styleTableCategory(category, map.get(category)));
			}
		}
		return table.toString();
	}

	private String styleTableCategory(CommandCategory category, ArrayList<String> commands) {
		return category.getEmoticon() + " " + category.getDisplayName() + "\n" + Misc.makeTable(commands);
	}

	private EmbedBuilder writeFancyEmbed(String description, String category, String table, String footer) {
		EmbedBuilder ret = new EmbedBuilder();
		ret.setColor(QuickEmbedBuilder.DEFAULT_COL);
		ret.setTitle("Help Overview");
		ret.setDescription(description);
		ret.addField(category, table, false);
		ret.addField("Details", footer, false);
		return ret;
	}

	private String writeFancyDescription(MessageChannel channel, CommandCategory active, Set<CommandCategory> categories) {
		StringBuilder header = new StringBuilder();

		for (CommandCategory cat : CommandCategory.values()) {
			if (!categories.contains(cat)) {
				continue;
			}

			if (cat.equals(active)) {
				header.append("__**" + Emojibet.DIAMOND_BLUE_SMALL).append(cat.getDisplayName()).append("**__");
			} else {
				header.append(cat.getDisplayName());
			}
			header.append(" | ");
		}
		return header.substring(0, header.length() - 3);
	}

	private String writeFancyFooter(MessageChannel channel) {
		return "To see without reactions use `" + DisUtil.getCommandPrefix(channel) + "help full`\n" + "For more details about a command use `"
				+ DisUtil.getCommandPrefix(channel) + "help <command>`\nUse the reactions below to switch between the pages";
	}

	@Override
	public CommandReactionListener<ReactionData> getReactionListener(long userId, ReactionData data) {
		CommandReactionListener<ReactionData> listener = new CommandReactionListener<>(userId, data);
		HashMap<CommandCategory, ArrayList<String>> map = getCommandMap(data.getRank());
		for (CommandCategory category : CommandCategory.values()) {
			if (map.containsKey(category)) {
				listener.registerReaction(category.getEmoticon(), message -> {
					if (listener.getData().getActiveCategory().equals(category)) {
						return;
					}
					listener.getData().setActiveCategory(category);
					MessageChannel channel = message.getChannel();
					message.editMessage(writeFancyEmbed(writeFancyDescription(channel, category, map.keySet()), category.getDisplayName(),
							Misc.makeTable(map.get(category)), writeFancyFooter(channel)).build()).complete();
					// Misc.clearReactions(message, userId);
					// message.editMessage(
					// writeFancyHeader(message.getChannel(), category, map.keySet()) +
					// styleTableCategory(category, map.get(category)) +
					// writeFancyFooter(message.getChannel())).complete();
				});
			}
		}
		return listener;
	}

	/**
	 * The type Reaction data.
	 */
	public class ReactionData {
		/**
		 * The Rank.
		 */
		final SimpleRank rank;
		private CommandCategory activeCategory;

		private ReactionData(SimpleRank rank, CommandCategory activeCategory) {
			this.rank = rank;
			this.activeCategory = activeCategory;
		}

		/**
		 * Gets active category.
		 *
		 * @return the active category
		 */
		public CommandCategory getActiveCategory() {
			return activeCategory;
		}

		/**
		 * Sets active category.
		 *
		 * @param activeCategory the active category
		 */
		public void setActiveCategory(CommandCategory activeCategory) {
			this.activeCategory = activeCategory;
		}

		/**
		 * Gets rank.
		 *
		 * @return the rank
		 */
		public SimpleRank getRank() {
			return rank;
		}
	}
}