package models;

import java.util.*;

import enumerations.*;
import utils.*;

public class Teacher extends Employee{
    private static final long serialVersionUID = 1L;
    private Faculty faculty;
    private TeacherType teacherType;
    private List<Complaint> complaints = new ArrayList<>();

    public Teacher(String userId, String name, String password, String login, double salary, Faculty faculty, TeacherType teacherType) {
        super(userId, name, password, login, salary);
        this.faculty = faculty;
        this.teacherType = teacherType;
    }

    public void createComplaint() {
        System.out.println(getName() + " создал жалобу");
    }

    public void putMark(Enrollment e, Mark markValue) {
        if (e == null) {
            return;
        }
        if (markValue == null) {
            return;
        }

        e.setMark(markValue);

        if (e.students == null) {
            return;
        }
        if (e.course == null) {
            return;
        }

        e.students.getMarks().put(e.course, markValue);
    }

    public void putMarks(Enrollment e, Mark markValue) {
        putMark(e, markValue);
    }

    public void markkAttendance(Lesson less, Student s, boolean present) {
        markAttendance(less, s, present);
    }

    public void markAttendance(Lesson less, Student s, boolean present) {
        if (less == null) {
            return;
        }
        if (s == null) {
            return;
        }

        less.markAttendance(s, present);
    }

    public void viewStudentInfo(Student s) {
        if (s == null) {
            return;
        }

        System.out.print(s);
    }

    public void sendComplaints(String text, UrgencyLevel urgency) {
        Complaint newComplaint = new Complaint(text, urgency, this.getUserId());
        complaints.add(newComplaint);
        System.out.println("Жалоба отправлена.");
    }

    public void sendComplaintToManager(Manager manager, String text, UrgencyLevel urgency) {
        if (manager == null) {
            return;
        }
        Complaint complaint = new Complaint(text, urgency, getUserId());
        complaints.add(complaint);
        manager.addComplaint(complaint);
    }

    public List<Course> getMyCourses(List<Course> allCourses) {
        List<Course> myCourses = new ArrayList<>();

        if (allCourses == null) {
            return myCourses;
        }

        for (Course course : allCourses) {
            if (course == null) {
                continue;
            }
            if (!course.getTeachers().contains(this)) {
                continue;
            }

            myCourses.add(course);
        }

        return myCourses;
    }

    public List<Student> viewStudents(Course c) {
        List<Student> students = new ArrayList<>();

        if (c == null) {
            return students;
        }
        if (c.getE() == null) {
            return students;
        }

        for (Enrollment enrollment : c.getE()) {
            if (enrollment == null) {
                continue;
            }
            if (enrollment.students == null) {
                continue;
            }
            if (!"APPROVED".equals(enrollment.getStatus())) {
                continue;
            }

            students.add(enrollment.students);
        }

        return students;
    }

    public List<Complaint> getComplaints() {
        return complaints;
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public TeacherType getTeacherType() {
        return teacherType;
    }

    @Override
    public String toString() {
        return "Преподаватель{имя='" + getName() + "', факультет=" + faculty + ", должность=" + teacherType + "}";
    }
}
