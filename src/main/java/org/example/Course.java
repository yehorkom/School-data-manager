package org.example;

public record Course(int courseId, String courseName) {

	@Override
	public String toString() {
		return "Course{" +
			"courseId=" + courseId +
			", courseName='" + courseName + '\'' +
			'}';
	}
}
