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
import takeshi.db.model.OTag;

/**
 * The type C tag.
 */
public class CTag {

	/**
	 * Find by o tag.
	 *
	 * @param discordGuildId the discord guild id
	 * @param tagname        the tagname
	 * @return the o tag
	 */
	public static OTag findBy(long discordGuildId, String tagname) {
        return findBy(CGuild.getCachedId(discordGuildId), tagname);
    }

	/**
	 * Find by o tag.
	 *
	 * @param serverId the server id
	 * @param tagName  the tag name
	 * @return the o tag
	 */
	public static OTag findBy(int serverId, String tagName) {
        OTag t = new OTag();
        try (ResultSet rs = WebDb.get().select(
                "SELECT *  " +
                        "FROM tags " +
                        "WHERE guild_id = ? AND tag_name = ? ", serverId, tagName)) {
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
	 * Count tags on int.
	 *
	 * @param guildId the guild id
	 * @return the int
	 */
	public static int countTagsOn(int guildId) {
        int tagCount = 0;
        try (ResultSet rs = WebDb.get().select(
                "SELECT count(*) AS sum  " +
                        "FROM tags " +
                        "WHERE guild_id = ? ", guildId)) {
            if (rs.next()) {
                tagCount = rs.getInt("sum");
            }
            rs.getStatement().close();
        } catch (Exception e) {
            Logger.fatal(e);
        }
        return tagCount;
    }


	/**
	 * Gets tags for.
	 *
	 * @param guildDiscordId the guild discord id
	 * @param userDiscordId  the user discord id
	 * @return the tags for
	 */
	public static List<OTag> getTagsFor(long guildDiscordId, long userDiscordId) {
        return getTagsFor(CGuild.getCachedId(guildDiscordId), CUser.getCachedId(userDiscordId));
    }

	/**
	 * Gets tags for.
	 *
	 * @param guildId the guild id
	 * @param userId  the user id
	 * @return the tags for
	 */
	public static List<OTag> getTagsFor(int guildId, int userId) {
        List<OTag> result = new ArrayList<>();
        try (ResultSet rs = WebDb.get().select(
                "SELECT *  " +
                        "FROM tags " +
                        "WHERE guild_id = ? AND user_id = ? ", guildId, userId)) {
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
	 * Gets tags for.
	 *
	 * @param guildDiscordId the guild discord id
	 * @return the tags for
	 */
	public static List<OTag> getTagsFor(long guildDiscordId) {
        return getTagsFor(guildDiscordId, 0, 25);
    }

	/**
	 * Gets tags for.
	 *
	 * @param guildDiscordId the guild discord id
	 * @param offset         the offset
	 * @param limit          the limit
	 * @return the tags for
	 */
	public static List<OTag> getTagsFor(long guildDiscordId, int offset, int limit) {
        return getTagsFor(CGuild.getCachedId(guildDiscordId), offset, limit);
    }

	/**
	 * Gets tags for.
	 *
	 * @param guildId the guild id
	 * @param offset  the offset
	 * @param limit   the limit
	 * @return the tags for
	 */
	public static List<OTag> getTagsFor(int guildId, int offset, int limit) {
        List<OTag> result = new ArrayList<>();
        try (ResultSet rs = WebDb.get().select(
                "SELECT *  " +
                        "FROM tags " +
                        "WHERE guild_id = ? " +
                        "LIMIT ?,? ", guildId, offset, limit)) {
            while (rs.next()) {
                result.add(fillRecord(rs));
            }
            rs.getStatement().close();
        } catch (Exception e) {
            Logger.fatal(e);
        }
        return result;
    }

    private static OTag fillRecord(ResultSet rs) throws SQLException {
        OTag t = new OTag();
        t.id = rs.getInt("id");
        t.tagname = rs.getString("tag_name");
        t.guildId = rs.getInt("guild_id");
        t.response = rs.getString("response");
        t.userId = rs.getInt("user_id");
        t.created = rs.getTimestamp("creation_date");
        return t;
    }

	/**
	 * Delete.
	 *
	 * @param record the record
	 */
	public static void delete(OTag record) {
        try {
            WebDb.get().query(
                    "DELETE FROM tags WHERE tag_name = ? AND guild_id = ? ",
                    record.tagname, record.guildId
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
	public static void update(OTag record) {
        try {
            record.id = WebDb.get().query(
                    "UPDATE tags SET response = ? WHERE id = ?",
                    record.response, record.id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	/**
	 * Insert.
	 *
	 * @param record the record
	 */
	public static void insert(OTag record) {
        if (record.id > 0) {
            update(record);
            return;
        }
        try {
            record.id = WebDb.get().insert(
                    "INSERT INTO tags(tag_name, guild_id, response, user_id, creation_date) " +
                            "VALUES (?,?,?,?,?)",
                    record.tagname, record.guildId, record.response, record.userId, record.created);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	/**
	 * Delete tags by.
	 *
	 * @param guildId the guild id
	 * @param userId  the user id
	 */
	public static void deleteTagsBy(int guildId, int userId) {
        try {
            WebDb.get().query(
                    "DELETE FROM tags WHERE guild_id = ? AND user_id= ? ",
                    guildId, userId
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	/**
	 * Find by user list.
	 *
	 * @param guildId the guild id
	 * @param userId  the user id
	 * @return the list
	 */
	public static List<OTag> findByUser(int guildId, int userId) {
        List<OTag> result = new ArrayList<>();
        try (ResultSet rs = WebDb.get().select(
                "SELECT *  " +
                        "FROM tags " +
                        "WHERE guild_id = ? AND user_id = ?", guildId, userId)) {
            while (rs.next()) {
                result.add(fillRecord(rs));
            }
            rs.getStatement().close();
        } catch (Exception e) {
            Logger.fatal(e);
        }
        return result;
    }
}
