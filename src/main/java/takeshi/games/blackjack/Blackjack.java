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

import java.util.ArrayList;
import java.util.Collections;

import takeshi.games.card.Card;

/**
 * The type Blackjack.
 */
public class Blackjack {

	private final String playerMention;
	private BlackJackHand dealerHand;
	private BlackJackHand playerHand;
	private ArrayList<Card> deck;
	private boolean gameInProgress = true;
	private boolean playerStands = false;

	/**
	 * Instantiates a new Blackjack.
	 *
	 * @param playerMention the player mention
	 */
	public Blackjack(String playerMention) {
		this.playerMention = playerMention;

		resetGame();
	}

	/**
	 * Is the game still going?
	 *
	 * @return gamestatus boolean
	 */
	public boolean isInProgress() {
		return gameInProgress;
	}

	/**
	 * Player is standing boolean.
	 *
	 * @return the boolean
	 */
	public boolean playerIsStanding() {
		return playerStands;
	}

	/**
	 * Print player hand string.
	 *
	 * @return the string
	 */
	public String printPlayerHand() {
		return playerHand.printHand();
	}

	/**
	 * Gets player value.
	 *
	 * @return the player value
	 */
	public int getPlayerValue() {
		return playerHand.getValue();
	}

	/**
	 * Gets dealer value.
	 *
	 * @return the dealer value
	 */
	public int getDealerValue() {
		return dealerHand.getValue();
	}

	private Card drawCard() {
		return deck.remove(0);
	}

	/**
	 * Hit.
	 */
	public void hit() {
		if (playerStands) {
			return;
		}
		if (playerHand.getValue() == 0) {
			playerHand.add(drawCard());
		}
		playerHand.add(drawCard());
		if (dealerHand.getValue() == 0) {
			dealerHand.add(drawCard());
		}
		if (getPlayerValue() > 21) {
			gameInProgress = false;
		}
	}

	/**
	 * Dealer hit boolean.
	 *
	 * @return the boolean
	 */
	public boolean dealerHit() {
		if (getPlayerValue() <= 21 && getDealerValue() < 21 && getDealerValue() <= getPlayerValue()) {
			dealerHand.add(drawCard());
			return true;
		}
		gameInProgress = false;
		return false;
	}

	/**
	 * Stand.
	 */
	public void stand() {
		playerStands = true;
	}

	/**
	 * Reset game.
	 */
	public void resetGame() {

		dealerHand = new BlackJackHand();
		playerHand = new BlackJackHand();
		deck = Card.newDeck();
		Collections.shuffle(deck);
		gameInProgress = true;
		playerStands = false;
	}

	@Override
	public String toString() {
		StringBuilder game = new StringBuilder("Blackjack game:\n");
		game.append(String.format("Dealers hand (%s):\n", getDealerValue()));
		game.append(dealerHand.printHand()).append("\n");
		game.append("\n");
		game.append(String.format("%s's hand (%s):\n", playerMention, getPlayerValue()));
		game.append(playerHand.printHand()).append("\n");
		if (getPlayerValue() > 21) {
			game.append("**Bust!** I win, better luck next time.").append("\n");
		} else if (!gameInProgress) {
			game.append("\n");
			if (getPlayerValue() == getDealerValue()) {
				game.append("Looks like it ended in a draw");
			} else if (getPlayerValue() > getDealerValue() || getDealerValue() > 21) {
				game.append("Alright you win this one.");
			} else {
				game.append("Yey! I win");
			}
		}
		return game.toString();
	}
}
