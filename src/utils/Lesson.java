package utils;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

import core.LocalizationManager;
import enumerations.LessonType;
import models.Student;

public class Lesson implements Serializable{
	private static final long serialVersionUID = 1L;
	private LessonType lessontype;
    private int hours;
    private Course course;
    private Map<Student,Boolean> attendance = new java.util.HashMap<>();

    public Lesson() {
    }

    public Lesson(LessonType lessontype) {
        this.lessontype = lessontype;
    }

    public Lesson(LessonType lessontype, int hours) {
        this.lessontype = lessontype;
        this.hours = hours;
    }

    public void markAttendance(Student s, boolean present) {
        if (s == null) return;
        if (course == null) {
            attendance.put(s, present);
            return;
        }
        boolean isEnrolled = course.getE().stream()
            .anyMatch(en -> en.getStudent().equals(s) && "APPROVED".equals(en.getStatus()));
        if (!isEnrolled) {
            System.out.println(LocalizationManager.getString("err_student_not_enrolled", course.getName()));
            return;
        }
        attendance.put(s, present);
    }

    public Map<Student, Boolean> getAttendance() {
        return attendance;
    }

    public int getHours() {
        return hours;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Lesson)) return false;

        Lesson lesson = (Lesson) o;

        if (hours != lesson.hours) return false;
        if (lessontype != lesson.lessontype) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lessontype, hours);
    }

    @Override
    public String toString() {
        return LocalizationManager.getString("lesson_info", lessontype, hours, attendance);
    }

	public Course getCourse() {
		return course;
	}
	public void setCourse(Course course) { 
		this.course = course; 
	}
}
