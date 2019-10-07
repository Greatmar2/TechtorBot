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

import takeshi.permission.SimpleRank;
import takeshi.util.Emojibet;

/**
 * The enum Command category.
 */
public enum CommandCategory {
	/**
	 * Creator command category.
	 */
	CREATOR("creator", Emojibet.MAN_IN_SUIT, "Development", SimpleRank.CREATOR),
	/**
	 * The Bot administration.
	 */
	BOT_ADMINISTRATION("bot_administration", Emojibet.MONKEY, "Bot administration", SimpleRank.BOT_ADMIN),
	/**
	 * Administrative command category.
	 */
	ADMINISTRATIVE("administrative", Emojibet.POLICE, "Administration", SimpleRank.GUILD_ADMIN),
	/**
	 * Informative command category.
	 */
	INFORMATIVE("informative", Emojibet.INFORMATION, "Information"),
	/**
	 * Music command category.
	 */
	MUSIC("music", Emojibet.MUSIC_NOTE, "Music"),
	/**
	 * Economy command category.
	 */
	ECONOMY("economy", Emojibet.MONEY_BAG, "Money"),
	/**
	 * Fun command category.
	 */
	FUN("fun", Emojibet.GAME_DICE, "Fun"),
	/**
	 * The Poe.
	 */
	POE("poe", Emojibet.CURRENCY_EXCHANGE, "Path of exile"),
	/**
	 * Hearthstone command category.
	 */
	HEARTHSTONE("hearthstone", Emojibet.SLOT_MACHINE, "Hearthstone"),
	/**
	 * Adventure command category.
	 */
	ADVENTURE("adventure", Emojibet.FOOTPRINTS, "Adventure"),
	/**
	 * Unknown command category.
	 */
	UNKNOWN("nopackage", Emojibet.QUESTION_MARK, "Misc");
    private final String packageName;
    private final String emoticon;
    private final String displayName;
    private final SimpleRank rankRequired;

    CommandCategory(String packageName, String emoticon, String displayName) {

        this.packageName = packageName;
        this.emoticon = emoticon;
        this.displayName = displayName;
        this.rankRequired = SimpleRank.USER;
    }

    CommandCategory(String packageName, String emoticon, String displayName, SimpleRank rankRequired) {

        this.packageName = packageName;
        this.emoticon = emoticon;
        this.displayName = displayName;
        this.rankRequired = rankRequired;
    }

	/**
	 * Gets first with permission.
	 *
	 * @param rank the rank
	 * @return the first with permission
	 */
	public static CommandCategory getFirstWithPermission(SimpleRank rank) {
        if (rank == null) {
            return INFORMATIVE;
        }
        for (CommandCategory category : values()) {
            if (rank.isAtLeast(category.getRankRequired())) {
                return category;
            }
        }
        return INFORMATIVE;
    }

	/**
	 * From package command category.
	 *
	 * @param packageName the package name
	 * @return the command category
	 */
	public static CommandCategory fromPackage(String packageName) {
        if (packageName != null) {
            for (CommandCategory cc : values()) {
                if (packageName.equalsIgnoreCase(cc.packageName)) {
                    return cc;
                }
            }
        }
        return UNKNOWN;
    }

	/**
	 * Gets display name.
	 *
	 * @return the display name
	 */
	public String getDisplayName() {
        return displayName;
    }

	/**
	 * Gets package name.
	 *
	 * @return the package name
	 */
	public String getPackageName() {
        return packageName;
    }

	/**
	 * Gets emoticon.
	 *
	 * @return the emoticon
	 */
	public String getEmoticon() {
        return emoticon;
    }

	/**
	 * Gets rank required.
	 *
	 * @return the rank required
	 */
	public SimpleRank getRankRequired() {
        return rankRequired;
    }
}