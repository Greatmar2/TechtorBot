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

import takeshi.core.Logger;
import takeshi.db.WebDb;
import takeshi.db.model.OService;

/**
 * data communication with the controllers `services`
 */
public class CServices {
    private static Map<String, Integer> serviceCache = new ConcurrentHashMap<>();

	/**
	 * Gets cached id.
	 *
	 * @param serviceName the service name
	 * @return the cached id
	 */
	public static int getCachedId(String serviceName) {
        if (!serviceCache.containsKey(serviceName)) {
            OService service = findBy(serviceName);
            if (service.id == 0) {
                service.name = serviceName;
                insert(service);
            }
            serviceCache.put(serviceName, service.id);
        }
        return serviceCache.get(serviceName);
    }

	/**
	 * Gets all active.
	 *
	 * @return the all active
	 */
	public static List<OService> getAllActive() {
        ArrayList<OService> list = new ArrayList<>();
        try (ResultSet rs = WebDb.get().select("SELECT * FROM services WHERE activated = 1")) {
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
	 * Find by o service.
	 *
	 * @param name the name
	 * @return the o service
	 */
	public static OService findBy(String name) {
        OService token = new OService();
        try (ResultSet rs = WebDb.get().select(
                "SELECT id, name, display_name, description, activated  " +
                        "FROM services " +
                        "WHERE name = ? ", name)) {
            if (rs.next()) {
                token = fillRecord(rs);
            }
            rs.getStatement().close();
        } catch (Exception e) {
            Logger.fatal(e);
        }
        return token;
    }

    private static OService fillRecord(ResultSet resultset) throws SQLException {
        OService service = new OService();
        service.id = resultset.getInt("id");
        service.name = resultset.getString("name");
        service.displayName = resultset.getString("display_name");
        service.description = resultset.getString("description");
        service.activated = resultset.getInt("activated");
        return service;
    }

	/**
	 * Update.
	 *
	 * @param record the record
	 */
	public static void update(OService record) {
        if (record.id == 0) {
            insert(record);
            return;
        }
        try {
            WebDb.get().query(
                    "UPDATE services SET name = ?, display_name = ?, description = ?, activated = ? " +
                            "WHERE id = ? ",
                    record.name, record.displayName, record.description, record.activated, record.id
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
	public static void insert(OService record) {
        try {
            record.id = WebDb.get().insert(
                    "INSERT INTO services(name, display_name, description, activated) " +
                            "VALUES (?,?,?,?)",
                    record.name, record.displayName, record.description, record.activated);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}