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
import takeshi.db.controllers.CBankTransactions;
import takeshi.db.controllers.CBanks;

/**
 * Created on 5-9-2016
 */
public class OBank extends AbstractModel {
	/**
	 * The User id.
	 */
	public int userId = 0;
	/**
	 * The Id.
	 */
	public int id = 0;
	/**
	 * The Current balance.
	 */
	public long currentBalance = 0L;
	/**
	 * The Created on.
	 */
	public Timestamp createdOn = null;

	/**
	 * Transfer to boolean.
	 *
	 * @param target      the target
	 * @param amount      the amount
	 * @param description the description
	 * @return the boolean
	 */
	public boolean transferTo(OBank target, int amount, String description) {
        if (id == 0) {
            return false;
        }
        if (id == target.id) {
            return false;
        }
        if (amount < 1 || currentBalance - amount < 0) {
            return false;
        }
        if (description != null && description.length() > 150) {
            description = description.substring(0, 150);
        }
        CBankTransactions.insert(id, target.id, amount, description);
        target.currentBalance += amount;
        currentBalance -= amount;
        CBanks.updateBalance(id, -amount);
        CBanks.updateBalance(target.id, amount);
        return true;
    }
}
