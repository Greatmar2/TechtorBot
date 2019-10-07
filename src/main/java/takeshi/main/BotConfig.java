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

package takeshi.main;

import com.kaaz.configuration.ConfigurationOption;

import takeshi.util.Emojibet;

/**
 * The type Bot config.
 */
public class BotConfig {

	/**
	 * The constant USER_AGENT.
	 */
	public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36";
	/**
	 * The constant GUILD_MAX_BOT_USER_RATIO.
	 */
// the bot/users ratio for guilds
	public static final double GUILD_MAX_BOT_USER_RATIO = 0.5D;
	/**
	 * The constant GUILD_OWNER_MIN_ACCOUNT_AGE.
	 */
// the minimum age of the guild owner's account in days
	public static final long GUILD_OWNER_MIN_ACCOUNT_AGE = 7;
	/**
	 * The constant GUILD_MIN_USERS.
	 */
// if a guild has less users it will be marked as a test guild
	public static final int GUILD_MIN_USERS = 5;
	/**
	 * The constant MAX_MESSAGE_SIZE.
	 */
	public static final int MAX_MESSAGE_SIZE = 2000;
	/**
	 * The constant DELETE_MESSAGES_AFTER.
	 */
// the default time to delete messages after milliseconds
	public static long DELETE_MESSAGES_AFTER = 120_000;

	/**
	 * The constant TEMPLATE_QUOTE.
	 */
	public static String TEMPLATE_QUOTE = "%";
	/**
	 * The constant BOT_ENABLED.
	 */
// bot enabled? must be set to true in order to run
	@ConfigurationOption
	public static boolean BOT_ENABLED = false;

	/**
	 * The constant MUSIC_MAX_VOLUME.
	 */
	@ConfigurationOption
	public static int MUSIC_MAX_VOLUME = 100;

	/**
	 * The constant SUBSCRIBE_UNSUB_ON_NOT_FOUND.
	 */
	@ConfigurationOption
	public static boolean SUBSCRIBE_UNSUB_ON_NOT_FOUND = false;

	/**
	 * The constant BOT_RESTART_INACTIVE_SHARDS.
	 */
	@ConfigurationOption
	public static boolean BOT_RESTART_INACTIVE_SHARDS = false;

	/**
	 * The constant BOT_STATS_DISCORDLIST_NET.
	 */
// send stats to discordlist.net?
	@ConfigurationOption
	public static boolean BOT_STATS_DISCORDLIST_NET = false;

	/**
	 * The constant BOT_STATS_DISCORDLIST_NET_TOKEN.
	 */
// the token for it
	@ConfigurationOption
	public static String BOT_STATS_DISCORDLIST_NET_TOKEN = "";

	/**
	 * The constant BOT_STATS_DISCORD_PW_ENABLED.
	 */
// toggle sending stats to discord.bots.pw
	@ConfigurationOption
	public static boolean BOT_STATS_DISCORD_PW_ENABLED = false;

	/**
	 * The constant BOT_TOKEN_BOTS_DISCORD_PW.
	 */
// token for discord.bots.pw
	@ConfigurationOption
	public static String BOT_TOKEN_BOTS_DISCORD_PW = "token-here";

	/**
	 * The constant BOT_TOKEN_DISCORDBOTS_ORG.
	 */
	@ConfigurationOption
	public static String BOT_TOKEN_DISCORDBOTS_ORG = "token-here";

	/**
	 * The constant TOKEN_RIOT_GAMES.
	 */
	@ConfigurationOption
	public static String TOKEN_RIOT_GAMES = "token-here";

	/**
	 * The constant WIT_AI_TOKEN.
	 */
	@ConfigurationOption
	public static String WIT_AI_TOKEN = "token-here";

	/**
	 * The constant BOT_WEBSITE.
	 */
// the website of the bot
	@ConfigurationOption
	public static String BOT_WEBSITE = "dragonpress.net";
	/**
	 * The constant BOT_ENV.
	 */
	@ConfigurationOption
	public static String BOT_ENV = "test";

