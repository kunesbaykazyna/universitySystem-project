package models;

import java.util.*;

import core.LocalizationManager;
import data.Database;
import enumerations.*;
import utils.*;

public class Teacher extends Employee{
    private static final long serialVersionUID = 1L;
    private Faculty faculty;
    private TeacherType teacherType;
    private List<Complaint> myComplaints = new ArrayList<>();

    public Teacher(String userId, String name, String password, String login, double salary, Faculty faculty, TeacherType teacherType) {
        super(userId, name, password, login, salary);
        this.faculty = faculty;
        this.teacherType = teacherType;
    }

//    public void putMark(Enrollment e, Mark markValue) {
//        if (e == null) {
//            return;
//        }
//        if (markValue == null) {
//            return;
//        }
//
//        e.setMark(markValue);
//
//        if (e.getStudent() == null) {
//            return;
//        }
//        if (e.getCourse() == null) {
//            return;
//        }
//
//        e.getStudent().getMarks().put(e.getCourse(), markValue);
//    }
    
    public void putMark(Enrollment enrollment, Mark mark) {
        if (enrollment == null || mark == null) return;
        Course course = enrollment.getCourse();
        if (!course.getTeachers().contains(this)) {
            System.out.println(LocalizationManager.getString("err_not_your_course"));
            return;
        }
        if (!"APPROVED".equals(enrollment.getStatus())) {
            System.out.println(LocalizationManager.getString("err_enrollment_not_approved"));
            return;
        }
        enrollment.setMark(mark);
        System.out.println(LocalizationManager.getString("mark_successfully_put"));
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

    public String getStudentInfo(Student s) {
        return s.toString();
    }

    public void sendComplaint(Student student, String text, UrgencyLevel urgency, Manager dean) {
        Complaint complaint = new Complaint(this, student, text, urgency);
        myComplaints.add(complaint);
        dean.addComplaint(complaint);
        System.out.println(LocalizationManager.getString("complaint_sent", student.getName()));
    }

    public List<Course> getMyCourses() {
        List<Course> allCourses = Database.getInstance().getCourses();
        List<Course> myCourses = new ArrayList<>();
        for (Course c : allCourses) {
            if (c.getTeachers().contains(this)) {
                myCourses.add(c);
            }
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
            if (enrollment.getStudent() == null) {
                continue;
            }
            if (!"APPROVED".equals(enrollment.getStatus())) {
                continue;
            }

            students.add(enrollment.getStudent());
        }

        return students;
    }
    
    public void sendMessage(Employee receiver, String content) {
        Message msg = new Message(this, receiver, content);
        Database.getInstance().getMessages().add(msg);
        Database.getInstance().save();
        System.out.println(LocalizationManager.getString("message_sent", receiver.getName()));
    }

    public List<Complaint> getComplaints() {
        return myComplaints;
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public TeacherType getTeacherType() {
        return teacherType;
    }

    @Override
    public String toString() {
        return LocalizationManager.getString("teacher_info", getName(), faculty, teacherType);
    }
}
