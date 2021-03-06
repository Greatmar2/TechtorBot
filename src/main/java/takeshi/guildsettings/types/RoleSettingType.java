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

package takeshi.guildsettings.types;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import takeshi.guildsettings.IGuildSettingType;
import takeshi.util.DisUtil;
import takeshi.util.Emojibet;

/**
 * TextChannel settings type
 * the value has to be a real channel in a guild + will be saved as the channel id
 */
public class RoleSettingType implements IGuildSettingType {

    private final boolean allowNull;

	/**
	 * Allow a null/false value?
	 *
	 * @param allowNull true if it can be null
	 */
	public RoleSettingType(boolean allowNull) {

        this.allowNull = allowNull;
    }

    @Override
    public String typeName() {
        return "discord-role";
    }

    @Override
    public boolean validate(Guild guild, String value) {
        if (allowNull && (value == null || value.isEmpty() || value.equalsIgnoreCase("false"))) {
            return true;
        }
        if (DisUtil.isRoleMention(value)) {
            return guild.getRoleById(DisUtil.mentionToId(value)) != null;
        }
        return DisUtil.findRole(guild, value) != null;
    }

    @Override
    public String fromInput(Guild guild, String value) {
        if (allowNull && (value == null || value.isEmpty() || value.equalsIgnoreCase("false"))) {
            return "";
        }
        if (DisUtil.isRoleMention(value)) {
            Role role = guild.getRoleById(DisUtil.mentionToId(value));
            if (role != null) {
                return role.getId();
            }
        }
        Role role = DisUtil.findRole(guild, value);
        if (role != null) {
            return role.getId();
        }
        return "";
    }

    @Override
    public String toDisplay(Guild guild, String value) {
        if (value == null || value.isEmpty() || !value.matches("\\d{10,}")) {
            return Emojibet.X;
        }
        Role role = guild.getRoleById(value);
        if (role != null) {
            return role.getName();
        }
        return Emojibet.X;
    }
}
