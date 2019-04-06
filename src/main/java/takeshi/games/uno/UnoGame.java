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
import java.util.List;

import net.dv8tion.jda.core.entities.User;
import takeshi.games.meta.AbstractGame;

public class UnoGame extends AbstractGame<UnoTurn> {
	// This will hook into the game system and keep track of the state of the game
	private UnoHand board;
	private UnoHand discard;
	private List<UnoHand> hands;
	private boolean canChallenge;
	private boolean canUno;

	public UnoGame() {
		reset();
	}

	@Override
	public String getCodeName() {
		return "uno";
	}

	@Override
	public String[] getReactions() {
		return new String[] { "13" };
	}

	@Override
	public String[] getReactions(User player) {
		List<String> reactions = new ArrayList<String>();
		UnoHand hand = hands.get(getActivePlayerIndex());
		int remainingHandSize = hand.getSize() - (hand.page * 10);
		int end = Math.min(remainingHandSize, 10);

		for (int i = 1; i < end; i++) {
			reactions.add("" + i);
		}

		if (hand.page > 0) {
			reactions.add("11");
		}
		if (remainingHandSize > 10) {
			reactions.add("12");
		}
		if (canChallenge) {
			reactions.add("13");
		}

		String[] retArr = new String[reactions.size()];

		reactions.toArray(retArr);

		return retArr;

	}

	@Override
	public boolean shouldClearReactionsEachTurn() {
		return true;
	}

	@Override
	public String getFullname() {
		return "Uno";
	}

	@Override
	public int getTotalPlayers() {
		return hands.size();
	}

	@Override
	protected boolean isTheGameOver() {
		if (hands.get(getActivePlayerIndex()).getSize() == 0) {
			setWinner(getActivePlayerIndex());
			return true;
		}
		return false;
	}

	@Override
	public boolean isValidMove(User player, UnoTurn turnInfo) {
		if (turnInfo.getAction() > 10) {

		} else {
			UnoHand thisHand = hands.get(getActivePlayerIndex());
			if (turnInfo.getAction() <= thisHand.getSize()) {
				UnoCard discardTop = discard.getLast();
				UnoCard thisCard = thisHand.getCard(turnInfo.getAction());
				if (discardTop.color == thisCard.color || discardTop.value == thisCard.value || thisCard.color == Color.BLACK) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	protected void doPlayerMove(User player, UnoTurn turnInfo) {
		// TODO Auto-generated method stub

	}
	// Will manage the state of the game

	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder("A Uno game.\n");

		return ret.toString();
	}
}
