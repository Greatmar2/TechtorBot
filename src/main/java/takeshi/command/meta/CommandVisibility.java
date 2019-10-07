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
 * Visibility of a command
 */
public enum CommandVisibility {
	/**
	 * Private command visibility.
	 */
	PRIVATE(),
	/**
	 * Public command visibility.
	 */
	PUBLIC(),
	/**
	 * Both command visibility.
	 */
	BOTH();

	/**
	 * Is for private boolean.
	 *
	 * @return the boolean
	 */
	public boolean isForPrivate() {
        return this.equals(PRIVATE) || this.equals(BOTH);
    }

	/**
	 * Is for public boolean.
	 *
	 * @return the boolean
	 */
	public boolean isForPublic() {
        return this.equals(PUBLIC) || this.equals(BOTH);
    }
}
