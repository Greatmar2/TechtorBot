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

package takeshi.games.blackjack;

import takeshi.games.card.Card;
import takeshi.games.card.CardHand;
import takeshi.games.card.CardRank;

/**
 * The type Black jack hand.
 */
public class BlackJackHand extends CardHand {

	/**
	 * calculates the value of the hand
	 *
	 * @return points value
	 */
	public int getValue() {
        int value = 0;
        int aces = 0;
        for (Card card : cardsInHand) {
            if (card.getRank().equals(CardRank.ACE)) {
                aces++;
            }
            value += card.getRank().getValue();
        }
        while (aces > 0 && value > 21) {
            aces--;
            value -= 10;
        }
        return value;
    }

	/**
	 * Print hand string.
	 *
	 * @return the string
	 */
	public String printHand() {
        StringBuilder hand = new StringBuilder();
        for (Card card : cardsInHand) {
            hand.append(card.toEmote()).append(" ");
        }
        return hand.toString();
    }
}
