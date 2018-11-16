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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import takeshi.core.Logger;
import takeshi.db.WebDb;
import takeshi.db.model.ORaffle;

public class CRaffle {
	public static final int PRIZE_LENGTH = 100;
	public static final int DESC_LENGTH = 250;
	public static final int IMAGE_LENGTH = 150;

	public static ORaffle findBy(long discordGuildId, int raffleId) {
		return findBy(CGuild.getCachedId(discordGuildId), raffleId);
	}

	public static ORaffle findOrCreate(long discordGuildId, int raffleId) {
		ORaffle rec = findBy(discordGuildId, raffleId);
		if (rec.id == 0) {
			rec.guildId = CGuild.getCachedId(discordGuildId);
			insert(rec);
		}
		return rec;
	}

	public static ORaffle findBy(int serverId, int raffleId) {
		ORaffle t = new ORaffle();
		try (ResultSet rs = WebDb.get().select("SELECT * FROM raffle WHERE guild_id = ? AND id = ?", serverId, raffleId)) {
			if (rs.next()) {
				t = fillRecord(rs);
			}
			rs.getStatement().close();
		} catch (Exception e) {
			Logger.fatal(e);
		}
		return t;
	}

	public static ORaffle findByMessage(long discordGuildId, long messageId) {
		return findByMessage(CGuild.getCachedId(discordGuildId), messageId);
	}

	public static ORaffle findByMessage(int serverId, long messageId) {
		ORaffle t = new ORaffle();
		try (ResultSet rs = WebDb.get().select("SELECT * " + "FROM raffle " + "WHERE guild_id = ? AND message_id = ?", serverId, messageId)) {
			if (rs.next()) {
				t = fillRecord(rs);
			}
			rs.getStatement().close();
		} catch (Exception e) {
			Logger.fatal(e);
		}
		return t;
	}

	public static List<ORaffle> getMessagesForGuild(long guildDiscordId) {
		return getMessagesForGuild(CGuild.getCachedId(guildDiscordId));
	}

	public static List<ORaffle> getMessagesForGuild(int guildId) {
		List<ORaffle> result = new ArrayList<>();
		try (ResultSet rs = WebDb.get().select("SELECT *  " + "FROM raffle " + "WHERE guild_id = ?", guildId)) {
			while (rs.next()) {
				result.add(fillRecord(rs));
			}
			rs.getStatement().close();
		} catch (Exception e) {
			Logger.fatal(e);
		}
		return result;

	}

	private static ORaffle fillRecord(ResultSet rs) throws SQLException {
		ORaffle t = new ORaffle();
		t.id = rs.getInt("id");
		t.guildId = CGuild.getCachedDiscordIdL(rs.getInt("guild_id"));
		t.ownerId = rs.getLong("owner_id");
		t.prize = rs.getString("prize");
		t.description = rs.getString("description");
		t.duration = rs.getInt("duration");
		t.durationUnit = TimeUnit.valueOf(rs.getString("duration_unit"));
		t.entrants = rs.getInt("entrants");
		t.winners = rs.getInt("winners");
		t.thumb = rs.getString("thumb");
		t.image = rs.getString("image");
		t.channelId = rs.getLong("channel_id");
		t.messageId = rs.getLong("message_id");
		t.raffleEnd = rs.getTimestamp("raffle_end");
		t.deleteOnEnd = rs.getBoolean("delete_on_end");
		return t;
	}

	public static void delete(ORaffle record) {
		try {
			WebDb.get().query("DELETE FROM raffle WHERE id = ?", record.id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void deleteGuild(long guildId) {
		deleteGuild(CGuild.getCachedId(guildId));
	}

	public static void deleteGuild(int guildId) {
		try {
			WebDb.get().query("DELETE FROM raffle WHERE guild_id = ? ", guildId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void update(ORaffle record) {
		if (record.id == 0) {
			insert(record);
		} else {
			try {
				WebDb.get().query(
						"UPDATE raffle SET guild_id = ?, owner_id = ?, prize = ?, description = ?, duration = ?, duration_unit = ?, entrants = ?, winners = ?, thumb = ?, image = ?, channel_id = ?, message_id = ?, raffle_end = ?, delete_on_end = ? WHERE id = ?",
						CGuild.getCachedId(record.guildId), record.ownerId, record.prize, record.description, record.duration, record.durationUnit.name(),
						record.entrants, record.winners, record.thumb, record.image, record.channelId, record.messageId, checkTimeStamp(record.raffleEnd),
						record.deleteOnEnd, record.id);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static ORaffle insert(ORaffle record) {
		if (record.id > 0) {
			update(record);
		} else {
			try {
				record.id = WebDb.get().insert(
						"INSERT INTO raffle(guild_id, owner_id, prize, description, duration, duration_unit, entrants, winners, thumb, image, channel_id, message_id, raffle_end, delete_on_end) "
								+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
						CGuild.getCachedId(record.guildId), record.ownerId, record.prize, record.description, record.duration, record.durationUnit.name(),
						record.entrants, record.winners, record.thumb, record.image, record.channelId, record.messageId, checkTimeStamp(record.raffleEnd),
						record.deleteOnEnd);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return record;
	}

	private static Timestamp checkTimeStamp(Timestamp stamp) {
		if (stamp == null || stamp.getTime() == 0) {
			return null;
		}
		return stamp;
	}
}
