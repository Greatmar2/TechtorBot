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
import java.util.ArrayList;
import java.util.List;

import takeshi.core.Logger;
import takeshi.db.WebDb;
import takeshi.db.model.OPoll;

public class CPoll {

	public static OPoll findBy(long discordGuildId, long messageId) {
		return findBy(CGuild.getCachedId(discordGuildId), messageId);
	}

	public static OPoll findOrCreate(long discordGuildid, long messageId) {
		OPoll rec = findBy(CGuild.getCachedId(discordGuildid), messageId);
		if (rec.id == 0) {
			rec.guildId = CGuild.getCachedId(discordGuildid);
			insert(rec);
		}
		return rec;
	}

	public static OPoll findBy(int serverId, long messageId) {
		OPoll t = new OPoll();
		try (ResultSet rs = WebDb.get().select("SELECT *  " + "FROM poll " + "WHERE guild_id = ? AND message_id = ?", serverId, messageId)) {
			if (rs.next()) {
				t = fillRecord(rs);
			}
			rs.getStatement().close();
		} catch (Exception e) {
			Logger.fatal(e);
		}
		return t;
	}

	public static List<OPoll> getMessagesForGuild(long guildDiscordId) {
		return getMessagesForGuild(CGuild.getCachedId(guildDiscordId));
	}

	public static List<OPoll> getMessagesForGuild(int guildId) {
		List<OPoll> result = new ArrayList<>();
		try (ResultSet rs = WebDb.get().select("SELECT *  " + "FROM poll " + "WHERE guild_id = ?", guildId)) {
			while (rs.next()) {
				result.add(fillRecord(rs));
			}
			rs.getStatement().close();
		} catch (Exception e) {
			Logger.fatal(e);
		}
		return result;

	}

	private static OPoll fillRecord(ResultSet rs) throws SQLException {
		OPoll t = new OPoll();
		t.id = rs.getInt("id");
		t.guildId = rs.getInt("guild_id");
		t.channelId = rs.getLong("channel_id");
		t.message = rs.getString("message");
		t.messageExpire = rs.getTimestamp("message_expire");
		t.messageId = rs.getLong("message_id");
		return t;
	}

	public static void delete(OPoll record) {
		try {
			WebDb.get().query("DELETE FROM poll WHERE message_id = ? AND guild_id = ? ", record.messageId, record.guildId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void deleteGuild(long guildId) {
		deleteGuild(CGuild.getCachedId(guildId));
	}

	public static void deleteGuild(int guildId) {
		try {
			WebDb.get().query("DELETE FROM poll WHERE guild_id = ? ", guildId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void update(OPoll record) {
		try {
			WebDb.get().query("UPDATE poll SET channel_id = ?, message = ?, message_id = ? WHERE id = ?", record.channelId, record.message, record.messageId, record.id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void insert(OPoll record) {
		if (record.id > 0) {
			update(record);
			return;
		}
		try {
			record.id = WebDb.get().insert("INSERT INTO poll(guild_id, channel_id, message, message_id, message_expire) " + "VALUES (?,?,?,?,?)", record.guildId,
					record.channelId, record.message, record.messageId, record.messageExpire);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
