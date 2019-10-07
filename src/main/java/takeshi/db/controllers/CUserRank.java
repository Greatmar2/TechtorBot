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
import takeshi.db.model.OUserRank;

/**
 * data communication with the controllers `user_rank`
 */
public class CUserRank {

	/**
	 * Find by o user rank.
	 *
	 * @param userDiscordId the user discord id
	 * @return the o user rank
	 */
	public static OUserRank findBy(long userDiscordId) {
        return findBy(CUser.getCachedId(userDiscordId));
    }

	/**
	 * Find by o user rank.
	 *
	 * @param userId the user id
	 * @return the o user rank
	 */
	public static OUserRank findBy(int userId) {
        OUserRank record = new OUserRank();
        try (ResultSet rs = WebDb.get().select(
                "SELECT user_id, rank_type  " +
                        "FROM user_rank " +
                        "WHERE user_id = ? ", userId)) {
            if (rs.next()) {
                record = fillRecord(rs);
            } else {
                record.userId = userId;
                record.rankId = 0;
            }
            rs.getStatement().close();
        } catch (Exception e) {
            Logger.fatal(e);
        }
        return record;
    }

	/**
	 * Gets users with.
	 *
	 * @param rankId the rank id
	 * @return the users with
	 */
	public static List<OUserRank> getUsersWith(int rankId) {
        List<OUserRank> list = new ArrayList<>();
        try (ResultSet rs = WebDb.get().select(
                "SELECT user_id, rank_type  " +
                        "FROM user_rank " +
                        "WHERE rank_type = ? ", rankId)) {
            while (rs.next()) {
                list.add(fillRecord(rs));
            }
            rs.getStatement().close();
        } catch (Exception e) {
            Logger.fatal(e);
        }
        return list;
    }

    private static OUserRank fillRecord(ResultSet resultset) throws SQLException {
        OUserRank record = new OUserRank();
        record.userId = resultset.getInt("user_id");
        record.rankId = resultset.getInt("rank_type");
        return record;
    }

	/**
	 * Insert or update.
	 *
	 * @param record the record
	 */
	public static void insertOrUpdate(OUserRank record) {
        try {
            WebDb.get().insert(
                    "INSERT INTO user_rank(user_id, rank_type) " +
                            "VALUES (?,?) ON DUPLICATE KEY UPDATE rank_type = ?",
                    record.userId, record.rankId, record.rankId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
