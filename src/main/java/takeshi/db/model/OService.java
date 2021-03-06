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
 * Created on 8-9-2016
 */
public class OService extends AbstractModel {
	/**
	 * The Activated.
	 */
	public int activated = 0;
	/**
	 * The Description.
	 */
	public String description = "";
	/**
	 * The Display name.
	 */
	public String displayName = "";
	/**
	 * The Name.
	 */
	public String name = "";
	/**
	 * The Id.
	 */
	public int id = 0;
}
