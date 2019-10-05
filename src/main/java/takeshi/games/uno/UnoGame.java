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

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import takeshi.games.meta.AbstractGame;
import takeshi.games.meta.GameState;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Uno game.
 */
public class UnoGame extends AbstractGame<UnoTurn> {
	private static final String[] specialCommands = {"left", "right", "!", "red", "yellow", "green", "blue"};
	// This will hook into the game system and keep track of the state of the game
	private UnoHand deck;
	private UnoHand discard;
	private UnoHand[] hands;
	private UnoCard[] wildDrawCards = new UnoCard[4];
	private boolean canChallenge; // Indicates whether current player can challenge the previous' use of a +4
	private int canUno; // Indicates whether the previous player should call uno. Players can press the
	// !. Only first to click is counted.
	private boolean reverse;
	private boolean canSelectColor;
	private boolean playableCheck;
	private String lastTurnDesc;
	//Keeps track of special commands. All are triggered by a game command greater than 10.

	/**
	 * Instantiates a new Uno game.
	 */
	public UnoGame() {
		reset(2);
	}

	/**
	 * Instantiates a new Uno game.
	 *
	 * @param totalPlayers the total players
	 */
	public UnoGame(int totalPlayers) {
		reset(totalPlayers);
	}

	/**
	 * Reaction to command int.
	 *
	 * @param reaction the reaction
	 * @return the int
	 */
	static int reactionToCommand(String reaction) {
		//System.out.println("Trying to convert " + reaction + " to command");
		try {
			return Integer.parseInt(reaction);
		} catch (NumberFormatException ex) {
			//String text = Emojibet.getTextFor(reaction);
			if (reaction.length() <= 2) {
				char chr = reaction.toLowerCase().charAt(0);
				if (chr >= 'a' && chr <= 'j') {
					//System.out.println("Checking '" + chr + "', got '" + chr + "' - 'a' = " + (chr - 'a'));
					return chr - 'a';
				}
			}
			for (int i = 0; i < specialCommands.length; i++) {
				if (specialCommands[i].equalsIgnoreCase(reaction)) {
					return i + 11;
				}
			}
		}
		System.out.println("Could not find command for " + reaction);
		return -1;
	}

	/**
	 * Command to reaction string.
	 *
	 * @param command the command
	 * @return the string
	 */
	static String commandToReaction(int command) {
		//System.out.println("Converting " + command + " to reaction");
		//if (command <= 10) return Emojibet.getEmojiFor(((char) ('a' + (command - 1))) + "");
		if (command <= 10) return ((char) ('a' + (command - 1))) + "";
		else if (command < (specialCommands.length + 11)) return specialCommands[command - 11];
		else return "?";
	}

	/*
	 * Set up the game
	 */
	@Override
	public void reset(int playerCount) {
		super.reset(playerCount);
		deck = new UnoHand();
		discard = new UnoHand();
		canChallenge = false;
		canUno = -1;
		reverse = false;
		canSelectColor = false;
		playableCheck = false;

		// Build deck
		for (int i = Color.RED.ordinal(); i <= Color.BLUE.ordinal(); i++) { // Loop through each normal color
			for (int j = Value.ZERO.ordinal(); j <= Value.ONE.ordinal(); j++) { // Only add the zeroes once
				for (int k = j; k <= Value.DRAW.ordinal(); k++) {
					deck.addCard(new UnoCard(Color.values()[i], Value.values()[k]), false);
				}
			}
		}
		// Add wild (black) cards
		for (int i = Value.DRAW.ordinal(); i <= Value.WILD.ordinal(); i++) {
			for (int j = 0; j < 4; j++) {
				deck.addCard(new UnoCard(Color.BLACK, Value.values()[i]), false);
			}
		}

		// Shuffle deck
		deck.shuffleHand();

		// Deal cards
		hands = new UnoHand[getTotalPlayers()];
		for (int i = 0; i < getTotalPlayers(); i++) {
			hands[i] = new UnoHand();
			for (int j = 0; j < 7; j++) {
				hands[i].addCard(drawCard());
			}
		}

		// Place first card
		UnoCard startCard = drawCard();
		while (startCard.value == Value.DRAW && startCard.color == Color.BLACK) { //Make sure to not start on a wild draw four
			deck.addCard(startCard, false);
			deck.shuffleHand();
			startCard = drawCard();
		}

		discard.addCard(startCard, false);

		lastTurnDesc = "The game begins.";
	}

