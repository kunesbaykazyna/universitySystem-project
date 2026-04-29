package utils;

import java.util.Map;

import enumerations.LessonType;
import models.Student;

public class Lesson {
	public LessonType lessontype;
	private Map<Student,Boolean> attendance= new java.util.HashMap<>();;
	
	public void markAttendance(Student s,boolean present) {
		attendance.put(s,present);
	}
}
