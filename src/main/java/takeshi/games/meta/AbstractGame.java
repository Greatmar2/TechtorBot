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

package takeshi.games.meta;

import net.dv8tion.jda.api.entities.User;
import takeshi.main.BotConfig;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.Random;

/**
 * The type Abstract game.
 *
 * @param <turnType> the type parameter
 */
public abstract class AbstractGame<turnType extends GameTurn> {
	private User[] players;
	private boolean reverse;
	private int activePlayerIndex = 0;
	private GameState gameState = GameState.OVER;
	private volatile int winnerIndex = -1;
	private String lastPrefix = BotConfig.BOT_COMMAND_PREFIX;
	private volatile long lastTurnTimestamp = System.currentTimeMillis();

	/**
	 * Is listed boolean.
	 *
	 * @return the boolean
	 */
	public boolean isListed() {
		return true;
	}

	/**
	 * Gets last turn timestamp.
	 *
	 * @return the last turn timestamp
	 */
	public long getLastTurnTimestamp() {
		return lastTurnTimestamp;
	}

	/**
	 * Gets last prefix.
	 *
	 * @return the last prefix
	 */
	protected String getLastPrefix() {
		return lastPrefix;
	}

	/**
	 * Sets last prefix.
	 *
	 * @param prefix the prefix
	 */
	public void setLastPrefix(String prefix) {
		this.lastPrefix = prefix;
	}

	/**
	 * gets a short name of the game, this name is used as input to create a new
	 * game and as an identifier in the database
	 *
	 * @return codeName of the game
	 */
	public abstract String getCodeName();

	/**
	 * Could add reactions boolean.
	 *
	 * @return the boolean
	 */
	public boolean couldAddReactions() {
		return GameState.READY.equals(gameState) && getReactions().length > 0;
	}

	/**
	 * Get reactions string [ ].
	 *
	 * @return the string [ ]
	 */
	public abstract String[] getReactions();

	/**
	 * Should update reactions each turn boolean.
	 *
	 * @return the boolean
	 */
	public abstract boolean shouldUpdateReactionsEachTurn();

	/**
	 * a full version of the name, this is used to display
	 *
	 * @return full game name
	 */
	public abstract String getFullname();

