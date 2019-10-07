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
 * The type O bet.
 */
public class OBet extends AbstractModel {
	/**
	 * The Id.
	 */
	public int id = 0;
	/**
	 * The Guild id.
	 */
	public int guildId = 0;
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
	 * The Started on.
	 */
	public Timestamp startedOn = null;
	/**
	 * The Ends at.
	 */
	public Timestamp endsAt = null;
	/**
	 * The Price.
	 */
	public int price = 0;
	/**
	 * The Status.
	 */
	public Status status = Status.PREPARING;

	/**
	 * Sets status.
	 *
	 * @param id the id
	 */
	public void setStatus(int id) {
        status = Status.fromId(id);
    }

	/**
	 * The enum Status.
	 */
	public enum Status {
		/**
		 * Preparing status.
		 */
		PREPARING(1),
		/**
		 * Pending status.
		 */
		PENDING(2),
		/**
		 * Active status.
		 */
		ACTIVE(3),
		/**
		 * Closed status.
		 */
		CLOSED(4),
		/**
		 * Canceled status.
		 */
		CANCELED(5);

        private int id;

        Status(int id) {

            this.id = id;
        }

		/**
		 * From id status.
		 *
		 * @param id the id
		 * @return the status
		 */
		public static Status fromId(int id) {
            for (Status s : values()) {
                if (id == s.getId()) {
                    return s;
                }
            }
            return PREPARING;
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
		 * Sets id.
		 *
		 * @param id the id
		 */
		public void setId(int id) {
            this.id = id;
        }
    }
}
