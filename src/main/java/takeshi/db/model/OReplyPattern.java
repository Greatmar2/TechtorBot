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
 * The type O reply pattern.
 */
public class OReplyPattern extends AbstractModel {
	/**
	 * The User id.
	 */
	public int userId = 0;
	/**
	 * The Id.
	 */
	public int id = 0;
	/**
	 * The Guild id.
	 */
	public int guildId = 0;
	/**
	 * The Tag.
	 */
	public String tag = "";
	/**
	 * The Pattern.
	 */
	public String pattern = "";
	/**
	 * The Reply.
	 */
	public String reply = "";
	/**
	 * The Created on.
	 */
	public Timestamp createdOn = null;
	/**
	 * The Cooldown.
	 */
	public long cooldown = 0;
}