	@Override
	protected void startGame() {
		super.startGame();
		for (int i = 0; i < getTotalPlayers(); i++) {
			User player = getPlayer(i);
			PrivateChannel channel = player.openPrivateChannel().complete();
			Message msg = channel.sendMessage("I will update this message with your current hand as you play Uno.\n" + hands[i]).complete();
			hands[i].player = player;
			hands[i].message = msg;
		}

		UnoCard startCard = discard.getCard(0);
		switch (startCard.value) {
			case DRAW:
				hands[getActivePlayerIndex()].addCard(drawCard());
				hands[getActivePlayerIndex()].addCard(drawCard());
				break;
			case WILD:
				canSelectColor = true;
				break;
			case SKIP:
				adjustTurn(1);
				break;
			case REVERSE:
				reverse = true;
				break;
		}
		checkPlayerHasMove();
	}

	@Override
	public boolean playTurn(User player, UnoTurn turnInfo) {
		boolean ret = super.playTurn(player, turnInfo);
		while (!checkPlayerHasMove()) ;
		return ret;
	}

	/**
	 * Checks if the active player has a move available. If not, skip their turn.
	 */
	private boolean checkPlayerHasMove() {
		boolean hasValidMove = false;
		if (!canSelectColor) {
			User player = getActivePlayer();
			UnoHand hand = getHand(getActivePlayerIndex());
			for (int i = 0; !hasValidMove && i < hand.getSize(); i++) {
				if (isCardPlayable(hand.getCardAbs(i), discard.getLastCard())) hasValidMove = true;
			}
			if (!hasValidMove) {
				playableCheck = true;
				UnoCard card = drawCard();
				lastTurnDesc += "\n**" + player.getName() + "** had no playable cards, so they drew one.";

				hand.addCard(card);//, false);
				/*if (isCardPlayable(card, discard.getLastCard())) {
					playCard(card, true);
				} else {
					hand.addCard(card);
				}*/
				//playTurn(player, new UnoTurn(hand.getSize() - 1));
				for (int i = 0; !hasValidMove && i < hand.getSize(); i++) {
					if (isCardPlayable(hand.getCardAbs(i), discard.getLastCard())) hasValidMove = true;
				}
				if (!hasValidMove) adjustTurn(1);
				playableCheck = false;
			}
		} else hasValidMove = true;
		return hasValidMove;
	}

	@Override
	public String getCodeName() {
		return "uno";
	}

	@Override
	public String[] getReactions() {
		//User player = getActivePlayer();
		List<Integer> reactions = new ArrayList<>();
		if (!canSelectColor) {
			UnoHand hand = hands[getActivePlayerIndex()];
			int remainingHandSize = hand.getSize() - (hand.page * 10);
			int end = Math.min(hand.getSize(), 10);//Math.min(remainingHandSize, 10); - For time efficiency, don't remove letter emotes when paging

			for (int i = 1; i <= end; i++) { //Add letter controls
				reactions.add(i);
			}

			if (hand.page > 0) {
				reactions.add(11);
			}
			if (remainingHandSize > 10) {
				reactions.add(12);
			}
			if (canChallenge || canUno >= 0) {
				reactions.add(13);
			}
		} else {
			for (int i = 14; i <= 17; i++) {
				reactions.add(i);
			}
		}

		String[] retArr = new String[reactions.size()];

		for (int i = 0; i < reactions.size(); i++) {
			retArr[i] = commandToReaction(reactions.get(i));
		}


		return retArr;
	}

