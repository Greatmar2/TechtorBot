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
import takeshi.db.model.OBetOption;

/**
 * The type C bet option.
 */
public class CBetOption {
	/**
	 * Gets options for bet.
	 *
	 * @param id the id
	 * @return the options for bet
	 */
	public static List<OBetOption> getOptionsForBet(int id) {
        List<OBetOption> ret = new ArrayList<>();
        try (ResultSet rs = WebDb.get().select(
                "SELECT *  " +
                        "FROM bet_options " +
                        "WHERE bet_id = ? ", id)) {
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
	 * Find by id o bet option.
	 *
	 * @param betId the bet id
	 * @param id    the id
	 * @return the o bet option
	 */
	public static OBetOption findById(int betId, int id) {
        OBetOption b = new OBetOption();
        try (ResultSet rs = WebDb.get().select(
                "SELECT *  " +
                        "FROM bet_options " +
                        "WHERE bet_id = ? AND id = ? ", betId, id)) {
            if (rs.next()) {
                b = fillRecord(rs);
            }
            rs.getStatement().close();
        } catch (Exception e) {
            Logger.fatal(e);
        }
        return b;
    }

	/**
	 * Find by id o bet option.
	 *
	 * @param id the id
	 * @return the o bet option
	 */
	public static OBetOption findById(int id) {
        OBetOption b = new OBetOption();
        try (ResultSet rs = WebDb.get().select(
                "SELECT *  " +
                        "FROM bet_options " +
                        "WHERE id = ? ", id)) {
            if (rs.next()) {
                b = fillRecord(rs);
            }
            rs.getStatement().close();
        } catch (Exception e) {
            Logger.fatal(e);
        }
        return b;
    }

    private static OBetOption fillRecord(ResultSet rs) throws SQLException {
        OBetOption b = new OBetOption();
        b.id = rs.getInt("id");
        b.betId = rs.getInt("bet_id");
        b.description = rs.getString("description");
        return b;
    }

	/**
	 * Delete.
	 *
	 * @param record the record
	 */
	public static void delete(OBetOption record) {
        try {
            WebDb.get().query(
                    "DELETE FROM bet_options WHERE id = ? ",
                    record.id
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	/**
	 * Delete options for.
	 *
	 * @param betId the bet id
	 */
	public static void deleteOptionsFor(int betId) {
        try {
            WebDb.get().query(
                    "DELETE FROM bet_options WHERE bet_id = ? ",
                    betId
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
	public static void update(OBetOption record) {
        try {
            record.id = WebDb.get().query(
                    "UPDATE bet_options SET description = ? WHERE id = ?",
                    record.description, record.id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	/**
	 * Insert.
	 *
	 * @param record the record
	 */
	public static void insert(OBetOption record) {
        if (record.id > 0) {
            update(record);
            return;
        }
        try {
            record.id = WebDb.get().insert(
                    "INSERT INTO bet_options(bet_id, description) " +
                            "VALUES (?,?)",
                    record.betId, record.description);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	/**
	 * Add option.
	 *
	 * @param betId       the bet id
	 * @param description the description
	 */
	public static void addOption(int betId, String description) {
        OBetOption b = new OBetOption();
        b.betId = betId;
        b.description = description;
        insert(b);
    }
}