	/**
	 * The constant BOT_GRAYLOG_ACTIVE.
	 */
// if you want to use graylog
	@ConfigurationOption
	public static boolean BOT_GRAYLOG_ACTIVE = false;
	/**
	 * The constant BOT_GRAYLOG_HOST.
	 */
	@ConfigurationOption
	public static String BOT_GRAYLOG_HOST = "10.120.34.139";
	/**
	 * The constant BOT_GRAYLOG_PORT.
	 */
	@ConfigurationOption
	public static int BOT_GRAYLOG_PORT = 12202;

	/**
	 * The constant BOT_AUTO_UPDATE.
	 */
	@ConfigurationOption
	public static boolean BOT_AUTO_UPDATE = false;

	/**
	 * The constant BOT_NAME.
	 */
// display name of the bot
	@ConfigurationOption
	public static String BOT_NAME = "Techtor";

	/**
	 * The constant BOT_GUILD_ID.
	 */
// Bot's own discord server
	@ConfigurationOption
	public static String BOT_GUILD_ID = "225168913808228352";

	/**
	 * The constant BOT_CHANNEL_ID.
	 */
// Bot's own channel on its own server
	@ConfigurationOption
	public static String BOT_CHANNEL_ID = "225170823898464256";

	/**
	 * The constant BOT_ERROR_CHANNEL_ID.
	 */
// Bot's error channel id
	@ConfigurationOption
	public static String BOT_ERROR_CHANNEL_ID = "249646038443491340";

	/**
	 * The constant BOT_STATUS_CHANNEL_ID.
	 */
// Bot's status update
	@ConfigurationOption
	public static String BOT_STATUS_CHANNEL_ID = "260721966430814210";

	/**
	 * The constant BOT_TOKEN.
	 */
// token used to login to discord
	@ConfigurationOption
	public static String BOT_TOKEN = "mybottokenhere";

	/**
	 * The constant BOT_CHATTING_ENABLED.
	 */
// prefix for all commands !help etc.
	@ConfigurationOption
	public static boolean BOT_CHATTING_ENABLED = false;

	/**
	 * The constant PRIVATE_MESSAGE_FORWARDING_ENABLED.
	 */
// whether to log non-command messages on the bot's forward channel, allowing
	// bot admins to use !reply to give the appearance of an extremely intelligent
	// chat bot.
	@ConfigurationOption
	public static boolean PRIVATE_MESSAGE_FORWARDING_ENABLED = false;

	/**
	 * The constant GUILD_MESSAGE_FORWARDING_ENABLED.
	 */
	@ConfigurationOption
	public static boolean GUILD_MESSAGE_FORWARDING_ENABLED = false;

	/**
	 * The constant CHANNEL_WATCH_DURATION.
	 */
// how long (minutes) to forward all messages from a guild channel that !reply
	// was used on
	@ConfigurationOption
	public static int CHANNEL_WATCH_DURATION = 15;

	/**
	 * The constant BOT_FORWARD_CHANNEL_ID.
	 */
// which channel to forward messages to
	@ConfigurationOption
	public static String BOT_FORWARD_CHANNEL_ID = "249646038443491340";

	/**
	 * The constant BOT_COMMAND_PREFIX.
	 */
// default prefix to mark messages as commands
	@ConfigurationOption
	public static String BOT_COMMAND_PREFIX = "!";

	/**
	 * The constant BOT_COMMAND_LOGGING.
	 */
// save the usage of commands?
	@ConfigurationOption
	public static boolean BOT_COMMAND_LOGGING = true;

	/**
	 * The constant SHOW_KEYPHRASE.
	 */
// show keyphrases?
	@ConfigurationOption
	public static boolean SHOW_KEYPHRASE = false;

	/**
	 * The constant BOT_COMMAND_SHOW_UNKNOWN.
	 */
// Reply to non existing commands?
	@ConfigurationOption
	public static boolean BOT_COMMAND_SHOW_UNKNOWN = false;

	/**
	 * The constant MUSIC_MAX_PLAYLIST_SIZE.
	 */
	@ConfigurationOption
	public static int MUSIC_MAX_PLAYLIST_SIZE = 50;

