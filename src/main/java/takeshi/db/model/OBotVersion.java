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

import takeshi.main.ProgramVersion;

/**
 * The type O bot version.
 */
public class OBotVersion {

	/**
	 * The Id.
	 */
	public int id = 0;
	/**
	 * The Major.
	 */
	public int major = 1;
	/**
	 * The Minor.
	 */
	public int minor = 0;
	/**
	 * The Patch.
	 */
	public int patch = 0;
	/**
	 * The Created on.
	 */
	public Timestamp createdOn = null;
	/**
	 * The Published.
	 */
	public int published = 0;

	/**
	 * Gets version.
	 *
	 * @return the version
	 */
	public ProgramVersion getVersion() {
        return new ProgramVersion(major, minor, patch);
    }
}
