package models;

import enumerations.Faculty;
import utils.Course;
import utils.Enrollment;

public class Student extends User{
	private static final long serialVersionUID = 1L;
	private int yearsOfStudy;
	private double gpa;
	private Faculty faculty;
	
	public Student(String userId, String name, String password, String login,int yos,Faculty faculty) {
		super(userId, name, password, login);
		this.faculty=faculty;
		this.yearsOfStudy=yos;
		
	}
	
	public void viewTeacherInfo(Teacher t) {
		System.out.print(t.toString());
	}
	
	public void viewMarks(Enrollment e) {
		System.out.print(e.getMark().getTotal());
	}
	
	public void registerForCourse(Course c) {
        Enrollment newEnrollment = new Enrollment();
        newEnrollment.course = c;
        newEnrollment.students = this;
        System.out.println("Студент отправил запрос на регистрацию: " + c.getName());
    }

    public void rateTeacher(Teacher t, int rating) {
        System.out.println("Студент оценил преподавателя " + t.getName() + " на " + rating + "/10");
    }

	public int getYearsOfStudy() {
		return yearsOfStudy;
	}

	public double getGpa() {
		return gpa;
	}

	public Faculty getFaculty() {
		return faculty;
	}
	
	@Override
	public String toString() {
        return "Student{username='" + getName() + "', faculty=" + faculty + ", studyYear=" + yearsOfStudy + "gpa="+gpa+"}";
    }
	

}
