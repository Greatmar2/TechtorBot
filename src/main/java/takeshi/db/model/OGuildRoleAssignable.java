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

import takeshi.db.AbstractModel;

/**
 * The type O guild role assignable.
 */
public class OGuildRoleAssignable extends AbstractModel {
	/**
	 * The Guild id.
	 */
	public int guildId = 0;
	/**
	 * The Discord role id.
	 */
	public String discordRoleId = "";
	/**
	 * The Description.
	 */
	public String description = "";
	/**
	 * The Role name.
	 */
	public String roleName = "";
}
