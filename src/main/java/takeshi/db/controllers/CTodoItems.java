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
import takeshi.db.model.OTodoItem;

/**
 * The type C todo items.
 */
public class CTodoItems {

	/**
	 * Find by o todo item.
	 *
	 * @param id the id
	 * @return the o todo item
	 */
	public static OTodoItem findBy(int id) {
        OTodoItem t = new OTodoItem();
        try (ResultSet rs = WebDb.get().select(
                "SELECT *  " +
                        "FROM todo_item " +
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
	 * Gets list for.
	 *
	 * @param listId the list id
	 * @return the list for
	 */
	public static List<OTodoItem> getListFor(int listId) {
        ArrayList<OTodoItem> ret = new ArrayList<>();
        try (ResultSet rs = WebDb.get().select(
                "SELECT *  " +
                        "FROM todo_item " +
                        "WHERE list_id= ? " +
                        "ORDER BY priority DESC, id ASC", listId)) {
            while (rs.next()) {
                ret.add(fillRecord(rs));
            }
            rs.getStatement().close();
        } catch (Exception e) {
            Logger.fatal(e);
        }
        return ret;
    }

    private static OTodoItem fillRecord(ResultSet rs) throws SQLException {
        OTodoItem t = new OTodoItem();
        t.id = rs.getInt("id");
        t.listId = rs.getInt("list_id");
        t.description = rs.getString("description");
        t.checked = rs.getInt("checked");
        t.priority = rs.getInt("priority");
        return t;
    }

	/**
	 * Delete.
	 *
	 * @param record the record
	 */
	public static void delete(OTodoItem record) {
        try {
            WebDb.get().query(
                    "DELETE FROM todo_item WHERE id = ? ",
                    record.id
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	/**
	 * Delete checked.
	 *
	 * @param listId the list id
	 */
	public static void deleteChecked(int listId) {
        try {
            WebDb.get().query(
                    "DELETE FROM todo_item WHERE list_id = ? AND checked = 1",
                    listId
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
	public static void update(OTodoItem record) {
        if (record.id == 0) {
            insert(record);
            return;
        }
        try {
            WebDb.get().query(
                    "UPDATE todo_item SET description = ?, checked = ?, priority = ? WHERE id = ?",
                    record.description, record.checked, record.priority, record.id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	/**
	 * Insert.
	 *
	 * @param record the record
	 */
	public static void insert(OTodoItem record) {
        if (record.id > 0) {
            update(record);
            return;
        }
        try {
            record.id = WebDb.get().insert(
                    "INSERT INTO todo_item(list_id, description, checked, priority) " +
                            "VALUES (?,?,?,?)",
                    record.listId, record.description, record.checked, record.priority);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
