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

package takeshi.permission;

/**
 * The enum Simple rank.
 */
public enum SimpleRank {
	/**
	 * The Banned user.
	 */
	BANNED_USER("Will be ignored"),
	/**
	 * The Bot.
	 */
	BOT("Will be ignored"),
	/**
	 * The User.
	 */
	USER("Regular user"),
	/**
	 * The Interaction bot.
	 */
	INTERACTION_BOT("Bot can interact"),
	/**
	 * The Guild bot admin.
	 */
	GUILD_BOT_ADMIN("Bot admin for a guild"),
	/**
	 * The Guild admin.
	 */
	GUILD_ADMIN("Admin in a guild"),
	/**
	 * The Guild owner.
	 */
	GUILD_OWNER("Owner of a guild"),
	/**
	 * Contributor simple rank.
	 */
	CONTRIBUTOR("Contributor"),
	/**
	 * The Bot admin.
	 */
	BOT_ADMIN("Bot administrator"),
	/**
	 * The System admin.
	 */
	SYSTEM_ADMIN("System admin"),
	/**
	 * Creator simple rank.
	 */
	CREATOR("Creator");
    private final String description;

    SimpleRank(String description) {
        this.description = description;
    }

	/**
	 * find a rank by name
	 *
	 * @param search the role to search for
	 * @return rank || null
	 */
	public static SimpleRank findRank(String search) {
        for (SimpleRank simpleRank : values()) {
            if (simpleRank.name().equalsIgnoreCase(search)) {
                return simpleRank;
            }
        }
        return null;
    }

	/**
	 * Is at least boolean.
	 *
	 * @param rank the rank
	 * @return the boolean
	 */
	public boolean isAtLeast(SimpleRank rank) {
        return this.ordinal() >= rank.ordinal();
    }

	/**
	 * Is higher than boolean.
	 *
	 * @param rank the rank
	 * @return the boolean
	 */
	public boolean isHigherThan(SimpleRank rank) {
        return this.ordinal() > rank.ordinal();
    }

	/**
	 * Gets description.
	 *
	 * @return the description
	 */
	public String getDescription() {
        return description;
    }
}
