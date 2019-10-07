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

import takeshi.db.WebDb;
import takeshi.db.model.OMusicLog;

/**
 * data communication with the table `music_log`
 */
public class CMusicLog {


    private static OMusicLog fillRecord(ResultSet rs) throws SQLException {
        OMusicLog s = new OMusicLog();
        s.id = rs.getInt("id");
        s.musicId = rs.getInt("music_id");
        s.guildId = rs.getInt("guild_id");
        s.userId = rs.getInt("use_id");
        s.playDate = rs.getTimestamp("play_date");
        return s;
    }

	/**
	 * Insert.
	 *
	 * @param guildId the guild id
	 * @param musicId the music id
	 * @param userId  the user id
	 */
	public static void insert(int guildId, int musicId, int userId) {
        OMusicLog log = new OMusicLog();
        log.musicId = musicId;
        log.guildId = guildId;
        log.userId = userId;
        log.playDate = new Timestamp(System.currentTimeMillis());
        insert(log);
    }

	/**
	 * Insert.
	 *
	 * @param record the record
	 */
	public static void insert(OMusicLog record) {
        try {
            record.id = WebDb.get().insert(
                    "INSERT INTO music_log(music_id, guild_id, user_id, play_date) " +
                            "VALUES (?,?,?,?)",
                    record.musicId, record.guildId, record.userId, record.playDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
