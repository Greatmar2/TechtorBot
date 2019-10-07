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
import takeshi.db.model.ORaffleBlacklist;

/**
 * The type C raffle blacklist.
 */
public class CRaffleBlacklist {
	/**
	 * Find by o raffle blacklist.
	 *
	 * @param guildId  the guild id
	 * @param userId   the user id
	 * @param raffleId the raffle id
	 * @return the o raffle blacklist
	 */
	public static ORaffleBlacklist findBy(long guildId, long userId, int raffleId) {
		return findBy(CGuild.getCachedId(guildId), userId, raffleId);
	}

	/**
	 * Find by o raffle blacklist.
	 *
	 * @param serverId the server id
	 * @param userId   the user id
	 * @param raffleId the raffle id
	 * @return the o raffle blacklist
	 */
	public static ORaffleBlacklist findBy(int serverId, long userId, int raffleId) {
		ORaffleBlacklist t = new ORaffleBlacklist();
		try (ResultSet rs = WebDb.get().select(
				"SELECT * FROM raffle_blacklist WHERE guild_id = ? AND user_id = ? AND (raffle_id = ? OR raffle_id = ?) ORDER BY raffle_id DESC", serverId,
				userId, raffleId, 0)) {
			if (rs.next()) {
				t = fillRecord(rs);
			}
			rs.getStatement().close();
		} catch (Exception e) {
			Logger.fatal(e);
		}
		return t;
	}

	/**
	 * Gets for guild.
	 *
	 * @param guildDiscordId the guild discord id
	 * @return the for guild
	 */
	public static List<ORaffleBlacklist> getForGuild(long guildDiscordId) {
		return getForGuild(CGuild.getCachedId(guildDiscordId));
	}

	/**
	 * Gets for guild.
	 *
	 * @param guildId the guild id
	 * @return the for guild
	 */
	public static List<ORaffleBlacklist> getForGuild(int guildId) {
		List<ORaffleBlacklist> result = new ArrayList<>();
		try (ResultSet rs = WebDb.get().select("SELECT * FROM raffle_blacklist WHERE guild_id = ? ORDER BY user_id ASC, raffle_id ASC", guildId)) {
			while (rs.next()) {
				result.add(fillRecord(rs));
			}
			rs.getStatement().close();
		} catch (Exception e) {
			Logger.fatal(e);
		}
		return result;

	}

	/**
	 * Gets for raffle.
	 *
	 * @param guildDiscordId the guild discord id
	 * @param raffleId       the raffle id
	 * @return the for raffle
	 */
	public static List<ORaffleBlacklist> getForRaffle(long guildDiscordId, int raffleId) {
		return getForRaffle(CGuild.getCachedId(guildDiscordId), raffleId);
	}

	/**
	 * Gets for raffle.
	 *
	 * @param guildId  the guild id
	 * @param raffleId the raffle id
	 * @return the for raffle
	 */
	public static List<ORaffleBlacklist> getForRaffle(int guildId, int raffleId) {
		List<ORaffleBlacklist> result = new ArrayList<>();
		try (ResultSet rs = WebDb.get().select("SELECT * FROM raffle_blacklist WHERE guild_id = ? AND (raffle_id = ? OR raffle_id = ?) ORDER BY user_id ASC",
				guildId, raffleId, 0)) {
			while (rs.next()) {
				result.add(fillRecord(rs));
			}
			rs.getStatement().close();
		} catch (Exception e) {
			Logger.fatal(e);
		}
		return result;

	}

	/**
	 * Gets for user.
	 *
	 * @param guildDiscordId the guild discord id
	 * @param userId         the user id
	 * @return the for user
	 */
	public static List<ORaffleBlacklist> getForUser(long guildDiscordId, long userId) {
		return getForUser(CGuild.getCachedId(guildDiscordId), userId);
	}

	/**
	 * Gets for user.
	 *
	 * @param guildId the guild id
	 * @param userId  the user id
	 * @return the for user
	 */
	public static List<ORaffleBlacklist> getForUser(int guildId, long userId) {
		List<ORaffleBlacklist> result = new ArrayList<>();
		try (ResultSet rs = WebDb.get().select("SELECT * FROM raffle_blacklist WHERE guild_id = ? AND user_id = ? ORDER BY raffle_id DESC", guildId, userId)) {
			while (rs.next()) {
				result.add(fillRecord(rs));
			}
			rs.getStatement().close();
		} catch (Exception e) {
			Logger.fatal(e);
		}
		return result;

	}

	private static ORaffleBlacklist fillRecord(ResultSet rs) throws SQLException {
		ORaffleBlacklist t = new ORaffleBlacklist();
		t.id = rs.getInt("id");
		t.guildId = CGuild.getCachedDiscordIdL(rs.getInt("guild_id"));
		t.userId = rs.getLong("user_id");
		t.raffleId = rs.getInt("raffle_id");
		t.currently = rs.getBoolean("currently");
		return t;
	}

	/**
	 * Delete.
	 *
	 * @param record the record
	 */
	public static void delete(ORaffleBlacklist record) {
		try {
			WebDb.get().query("DELETE FROM raffle_blacklist WHERE id = ?", record.id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Delete guild.
	 *
	 * @param guildId the guild id
	 */
	public static void deleteGuild(long guildId) {
		deleteGuild(CGuild.getCachedId(guildId));
	}

	/**
	 * Delete guild.
	 *
	 * @param guildId the guild id
	 */
	public static void deleteGuild(int guildId) {
		try {
			WebDb.get().query("DELETE FROM raffle_blacklist WHERE guild_id = ? ", guildId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Safe update.
	 *
	 * @param bl the bl
	 */
	public static void safeUpdate(ORaffleBlacklist bl) {
		bl.id = findBy(bl.guildId, bl.userId, bl.raffleId).id;

		// If a global bl is set to false, delete it
		if (bl.raffleId == 0 && !bl.currently) {
			delete(bl);
			return;
		}

		// If a specific bl is set to false and the global is false, delete it
//		if (!bl.currently) {
//			ORaffleBlacklist gloBl = findBy(bl.guildId, bl.userId, 0);
//			if (!gloBl.currently) {
//				delete(bl);
//				return;
//			}
//		}

		// If a specific bl is set to the same as global, delete it
		ORaffleBlacklist gloBl = findBy(bl.guildId, bl.userId, 0);
		if (bl.raffleId != 0 && gloBl.currently == bl.currently) {
			delete(bl);
			return;
		}

		insert(bl);
	}

	/**
	 * Update.
	 *
	 * @param record the record
	 */
	public static void update(ORaffleBlacklist record) {
		if (record.id == 0) {
			insert(record);
		} else {
			try {
				WebDb.get().query("UPDATE raffle_blacklist SET guild_id = ?, user_id = ?, raffle_id = ?, currently = ? WHERE id = ?",
						CGuild.getCachedId(record.guildId), record.userId, record.raffleId, record.currently, record.id);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Insert o raffle blacklist.
	 *
	 * @param record the record
	 * @return the o raffle blacklist
	 */
	public static ORaffleBlacklist insert(ORaffleBlacklist record) {
		if (record.id > 0) {
			update(record);
		} else {
			try {
				record.id = WebDb.get().insert("INSERT INTO raffle_blacklist(guild_id, user_id, raffle_id, currently) VALUES (?,?,?,?)",
						CGuild.getCachedId(record.guildId), record.userId, record.raffleId, record.currently);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return record;
	}
}