	/**
	 * receives a new instance of turnType
	 *
	 * @return new instance of turnType
	 */
	public final turnType getGameTurnInstance() {
		Class<?> turnTypeClass = (Class<?>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		try {
			return (turnType) turnTypeClass.getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Gets player.
	 *
	 * @param index the index
	 * @return the player
	 */
	protected User getPlayer(int index) {
		return players[index < 0 ? index + getTotalPlayers() : index % getTotalPlayers()];
	}

	/**
	 * Gets active player index.
	 *
	 * @return the active player index
	 */
	protected int getActivePlayerIndex() {
		return activePlayerIndex;
	}

	/**
	 * Gets active player.
	 *
	 * @return the active player
	 */
	protected User getActivePlayer() {
		return players[activePlayerIndex];
	}

	/**
	 * Sets winner.
	 *
	 * @param playerIndex the player index
	 */
	protected void setWinner(int playerIndex) {
		winnerIndex = playerIndex;
	}

	/**
	 * Gets winner index.
	 *
	 * @return the winner index
	 */
	protected int getWinnerIndex() {
		return winnerIndex;
	}

	/**
	 * the total amount of players in a game
	 *
	 * @return total players
	 */
	public int getTotalPlayers() {
		return players.length;
	}

	;

	/**
	 * The maximum amount of players allowed in a game
	 *
	 * @return max players
	 */
	public abstract int getMaxPlayers();

	/**
	 * Resets the game
	 */
	public void reset() {
		reset(2);
	}

	/**
	 * Resets the game
	 *
	 * @param playerCount the player count
	 */
	public void reset(int playerCount) {
		winnerIndex = playerCount;
		players = new User[playerCount];
		for (int i = 0; i < playerCount; i++) {
			players[i] = null;
		}
		gameState = GameState.INITIALIZING;
	}

	/**
	 * Start game.
	 */
	protected void startGame() {
		gameState = GameState.READY;
	}

	/**
	 * Gets game state.
	 *
	 * @return the game state
	 */
	public GameState getGameState() {
		return gameState;
	}

	/**
	 * attempts to play a turn
	 *
	 * @param player   the player
	 * @param turnInfo the details about the move
	 * @return turn successfully played?
	 */
	public boolean playTurn(User player, turnType turnInfo) {
		if (!(gameState.equals(GameState.IN_PROGRESS) || gameState.equals(GameState.READY))) {
			return false;
		}
		if (!isTurnOf(player) || !isValidMove(player, turnInfo)) {
			return false;
		}
		doPlayerMove(player, turnInfo);
		if (isTheGameOver()) {
			gameState = GameState.OVER;
		}
		lastPrefix = turnInfo.getCommandPrefix();
		endTurn();
		lastTurnTimestamp = System.currentTimeMillis();
		return true;
	}

	/**
	 * adds a player to the game
	 *
	 * @param player the player
	 * @return if it added the player to the game or not
	 */
	public final boolean addPlayer(User player) {
		if (!gameState.equals(GameState.INITIALIZING)) {
			return false;
		}
		for (int i = 0; i < getTotalPlayers(); i++) {
			if (players[i] == null) {
				players[i] = player;
				if (i == (getTotalPlayers() - 1)) {
					activePlayerIndex = new Random().nextInt(getTotalPlayers());
					startGame();
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * Sets reverse.
	 *
	 * @param reverse the reverse
	 */
	protected final void setReverse(boolean reverse) {
		this.reverse = reverse;
	}

	/**
	 * shifts the active player index over to the next one
	 */
	private void endTurn() {
		//System.out.println("Ending turn of " + getActivePlayer().getName() + ", reverse = " + reverse);
		activePlayerIndex = (reverse ? activePlayerIndex - 1 : activePlayerIndex + 1) % getTotalPlayers();
		if (activePlayerIndex < 0) activePlayerIndex += getTotalPlayers();
		//System.out.println("Now " + getActivePlayer().getName() + "'s turn");
	}

	/**
	 * Shifts the active player index a custom amount
	 *
	 * @param amount to adjust the index by
	 */
	protected void adjustTurn(int amount) {
		activePlayerIndex = (reverse ? activePlayerIndex - amount : activePlayerIndex + amount) % getTotalPlayers();
		if (activePlayerIndex < 0) activePlayerIndex += getTotalPlayers();
	}

	/**
	 * Gets the amount that the index will be modified by for the next turn.
	 * 1 if the game is going in normal direction, -1 if in reverse.
	 *
	 * @return index mod
	 */
	protected int getIndexMod() {
		return reverse ? -1 : 1;
	}

	/**
	 * checks if the game is still in progress
	 *
	 * @return true if the game is over, false if its still in progress
	 */
	protected abstract boolean isTheGameOver();

	/**
	 * Is turn of boolean.
	 *
	 * @param player to check
	 * @return is it players turn?
	 */
	public boolean isTurnOf(User player) {
		return players[activePlayerIndex].getId().equals(player.getId());
	}

	/**
	 * checks if the attempted move is a valid one
	 *
	 * @param player   the player
	 * @param turnInfo the details about the move
	 * @return is a valid move?
	 */
	public abstract boolean isValidMove(User player, turnType turnInfo);

	/**
	 * play the turn
	 *
	 * @param player   the player
	 * @param turnInfo the details about the move
	 */
	protected abstract void doPlayerMove(User player, turnType turnInfo);

	/**
	 * are we still waiting for more players?
	 *
	 * @return well ?
	 */
	public boolean waitingForPlayer() {
		return gameState.equals(GameState.INITIALIZING);
	}
}
