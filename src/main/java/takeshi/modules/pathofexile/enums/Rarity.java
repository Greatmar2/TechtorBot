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

package takeshi.modules.pathofexile.enums;

/**
 * Created on 2-9-2016
 */
public enum Rarity {
	/**
	 * Common rarity.
	 */
	COMMON("Common"),
	/**
	 * Uncommon rarity.
	 */
	UNCOMMON("Uncommon"),
	/**
	 * Rare rarity.
	 */
	RARE("Rare"),
	/**
	 * Unique rarity.
	 */
	UNIQUE("Unique"),
	/**
	 * Unknown rarity.
	 */
	UNKNOWN("Unknown");

    private final String displayName;

    Rarity(String displayName) {

        this.displayName = displayName;
    }

	/**
	 * From string rarity.
	 *
	 * @param rarityName the rarity name
	 * @return the rarity
	 */
	public static Rarity fromString(String rarityName) {
        if (rarityName != null) {
            for (Rarity rarity : values()) {
                if (rarityName.equalsIgnoreCase(rarity.displayName)) {
                    return rarity;
                }
            }
        }
        return UNKNOWN;
    }
}
