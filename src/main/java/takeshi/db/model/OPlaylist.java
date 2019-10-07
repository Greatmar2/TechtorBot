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
 * The type O playlist.
 */
public class OPlaylist extends AbstractModel {
	/**
	 * The Id.
	 */
	public int id = 0;
	/**
	 * The Title.
	 */
	public String title = "";
	/**
	 * The Owner id.
	 */
	public int ownerId = 0;
	/**
	 * The Created on.
	 */
	public Timestamp createdOn = null;
	/**
	 * The Guild id.
	 */
	public int guildId = 0;
	/**
	 * The Code.
	 */
	public String code = "";
    private Visibility visibility = Visibility.GUILD;
    private EditType editType = EditType.PUBLIC_AUTO;
    private PlayType playType = PlayType.SHUFFLE;
    private String originalCode = "";

	/**
	 * Has code changed boolean.
	 *
	 * @return the boolean
	 */
	public boolean hasCodeChanged() {
        return !originalCode.equals(code);
    }

	/**
	 * Sets code.
	 *
	 * @param code the code
	 */
	public void setCode(String code) {
        if (!code.isEmpty()) {
            originalCode = code;
        }
        this.code = code;
    }

	/**
	 * Is global list boolean.
	 *
	 * @return the boolean
	 */
	public boolean isGlobalList() {
        return id > 0 && ownerId == 0 && guildId == 0;
    }

	/**
	 * Is guild list boolean.
	 *
	 * @return the boolean
	 */
	public boolean isGuildList() {
        return id > 0 && guildId > 0 && ownerId == 0;
    }

	/**
	 * Is personal boolean.
	 *
	 * @return the boolean
	 */
	public boolean isPersonal() {
        return id > 0 && guildId == 0 && ownerId > 0;
    }

	/**
	 * Gets visibility.
	 *
	 * @return the visibility
	 */
	public Visibility getVisibility() {
        return visibility;
    }

	/**
	 * Sets visibility.
	 *
	 * @param visibilityId the visibility id
	 */
	public void setVisibility(int visibilityId) {
        this.visibility = Visibility.fromId(visibilityId);
    }

	/**
	 * Sets visibility.
	 *
	 * @param visibility the visibility
	 */
	public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

	/**
	 * Gets edit type.
	 *
	 * @return the edit type
	 */
	public EditType getEditType() {
        return editType;
    }

	/**
	 * Sets edit type.
	 *
	 * @param editId the edit id
	 */
	public void setEditType(int editId) {
        this.editType = EditType.fromId(editId);
    }

	/**
	 * Sets edit type.
	 *
	 * @param editType the edit type
	 */
	public void setEditType(EditType editType) {
        this.editType = editType;
    }

	/**
	 * Gets play type.
	 *
	 * @return the play type
	 */
	public PlayType getPlayType() {
        return playType;
    }

	/**
	 * Sets play type.
	 *
	 * @param playType the play type
	 */
	public void setPlayType(PlayType playType) {
        this.playType = playType;
    }

	/**
	 * Sets play type.
	 *
	 * @param id the id
	 */
	public void setPlayType(int id) {
        setPlayType(PlayType.fromId(id));
    }


	/**
	 * The enum Visibility.
	 */
	public enum Visibility {
		/**
		 * Unknown visibility.
		 */
		UNKNOWN(0, "??"),
		/**
		 * The Public.
		 */
		PUBLIC(1, "Anyone can see and use the playlist"),
		/**
		 * The Public use.
		 */
		PUBLIC_USE(2, "Anyone can use the playlist"),
		/**
		 * The Guild.
		 */
		GUILD(3, "only this guild can see/use the playlist"),
		/**
		 * The Private.
		 */
		PRIVATE(4, "only you/admins can see/use it");

        private final int id;
        private final String description;

        Visibility(int id, String description) {

            this.id = id;
            this.description = description;
        }

		/**
		 * From id visibility.
		 *
		 * @param id the id
		 * @return the visibility
		 */
		public static Visibility fromId(int id) {
            for (Visibility vis : values()) {
                if (id == vis.getId()) {
                    return vis;
                }
            }
            return UNKNOWN;
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
		 * Gets description.
		 *
		 * @return the description
		 */
		public String getDescription() {
            return description;
        }
    }

	/**
	 * The enum Edit type.
	 */
	public enum EditType {
		/**
		 * Unknown edit type.
		 */
		UNKNOWN(0, "??"),
		/**
		 * The Public auto.
		 */
		PUBLIC_AUTO(1, "all music played is automatically added"),
		/**
		 * The Public full.
		 */
		PUBLIC_FULL(2, "Anyone can add and remove music from the playlist"),
		/**
		 * The Public add.
		 */
		PUBLIC_ADD(3, "Anyone can add music, but not remove it"),
		/**
		 * The Private auto.
		 */
		PRIVATE_AUTO(4, "Music played by you/admins will be added automatically"),
		/**
		 * The Private.
		 */
		PRIVATE(5, "Only the owner/admin can add/remove music from the playlist manually ");

        private final int id;
        private final String description;

        EditType(int id, String description) {

            this.id = id;
            this.description = description;
        }

		/**
		 * From id edit type.
		 *
		 * @param id the id
		 * @return the edit type
		 */
		public static EditType fromId(int id) {
            for (EditType et : values()) {
                if (id == et.getId()) {
                    return et;
                }
            }
            return UNKNOWN;
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
		 * Gets description.
		 *
		 * @return the description
		 */
		public String getDescription() {
            return description;
        }
    }

	/**
	 * The enum Play type.
	 */
	public enum PlayType {
		/**
		 * The Shuffle.
		 */
		SHUFFLE(1, "Randomly selects the next track"),
		/**
		 * The Loop.
		 */
		LOOP(2, "Iterates through the playlist");
        private final int id;
        private final String description;

        PlayType(int id, String description) {

            this.id = id;
            this.description = description;
        }

		/**
		 * From id play type.
		 *
		 * @param id the id
		 * @return the play type
		 */
		public static PlayType fromId(int id) {
            for (PlayType et : values()) {
                if (id == et.getId()) {
                    return et;
                }
            }
            return SHUFFLE;
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
		 * Gets description.
		 *
		 * @return the description
		 */
		public String getDescription() {
            return description;
        }
    }
}