	/**
	 * The constant DB_HOST.
	 */
// mysql hostname
	@ConfigurationOption
	public static String DB_HOST = "localhost";

	/**
	 * The constant DB_PORT.
	 */
// mysql port
	@ConfigurationOption
	public static int DB_PORT = 3306;

	/**
	 * The constant DB_USER.
	 */
// mysql user
	@ConfigurationOption
	public static String DB_USER = "root";

	/**
	 * The constant DB_PASS.
	 */
// mysql password
	@ConfigurationOption
	public static String DB_PASS = "";

	/**
	 * The constant DB_NAME.
	 */
// mysql database name
	@ConfigurationOption
	public static String DB_NAME = "discord";

	/**
	 * The constant MODULE_MUSIC_ENABLED.
	 */
// enable economy globally
	@ConfigurationOption
	public static boolean MODULE_MUSIC_ENABLED = true;

	/**
	 * The constant MODULE_ECONOMY_ENABLED.
	 */
// enable economy globally
	@ConfigurationOption
	public static boolean MODULE_ECONOMY_ENABLED = true;

	/**
	 * The constant MODULE_POE_ENABLED.
	 */
// enable poe globally
	@ConfigurationOption
	public static boolean MODULE_POE_ENABLED = false;

	/**
	 * The constant MODULE_HEARTHSTONE_ENABLED.
	 */
// enable hearthstone globally
	@ConfigurationOption
	public static boolean MODULE_HEARTHSTONE_ENABLED = false;

	/**
	 * The constant ECONOMY_CURRENCY_NAME.
	 */
// name of the currency
	@ConfigurationOption
	public static String ECONOMY_CURRENCY_NAME = "cookie";
	/**
	 * The constant ECONOMY_CURRENCY_NAMES.
	 */
	@ConfigurationOption
	public static String ECONOMY_CURRENCY_NAMES = "cookies";

	/**
	 * The constant ECONOMY_CURRENCY_ICON.
	 */
// emoticon of the currency
	@ConfigurationOption
	public static String ECONOMY_CURRENCY_ICON = Emojibet.COOKIE;

	/**
	 * The constant ECONOMY_START_BALANCE.
	 */
// a new user starts with this balance
	@ConfigurationOption
	public static int ECONOMY_START_BALANCE = 1;
	/**
	 * The constant TRELLO_ACTIVE.
	 */
// Use trello integration
	@ConfigurationOption
	public static boolean TRELLO_ACTIVE = false;

	/**
	 * The Google api key.
	 */
	@ConfigurationOption
	public static String[] GOOGLE_API_KEY = { "google-api-key-here" };

	/**
	 * The constant GIPHY_TOKEN.
	 */
	@ConfigurationOption
	public static String GIPHY_TOKEN = "dc6zaTOxFJmzC";

	/**
	 * The constant TRELLO_API_KEY.
	 */
// Use trello integration
	@ConfigurationOption
	public static String TRELLO_API_KEY = "api-key-here";

	/**
	 * The constant TRELLO_BOARD_ID.
	 */
	@ConfigurationOption
	public static String TRELLO_BOARD_ID = "57beb462bac8baf93c4bba47";

	/**
	 * The constant TRELLO_LIST_BUGS.
	 */
	@ConfigurationOption
	public static String TRELLO_LIST_BUGS = "57beb482265f090f6a425e01";

	/**
	 * The constant TRELLO_LIST_IN_PROGRESS.
	 */
	@ConfigurationOption
	public static String TRELLO_LIST_IN_PROGRESS = "57beb4850d0e12837dca475d";

	/**
	 * The constant TRELLO_LIST_PLANNED.
	 */
	@ConfigurationOption
	public static String TRELLO_LIST_PLANNED = "57beb4b9146625cc9f255073";

	/**
	 * The constant TRELLO_TOKEN.
	 */
// the trello token
	@ConfigurationOption
	public static String TRELLO_TOKEN = "token-here";

	/**
	 * The constant CREATOR_ID.
	 */
	@ConfigurationOption
	public static long CREATOR_ID = 161886374054592513L;
}
