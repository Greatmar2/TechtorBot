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
 * The type O todo item.
 */
public class OTodoItem extends AbstractModel {
	/**
	 * The Id.
	 */
	public int id = 0;
	/**
	 * The List id.
	 */
	public int listId = 0;
	/**
	 * The Description.
	 */
	public String description = "";
	/**
	 * The Checked.
	 */
	public int checked = 0;
	/**
	 * The Priority.
	 */
	public int priority = 0;
}
