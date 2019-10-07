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

package takeshi.util;

/**
 * The type Game util.
 */
public class GameUtil {
	/**
	 * The constant MULTIPLICATION.
	 */
	public static final int MULTIPLICATION = 2;
	/**
	 * The constant BASE.
	 */
	public static final int BASE = 3;

	/**
	 * Gets xp for.
	 *
	 * @param level the level
	 * @return the xp for
	 */
	public static long getXpFor(int level) {
        return getXpFor(level, MULTIPLICATION, BASE);
    }

	/**
	 * Gets xp for.
	 *
	 * @param level            the level
	 * @param multiIncremental the multi incremental
	 * @param multiBase        the multi base
	 * @return the xp for
	 */
	public static long getXpFor(int level, int multiIncremental, int multiBase) {
        return (multiIncremental * level * level) + multiBase * level;
    }
}
