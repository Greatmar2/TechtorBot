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

package takeshi.games.tictactoe;

/**
 * The type Tic tile.
 */
public class TicTile {
    private int player;
    private TileState state;

	/**
	 * Instantiates a new Tic tile.
	 */
	public TicTile() {
        reset();
    }

    private void reset() {
        state = TileState.FREE;
        player = -1;
    }

	/**
	 * Is free boolean.
	 *
	 * @return the boolean
	 */
	public boolean isFree() {
        return state.equals(TileState.FREE);
    }

	/**
	 * Gets state.
	 *
	 * @return the state
	 */
	public TileState getState() {
        return state;
    }

	/**
	 * Sets state.
	 *
	 * @param state the state
	 */
	public void setState(TileState state) {
        this.state = state;
    }

	/**
	 * Gets player.
	 *
	 * @return the player
	 */
	public int getPlayer() {
        return player;
    }

	/**
	 * Sets player.
	 *
	 * @param player the player
	 */
	public void setPlayer(int player) {
        switch (player) {
            case 0:
                this.player = player;
                state = TileState.X;
                break;
            case 1:
                this.player = player;
                state = TileState.O;
                break;
            default:
                this.player = -1;
                state = TileState.FREE;
                break;
        }
    }
}
