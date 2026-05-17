package models;

import java.util.ArrayList;
import data.Database;
import java.util.List;

import core.LocalizationManager;
import utils.Complaint;
import utils.Course;
import utils.Enrollment;
import utils.Mark;
import utils.News;

public class Manager extends Employee{
    private static final long serialVersionUID = 1L;
    private List<Enrollment> registrationQueue = new ArrayList<>();
    private List<Complaint> complaints = new ArrayList<>();

    public Manager(String userId, String name, String password, String login, double salary) {
        super(userId, name, password, login, salary);
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
        if (e == null) {
            return;
        }

        if (!"PENDING".equals(e.getStatus())) {
            System.out.println(LocalizationManager.getString("request_state"));
            return;
        }

        Student student = e.getStudent();
        Course course = e.getCourse();

        if (student == null || course == null) {
            System.out.println("Ошибка: студент или курс отсутствуют.");
            return;
        }

        // 2. Проверка, не зарегистрирован ли студент уже на этот курс (одобренная заявка)
        boolean alreadyApproved = student.getEnrollments().stream()
                .anyMatch(en -> en.getCourse().equals(course) && "APPROVED".equals(en.getStatus()));
        if (alreadyApproved) {
            e.reject();
            System.out.println(LocalizationManager.getString("alreadyregister",getName()));
            registrationQueue.remove(e);
            return;
        }

        // 3. Основная проверка (кредиты, тип курса, провалы и т.д.)
        if (!student.canRegisterForCourse(course)) {
            e.reject();
            System.out.println("Регистрация отклонена: студент не соответствует требованиям курса.");
            registrationQueue.remove(e);
            return;
        }

        // 4. Всё хорошо – одобряем
        e.approve();
        student.getEnrollments().add(e);
        course.addEnrollment(e);
        registrationQueue.remove(e);
        System.out.println(LocalizationManager.getString("failtoregister",getName()));
    }

    public void addRegistration(Enrollment e) {
        if (e == null) {
            return;
        }
        if (registrationQueue.contains(e)) {
            return;
        }

        registrationQueue.add(e);
    }

    public List<Enrollment> getRegistrationQueue() {
        return registrationQueue;
    }

    public List<Complaint> getComplaints() {
        return complaints;
    }

    public void addComplaint(Complaint complaint) {
        if (complaint != null) {
            complaints.add(complaint);
        }
    }

}
