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

/**
 * The enum Game state.
 */
public enum GameState {
	/**
	 * Initializing game state.
	 */
	INITIALIZING,
	/**
	 * Ready game state.
	 */
	READY,
	/**
	 * In progress game state.
	 */
	IN_PROGRESS,
	/**
	 * Over game state.
	 */
	OVER;
}
