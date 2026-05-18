package models;

import java.util.ArrayList;

import data.Database;
import java.util.List;
import enumerations.ManagerTypes;
import core.LocalizationManager;
import utils.Complaint;
import utils.Course;
import utils.Enrollment;
import utils.Mark;
import utils.Message;
import utils.News;

public class Manager extends Employee{
    private static final long serialVersionUID = 1L;
//    private List<Enrollment> registrationQueue = new ArrayList<>();
    private List<Complaint> complaints = new ArrayList<>();
    private ManagerTypes managerType;

    public Manager(String userId, String name, String password, String login, double salary,ManagerTypes managerType) {
        super(userId, name, password, login, salary);
        this.managerType=managerType;
    }
    
    public void manageNews(String title, String text, boolean pin) {
        Database db = Database.getInstance();
        News news = new News(title, text, pin);
        db.getNews().add(news);
        db.save();
      }

    public void assignTeacher(Teacher t, Course c) {
        if (t == null) {
            return;
        }
        if (c == null) {
            return;
        }

        c.addTeacher(t);
        System.out.println(LocalizationManager.getString("success_assign_teacher",t.getName(),c.getName()));
    }

    public void addCourse(Course newCourse) {
        Database db = Database.getInstance();
        db.getCourses().add(newCourse);
        db.save();
        System.out.println(LocalizationManager.getString("success_course"));
    }

    public String createReport(List<Course> courses) {
        StringBuilder report = new StringBuilder();

        if (courses == null) {
            return report.toString();
        }

        for (Course course : courses) {
            if (course == null) {
                continue;
            }
            if (course.getE() == null) {
                continue;
            }

            report.append(course.getCourseCode());
            report.append(" ");
            report.append(course.getName());
            report.append("\n");

            for (Enrollment enrollment : course.getE()) {
                if (enrollment == null) {
                    continue;
                }
                if (enrollment.getStudent() == null) {
                    continue;
                }
                if (enrollment.getMark() == null) {
                    continue;
                }
                if (!"APPROVED".equals(enrollment.getStatus())) {
                    continue;
                }

                Mark mark = enrollment.getMark();

                report.append(enrollment.getStudent().getName());
                report.append(": ");
                report.append(mark.getTotal());
                report.append(" GPA ");
                report.append(mark.convertToGpa());
                report.append("\n");
            }
        }

        return report.toString();
    }

    public void viewComplaints() {
        for (Complaint complaint : complaints) {
            System.out.println(complaint);
        }
    }

    public void approveRegistration(Enrollment e) {
        if (e == null) return;

        if (!"PENDING".equals(e.getStatus())) {
            System.out.println(LocalizationManager.getString("request_state"));
            return;
        }

        Student student = e.getStudent();
        Course course = e.getCourse();

        if (student == null || course == null) {
            System.out.println(LocalizationManager.getString("err_student_or_course_missing"));
            return;
        }

        boolean alreadyApproved = student.getEnrollments().stream()
                .anyMatch(en -> en.getCourse().equals(course) && "APPROVED".equals(en.getStatus()));
        if (alreadyApproved) {
            e.reject();
            System.out.println(LocalizationManager.getString("alreadyregister", course.getName()));
            Database.getInstance().getRegistrationQueue().remove(e);
            return;
        }

        if (!student.canRegisterForCourse(course)) {
            e.reject();
            System.out.println(LocalizationManager.getString("err_student_not_meet_requirements"));
            Database.getInstance().getRegistrationQueue().remove(e);
            return;
        }

        e.approve();
        student.getEnrollments().add(e);
        course.addEnrollment(e);
        Database.getInstance().getRegistrationQueue().remove(e);
        Database.getInstance().save();
        System.out.println(LocalizationManager.getString("registration_approved", course.getName()));
    }

//    public void addRegistration(Enrollment e) {
//        if (e == null) {
//            return;
//        }
//        if (Database.getInstance().getRegistrationQueue().contains(e)) {
//            return;
//        }
//
//        Database.getInstance().getRegistrationQueue().add(e);
//    }

    public void addRegistration(Enrollment e) {
        if (e == null) return;
        List<Enrollment> queue = Database.getInstance().getRegistrationQueue();
        if (queue.contains(e)) return;
        queue.add(e);
    }
    
    public void sendMessage(Employee receiver, String content) {
        Message msg = new Message(this, receiver, content);
        Database.getInstance().getMessages().add(msg);
        Database.getInstance().save();
        System.out.println(LocalizationManager.getString("message_sent", receiver.getName()));
    }

    public List<Enrollment> getRegistrationQueue() {
        return Database.getInstance().getRegistrationQueue();
    }
    
    public List<Complaint> getComplaints() {
        return complaints;
    }

    public void addComplaint(Complaint complaint) {
        if (complaint != null) {
            complaints.add(complaint);
        }
    }

	public ManagerTypes getManagerType() {
		return managerType;
	}

	@Override
	public String toString() {
	    return LocalizationManager.getString("manager_info", getUserId(), getName(), getSalary(), managerType);
	}
}
