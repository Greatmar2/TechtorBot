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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import takeshi.core.Logger;
import takeshi.db.WebDb;
import takeshi.db.model.OGuild;

/**
 * data communication with the controllers `servers` Created on 10-8-2016
 */
public class CGuild {
	private static Map<Long, Integer> guildIdCache = new ConcurrentHashMap<>();
	private static Map<Integer, Long> discordIdCache = new ConcurrentHashMap<>();

	/**
	 * Retrieves the internal guild id for {@link MessageChannel} channel
	 *
	 * @param channel the channel to check
	 * @return internal guild-id OR 0 if no guild could be found
	 */
	public static int getCachedId(MessageChannel channel) {
		if (channel instanceof TextChannel) {
			return getCachedId(((TextChannel) channel).getGuild().getIdLong());
		}
		return 0;
	}

	/**
	 * Gets cached id.
	 *
	 * @param discordId the discord id
	 * @return the cached id
	 */
	public static int getCachedId(long discordId) {
		if (!guildIdCache.containsKey(discordId)) {
			OGuild server = findBy(discordId);
			if (server.id == 0) {
				server.discord_id = discordId;
				server.name = Long.toString(discordId);
				insert(server);
			}
			guildIdCache.put(discordId, server.id);
		}
		return guildIdCache.get(discordId);
	}

	/**
	 * Gets cached discord id l.
	 *
	 * @param id the id
	 * @return the cached discord id l
	 */
	public static long getCachedDiscordIdL(int id) {
		if (!discordIdCache.containsKey(id)) {
			OGuild server = findById(id);
			if (server.id == 0L) {
				return 0L;
			}
			discordIdCache.put(id, server.discord_id);
		}
		return discordIdCache.get(id);
	}

	/**
	 * Gets cached discord id.
	 *
	 * @param id the id
	 * @return the cached discord id
	 */
	public static String getCachedDiscordId(int id) {
		if (!discordIdCache.containsKey(id)) {
			OGuild server = findById(id);
			if (server.id == 0) {
				return "0";
			}
			discordIdCache.put(id, server.discord_id);
		}
		return Long.toString(discordIdCache.get(id));
	}

	/**
	 * Gets most used guilds for.
	 *
	 * @param userId the user id
	 * @return the most used guilds for
	 */
	public static List<OGuild> getMostUsedGuildsFor(int userId) {
		List<OGuild> list = new ArrayList<>();
		try (ResultSet rs = WebDb.get().select("SELECT g.id, discord_id, name, owner, active, banned " + "FROM command_log l "
				+ "JOIN guilds g ON g.id = l.guild " + "WHERE l.user_id = ? " + "GROUP BY g.id ORDER BY count(l.id) DESC LIMIT 10", userId)) {
			while (rs.next()) {
				list.add(loadRecord(rs));
			}
			rs.getStatement().close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * Find by o guild.
	 *
	 * @param discordId the discord id
	 * @return the o guild
	 */
	public static OGuild findBy(long discordId) {
		return findBy(String.valueOf(discordId));
	}

	/**
	 * Find by o guild.
	 *
	 * @param discordId the discord id
	 * @return the o guild
	 */
	public static OGuild findBy(String discordId) {
		OGuild s = new OGuild();
		try (ResultSet rs = WebDb.get().select("SELECT id, discord_id, name, owner,active,banned  " + "FROM guilds " + "WHERE discord_id = ? ", discordId)) {
			if (rs.next()) {
				s = loadRecord(rs);
			}
			rs.getStatement().close();
		} catch (Exception e) {
			Logger.fatal(e);
		}
		return s;
	}

	/**
	 * Find by id o guild.
	 *
	 * @param id the id
	 * @return the o guild
	 */
	public static OGuild findById(int id) {
		OGuild s = new OGuild();
		try (ResultSet rs = WebDb.get().select("SELECT id, discord_id, name, owner,active,banned  " + "FROM guilds " + "WHERE id = ? ", id)) {
			if (rs.next()) {
				s = loadRecord(rs);
			}
			rs.getStatement().close();
		} catch (Exception e) {
			Logger.fatal(e);
		}
		return s;
	}

	/**
	 * Update.
	 *
	 * @param record the record
	 */
	public static void update(OGuild record) {
		if (record.id == 0) {
			insert(record);
			return;
		}
		try {
			WebDb.get().query("UPDATE guilds SET discord_id = ?, name = ?, owner = ?, active = ?, banned = ? " + "WHERE id = ? ", record.discord_id,
					record.name, record.owner == 0 ? null : record.owner, record.active, record.banned, record.id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Insert.
	 *
	 * @param record the record
	 */
	public static void insert(OGuild record) {
		try {
			record.id = WebDb.get().insert("INSERT INTO guilds(discord_id, name, owner,active,banned) " + "VALUES (?,?,?,?,?)", record.discord_id, record.name,
					record.owner == 0 ? null : record.owner, record.active, record.banned);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * retrieves the amount of active guilds note: this value could be higher than
	 * the actual active guilds if the bot missed a leave guild event
	 *
	 * @return active guild count
	 */
	public static int getActiveGuildCount() {
		int amount = 0;
		try (ResultSet rs = WebDb.get().select("SELECT count(id) AS amount FROM guilds WHERE active = 1")) {
			while (rs.next()) {
				amount = rs.getInt("amount");
			}
			rs.getStatement().close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return amount;
	}

	/**
	 * Gets banned guilds.
	 *
	 * @return the banned guilds
	 */
	public static List<OGuild> getBannedGuilds() {
		List<OGuild> list = new ArrayList<>();
		try (ResultSet rs = WebDb.get().select("SELECT * FROM guilds WHERE banned = 1")) {
			while (rs.next()) {
				list.add(loadRecord(rs));
			}
			rs.getStatement().close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	private static OGuild loadRecord(ResultSet rs) throws SQLException {
		OGuild s = new OGuild();
		s.id = rs.getInt("id");
		s.discord_id = rs.getLong("discord_id");
		s.name = rs.getString("name");
		s.owner = rs.getInt("owner");
		s.active = rs.getInt("active");
		s.banned = rs.getInt("banned");
		return s;
	}
}
