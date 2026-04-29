package utils;

import java.util.List;

public class Course {
	private String courseCode;
	private String name;
	private int credits;
	private List<Enrollment> e;
	public String getCourseCode() {
		return courseCode;
	}
	public String getName() {
		return name;
	}
	public int getCredits() {
		return credits;
	}
	public List<Enrollment> getE() {
		return e;
	}
}
