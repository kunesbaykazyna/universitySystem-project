package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import enumerations.CourseType;
import models.Teacher;

public class Course {
    private String courseCode;
    private String name;
    private int credits;
    private List<Enrollment> e = new ArrayList<>();
    private CourseType courseType;
    private List<Teacher> teachers = new ArrayList<>();

    public Course() {
    }

    public Course(String courseCode, String name, int credits, String courseType) {
        this.courseCode = courseCode;
        this.name = name;
        this.credits = credits;
        this.courseType = parseCourseType(courseType);
    }

    public Course(String courseCode, String name, int credits, CourseType courseType) {
        this.courseCode = courseCode;
        this.name = name;
        this.credits = credits;
        this.courseType = courseType;
    }

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

    public CourseType getCourseType() {
        return courseType;
    }

    public List<Teacher> getTeachers() {
        return teachers;
    }

    public void addEnrollment(Enrollment enrollment) {
        if (enrollment == null) {
            return;
        }
        if (e.contains(enrollment)) {
            return;
        }

        e.add(enrollment);
    }

    public void addTeacher(Teacher teacher) {
        if (teacher == null) {
            return;
        }
        if (teachers.contains(teacher)) {
            return;
        }

        teachers.add(teacher);
    }

    public boolean isValidCourseType() {
        if (courseType == CourseType.MAJOR) {
            return true;
        }
        if (courseType == CourseType.MINOR) {
            return true;
        }

        return false;
    }

    private CourseType parseCourseType(String type) {
        if (type == null) {
            return null;
        }

        if (type.equalsIgnoreCase("Major")) {
            return CourseType.MAJOR;
        }
        if (type.equalsIgnoreCase("Minor")) {
            return CourseType.MINOR;
        }

        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course)) return false;
        Course course = (Course) o;
        return Objects.equals(courseCode, course.courseCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseCode);
    }

    @Override
    public String toString() {
        return "Курс{код='" + courseCode + "', название='" + name + "', кредиты=" + credits + ", тип=" + courseType + "}";
    }
}
