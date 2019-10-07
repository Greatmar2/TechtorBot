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

package takeshi.core;

/**
 * bot exit codes
 * Created on 22-9-2016
 */
public enum ExitCode {
	/**
	 * Reboot exit code.
	 */
	REBOOT(100),
	/**
	 * Stop exit code.
	 */
	STOP(101),
	/**
	 * Need more shards exit code.
	 */
	NEED_MORE_SHARDS(102),
	/**
	 * Update exit code.
	 */
	UPDATE(200),
	/**
	 * Generic error exit code.
	 */
	GENERIC_ERROR(300),
	/**
	 * Bad config exit code.
	 */
	BAD_CONFIG(301),
	/**
	 * Disconnected exit code.
	 */
	DISCONNECTED(302),
	/**
	 * Unknown exit code.
	 */
	UNKNOWN(-1);

    private final int code;

    ExitCode(int code) {

        this.code = code;
    }

	/**
	 * From code exit code.
	 *
	 * @param exitCode the exit code
	 * @return the exit code
	 */
	public static ExitCode fromCode(int exitCode) {
        for (ExitCode code : ExitCode.values()) {
            if (code.getCode() == exitCode) {
                return code;
            }
        }
        return ExitCode.UNKNOWN;
    }

	/**
	 * Gets code.
	 *
	 * @return the code
	 */
	public int getCode() {
        return code;
    }
}
