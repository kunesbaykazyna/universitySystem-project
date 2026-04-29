package models;

import java.util.ArrayList;
import java.util.List;

import utils.Course;
import utils.Enrollment;
import utils.News;

public class Manager extends Employee{
	private static final long serialVersionUID = 1L;

	public Manager(String userId, String name, String password, String login, double salary) {
		super(userId, name, password, login, salary);
	}
	
	public void manageNews(ArrayList<News> newsList, String title, String text, boolean pin) {
        newsList.add(new News(title, text, pin));
	}
	
	public void assignTeacher(Teacher t, Course c) {
        System.out.println("Учитель " + t.getName() + " назначен на курс " + c.getName());
    }
	
	public void addCourse(List<Course> allCourses, Course newCourse) {
        allCourses.add(newCourse);
    }

    public void createReport(String reportData) {
        System.out.println("Отчет сформирован: " + reportData);
    }
    
    public void viewComplaints() {}

    public void approveRegistration(Enrollment e) {
        System.out.println("Регистрация для студента на курс " + e.course.getName() + " одобрена.");
    }

}
