/*
 * Copyright 2018 Greatmar2
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

import takeshi.core.Logger;
import takeshi.db.WebDb;
import takeshi.db.model.OAutoRole;

/**
 * data communication with the controllers `ranks`
 */
public class CAutoRole {

	public static OAutoRole findBy(Long guildId) {
		return findBy(CGuild.getCachedId(guildId));
	}

	public static OAutoRole findBy(int guildId) {
		OAutoRole s = new OAutoRole();
		try (ResultSet rs = WebDb.get().select("SELECT id, role_name, role_id, guild_id FROM auto_role WHERE guild_id = ? ", guildId)) {
			if (rs.next()) {
				s = fillRecord(rs);
			}
			rs.getStatement().close();
		} catch (Exception e) {
			Logger.fatal(e);
		}
		return s;
	}

	public static OAutoRole findById(int internalId) {
		OAutoRole s = new OAutoRole();
		try (ResultSet rs = WebDb.get().select("SELECT id, role_name, role_id, guild_id FROM auto_role WHERE id = ? ", internalId)) {
			if (rs.next()) {
				s = fillRecord(rs);
			}
			rs.getStatement().close();
		} catch (Exception e) {
			Logger.fatal(e);
		}
		return s;
	}

	private static OAutoRole fillRecord(ResultSet rs) throws SQLException {
		OAutoRole s = new OAutoRole();
		s.id = rs.getInt("id");
		s.guildId = CGuild.getCachedDiscordIdL(rs.getInt("guild_id"));
		s.roleId = rs.getLong("role_id");
		s.roleName = rs.getString("role_name");
		return s;
	}

	public static void update(OAutoRole record) {
		if (record.id == 0) {
			insert(record);
			return;
		}
		try {
			WebDb.get().query("UPDATE auto_role SET role_name = ?, role_id = ?, guild_id = ? WHERE id = ? ", record.roleName, record.roleId,
					CGuild.getCachedId(record.guildId), record.id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void insert(OAutoRole record) {
		if (record.id > 0) {
			update(record);
			return;
		}
		try {
			record.id = WebDb.get().insert("INSERT INTO auto_role(role_name, role_id, guild_id) VALUES (?,?,?)", record.roleName, record.roleId,
					CGuild.getCachedId(record.guildId));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
