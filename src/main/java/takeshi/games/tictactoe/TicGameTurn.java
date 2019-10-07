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

import takeshi.games.meta.GameTurn;

/**
 * The type Tic game turn.
 */
public class TicGameTurn extends GameTurn {
    private int boardIndex = 0;

	/**
	 * Instantiates a new Tic game turn.
	 */
	public TicGameTurn() {

    }

	/**
	 * Instantiates a new Tic game turn.
	 *
	 * @param boardIndex the board index
	 */
	public TicGameTurn(int boardIndex) {

        this.boardIndex = boardIndex;
    }

	/**
	 * Gets board index.
	 *
	 * @return the board index
	 */
	public int getBoardIndex() {
        return boardIndex;
    }

    @Override
    public boolean parseInput(String input) {
        if (input != null && input.matches("^[1-9]$")) {
            this.boardIndex = Integer.parseInt(input) - 1;
            return true;
        }
        return false;
    }

    @Override
    public String getInputErrorMessage() {
        return "Expecting a numeric input in range 1-9";
    }
}
