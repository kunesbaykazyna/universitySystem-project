package utils;

import java.util.Map;
import java.util.Objects;

import enumerations.LessonType;
import models.Student;

public class Lesson {
    public LessonType lessontype;
    private int hours;
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

    public void markAttendance(Student s,boolean present) {
        if (s == null) {
            return;
        }

        attendance.put(s,present);
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
        return "Занятие{тип=" + lessontype + ", часы=" + hours + ", посещаемость=" + attendance + "}";
    }
}
