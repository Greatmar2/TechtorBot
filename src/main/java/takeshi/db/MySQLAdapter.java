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

package takeshi.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;

import com.mysql.cj.jdbc.MysqlDataSource;

import takeshi.core.ExitCode;
import takeshi.exceptions.UnimplementedParameterException;
import takeshi.main.DiscordBot;
import takeshi.main.Launcher;

/**
 * The type My sql adapter.
 */
public class MySQLAdapter {

	private String DB_NAME;
	private String DB_USER;
	private String DB_ADRES;
	private int DB_PORT;
	private String DB_PASSWORD;
	private Connection c;

	/**
	 * Instantiates a new My sql adapter.
	 *
	 * @param server           the server
	 * @param port             the port
	 * @param databaseUser     the database user
	 * @param databasePassword the database password
	 * @param databaseName     the database name
	 */
	public MySQLAdapter(String server, int port, String databaseUser, String databasePassword, String databaseName) {
		DB_ADRES = server;
		DB_USER = databaseUser;
		DB_PASSWORD = databasePassword;
		DB_NAME = databaseName;
		DB_PORT = port;
	}

	private Connection createConnection() {
		try {
			MysqlDataSource dataSource = new MysqlDataSource();
			dataSource.setUser(DB_USER);
			dataSource.setPassword(DB_PASSWORD);
			dataSource.setServerName(DB_ADRES);
			dataSource.setPort(DB_PORT);
			dataSource.setDatabaseName(DB_NAME);
			dataSource.setZeroDateTimeBehavior("CONVERT_TO_NULL");
			dataSource.setServerTimezone("UTC");
			dataSource.setCharacterEncoding("UTF-8");
//            dataSource.unisetUseUnicode(true);
			return dataSource.getConnection();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			DiscordBot.LOGGER
					.error("Can't connect to the database! Make sure the database settings are corrent and the database server is running AND the database `"
							+ DB_NAME + "` exists");
			Launcher.stop(ExitCode.BAD_CONFIG, e);
		}
		return null;
	}

	/**
	 * Gets connection.
	 *
	 * @return the connection
	 */
	public Connection getConnection() {
		if (c == null) {
			c = createConnection();
		}
		return c;
	}

	/**
	 * Select result set.
	 *
	 * @param sql    the sql
	 * @param params the params
	 * @return the result set
	 * @throws SQLException the sql exception
	 */
	public ResultSet select(String sql, Object... params) throws SQLException {
		PreparedStatement query;
		query = getConnection().prepareStatement(sql);
		resolveParameters(query, params);
		return query.executeQuery();
	}

	/**
	 * Query int.
	 *
	 * @param sql the sql
	 * @return the int
	 * @throws SQLException the sql exception
	 */
	public int query(String sql) throws SQLException {
		try (Statement stmt = getConnection().createStatement()) {
			return stmt.executeUpdate(sql);
		}
	}

	private void resolveParameters(PreparedStatement query, Object... params) throws SQLException {
		int index = 1;
		for (Object p : params) {
			if (p instanceof String) {
				query.setString(index, (String) p);
			} else if (p instanceof Integer) {
				query.setInt(index, (int) p);
			} else if (p instanceof Long) {
				query.setLong(index, (Long) p);
			} else if (p instanceof Double) {
				query.setDouble(index, (double) p);
			} else if (p instanceof Boolean) {
				query.setBoolean(index, (boolean) p);
			} else if (p instanceof java.sql.Date) {
				java.sql.Date d = (java.sql.Date) p;
				Timestamp ts = new Timestamp(d.getTime());
				query.setTimestamp(index, ts);
			} else if (p instanceof java.util.Date) {
				java.util.Date d = (java.util.Date) p;
				Timestamp ts = new Timestamp(d.getTime());
				query.setTimestamp(index, ts);
			} else if (p instanceof Calendar) {
				Calendar cal = (Calendar) p;
				Timestamp ts = new Timestamp(cal.getTimeInMillis());
				query.setTimestamp(index, ts);
			} else if (p == null) {
				query.setNull(index, Types.NULL);
			} else {
				throw new UnimplementedParameterException(p, index);
			}
			index++;
		}
	}

	/**
	 * Query int.
	 *
	 * @param sql    the sql
	 * @param params the params
	 * @return the int
	 * @throws SQLException the sql exception
	 */
	public int query(String sql, Object... params) throws SQLException {
		try (PreparedStatement query = getConnection().prepareStatement(sql)) {
			resolveParameters(query, params);
			return query.executeUpdate();
		}
	}

	/**
	 * Insert int.
	 *
	 * @param sql    the sql
	 * @param params the params
	 * @return the int
	 * @throws SQLException the sql exception
	 */
	public int insert(String sql, Object... params) throws SQLException {
		try (PreparedStatement query = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			resolveParameters(query, params);
			query.executeUpdate();
			ResultSet rs = query.getGeneratedKeys();

			if (rs.next()) {
				return rs.getInt(1);
			}
		}
		return -1;
	}
}
