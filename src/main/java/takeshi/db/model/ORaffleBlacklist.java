/*
 * Copyright 2018 Greatmar2
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

import takeshi.db.AbstractModel;

/**
 * The type O raffle blacklist.
 */
public class ORaffleBlacklist extends AbstractModel {
	/**
	 * The Id.
	 */
	public int id = 0;
	/**
	 * The Guild id.
	 */
	public long guildId = 0;
	/**
	 * The User id.
	 */
	public long userId = 0L;
	/**
	 * The Message id.
	 */
	public long messageId = 0L;
	/**
	 * The Raffle id.
	 */
	public int raffleId = 0;
	/**
	 * The Currently.
	 */
	public boolean currently = true;
}