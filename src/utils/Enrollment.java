package utils;

import java.util.Objects;

import enumerations.Semester;
import models.Student;


public class Enrollment {
    public Course course;
    public Student students;
    public Semester semester;
    private Mark mark;
    private String status = "PENDING";

    public Enrollment() {
    }

    public Enrollment(Course course, Student students, Semester semester) {
        this.course = course;
        this.students = students;
        this.semester = semester;
    }

    public void setMark(Mark mark) {
        this.mark = mark;
    }

    public Mark getMark() {
        return mark;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void approve() {
        this.status = "APPROVED";
    }

    public void reject() {
        this.status = "REJECTED";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Enrollment)) return false;

        Enrollment that = (Enrollment) o;

        if (!Objects.equals(course, that.course)) return false;
        if (!Objects.equals(students, that.students)) return false;
        if (semester != that.semester) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(course, students, semester);
    }

    @Override
    public String toString() {
        return "Регистрация{курс=" + course + ", студент=" + students + ", семестр=" + semester + ", оценка=" + mark + ", статус='" + status + "'}";
    }

}