	@Override
	public boolean shouldUpdateReactionsEachTurn() {
		return true;
	}

	@Override
	public String getFullname() {
		return "Uno";
	}

	@Override
	public int getMaxPlayers() {
		return 10;
	}

	@Override
	protected boolean isTheGameOver() {
		if (hands[getActivePlayerIndex()].getSize() == 0) {
			setWinner(getActivePlayerIndex());
			return true;
		}
		return false;
	}

	@Override
	public boolean isTurnOf(User player) {
		//System.out.println("Checking if turn of " + player.getName() + ", is turn of " + getActivePlayer().getName());
		if (canUno >= 0) return true;
		return super.isTurnOf(player);
	}

	@Override
	public boolean isValidMove(User player, UnoTurn turnInfo) {
		if (!super.isTurnOf(player)) {
			return canUno >= 0;
		}
		if (canChallenge) return true;
		UnoHand hand = hands[getActivePlayerIndex()];
		//System.out.println("Checking if " + turnInfo.getAction() + " is a valid move.");
		if (playableCheck && turnInfo.getAction() == -1) {
			playableCheck = false;
			return true;
		}
		if (turnInfo.getAction() > 10) { // Special actions (change page, challenge).
			if (turnInfo.getAction() == 11) {
				return hand.page > 0;
			} else if (turnInfo.getAction() == 12) {
				return (hand.getSize() - (10 * hand.page)) > 0;
			} else if (turnInfo.getAction() == 13) {
				return canChallenge || canUno >= 0;
			} else if (turnInfo.getAction() >= 14 && turnInfo.getAction() <= 17) {
				return canSelectColor;
			}
		} else { // Play card from hand
			if (turnInfo.getAction() <= hand.getSize()) {
				UnoCard discardTop = discard.getLastCard();
				UnoCard thisCard = hand.getCard(turnInfo.getAction());
				return isCardPlayable(thisCard, discardTop);
			}
		}

		return false;
	}

	/**
	 * Determines whether a card can be played on top of another card.
	 *
	 * @param playedCard the card that is being played
	 * @param facingCard the card at the top of the discard pile
	 * @return can the card be played
	 */
	private boolean isCardPlayable(UnoCard playedCard, UnoCard facingCard) {
		return playedCard != null && facingCard != null && (facingCard.getCurrentColor() == playedCard.color || (facingCard.value == playedCard.value && facingCard.color != Color.BLACK)
				|| playedCard.color == Color.BLACK);
	}

