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

import takeshi.util.Emojibet;

/**
 * The type O bot version change.
 */
public class OBotVersionChange {

	/**
	 * The Id.
	 */
	public int id = 0;
	/**
	 * The Author.
	 */
	public int author = 0;
	/**
	 * The Description.
	 */
	public String description = "";
	/**
	 * The Version.
	 */
	public int version = 0;
	/**
	 * The Change type.
	 */
	public ChangeType changeType = ChangeType.UNKNOWN;

	/**
	 * Sets change type.
	 *
	 * @param changeType the change type
	 */
	public void setChangeType(int changeType) {
        this.changeType = ChangeType.fromId(changeType);
    }

	/**
	 * The enum Change type.
	 */
	public enum ChangeType {
		/**
		 * Added change type.
		 */
		ADDED(1, "A", "Added", Emojibet.CHECK_MARK_GREEN),
		/**
		 * Changed change type.
		 */
		CHANGED(2, "C", "Changed", Emojibet.WRENCH),
		/**
		 * Removed change type.
		 */
		REMOVED(3, "R", "Removed", Emojibet.BASKET),
		/**
		 * The Fixed.
		 */
		FIXED(4, "F", "Bugs fixed", Emojibet.BUG),
		/**
		 * Unknown change type.
		 */
		UNKNOWN(5, "?", "Misc", Emojibet.QUESTION_MARK);

        private final int id;
        private final String title;
        private final String code;
        private final String emoji;

        ChangeType(int id, String code, String title, String emoji) {
            this.title = title;
            this.id = id;
            this.code = code;
            this.emoji = emoji;
        }

		/**
		 * From id change type.
		 *
		 * @param id the id
		 * @return the change type
		 */
		public static ChangeType fromId(int id) {
            for (ChangeType et : values()) {
                if (id == et.getId()) {
                    return et;
                }
            }
            return UNKNOWN;
        }

		/**
		 * From code change type.
		 *
		 * @param code the code
		 * @return the change type
		 */
		public static ChangeType fromCode(String code) {
            for (ChangeType et : values()) {
                if (code.equalsIgnoreCase(et.getCode())) {
                    return et;
                }
            }
            return UNKNOWN;
        }

		/**
		 * Gets emoji.
		 *
		 * @return the emoji
		 */
		public String getEmoji() {
            return emoji;
        }

		/**
		 * Gets id.
		 *
		 * @return the id
		 */
		public int getId() {
            return id;
        }

		/**
		 * Gets title.
		 *
		 * @return the title
		 */
		public String getTitle() {
            return title;
        }

		/**
		 * Gets code.
		 *
		 * @return the code
		 */
		public String getCode() {
            return code;
        }
    }
}
