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

package takeshi.command.meta;

/**
 * The scale of the cooldown, is it based per user, channel, guild or global?
 */
public enum CooldownScope {
	/**
	 * User cooldown scope.
	 */
	USER(1),
	/**
	 * Channel cooldown scope.
	 */
	CHANNEL(2),
	/**
	 * Guild cooldown scope.
	 */
	GUILD(3),
	/**
	 * Global cooldown scope.
	 */
	GLOBAL(4);

    private final int identifier;

    CooldownScope(int identifier) {

        this.identifier = identifier;
    }

	/**
	 * Gets id.
	 *
	 * @return the id
	 */
	public int getId() {
        return identifier;
    }
}
