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

package takeshi.db.controllers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import takeshi.core.Logger;
import takeshi.db.WebDb;
import takeshi.db.model.OGuildRoleAssignable;

/**
 * The type C guild role assignable.
 */
public class CGuildRoleAssignable {

	/**
	 * Find by o guild role assignable.
	 *
	 * @param guildId       the guild id
	 * @param discordRoleId the discord role id
	 * @return the o guild role assignable
	 */
	public static OGuildRoleAssignable findBy(int guildId, String discordRoleId) {
        OGuildRoleAssignable record = new OGuildRoleAssignable();
        try (ResultSet rs = WebDb.get().select(
                "SELECT guild_id, discord_role_id, description, role_name  " +
                        "FROM guild_roles_self " +
                        "WHERE guild_id = ? AND discord_role_id = ? ", guildId, discordRoleId)) {
            if (rs.next()) {
                record = fillRecord(rs);
            }
            rs.getStatement().close();
        } catch (Exception e) {
            Logger.fatal(e);
        }
        return record;
    }

	/**
	 * Gets roles for.
	 *
	 * @param guildId the guild id
	 * @return the roles for
	 */
	public static List<OGuildRoleAssignable> getRolesFor(int guildId) {
        List<OGuildRoleAssignable> list = new ArrayList<>();
        try (ResultSet rs = WebDb.get().select(
                "SELECT *  " +
                        "FROM guild_roles_self " +
                        "WHERE guild_id = ? ", guildId)) {
            while (rs.next()) {
                list.add(fillRecord(rs));
            }
            rs.getStatement().close();
        } catch (Exception e) {
            Logger.fatal(e);
        }
        return list;
    }

    private static OGuildRoleAssignable fillRecord(ResultSet resultset) throws SQLException {
        OGuildRoleAssignable record = new OGuildRoleAssignable();
        record.guildId = resultset.getInt("guild_id");
        record.discordRoleId = resultset.getString("discord_role_id");
        record.description = resultset.getString("description");
        record.roleName = resultset.getString("role_name");
        return record;
    }

	/**
	 * Delete.
	 *
	 * @param guildId       the guild id
	 * @param discordRoleId the discord role id
	 * @param roleName      the role name
	 */
	public static void delete(int guildId, long discordRoleId, String roleName) {
        try {
            WebDb.get().query(
                    "DELETE FROM guild_roles_self WHERE guild_id = ? AND (discord_role_id = ? OR role_name = '?')", guildId, discordRoleId, roleName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	/**
	 * Insert or update.
	 *
	 * @param guildId       the guild id
	 * @param discordRoleId the discord role id
	 * @param roleName      the role name
	 */
	public static void insertOrUpdate(int guildId, long discordRoleId, String roleName) {
        OGuildRoleAssignable role = new OGuildRoleAssignable();
        role.guildId = guildId;
        role.discordRoleId = String.valueOf(discordRoleId);
        role.roleName = roleName;
        insertOrUpdate(role);
    }

	/**
	 * Insert or update.
	 *
	 * @param record the record
	 */
	public static void insertOrUpdate(OGuildRoleAssignable record) {
        try {
            WebDb.get().insert(
                    "INSERT INTO guild_roles_self(guild_id, discord_role_id, description, role_name) " +
                            "VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE role_name = VALUES(role_name), description= VALUES(description)",
                    record.guildId, record.discordRoleId, record.description, record.roleName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
