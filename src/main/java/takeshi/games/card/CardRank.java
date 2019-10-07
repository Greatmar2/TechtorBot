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

package takeshi.games.card;

/**
 * The enum Card rank.
 */
public enum CardRank {
	/**
	 * Deuce card rank.
	 */
	DEUCE("two", " 2", 2),
	/**
	 * Three card rank.
	 */
	THREE("tree", " 3", 3),
	/**
	 * Four card rank.
	 */
	FOUR("four", " 4", 4),
	/**
	 * Five card rank.
	 */
	FIVE("five", " 5", 5),
	/**
	 * Six card rank.
	 */
	SIX("six", " 6", 6),
	/**
	 * Seven card rank.
	 */
	SEVEN("seven", " 7", 7),
	/**
	 * Eight card rank.
	 */
	EIGHT("eight", " 8", 8),
	/**
	 * Nine card rank.
	 */
	NINE("nine", " 9", 9),
	/**
	 * Ten card rank.
	 */
	TEN("ten", "10", 10),
	/**
	 * Jack card rank.
	 */
	JACK("jack", " J", 10),
	/**
	 * Queen card rank.
	 */
	QUEEN("queen", " Q", 10),
	/**
	 * King card rank.
	 */
	KING("king", " K", 10),
	/**
	 * Ace card rank.
	 */
	ACE("ace", " A", 11);

    private String cardName;
    private String emoticon;
    private int value;

    CardRank(String cardName, String emoticon, int value) {
        this.cardName = cardName;

        this.emoticon = emoticon;
        this.value = value;
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
	 * Gets display name.
	 *
	 * @return the display name
	 */
	public String getDisplayName() {
        return cardName;
    }

	/**
	 * Gets value.
	 *
	 * @return the value
	 */
	public int getValue() {
        return value;
    }
}
