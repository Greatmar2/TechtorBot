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

package takeshi.command.bot_administration;

import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import takeshi.command.meta.AbstractCommand;
import takeshi.main.DiscordBot;
import takeshi.permission.SimpleRank;
import takeshi.templates.Templates;
import takeshi.util.Emojibet;
import takeshi.util.Misc;

/**
 * !botstatus changes the bot status (the playing game, or streaming)
 */
public class BotStatusCommand extends AbstractCommand {
	public BotStatusCommand() {
		super();
	}

	@Override
	public String getDescription() {
		return "Set the game I'm currently playing";
	}

	@Override
	public String getCommand() {
		return "botstatus";
	}

	@Override
	public String[] getUsage() {
		return new String[] { "botstatus reset                      //unlocks the status",
				"botstatus game <game>                //changes the playing game to <game>",
				"botstatus watch <watching>                //changes the watching status to <watching>",
				"botstatus listen <listening>                //changes the listening status to <listening>",
				"botstatus stream <username> <game>   //streaming twitch.tv/<username> playing <game>", };
	}

	@Override
	public String[] getAliases() {
		return new String[] {};
	}

	@Override
	public String simpleExecute(DiscordBot bot, String[] args, MessageChannel channel, User author, Message inputMessage) {
		SimpleRank rank = bot.security.getSimpleRank(author);
		if (!rank.isAtLeast(SimpleRank.BOT_ADMIN)) {
			return Templates.no_permission.formatGuild(channel);
		}
		if (args.length == 0) {
			return Templates.invalid_use.formatGuild(channel);
		}
		if (args.length > 0) {
			switch (args[0].toLowerCase()) {
			case "reset":
				bot.getContainer().setStatusLocked(false);
				return Emojibet.THUMBS_UP;
			case "game":
			case "watch":
			case "listen":
				if (args.length < 2) {
					return Templates.invalid_use.formatGuild(channel);
				}
				Game.GameType gameType = Game.GameType.DEFAULT;
				if (args[0].equalsIgnoreCase("watch")) {
					gameType = Game.GameType.WATCHING;
				} else if (args[0].equalsIgnoreCase("listen")) {
					gameType = Game.GameType.LISTENING;
				}
				channel.getJDA().getPresence().setGame(Game.of(gameType, Misc.joinStrings(args, 1)));
				break;
			case "stream":
				if (args.length < 3) {
					return Templates.invalid_use.formatGuild(channel);
				}
				try {
					channel.getJDA().getPresence().setGame(Game.of(Game.GameType.STREAMING, Misc.joinStrings(args, 2),
							"http://www.twitch.tv/" + args[1]));
				} catch (Exception e) {
					return Emojibet.THUMBS_DOWN + " " + e.getMessage();
				}
				break;
			default:
				return Templates.invalid_use.formatGuild(channel);
			}
			bot.getContainer().setStatusLocked(true);
			try {
				Thread.sleep(5_000L);
			} catch (InterruptedException ignored) {
			}
			return Emojibet.THUMBS_UP;
		}
		return Templates.invalid_use.formatGuild(channel);
	}
}