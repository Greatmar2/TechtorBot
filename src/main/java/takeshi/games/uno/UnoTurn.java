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

import takeshi.games.meta.GameTurn;

public class UnoTurn extends GameTurn {
	private int action;

	public UnoTurn() {

	}

	public UnoTurn(int action) {

		this.action = action;
	}

	public int getAction() {
		return action;
	}

	@Override
	public boolean parseInput(String input) {
		if (input != null && input.matches("^[1-13]$")) {
			this.action = Integer.parseInt(input) - 1;
			return true;
		}
		return false;
	}

	@Override
	public String getInputErrorMessage() {
		return "Expecting a numeric input between 1 and 13";
	}
	// Will handle the events of a turn

}
