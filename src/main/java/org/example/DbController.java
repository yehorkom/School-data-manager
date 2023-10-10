package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Objects;

public class DbController {

	public void createTablesAndFillIn() throws IOException {
		DataGenerator dataGenerator = new DataGenerator();
		BufferedReader sqlFile = readSQLFile();

		try {
			if (tablesExist()){
				dropTables();
			}
		} catch (SQLException e) {
			System.out.println("'log' Error during table check");
			throw new RuntimeException(e);
		}
		createTable(sqlFile);
		sqlFile.close();
		dataGenerator.generateData();
	}

	private BufferedReader readSQLFile() {
		InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("tableQuery.sql");

		return new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)));
	}

	public boolean tablesExist() throws SQLException {
		try (Connection connection = DdConnector.getConnection();
			 Statement statement = connection.createStatement()) {
			ResultSet resultSet = statement.executeQuery(
				"SELECT EXISTS (" +
					"   SELECT 1 " +
					"   FROM information_schema.tables " +
					"   WHERE table_name = 'students_courses' " +
					"      OR table_name = 'students' " +
					"      OR table_name = 'courses'" +
					"	   OR table_name = 'groups'" +
					")"
			);
			resultSet.next();

			return resultSet.getBoolean(1);
		}
	}

	public void dropTables() throws SQLException {
		try (Connection connection = DdConnector.getConnection();
			 Statement statement = connection.createStatement()){
			statement.executeUpdate("DROP TABLE IF EXISTS groups, students, courses, students_courses");
			System.out.println("Tables dropped successfully.");
		}
	}

	private void createTable(BufferedReader sqlFile) {
		try (Connection connection = DdConnector.getConnection();
			 Statement statement = connection.createStatement()) {

			String line;
			StringBuilder sqlQuery = new StringBuilder();

			while ((line = sqlFile.readLine()) != null) {

				//rewrite??
				if (!line.trim().isEmpty() && !line.trim().startsWith("--")) {
					sqlQuery.append(line);

					if (line.trim().endsWith(";")) {
						statement.execute(sqlQuery.toString());
							sqlQuery.setLength(0);
					}
				}
			}
		} catch (SQLException | IOException e) {
			System.out.println("'log' Error in table creating process");
            throw new RuntimeException(e);
        }
    }
}

