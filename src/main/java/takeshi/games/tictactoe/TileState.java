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
 * The enum Tile state.
 */
public enum TileState {
	/**
	 * X tile state.
	 */
	X("\u274C"),
	/**
	 * O tile state.
	 */
	O("\u2B55"),
	/**
	 * Free tile state.
	 */
	FREE("\u2754");

    private final String emoticon;

    TileState(String emoticon) {

        this.emoticon = emoticon;
    }

	/**
	 * Gets emoticon.
	 *
	 * @return the emoticon
	 */
	public String getEmoticon() {
        return emoticon;
    }
}
