package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class DataGenerator {

	public void generateData() {
		generateGroupsData();
		generateStudentsData();
		generateCoursesData();
		generateStudentsCoursesData();
	}
	Random random = new Random();
	private void generateGroupsData(){
		try (Connection connection = DdConnector.getConnection()) {
			String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};

			for (int i = 0; i < 10; i++) {
				String groupName = letters[random.nextInt(letters.length)] + letters[random.nextInt(letters.length)] + "-" + random.nextInt(10) + random.nextInt(10);

				PreparedStatement statement = connection.prepareStatement("INSERT INTO groups (group_name) VALUES (?)");
				statement.setString(1, groupName);
				statement.executeUpdate();
				statement.close();
			}
		} catch (SQLException e) {
			System.out.println("'log' Error during generation Group Data");
            throw new RuntimeException(e);
        }
    }

	private void generateStudentsData(){
		try (Connection connection = DdConnector.getConnection()) {
			String[] firstNames = {"John", "Jane", "Michael", "Mary", "David", "Susan", "Robert", "Lisa", "William", "Jessica"};
			String[] lastNames = {"Doe", "Smith", "Jones", "Johnson", "Williams", "Brown", "Taylor", "Davis", "White", "Miller"};

			for (int i = 0; i < 200; i++) {
				String firstName = firstNames[random.nextInt(firstNames.length)];
				String lastName = lastNames[random.nextInt(lastNames.length)];
				int groupId = random.nextInt(10) + 1;

				PreparedStatement statement = connection.prepareStatement("INSERT INTO students (first_name, last_name, group_id) VALUES (?, ?, ?)");
				statement.setString(1, firstName);
				statement.setString(2, lastName);
				statement.setInt(3, groupId);
				statement.executeUpdate();
				statement.close();
			}
		} catch (SQLException e) {
			System.out.println("'log' Error during generation Student Data");
			throw new RuntimeException(e);
        }
    }

	private void generateCoursesData(){
		try (Connection connection = DdConnector.getConnection()) {
			String[] courseNames = {"Math", "Biology", "Physics", "Chemistry", "History", "English", "Geography", "Computer Science", "Art", "Music"};

			for (int i = 0; i < 10; i++) {
				String courseName = courseNames[i];
				String description = courseName + " course";

				PreparedStatement statement = connection.prepareStatement("INSERT INTO courses (course_name,course_description) VALUES (?, ?) ON CONFLICT DO NOTHING");
				statement.setString(1, courseName);
				statement.setString(2, description);
				statement.executeUpdate();
				statement.close();
			}
		}catch (SQLException e) {
			System.out.println("'log' Error during generation Courses Data");
			throw new RuntimeException(e);
        }
    }
	private void generateStudentsCoursesData(){
		try (Connection connection = DdConnector.getConnection()) {

			for (int i = 0; i < 200; i++) {
				int studentId = i + 1;
				int numberOfCourses = random.nextInt(3) + 1;

				for (int j = 0; j < numberOfCourses; j++) {
					int courseId = random.nextInt(10) + 1;

					PreparedStatement statement = connection.prepareStatement("INSERT INTO students_courses (student_id, course_id) VALUES (?, ?) ON CONFLICT DO NOTHING");
					statement.setInt(1, studentId);
					statement.setInt(2, courseId);
					statement.executeUpdate();
					statement.close();
				}
			}
		} catch (SQLException e) {
			System.out.println("'log' Error during generation Student-Courses Data");
			throw new RuntimeException(e);
        }
    }
}
