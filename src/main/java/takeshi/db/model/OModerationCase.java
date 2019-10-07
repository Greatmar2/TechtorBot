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

import java.awt.Color;
import java.sql.Timestamp;

import takeshi.db.AbstractModel;

/**
 * The type O moderation case.
 */
public class OModerationCase extends AbstractModel {
	/**
	 * The Guild id.
	 */
	public int guildId = 0;
	/**
	 * The User id.
	 */
	public int userId = 0;
	/**
	 * The Id.
	 */
	public int id = 0;
	/**
	 * The Moderator id.
	 */
	public int moderatorId = 0;
	/**
	 * The Message id.
	 */
	public String messageId = "";
	/**
	 * The Created at.
	 */
	public Timestamp createdAt = null;
	/**
	 * The Expires.
	 */
	public Timestamp expires = null;
	/**
	 * The Punishment.
	 */
	public PunishType punishment = PunishType.KICK;
	/**
	 * The Reason.
	 */
	public String reason = "";
	/**
	 * The Active.
	 */
	public int active = 1;
	/**
	 * The Moderator name.
	 */
	public String moderatorName = "";
	/**
	 * The User name.
	 */
	public String userName = "";

	/**
	 * Sets punishment.
	 *
	 * @param punishment the punishment
	 */
	public void setPunishment(int punishment) {
		this.punishment = PunishType.fromId(punishment);
	}

	/**
	 * The enum Punish type.
	 */
	public enum PunishType {
		/**
		 * The Warn.
		 */
		WARN(1, "Warn", "warned", "Adds a strike to the user", new Color(0xA8CF00)),
		/**
		 * The Mute.
		 */
		MUTE(2, "Mute", "muted", "Adds the configured muted role to user", new Color(0xFFF300)),
		/**
		 * The Kick.
		 */
		KICK(3, "Kick", "kicked", "Remove user from the guild", new Color(0xFF9600)),
		/**
		 * The Tmp ban.
		 */
		TMP_BAN(4, "Temp-ban", "temporarily banned", "Remove user from guild, unable to rejoin for a while", new Color(0xFF4700)),
		/**
		 * The Ban.
		 */
		BAN(5, "Ban", "banned", "Permanently removes user from guild", new Color(0xB70000));

		private final int id;
		private final String keyword;
		private final String verb;
		private final String description;
		private final Color color;

		PunishType(int id, String keyword, String verb, String description, Color color) {
			this.id = id;
			this.keyword = keyword;
			this.verb = verb;
			this.description = description;
			this.color = color;
		}

		/**
		 * From id punish type.
		 *
		 * @param id the id
		 * @return the punish type
		 */
		public static PunishType fromId(int id) {
			for (PunishType et : values()) {
				if (id == et.getId()) {
					return et;
				}
			}
			return KICK;
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

		/**
		 * Gets keyword.
		 *
		 * @return the keyword
		 */
		public String getKeyword() {
			return keyword;
		}

		/**
		 * Gets color.
		 *
		 * @return the color
		 */
		public Color getColor() {
			return color;
		}

		/**
		 * Gets verb.
		 *
		 * @return the verb
		 */
		public String getVerb() {
			return verb;
		}
	}
}
