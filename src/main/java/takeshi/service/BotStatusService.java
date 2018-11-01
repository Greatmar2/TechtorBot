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

package takeshi.service;

import java.util.Random;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Game;
import takeshi.core.AbstractService;
import takeshi.main.BotContainer;
import takeshi.main.DiscordBot;

/**
 * pseudo randomly sets the now playing tag of the bot
 */
public class BotStatusService extends AbstractService {
	private final static String[] playingStatusList = { "with %s human pets", "Teaching %s Minions", "Bot simulator 2%03d", "Planning for wold domination",
			"Talking to %s idiots", "Analyzing %s fellow humans", "Predicting the future", "with your CPU" };
	private final static String[] watchingStatusList = { "spoilers", "your every move", "your computer", "my top %s most wanted" };
	private final static String[] listeningStatusList = { "backchannel frequencies", "people scream", "Scope shouting", "leaks" };
	private final static String[] streamingStatusList = {};
	// private final Random rng;

	public BotStatusService(BotContainer b) {
		super(b);
		// rng = new Random();
	}

	@Override
	public String getIdentifier() {
		return "bot_nickname";
	}

	@Override
	public long getDelayBetweenRuns() {
		return 300_000;
	}

	@Override
	public boolean shouldIRun() {
		return !bot.isStatusLocked();
	}

	@Override
	public void beforeRun() {
	}

	@Override
	public void run() {
		// int roll = rng.nextInt(41) + 30;
		// TextChannel inviteChannel = bot.getShardFor(BotConfig.BOT_GUILD_ID).getJda()
		// .getTextChannelById(BotConfig.BOT_CHANNEL_ID);
		// if (inviteChannel != null && roll < 10) {
		// String fallback = "Feedback @ https://discord.gg/eaywDDt | #%s";
		// bot.getShardFor(BotConfig.BOT_GUILD_ID).queue.add(inviteChannel.getInvites(),
		// invites -> {
		// if (invites != null && !invites.isEmpty()) {
		// setGameOnShards(bot, "Feedback @ https://discord.gg/" +
		// invites.get(0).getCode() + " | %s");
		// } else {
		// setGameOnShards(bot, fallback);
		// }
		// });
		// } else if (roll < 50) {
		// String username = bot.getShards()[0].getJda().getSelfUser().getName();
		// setGameOnShards(bot, "@" + username + " help | @" + username + " invite |
		// #%s");
		// } else {
		int roll = new Random().nextInt(playingStatusList.length + watchingStatusList.length + listeningStatusList.length + streamingStatusList.length);
		// int statusNum = bot.getShards()[0].getJda().getUsers().size();
		String status;
		Game.GameType gameType;
		// Set the status type from one of the status lists, with the appropriate game
		// type
		if (roll < playingStatusList.length) {
			status = playingStatusList[roll];
			gameType = Game.GameType.DEFAULT;
		} else if (roll < (playingStatusList.length + watchingStatusList.length)) {
			status = watchingStatusList[roll - playingStatusList.length];
			gameType = Game.GameType.WATCHING;
		} else if (roll < (playingStatusList.length + watchingStatusList.length + listeningStatusList.length)) {
			status = listeningStatusList[roll - playingStatusList.length - watchingStatusList.length];
			gameType = Game.GameType.LISTENING;
		} else if (roll < (playingStatusList.length + watchingStatusList.length + listeningStatusList.length + streamingStatusList.length)) {
			status = streamingStatusList[roll - playingStatusList.length - watchingStatusList.length - listeningStatusList.length];
			gameType = Game.GameType.STREAMING;
		} else { // This shouldn't happen
			status = "broken code";
			gameType = Game.GameType.STREAMING;
		}
		setGameOnShards(bot, status, gameType);
		// }
	}

	private void setGameOnShards(BotContainer container, String status, Game.GameType gameType) {
		for (DiscordBot shard : container.getShards()) {
			JDA jda = shard.getJda();
			int statusNum = jda.getUsers().size() - 2;// Don't count Elek or itself
			if (gameType == Game.GameType.STREAMING) {
				jda.getPresence().setGame(Game.streaming(status, "http://www.twitch.tv/elektronxz"));
			} else {
				jda.getPresence().setGame(Game.of(gameType, String.format(status, statusNum)));
			}

			// Check polls every 5 min, with status change.
//			shard.pollHandler.checkPolls(shard);
		}
	}

	@Override
	public void afterRun() {
	}
}