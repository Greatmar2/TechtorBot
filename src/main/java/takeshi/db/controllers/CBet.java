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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import takeshi.core.Logger;
import takeshi.db.WebDb;
import takeshi.db.model.OBet;

/**
 * The type C bet.
 */
public class CBet {
	/**
	 * The constant MAX_BET_AMOUNT.
	 */
	public static final int MAX_BET_AMOUNT = 1_000_000;
	/**
	 * The constant MIN_BET_OPTIONS.
	 */
	public static final int MIN_BET_OPTIONS = 2;

	/**
	 * Gets active bets for guild.
	 *
	 * @param id the id
	 * @return the active bets for guild
	 */
	public static List<OBet> getActiveBetsForGuild(int id) {
        List<OBet> ret = new ArrayList<>();
        try (ResultSet rs = WebDb.get().select(
                "SELECT *  " +
                        "FROM bets " +
                        "WHERE guild_id = ? AND bet_status = ? ", id, OBet.Status.ACTIVE.getId())) {
            while (rs.next()) {
                ret.add(fillRecord(rs));
            }
            rs.getStatement().close();
        } catch (Exception e) {
            Logger.fatal(e);
        }
        return ret;
    }

	/**
	 * Find by id o bet.
	 *
	 * @param id the id
	 * @return the o bet
	 */
	public static OBet findById(int id) {
        OBet t = new OBet();
        try (ResultSet rs = WebDb.get().select(
                "SELECT *  " +
                        "FROM bets " +
                        "WHERE id = ? ", id)) {
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
	 * Gets active bet.
	 *
	 * @param guildId the guild id
	 * @param userId  the user id
	 * @return the active bet
	 */
	public static OBet getActiveBet(int guildId, int userId) {
        OBet t = new OBet();
        try (ResultSet rs = WebDb.get().select(
                "SELECT *  " +
                        "FROM bets " +
                        "WHERE guild_id = ? AND owner_id= ? AND bet_status IN (?,?,?)", guildId, userId,
                OBet.Status.PREPARING.getId(), OBet.Status.PENDING.getId(), OBet.Status.ACTIVE.getId())) {
            if (rs.next()) {
                t = fillRecord(rs);
            }
            rs.getStatement().close();
        } catch (Exception e) {
            Logger.fatal(e);
        }
        return t;
    }

    private static OBet fillRecord(ResultSet rs) throws SQLException {
        OBet b = new OBet();
        b.id = rs.getInt("id");
        b.title = rs.getString("title");
        b.ownerId = rs.getInt("owner_id");
        b.guildId = rs.getInt("guild_id");
        b.createdOn = rs.getTimestamp("created_on");
        b.startedOn = rs.getTimestamp("started_on");
        b.endsAt = rs.getTimestamp("ends_at");
        b.price = rs.getInt("price");
        return b;
    }

	/**
	 * Delete.
	 *
	 * @param record the record
	 */
	public static void delete(OBet record) {
        try {
            WebDb.get().query(
                    "DELETE FROM bets WHERE id = ? ",
                    record.id
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	/**
	 * Update.
	 *
	 * @param record the record
	 */
	public static void update(OBet record) {
        try {
            record.id = WebDb.get().query(
                    "UPDATE bets SET title = ?, started_on = ?, ends_at = ?, price = ? WHERE id = ?",
                    record.title, record.startedOn, record.endsAt, record.price, record.id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	/**
	 * Insert.
	 *
	 * @param record the record
	 */
	public static void insert(OBet record) {
        if (record.id > 0) {
            update(record);
            return;
        }
        if (record.createdOn == null) {
            record.createdOn = new Timestamp(System.currentTimeMillis());
        }
        try {
            record.id = WebDb.get().insert(
                    "INSERT INTO bets(title, owner_id, guild_id, created_on, started_on, ends_at, price,bet_status) " +
                            "VALUES (?,?,?,?,?,?,?,?)",
                    record.title, record.ownerId, record.guildId, record.createdOn, record.startedOn, record.endsAt, record.price, record.status.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	/**
	 * Create bet.
	 *
	 * @param title   the title
	 * @param price   the price
	 * @param guildId the guild id
	 * @param userId  the user id
	 */
	public static void createBet(String title, int price, int guildId, int userId) {
        OBet b = new OBet();
        b.title = title;
        b.price = price;
        b.guildId = guildId;
        b.ownerId = userId;
        insert(b);
    }
}
