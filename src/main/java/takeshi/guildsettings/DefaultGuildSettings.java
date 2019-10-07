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

package takeshi.guildsettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

/**
 * The type Default guild settings.
 */
public class DefaultGuildSettings {
    private static final TreeSet<String> tags = new TreeSet<>();
    private static boolean initialized = false;

    static {
        initSettings();
    }

	/**
	 * Gets all tags.
	 *
	 * @return the all tags
	 */
	public static TreeSet<String> getAllTags() {
        return new TreeSet<>(tags);
    }

	/**
	 * Gets default.
	 *
	 * @param key the key
	 * @return the default
	 */
	public static String getDefault(String key) {
        return GSetting.valueOf(key).getDefaultValue();
    }

	/**
	 * Count settings int.
	 *
	 * @return the int
	 */
	public static int countSettings() {
        return countSettings(true);
    }

	/**
	 * Count settings int.
	 *
	 * @param includeReadOnly the include read only
	 * @return the int
	 */
	public static int countSettings(boolean includeReadOnly) {
        if (includeReadOnly) {
            return GSetting.values().length;
        }
        return (int) Arrays.stream(GSetting.values()).filter(gSetting -> !gSetting.isInternal()).count();
    }

	/**
	 * Gets writable keys.
	 *
	 * @return the writable keys
	 */
	public static List<String> getWritableKeys() {
        ArrayList<String> set = new ArrayList<>();
        for (GSetting setting : GSetting.values()) {
            if (setting.isInternal()) {
                continue;
            }
            set.add(setting.name());
        }
        return set;
    }

	/**
	 * Gets all keys.
	 *
	 * @return the all keys
	 */
	public static List<String> getAllKeys() {
        ArrayList<String> set = new ArrayList<>();
        for (GSetting setting : GSetting.values()) {
            set.add(setting.name());
        }
        return set;
    }

	/**
	 * Get g setting.
	 *
	 * @param key the key
	 * @return the g setting
	 */
	public static GSetting get(String key) {
        return GSetting.valueOf(key.toUpperCase());
    }

	/**
	 * Gets default.
	 *
	 * @param setting the setting
	 * @return the default
	 */
	public static String getDefault(GSetting setting) {
        return setting.getDefaultValue();
    }

	/**
	 * Is valid key boolean.
	 *
	 * @param key the key
	 * @return the boolean
	 */
	public static boolean isValidKey(String key) {
        try {
            GSetting.valueOf(key.toUpperCase());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static void initSettings() {
        if (initialized) {
            return;
        }
        for (GSetting setting : GSetting.values()) {
            for (GSettingTag tag : setting.getTags()) {
                if (!tags.contains(tag.name())) {
                    tags.add(tag.name());
                }
            }
            initialized = true;
        }
    }
}