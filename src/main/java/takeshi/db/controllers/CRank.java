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
import takeshi.db.model.ORank;

/**
 * data communication with the controllers `ranks`
 */
public class CRank {

	/**
	 * Find by o rank.
	 *
	 * @param codeName the code name
	 * @return the o rank
	 */
	public static ORank findBy(String codeName) {
        ORank s = new ORank();
        try (ResultSet rs = WebDb.get().select(
                "SELECT id, code_name, full_name  " +
                        "FROM ranks " +
                        "WHERE code_name = ? ", codeName)) {
            if (rs.next()) {
                s = fillRecord(rs);
            }
            rs.getStatement().close();
        } catch (Exception e) {
            Logger.fatal(e);
        }
        return s;
    }

	/**
	 * Find by id o rank.
	 *
	 * @param internalId the internal id
	 * @return the o rank
	 */
	public static ORank findById(int internalId) {
        ORank s = new ORank();
        try (ResultSet rs = WebDb.get().select(
                "SELECT id, code_name, full_name  " +
                        "FROM ranks" +
                        " WHERE id = ? ", internalId)) {
            if (rs.next()) {
                s = fillRecord(rs);
            }
            rs.getStatement().close();
        } catch (Exception e) {
            Logger.fatal(e);
        }
        return s;
    }

    private static ORank fillRecord(ResultSet rs) throws SQLException {
        ORank s = new ORank();
        s.id = rs.getInt("id");
        s.codeName = rs.getString("code_name");
        s.fullName = rs.getString("full_name");
        return s;
    }

	/**
	 * Gets ranks.
	 *
	 * @return the ranks
	 */
	public static List<ORank> getRanks() {
        List<ORank> list = new ArrayList<>();
        try (ResultSet rs = WebDb.get().select("SELECT id, code_name, full_name FROM ranks ")) {
            while (rs.next()) {
                list.add(fillRecord(rs));
            }
            rs.getStatement().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

	/**
	 * Update.
	 *
	 * @param record the record
	 */
	public static void update(ORank record) {
        if (record.id == 0) {
            insert(record);
            return;
        }
        try {
            WebDb.get().query(
                    "UPDATE ranks SET code_name = ?, full_name = ? " +
                            "WHERE id = ? ",
                    record.codeName, record.fullName, record.id
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	/**
	 * Insert.
	 *
	 * @param record the record
	 */
	public static void insert(ORank record) {
        try {
            record.id = WebDb.get().insert(
                    "INSERT INTO ranks(code_name, full_name) " +
                            "VALUES (?,?)",
                    record.codeName, record.fullName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
