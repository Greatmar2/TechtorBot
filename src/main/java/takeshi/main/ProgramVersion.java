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

package takeshi.main;

import takeshi.util.Misc;

/**
 * Created on 22-9-2016
 */
public class ProgramVersion {
	private int majorVersion;
	private int minorVersion;
	private int patchVersion;

	/**
	 * Instantiates a new Program version.
	 *
	 * @param majorVersion the major version
	 * @param minorVersion the minor version
	 * @param patchVersion the patch version
	 */
	public ProgramVersion(int majorVersion, int minorVersion, int patchVersion) {
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;
		this.patchVersion = patchVersion;
	}

	private ProgramVersion(int majorVersion, int minorVersion) {
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;
		this.patchVersion = 0;
	}

	/**
	 * Instantiates a new Program version.
	 *
	 * @param majorVersion the major version
	 */
	ProgramVersion(int majorVersion) {
		this.majorVersion = majorVersion;
		this.minorVersion = 0;
		this.patchVersion = 0;
	}

	/**
	 * From string program version.
	 *
	 * @param version the version
	 * @return the program version
	 */
	public static ProgramVersion fromString(String version) {
		String[] parts = version.split("\\.");
		if (parts.length == 3) {
			return new ProgramVersion(Misc.parseInt(parts[0], 1), Misc.parseInt(parts[1], 0), Misc.parseInt(parts[2], 0));
		} else if (parts.length == 2) {
			return new ProgramVersion(Misc.parseInt(parts[0], 1), Misc.parseInt(parts[1], 0));
		} else if (parts.length == 1) {
			return new ProgramVersion(Misc.parseInt(parts[0], 1));
		}
		return new ProgramVersion(1);
	}

	/**
	 * Compares the version to another one
	 *
	 * @param version the version to compare it with
	 * @return true if this is higher than version
	 */
	public boolean isHigherThan(ProgramVersion version) {
		if (version == null || this.getMajorVersion() > version.getMajorVersion()) {
			return true;
		} else if (this.getMajorVersion() == version.getMajorVersion()) {
			if (this.getMinorVersion() > version.getMinorVersion()) {
				return true;
			} else if (this.getMinorVersion() == version.getMinorVersion()) {
				if (this.getPatchVersion() > version.getPatchVersion()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Gets patch version.
	 *
	 * @return the patch version
	 */
	public int getPatchVersion() {
		return patchVersion;
	}

	/**
	 * Gets minor version.
	 *
	 * @return the minor version
	 */
	public int getMinorVersion() {
		return minorVersion;
	}

	/**
	 * Gets major version.
	 *
	 * @return the major version
	 */
	public int getMajorVersion() {
		return majorVersion;
	}

	@Override
	public String toString() {
		return getMajorVersion() + "." + getMinorVersion() + "." + getPatchVersion();
	}
}
