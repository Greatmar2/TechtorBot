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

/**
 * The type Uno turn.
 */
public class UnoTurn extends GameTurn {
	private int action;

	/**
	 * Instantiates a new Uno turn.
	 */
	public UnoTurn() {

	}

	/**
	 * Instantiates a new Uno turn.
	 *
	 * @param action the action
	 */
	public UnoTurn(int action) {
		if (action <= 10) {
			action--;
		}
		this.action = action;
	}

	/**
	 * Gets action.
	 *
	 * @return the action
	 */
	int getAction() {
		return action;
	}

	@Override
	public boolean parseInput(String input) {
		action = UnoGame.reactionToCommand(input);
		if (action >= 0)
			return true;
		if (input != null && input.matches("^[1-9]{1,2}$")) {
			action = Integer.parseInt(input);
			/*
			 * if (action <= 10) { action--; }
			 */
			return true;
		}
		return false;
	}

	@Override
	public String getInputErrorMessage() {
		return "Expecting a numeric input between 1 and 17 or a letter between 'a' and 'j'";
	}
	// Will handle the events of a turn

}
