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

import takeshi.main.BotConfig;

/**
 * a turn in a game
 */
public abstract class GameTurn {
    private String commandPrefix = BotConfig.BOT_COMMAND_PREFIX;

	/**
	 * Parse input boolean.
	 *
	 * @param input the input
	 * @return the boolean
	 */
	abstract public boolean parseInput(String input);

	/**
	 * Gets input error message.
	 *
	 * @return the input error message
	 */
	abstract public String getInputErrorMessage();

	/**
	 * Gets command prefix.
	 *
	 * @return the command prefix
	 */
	public String getCommandPrefix() {
        return commandPrefix;
    }

	/**
	 * Sets command prefix.
	 *
	 * @param commandPrefix the command prefix
	 */
	public void setCommandPrefix(String commandPrefix) {
        this.commandPrefix = commandPrefix;
    }
}
