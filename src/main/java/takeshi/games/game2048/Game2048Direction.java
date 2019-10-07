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

/**
 * The enum Game 2048 direction.
 */
public enum Game2048Direction {
	/**
	 * Up game 2048 direction.
	 */
	UP(),
	/**
	 * Right game 2048 direction.
	 */
	RIGHT(),
	/**
	 * Left game 2048 direction.
	 */
	LEFT(),
	/**
	 * Down game 2048 direction.
	 */
	DOWN(),
	/**
	 * Unknown game 2048 direction.
	 */
	UNKNOWN();

	/**
	 * From string game 2048 direction.
	 *
	 * @param direction the direction
	 * @return the game 2048 direction
	 */
	public static Game2048Direction fromString(String direction) {
        if (direction == null) {
            return UNKNOWN;
        }
        switch (direction.toLowerCase()) {
            case "up":
            case "u":
                return UP;
            case "right":
            case "r":
                return RIGHT;
            case "down":
            case "d":
                return DOWN;
            case "left":
            case "l":
                return LEFT;
        }
        return UNKNOWN;
    }

}