	@Override
	protected void doPlayerMove(User player, UnoTurn turnInfo) {
		if (canUno >= 0 && turnInfo.getAction() == 13) {
			User unoPlayer = getPlayer(canUno);
			UnoHand hand = getHand(canUno);
			lastTurnDesc += "\n**" + player.getName() + "** calls Uno!";
			if (player.getIdLong() != unoPlayer.getIdLong()) {
				lastTurnDesc += " **" + unoPlayer.getName() + "** draws two cards.";
				hand.addCard(drawCard());
				hand.addCard(drawCard());
			}
			canUno = -1;
		}

		if (!super.isTurnOf(player)) {
			System.out.println("Not turn of " + player.getName());
			return;
		}

		UnoHand hand = hands[getActivePlayerIndex()];
		String playerName = getActivePlayer().getName();
		String previousName = getPlayer(getActivePlayerIndex() - getIndexMod()).getName();
		String nextName = getPlayer(getActivePlayerIndex() + getIndexMod()).getName();

		if (canSelectColor && turnInfo.getAction() >= 14 && turnInfo.getAction() <= 17) { // If player can select color, it's the only thing they can do
			Color selectedCol = Color.values()[turnInfo.getAction() - 14];
			discard.getLastCard().selCol = selectedCol;
			//if (discard.getLastCard().value == Value.DRAW) adjustTurn(1);
			canSelectColor = false;
			lastTurnDesc += "\nThey select the color " + selectedCol;
			if (canChallenge) lastTurnDesc += "\n**" + nextName + "** can challenge **" + playerName
					+ "** with:grey_exclamation:if they think **" + playerName + "** actually has a " + discard.getCardAbs(discard.getSize() - 2).color + " card or press another button to not challenge.";
		} else if (canChallenge) { //If the player can challenge, they either challenge or press anything else to not challenge.
			if (turnInfo.getAction() == 13) {
				UnoHand previousHand = getHand(getActivePlayerIndex() - getIndexMod());
				if (previousHand.isWildDrawLegal(discard.getCard(discard.getSize() - 2))) {// Playing the draw four was legal
					for (int i = 0; i < 2; i++) {
						hand.addCard(drawCard());
					}
					lastTurnDesc = "**" + playerName + "** challenges **" + previousName + "**'s use of a *wild draw four*, but it was played legally.\n**"
							+ playerName + "** picks up two extra cards.";
				} else {// Playing the draw four was illegal
					for (UnoCard card : wildDrawCards) {
						hand.removeCard(card);
						previousHand.addCard(card);
						lastTurnDesc = "**" + playerName + "** challenges **" + previousName
								+ "**'s use of a *wild draw four*. It was played illegally!\n**" + previousName + "** gets the four cards instead.";
					}
				}
			}
			canChallenge = false;
		} else if (turnInfo.getAction() >= 0) {
			if (turnInfo.getAction() > 10) { // Special Action
				if (turnInfo.getAction() == 11) {
					hand.page--;
					adjustTurn(-1);
				} else if (turnInfo.getAction() == 12) {
					hand.page++;
					adjustTurn(-1);
				} /*else if (turnInfo.getAction() == 13) { //Shouldn't get here
					lastTurnDesc += "Error, special action not consumed. Contact Mar.";
					if (canUno) { // Player calls Uno
						// handle player check
						// lastTurnDesc
						canUno = false;
					} else if (canChallenge) { // Player challenges previous ones' use of a wild draw four
						UnoHand previousHand = getHand(getActivePlayerIndex() - getIndexMod());
						if (previousHand.isWildDrawLegal(discard.getCard(discard.getSize() - 2))) {// Playing the draw four was legal
							for (int i = 0; i < 2; i++) {
								hand.addCard(drawCard());
							}
							lastTurnDesc = "**" + playerName + "** challenges **" + previousName + "**'s use of a *wild draw four*, but it was played legally.\n**"
									+ playerName + "** picks up two extra cards.";
						} else {// Playing the draw four was illegal
							for (UnoCard card : wildDrawCards) {
								hand.removeCard(card);
								previousHand.addCard(card);
								lastTurnDesc = "**" + playerName + "** challenges **" + previousName
										+ "**'s use of a *wild draw four*. It was played illegally!\n**" + previousName + "** gets the four cards instead.";
							}
						}
						canChallenge = false;
					}
				} else if (turnInfo.getAction() >= 14 && turnInfo.getAction() <= 17) { // Select color
					Color selectedCol = Color.values()[turnInfo.getAction() - 14];
					discard.getLastCard().selCol = selectedCol;
					//if (discard.getLastCard().value == Value.DRAW) adjustTurn(1);
					canSelectColor = false;
					lastTurnDesc += "\nThey select the color " + selectedCol;
					if (canChallenge) lastTurnDesc += "\n**" + nextName + "** can challenge **" + playerName
							+ "** with:grey_exclamation:if they think **" + playerName + "** actually has a " + discard.getCardAbs(discard.getSize() - 2).color + " card or press another button to not challenge.";
				}*/
			} else { // Play card from hand
				if (canUno >= 0) canUno = -1;
				if (canChallenge) canChallenge = false;
				playCard(hand.removeCard(turnInfo.getAction()));
				if (hand.getSize() == 1) {
					canUno = getActivePlayerIndex();
				}
				/*Message msg = updateMessage(hand.message, hand.toString());
				if (msg.getIdLong() != hand.message.getIdLong()) {
					hand.message = msg;
				}*/
			}

//			if (canSelectColor) canSelectColor = false;
//			if (canUno) canUno = false;
//			if (canChallenge) canChallenge = false;
		}
	}

