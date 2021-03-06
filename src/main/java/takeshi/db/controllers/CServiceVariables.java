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

import takeshi.core.Logger;
import takeshi.db.WebDb;
import takeshi.db.model.OServiceVariable;

/**
 * data communication with the controllers `service_variables`
 */
public class CServiceVariables {

	/**
	 * Find by o service variable.
	 *
	 * @param serviceName the service name
	 * @param variable    the variable
	 * @return the o service variable
	 */
	public static OServiceVariable findBy(String serviceName, String variable) {
        return findBy(CServices.getCachedId(serviceName), variable);
    }

	/**
	 * Find by o service variable.
	 *
	 * @param serviceId the service id
	 * @param variable  the variable
	 * @return the o service variable
	 */
	public static OServiceVariable findBy(int serviceId, String variable) {
        OServiceVariable record = new OServiceVariable();
        try (ResultSet rs = WebDb.get().select(
                "SELECT *  " +
                        "FROM service_variables " +
                        "WHERE service_id = ? AND variable = ? ", serviceId, variable)) {
            if (rs.next()) {
                record = fillRecord(rs);
            }
            rs.getStatement().close();
        } catch (Exception e) {
            Logger.fatal(e);
        }
        return record;
    }

    private static OServiceVariable fillRecord(ResultSet resultset) throws SQLException {
        OServiceVariable record = new OServiceVariable();
        record.serviceId = resultset.getInt("service_id");
        record.variable = resultset.getString("variable");
        record.value = resultset.getString("value");
        return record;
    }

	/**
	 * Insert or update.
	 *
	 * @param record the record
	 */
	public static void insertOrUpdate(OServiceVariable record) {
        try {
            WebDb.get().insert(
                    "INSERT INTO service_variables(service_id, variable, value) " +
                            "VALUES (?,?,?) ON DUPLICATE KEY UPDATE value = ?",
                    record.serviceId, record.variable, record.value, record.value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
