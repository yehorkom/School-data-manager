package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DdConnector {
	private static final String DB_URL = "jdbc:postgresql://localhost:5432/schoolDB";
	private static final String DB_USERNAME = "postgres";
	private static final String DB_PASSWORD = "1423";
	private static Connection connection;

	private DdConnector() {
		try {
			connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public static Connection getConnection() {
		closeConnection();
		new DdConnector();
		return connection;
	}

	public static void closeConnection() {
		if (connection != null) {

			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException("Failed to close connection");
			}
		}
	}
}
