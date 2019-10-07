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

import takeshi.guildsettings.types.BooleanSettingType;
import takeshi.guildsettings.types.NoSettingType;
import takeshi.guildsettings.types.NumberBetweenSettingType;
import takeshi.guildsettings.types.RoleSettingType;
import takeshi.guildsettings.types.TextChannelSettingType;
import takeshi.guildsettings.types.VoiceChannelSettingType;
import takeshi.main.BotConfig;

/**
 * The type Guild setting type.
 */
public class GuildSettingType {
	/**
	 * The constant INTERNAL.
	 */
	public static final IGuildSettingType INTERNAL = new NoSettingType();
	/**
	 * The constant TOGGLE.
	 */
	public static final IGuildSettingType TOGGLE = new BooleanSettingType();
	/**
	 * The constant PERCENTAGE.
	 */
	public static final IGuildSettingType PERCENTAGE = new NumberBetweenSettingType(0, 100);
	/**
	 * The constant VOLUME.
	 */
	public static final IGuildSettingType VOLUME = new NumberBetweenSettingType(0, BotConfig.MUSIC_MAX_VOLUME);
	/**
	 * The constant TEXT_CHANNEL_OPTIONAL.
	 */
	public static final IGuildSettingType TEXT_CHANNEL_OPTIONAL = new TextChannelSettingType(true);
	/**
	 * The constant TEXT_CHANNEL_MANDATORY.
	 */
	public static final IGuildSettingType TEXT_CHANNEL_MANDATORY = new TextChannelSettingType(false);
	/**
	 * The constant ROLE_OPTIONAL.
	 */
	public static final IGuildSettingType ROLE_OPTIONAL = new RoleSettingType(true);
	/**
	 * The constant ROLE_MANDATORY.
	 */
	public static final IGuildSettingType ROLE_MANDATORY = new RoleSettingType(false);
	/**
	 * The constant VOICE_CHANNEL_OPTIONAL.
	 */
	public static final IGuildSettingType VOICE_CHANNEL_OPTIONAL = new VoiceChannelSettingType(true);
	/**
	 * The constant VOICE_CHANNEL_MANDATORY.
	 */
	public static final IGuildSettingType VOICE_CHANNEL_MANDATORY = new VoiceChannelSettingType(false);
}
