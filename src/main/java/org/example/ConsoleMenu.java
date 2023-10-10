package org.example;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

	public class ConsoleMenu {
		private static final String INVALID_INPUT_MESSAGE = "Invalid input. Please enter a numeric.";
		public void launchingConsole() {
			try (Connection connection = DdConnector.getConnection()) {
				Scanner scanner = new Scanner(System.in);

				while (true) {
					System.out.println("Choose an option:");
					System.out.println("a. Find all groups with less or equal students’ number");
					System.out.println("b. Find all students related to the course with the given name");
					System.out.println("c. Add a new student");
					System.out.println("d. Delete a student by the STUDENT_ID");
					System.out.println("e. Add a student to the course (from a list)");
					System.out.println("f. Remove the student from one of their courses");
					System.out.println("q. Quit");

					String option = scanner.nextLine().trim().toLowerCase();

					switch (option) {
						case "a":
							findAllGroupsWithLessOrEqualStudents(connection);
							break;
						case "b":
							findStudentsRelatedToCourse(connection);
							break;
						case "c":
							addNewStudent(connection);
							break;
						case "d":
							deleteStudent(connection);
							break;
						case "e":
							addStudentToCourse(connection);
							break;
						case "f":
							removeStudentFromCourse(connection);
							break;
						case "q":
							System.out.println("Exiting.");
							return;
						default:
							System.out.println("Invalid option. Please try again.");
					}
				}
			} catch (SQLException e) {
				System.out.println("'log' Error during console menu working");
				e.printStackTrace();
			}
		}

		private void findAllGroupsWithLessOrEqualStudents(Connection connection) throws SQLException {
			int maxStudents;
			try {
				System.out.print("Enter the maximum number of students: ");
				maxStudents = Integer.parseInt(new Scanner(System.in).nextLine().trim());
			} catch (NumberFormatException e) {
				System.out.println(INVALID_INPUT_MESSAGE);
				return;
			}

			String sql = "SELECT group_name " +
				"FROM groups " +
				"WHERE (SELECT COUNT(*) FROM students WHERE students.group_id = groups.group_id) <= ?";

			try (PreparedStatement statement = connection.prepareStatement(sql)) {
				statement.setInt(1, maxStudents);
				ResultSet resultSet = statement.executeQuery();

				if (resultSet.next()){
					System.out.println("Groups with less or equal students' number:");
				} else System.out.println("There are no groups with that amount of students.");

				while (resultSet.next()) {
					String groupName = resultSet.getString("group_name");
					System.out.println(groupName);
				}
			}
		}

		private void findStudentsRelatedToCourse(Connection connection) throws SQLException {
			System.out.print("Enter the course name: ");
			String courseName = new Scanner(System.in).nextLine().trim();

			String sql = "SELECT students.first_name, students.last_name " +
				"FROM students " +
				"INNER JOIN students_courses ON students.student_id = students_courses.student_id " +
				"INNER JOIN courses ON students_courses.course_id = courses.course_id " +
				"WHERE LOWER(courses.course_name) = LOWER(?)";

			try (PreparedStatement statement = connection.prepareStatement(sql)) {
				statement.setString(1, courseName.toLowerCase());
				ResultSet resultSet = statement.executeQuery();

				if (resultSet.next()){
					System.out.println("Students related to the course " + courseName + ":");
				} else System.out.println("Invalid course or no students on this course.");

				while (resultSet.next()) {
					String firstName = resultSet.getString("first_name");
					String lastName = resultSet.getString("last_name");
					System.out.println(firstName + " " + lastName);
				}
			}
		}

		private void addNewStudent(Connection connection) throws SQLException {
			System.out.print("Enter first name: ");
			String firstName = new Scanner(System.in).nextLine().trim();
			System.out.print("Enter last name: ");
			String lastName = new Scanner(System.in).nextLine().trim();
			System.out.print("Enter group ID: ");

			int groupId;
			try {
				groupId = Integer.parseInt(new Scanner(System.in).nextLine().trim());
			} catch (NumberFormatException e){
				System.out.println(INVALID_INPUT_MESSAGE);
				return;
			}
			String sql = "INSERT INTO students (group_id, first_name, last_name) VALUES (?, ?, ?)";

			try (PreparedStatement statement = connection.prepareStatement(sql)) {
				statement.setInt(1, groupId);
				statement.setString(2, firstName);
				statement.setString(3, lastName);
				statement.executeUpdate();
				System.out.println("Student added successfully.");
			}
		}

		private void deleteStudent(Connection connection) throws SQLException {
			System.out.print("Enter STUDENT_ID to delete: ");

			int studentId;
			try {
				studentId = Integer.parseInt(new Scanner(System.in).nextLine().trim());
			} catch (NumberFormatException e){
				System.out.println(INVALID_INPUT_MESSAGE);
				return;
			}

			String deleteStudentCoursesSql = "DELETE FROM students_courses WHERE student_id = ?";

			try (PreparedStatement deleteStudentCoursesStatement = connection.prepareStatement(deleteStudentCoursesSql)) {
				deleteStudentCoursesStatement.setInt(1, studentId);
				int rowsDeleted = deleteStudentCoursesStatement.executeUpdate();
				if (rowsDeleted > 0) {
					System.out.println("Student's course enrollments deleted successfully.");
				}
			}

			String deleteStudentSql = "DELETE FROM students WHERE student_id = ?";

			try (PreparedStatement deleteStudentStatement = connection.prepareStatement(deleteStudentSql)) {
				deleteStudentStatement.setInt(1, studentId);
				int rowsDeleted = deleteStudentStatement.executeUpdate();

				if (rowsDeleted > 0) {
					System.out.println("Student with STUDENT_ID " + studentId + " deleted successfully.");
				} else {
					System.out.println("No student with STUDENT_ID " + studentId + " found.");
				}
			}
		}

		private void addStudentToCourse(Connection connection) throws SQLException {
			List<Course> courses = getCoursesFromDatabase(connection);

			System.out.println("Available Courses:");
			for (int i = 0; i < courses.size(); i++) {
				System.out.println((i + 1) + ". " + courses.get(i).courseName());
			}

			int courseId;
			try {
				System.out.print("Enter the number of the course to add the student to: ");
				int courseChoice = Integer.parseInt(new Scanner(System.in).nextLine().trim());

				if (courseChoice < 1 || courseChoice > courses.size()) {
					System.out.println("Invalid course selection.");
					return;
				}
				courseId = courses.get(courseChoice - 1).courseId();
			}catch (NumberFormatException e){
				System.out.println(INVALID_INPUT_MESSAGE);
				return;
			}

			int studentId;
			try {
				System.out.print("Enter STUDENT_ID to add to the course: ");
				studentId = Integer.parseInt(new Scanner(System.in).nextLine().trim());
			} catch (NumberFormatException e) {
				System.out.println("Invalid input. Please enter a valid student ID (numeric).");
				return;
			}

			String sql = "INSERT INTO students_courses (student_id, course_id) VALUES (?, ?)";

			try (PreparedStatement statement = connection.prepareStatement(sql)) {
				statement.setInt(1, studentId);
				statement.setInt(2, courseId);
				statement.executeUpdate();
				System.out.println("Student added to the course successfully.");
			} catch (SQLException e) {
				System.out.println("Invalid student ID. Range of students - 200");
            }
        }

		private List<Course> getCoursesFromDatabase(Connection connection) throws SQLException {
			List<Course> courses = new ArrayList<>();

			String sql = "SELECT course_id, course_name FROM courses";

			try (PreparedStatement statement = connection.prepareStatement(sql);
				 ResultSet resultSet = statement.executeQuery()) {

				while (resultSet.next()) {
					int courseId = resultSet.getInt("course_id");
					String courseName = resultSet.getString("course_name");
					courses.add(new Course(courseId, courseName));
				}
			}

			return courses;
		}

		private void removeStudentFromCourse(Connection connection) throws SQLException {
			System.out.print("Enter STUDENT_ID to remove from the course: ");

			int studentId;
			try {
				studentId = Integer.parseInt(new Scanner(System.in).nextLine().trim());
			} catch (NumberFormatException e){
				System.out.println(INVALID_INPUT_MESSAGE);
				return;
			}

			System.out.print("Enter COURSE_ID to remove the student from: ");

			int courseId;
			try {
				courseId = Integer.parseInt(new Scanner(System.in).nextLine().trim());
			} catch (NumberFormatException e){
				System.out.println(INVALID_INPUT_MESSAGE);
				return;
			}

			String sql = "DELETE FROM students_courses WHERE student_id = ? AND course_id = ?";

			try (PreparedStatement statement = connection.prepareStatement(sql)) {
				statement.setInt(1, studentId);
				statement.setInt(2, courseId);
				int rowsDeleted = statement.executeUpdate();

				if (rowsDeleted > 0) {
					System.out.println("Student removed from the course successfully.");
				} else {
					System.out.println("No matching record found in the students_courses table.");
				}
			}
		}
	}
