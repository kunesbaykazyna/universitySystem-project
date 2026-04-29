package models;

import java.util.List;

import enumerations.*;
import utils.*;

public class Teacher extends Employee{
	private static final long serialVersionUID = 1L;
	private Faculty faculty;
	private TeacherType teacherType;
	public Teacher(String userId, String name, String password, String login, double salary,Faculty faculty,TeacherType teacherType) {
		super(userId, name, password, login, salary);
		this.faculty=faculty;
		this.teacherType=teacherType;
	}
	
	public void createComplaint() {
        System.out.println(getName() + " создал жалобу");
    }
	
	public void putMark(Enrollment e, Mark markValue) {
        e.setMark(markValue);
    }
	
    public void markkAttendance(Lesson less,Student s,boolean present) {
    	less.markAttendance(s, present);
    }
	
    public void viewStudentInfo(Student s) {
    	System.out.print(s.toString());
    }
    
    public void sendComplaints(String text, UrgencyLevel urgency) {
        //Complaint newComplaint = new Complaint(text, urgency, this.getUserId());        
        System.out.println("Жалоба успешно отправлена.");
    }

    public List<Course> getMyCourses(List<Course> allCourses) {
        return allCourses; 
    }
    
	public Faculty getFaculty() {
		return faculty;
	}
	
	public TeacherType getTeacherType() {
		return teacherType;
	}
	
	@Override
	public String toString() {
        return "Преподаватель{username='" + getName() + "', faculty=" + faculty + ", позиция=" + teacherType+ "}";
    }
}
