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

package takeshi.games.slotmachine;

/**
 * The enum Slot.
 */
public enum Slot {
	/**
	 * Seven slot.
	 */
	SEVEN("Seven", ":seven:", 30, 4, 1),
	/**
	 * Crown slot.
	 */
	CROWN("Crown", ":crown:", 10),
	/**
	 * Bell slot.
	 */
	BELL("Bell", ":bell:", 10),
	/**
	 * Bar slot.
	 */
	BAR("Bar", ":chocolate_bar:", 10),
	/**
	 * Cherry slot.
	 */
	CHERRY("Cherry", ":cherries:", 10),
	/**
	 * Melon slot.
	 */
	MELON("Melon", ":melon:", 10);

    private final String name;
    private final String emote;
    private final int triplePayout;
    private final int doublePayout;
    private final int singlePayout;

    Slot(String name, String emote, int triplePayout) {
        this(name, emote, triplePayout, 0, 0);
    }

    Slot(String name, String emote, int triplePayout, int doublePayout) {
        this(name, emote, triplePayout, doublePayout, 0);
    }

    Slot(String name, String emote, int triplePayout, int doublePayout, int singlePayout) {

        this.name = name;
        this.emote = emote;
        this.triplePayout = triplePayout;
        this.doublePayout = doublePayout;
        this.singlePayout = singlePayout;
    }

	/**
	 * Gets triple payout.
	 *
	 * @return the triple payout
	 */
	public int getTriplePayout() {
        return triplePayout;
    }

	/**
	 * Gets emote.
	 *
	 * @return the emote
	 */
	public String getEmote() {
        return emote;
    }

	/**
	 * Gets name.
	 *
	 * @return the name
	 */
	public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return emote;
    }

	/**
	 * Gets double payout.
	 *
	 * @return the double payout
	 */
	public int getDoublePayout() {
        return doublePayout;
    }

	/**
	 * Gets single payout.
	 *
	 * @return the single payout
	 */
	public int getSinglePayout() {
        return singlePayout;
    }
}
