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

import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import takeshi.command.meta.AbstractCommand;
import takeshi.command.meta.CommandReactionListener;
import takeshi.command.meta.CooldownScope;
import takeshi.command.meta.ICommandCooldown;
import takeshi.command.meta.ICommandReactionListener;
import takeshi.games.game2048.Game2048;
import takeshi.games.game2048.Game2048Turn;
import takeshi.games.meta.GameState;
import takeshi.main.DiscordBot;
import takeshi.templates.Templates;
import takeshi.util.DisUtil;
import takeshi.util.Emojibet;

/**
 * The type Two zero four zero command.
 */
public class TwoZeroFourZeroCommand extends AbstractCommand implements ICommandReactionListener<Game2048>, ICommandCooldown {

	@Override
	public String getDescription() {
		return "play a game of 2048";
	}

	@Override
	public String getCommand() {
		return "2048";
	}

	@Override
	public boolean isListed() {
		return false;
	}

	@Override
	public String[] getUsage() {
		return new String[] { "2048       //play the game" };
	}

	@Override
	public String[] getAliases() {
		return new String[0];
	}

	@Override
	public String stringExecute(DiscordBot bot, String[] args, MessageChannel channel, User author, Message inputMessage) {
		if (!DisUtil.hasPermission(channel, channel.getJDA().getSelfUser(), Permission.MESSAGE_ADD_REACTION)) {
			return Templates.permission_missing.formatGuild(channel, Permission.MESSAGE_ADD_REACTION.toString());
		}
		Game2048 game = new Game2048();
		game.addPlayer(author);
		bot.queue.add(channel.sendMessage(game.toString()), message -> bot.commandReactionHandler
				.addReactionListener(((TextChannel) channel).getGuild().getIdLong(), message, getReactionListener(author.getIdLong(), game)));
		return "";

	}

	@Override
	public CommandReactionListener<Game2048> getReactionListener(long userId, Game2048 game) {
		CommandReactionListener<Game2048> listener = new CommandReactionListener<>(userId, game);
		listener.setExpiresIn(TimeUnit.MINUTES, 5);
		for (String reaction : game.getReactions()) {
			listener.registerReaction(Emojibet.getEmojiFor(reaction), message -> {
				Game2048Turn turn = new Game2048Turn();
				turn.parseInput(reaction);
				if (!game.isValidMove(message.getJDA().getUserById(userId), turn)) {
					message.editMessage(game.toString() + "\n" + Templates.playmode_not_a_valid_move.format()).complete();
				} else {
					game.playTurn(message.getJDA().getUserById(userId), turn);
					message.editMessage(game.toString()).complete();
				}
				if (game.getGameState().equals(GameState.OVER)) {
					listener.disable();
					message.clearReactions().complete();
				}
			});
		}
		return listener;
	}

	@Override
	public long getCooldownDuration() {
		return 300;
	}

	@Override
	public CooldownScope getScope() {
		return CooldownScope.USER;
	}
}
