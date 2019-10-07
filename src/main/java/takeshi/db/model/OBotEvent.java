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

package takeshi.db.model;

import java.sql.Timestamp;

import takeshi.db.AbstractModel;

/**
 * The type O bot event.
 */
public class OBotEvent extends AbstractModel {
	/**
	 * The Id.
	 */
	public int id = 0;
	/**
	 * The Created on.
	 */
	public Timestamp createdOn = null;
	/**
	 * The Group.
	 */
	public String group = "";
	/**
	 * The Sub group.
	 */
	public String subGroup = "";
	/**
	 * The Data.
	 */
	public String data = "";
	/**
	 * The Log level.
	 */
	public Level logLevel = Level.INFO;

	/**
	 * The enum Level.
	 */
	public enum Level {
		/**
		 * Fatal level.
		 */
		FATAL(1),
		/**
		 * Error level.
		 */
		ERROR(2),
		/**
		 * Warn level.
		 */
		WARN(3),
		/**
		 * Info level.
		 */
		INFO(4),
		/**
		 * Debug level.
		 */
		DEBUG(5),
		/**
		 * Trace level.
		 */
		TRACE(6);

        private final int id;

        Level(int id) {

            this.id = id;
        }

		/**
		 * From id level.
		 *
		 * @param id the id
		 * @return the level
		 */
		public static Level fromId(int id) {
            for (Level et : values()) {
                if (id == et.getId()) {
                    return et;
                }
            }
            return INFO;
        }

		/**
		 * Gets id.
		 *
		 * @return the id
		 */
		public int getId() {
            return id;
        }
    }
}