	private void playCard(UnoCard playedCard) {
		String playerName = getActivePlayer().getName();
		String previousName = getPlayer(getActivePlayerIndex() - getIndexMod()).getName();
		String nextName = getPlayer(getActivePlayerIndex() + getIndexMod()).getName();
		switch (playedCard.value) {
			case SKIP:
				adjustTurn(1);
				lastTurnDesc = "**" + playerName + "** *skips* the turn of **" + nextName + "**.";
				break;
			case REVERSE:
				if (getTotalPlayers() > 2) {
					//System.out.println("Reversing, reverse = " + reverse);
					//reverse = !reverse;
					setReverse(!reverse);
					//System.out.println("Reversed, reverse = " + reverse);
					lastTurnDesc = "**" + playerName + "** *reverses* the turn order! **" + previousName + "** plays next.";
				} else {
					adjustTurn(1);
					lastTurnDesc = "**" + playerName + "** *skips* the turn of **" + nextName + "**.";
				}
				break;
			case DRAW:
				UnoHand targetHand = getHand(getActivePlayerIndex() + getIndexMod());
				lastTurnDesc = "**" + playerName + "** plays a ";
				if (playedCard.color == Color.BLACK) {
					lastTurnDesc += "*wild draw four* card. **" + nextName + "** picks up four cards.";
					for (int i = 0; i < 4; i++) {
						UnoCard drawnCard = drawCard();
						targetHand.addCard(drawnCard);
						wildDrawCards[i] = drawnCard;
					}
					canSelectColor = true;
					canChallenge = true;
					adjustTurn(-1);
				} else {
					lastTurnDesc += "*draw two* card. **" + nextName + "** picks up two cards.";
					for (int i = 0; i < 2; i++) {
						targetHand.addCard(drawCard());
					}
					adjustTurn(1);
				}
				;
				break;
			case WILD:
				adjustTurn(-1);
				canSelectColor = true;
				lastTurnDesc = "**" + playerName + "** plays a *wild card*.";
				break;
			default:
				lastTurnDesc = "**" + playerName + "** plays a normal card.";
				break;
		}
		discard.addCard(playedCard, false);
	}

	private UnoCard drawCard() {
		if (deck.getSize() > 1) {
			return deck.removeCard(0);
		} else {
			for (int i = 0; i < discard.getSize() - 1; i++) {
				UnoCard card = discard.removeCard(0);
				card.selCol = null;
				deck.addCard(card, false);
			}
			deck.shuffleHand();
			return deck.removeCard(0);
		}
	}

	private UnoHand getHand(int index) {
		return hands[index < 0 ? index + getTotalPlayers() : index % getTotalPlayers()];
	}

	@Override
	public String toString() {
		String ret = "An Uno game.\n\n" +
				discard.getLastCard() +
				"\n\n" + lastTurnDesc;

		if (getGameState().equals(GameState.OVER)) {
			if (getWinnerIndex() == getTotalPlayers()) {
				ret += "\nIt is over! And its a draw!";
			} else {
				ret += "\nIt is over! The winner is " + getPlayer(getWinnerIndex()).getAsMention();
			}
		} else {
			ret += "\nIt is " + getActivePlayer().getAsMention() + "'s turn.";
		}

		return ret;
	}

	@Override
	public boolean couldAddReactions() {
		return GameState.READY.equals(getGameState());
	}
}
