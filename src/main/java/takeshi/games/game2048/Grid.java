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

package takeshi.games.game2048;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Random;

/**
 * The type Grid.
 */
public class Grid {

    private final int size;
    private final Random rng;
	/**
	 * The Board.
	 */
	public volatile Integer[][] board;

	/**
	 * Instantiates a new Grid.
	 *
	 * @param boardSize the board size
	 */
	public Grid(int boardSize) {
        size = boardSize;
        rng = new Random();
        board = getEmptyBoard();
    }

	/**
	 * Gets score.
	 *
	 * @return the score
	 */
	public int getScore() {
        int sum = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                sum += board[i][j];
            }
        }
        return sum;
    }

	/**
	 * Is board full boolean.
	 *
	 * @return the boolean
	 */
	public boolean isBoardFull() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private ArrayList<Pair<Integer, Integer>> getAvailablePositions() {
        ArrayList<Pair<Integer, Integer>> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == 0) {
                    list.add(Pair.of(i, j));
                }
            }
        }
        return list;
    }

	/**
	 * Add random two.
	 */
	public void addRandomTwo() {
        Pair<Integer, Integer> pos = getRandomFreePosition();
        board[pos.getKey()][pos.getValue()] = 2;
    }

	/**
	 * Can move horizontal boolean.
	 *
	 * @return the boolean
	 */
	public boolean canMoveHorizontal() {
        return canMove(true);
    }

	/**
	 * Can move vertical boolean.
	 *
	 * @return the boolean
	 */
	public boolean canMoveVertical() {
        return canMove(false);

    }

	/**
	 * Can move boolean.
	 *
	 * @param horizontal the horizontal
	 * @return the boolean
	 */
	public boolean canMove(boolean horizontal) {
        for (int i = 0; i < size; i++) {
            int lastVal = -1;
            for (int j = 0; j < size; j++) {
                if (board[horizontal ? i : j][horizontal ? j : i] == 0) {
                    return true;
                } else if (lastVal == board[horizontal ? i : j][horizontal ? j : i]) {
                    return true;
                }
                lastVal = board[horizontal ? i : j][horizontal ? j : i];
            }
        }
        return false;
    }

	/**
	 * Move right.
	 */
	public void moveRight() {
        Integer[][] tmp = getEmptyBoard();
        for (int i = 0; i < size; i++) {
            int index = size;
            int lastNumber = 0;
            for (int j = size - 1; j >= 0; j--) {
                if (board[i][j] == 0) {
                    continue;
                }
                if (lastNumber != board[i][j] || lastNumber * 2 == tmp[i][index]) {
                    index--;
                }
                tmp[i][index] += board[i][j];
                lastNumber = board[i][j];

            }
        }
        board = tmp;
    }

	/**
	 * Move left.
	 */
	public void moveLeft() {
        Integer[][] tmp = getEmptyBoard();
        for (int i = 0; i < size; i++) {
            int index = -1;
            int lastNumber = 0;
            for (int j = 0; j < size; j++) {
                if (board[i][j] == 0) {
                    continue;
                }
                if (lastNumber != board[i][j] || lastNumber * 2 == tmp[i][index]) {
                    index++;
                }
                tmp[i][index] += board[i][j];
                lastNumber = board[i][j];
            }
        }
        board = tmp;
    }

	/**
	 * Move up.
	 */
	public void moveUp() {
        Integer[][] tmp = getEmptyBoard();
        for (int i = 0; i < size; i++) {
            int index = -1;
            int lastNumber = 0;
            for (int j = 0; j < size; j++) {
                if (board[j][i] == 0) {
                    continue;
                }
                if (lastNumber != board[j][i] || lastNumber * 2 == tmp[index][i]) {
                    index++;
                }
                tmp[index][i] += board[j][i];
                lastNumber = board[j][i];

            }
        }
        board = tmp;
    }

	/**
	 * Move down.
	 */
	public void moveDown() {
        Integer[][] tmp = getEmptyBoard();
        for (int i = 0; i < size; i++) {
            int index = size;
            int lastNumber = 0;
            for (int j = size - 1; j >= 0; j--) {
                if (board[j][i] == 0) {
                    continue;
                }
                if (lastNumber != board[j][i] || lastNumber * 2 == tmp[index][i]) {
                    index--;
                }
                tmp[index][i] += board[j][i];
                lastNumber = board[j][i];
            }
        }
        board = tmp;
    }

    private Integer[][] getEmptyBoard() {
        Integer[][] board = new Integer[size][size];
        for (int i = 0; i < size; i++) {
            board[i] = new Integer[size];
            for (int j = 0; j < size; j++) {
                board[i][j] = 0;
            }
        }
        return board;
    }


	/**
	 * Gets random free position.
	 *
	 * @return the random free position
	 */
	public Pair<Integer, Integer> getRandomFreePosition() {
        if (isBoardFull()) {
            return null;
        }
        ArrayList<Pair<Integer, Integer>> availablePositions = getAvailablePositions();
        return availablePositions.get(rng.nextInt(availablePositions.size()));
    }
}