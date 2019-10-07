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

import java.util.ArrayList;
import java.util.List;

/**
 * The type Card hand.
 */
public class CardHand {

	/**
	 * The Cards in hand.
	 */
	protected ArrayList<Card> cardsInHand;

	/**
	 * Instantiates a new Card hand.
	 */
	public CardHand() {
        reset();
    }

	/**
	 * Reset.
	 */
	public void reset() {
        cardsInHand = new ArrayList<>();
    }

	/**
	 * Add.
	 *
	 * @param card the card
	 */
	public void add(Card card) {
        cardsInHand.add(card);
    }

	/**
	 * Remove boolean.
	 *
	 * @param card the card
	 * @return the boolean
	 */
	public boolean remove(Card card) {
        return cardsInHand.remove(card);
    }

	/**
	 * Gets hand.
	 *
	 * @return the hand
	 */
	public List<Card> getHand() {
        return cardsInHand;
    }
}