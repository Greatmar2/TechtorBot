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

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import takeshi.util.Emojibet;

/**
 * The type Uno hand.
 */
public class UnoHand {
	// This will keep track of players' hands, the deck and the played cards.

	/**
	 * The Page. Used for when there are more than 10 cards in the hand.
	 */
	public int page;
	User player;
	Message message;
	private List<UnoCard> hand;

	/**
	 * Instantiates a new Uno hand without any values.
	 */
	UnoHand() {
		player = null;
		message = null;
		hand = new ArrayList<UnoCard>();
		page = 0;
	}

	/**
	 * Instantiates a new Uno hand.
	 *
	 * @param player  the player
	 * @param message the message
	 */
	UnoHand(User player, Message message) {
		this.player = player;
		this.message = message;
		hand = new ArrayList<UnoCard>();
		page = 0;
	}

	/**
	 * Instantiates a new Uno hand.
	 *
	 * @param player  the player
	 * @param message the message
	 * @param hand    the hand
	 */
	public UnoHand(User player, Message message, List<UnoCard> hand) {
		this.player = player;
		this.message = message;
		this.hand = hand;
		page = 0;
	}

	private void updateMessage() {
		if (message == null)
			return;
		int numMessagesSince = message.getChannel().getHistoryAfter(message.getId(), 11).complete().size();
		if (numMessagesSince > 10) {
			message = message.getChannel().sendMessage(toString()).complete();
		} else {
			message.editMessage(toString()).queue();
			// return msg;
		}
	}

	/**
	 * Gets hand.
	 *
	 * @return the hand
	 */
	public List<UnoCard> getHand() {
		return hand;
	}

	/**
	 * Gets last.
	 *
	 * @return the last
	 */
	UnoCard getLastCard() {
		return hand.get(hand.size() - 1);
	}

	/**
	 * Gets card.
	 *
	 * @param index the index
	 * @return the card
	 */
	UnoCard getCard(int index) {
		return getCardAbs(index + (page * 10));
	}

	UnoCard getCardAbs(int index) {
//		return hand.get(index);
		if (index < hand.size())
			return hand.get(index);
		else
			return null;
	}

	/**
	 * Gets size.
	 *
	 * @return the size
	 */
	public int getSize() {
		return hand.size();
	}

	/**
	 * Remove card uno card.
	 *
	 * @param index the index
	 * @return the uno card
	 */
	UnoCard removeCard(int index) {
		return removeCardAbs(index + (page * 10));
	}

	/**
	 * Remove card.
	 *
	 * @param card the card
	 */
	public void removeCard(UnoCard card) {
		hand.remove(card);
		updateMessage();
		/*
		 * for (int i = 0; i < hand.size(); i++) { UnoCard checkCard = hand.get(i);
		 * if(checkCard.equals(card)) { } }
		 */
	}

	/**
	 * Remove card abs uno card.
	 *
	 * @param index the index
	 * @return the uno card
	 */
	public UnoCard removeCardAbs(int index) {
		UnoCard card = getCardAbs(index);
		hand.remove(index);
		updateMessage();
		return card;
	}

	/**
	 * Add card.
	 *
	 * @param card the card
	 */
	void addCard(UnoCard card) {
		hand.add(card);
		sortHand();
		updateMessage();
	}

	/**
	 * Add card.
	 *
	 * @param card the card
	 */
	void addCard(UnoCard card, boolean sort) {
		hand.add(card);
		if (sort)
			sortHand();
	}

	private void sortHand() {
		// Bubble sort: O(n^2) worst case (very bad), but also very good for sets that
		// are already or almost sorted.
		boolean sorted = false;
		for (int i = 1; !sorted && i < hand.size(); i++) {
			sorted = true;
			for (int j = 0; j < (hand.size() - i); j++) {
				int colComp = hand.get(j).color.compareTo(hand.get(j + 1).color);
				if (colComp > 0 || (colComp == 0 && hand.get(j).value.compareTo(hand.get(j + 1).value) > 0)) {
					UnoCard tempCard = hand.get(j);
					hand.set(j, hand.get(j + 1));
					hand.set(j + 1, tempCard);
					sorted = false;
				}
			}
		}

		// Sort by TNT Dragon - sorts by color only.
		// This will track where the last card of each color/value is
//		int[] colorPosition = new int[4];
//		int[] valuePosition = new int[13];
//		Arrays.fill(colorPosition, -1);
//		Arrays.fill(valuePosition, -1);
//		// card & number sort order
//		Color[] colorOrder = Color.values();
//		Value[] valueOrder = Value.values();
//		// now we iterate every card to order the colors
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

	/**
	 * Compare int.
	 *
	 * @param c1 the c 1
	 * @param c2 the c 2
	 * @return the int
	 */
	public int compare(UnoCard c1, UnoCard c2) {
		int colComp = c1.color.compareTo(c2.color);
		if (colComp != 0) {
			return colComp;
		} else {
			return c1.value.compareTo(c2.value);
		}
	}

	/**
	 * Shuffle hand.
	 */
	void shuffleHand() {
		Collections.shuffle(hand);
	}

	/**
	 * Is wild draw legal boolean.
	 *
	 * @param playedOnCard the played on card
	 * @return the boolean
	 */
	/*
	 * Returns whether the card that the wild draw was played on was legal to be
	 * played.
	 */
	boolean isWildDrawLegal(UnoCard playedOnCard) {
		for (UnoCard card : hand) {
			if (card.color == playedOnCard.getCurrentColor())
				return false;
		}
		return true;
	}

	/**
	 * Gets text of Uno hand.
	 *
	 * @return the Uno hand
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("Your Uno hand is:\n");
		for (UnoCard card : hand) {
			builder.append(card.value);
			builder.append(" ");
		}
		builder.append(" <- values\n");
		for (UnoCard card : hand) {
			builder.append(card.getCurrentColor());
			builder.append(" ");
		}
		builder.append(" <- colors\n");
		for (int i = 1; i <= hand.size(); i++) {
			if (i > (10 * page) && i <= (10 * (page + 1))) {
				// builder.append(Misc.numberToEmote(i - (10 * page)));
				builder.append(Emojibet.getEmojiFor(UnoGame.commandToReaction(i - (10 * page))));
				builder.append(" ");
			} else {
				builder.append(":stop_button: ");
			}
		}
		builder.append(" <- reactions");
		return builder.toString();
	}
}
