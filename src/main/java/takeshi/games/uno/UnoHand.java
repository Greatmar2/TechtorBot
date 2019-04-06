/*
 * Copyright 2019 github.com/greatmar2
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

package takeshi.games.uno;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UnoHand {
	// This will keep track of players' hands, the deck and the played cards.

	public final long playerID;
	public final String playerName;
	public int page;
	public List<UnoCard> hand;

	public UnoHand(long playerID, String playerName) {
		this.playerID = playerID;
		this.playerName = playerName;
		hand = new ArrayList<UnoCard>();
		page = 0;
	}

	public UnoHand(long playerID, String playerName, List<UnoCard> hand) {
		this.playerID = playerID;
		this.playerName = playerName;
		this.hand = hand;
		page = 0;
	}

	public List<UnoCard> getHand() {
		return hand;
	}

	public UnoCard getLast() {
		return hand.get(hand.size() - 1);
	}

	public UnoCard getCard(int index) {
		return hand.get(index + (page * 10));
	}

	public UnoCard getCardAbs(int index) {
		return hand.get(index);
	}

	public int getSize() {
		return hand.size();
	}

	public void removeCard(int index) {
		hand.remove(index + (page * 10));
	}

	public void removeCardAbs(int index) {
		hand.remove(index);
	}

	public void addCard(UnoCard card) {
		hand.add(card);
	}

	public void sortHand() {
		// Bubble sort: O(n^2) worst case (very bad), but also very good for sets that
		// are already or almost sorted.
		boolean sorted = false;
		for (int i = 1; !sorted && i < hand.size(); i++) {
			sorted = true;
			for (int j = 0; j < (i - hand.size()); j++) {
				int colComp = hand.get(j).color.compareTo(hand.get(j + 1).color);
				if (colComp < 0 || (colComp == 0 && hand.get(j).value.compareTo(hand.get(j + 1).value) < 0)) {
					UnoCard tempCard = hand.get(j);
					hand.set(j, hand.get(j + 1));
					hand.set(j + 1, tempCard);
					sorted = false;
				}
			}
		}

		// Sort by TNT Dragon - sorts by color only.
		// This will track where the last card of each color is
//		int[] colorPosition = new int[] { -1, -1, -1, -1 };
//		// card sort order (blacks go to the end automatically)
//		Color[] colorOrder = Color.values();
//		// now we iterate every card
//		for (int i = 0; i < hand.size(); i++) {
//			// and ever color
//			for (int j = 0; j < 4; j++) {
//				// Probably wrong way to check color, but that's what I do
//				if (hand.get(i).color == colorOrder[j]) {
//					// Checks if the previous card was of same color
//					if (colorPosition[j] == i - 1) {
//						// If yes, it updated the position info
//						colorPosition[j]++;
//					} else {
//						// if not, it saves the card after the last card of the color we want
//						UnoCard tempCard = hand.get(colorPosition[j] + 1);
//						// then it sets the card after the last card of our color to the card the loop
//						// is on
//						hand.set(colorPosition[j] + 1, hand.get(i));
//						// set the card we're currently on to the one we saved
//						hand.set(i, tempCard);
//						// push last known position of our color
//						colorPosition[j]++;
//					}
//				}
//			}
//		}
	}

	private int compare(UnoCard c1, UnoCard c2) {
		int colComp = c1.color.compareTo(c2.color);
		if (colComp < 0) {
			return colComp;
		} else {
			return c1.value.compareTo(c2.value);
		}
	}

	public void shuffleHand() {
		Collections.shuffle(hand);
	}
}
