package models;

import java.util.ArrayList;
import java.util.List;

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

    public void manageNews(ArrayList<News> newsList, String title, String text, boolean pin) {
        newsList.add(new News(title, text, pin));
    }

    public void assignTeacher(Teacher t, Course c) {
        if (t == null) {
            return;
        }
        if (c == null) {
            return;
        }

        c.addTeacher(t);
        System.out.println("Преподаватель " + t.getName() + " назначен на курс " + c.getName());
    }

    public void assiginTeacher(Teacher t, Course c) {
        assignTeacher(t, c);
    }

    public void addCourse(List<Course> allCourses, Course newCourse) {
        if (allCourses == null) {
            return;
        }
        if (newCourse == null) {
            return;
        }

        allCourses.add(newCourse);
    }

    public void createReport(String reportData) {
        System.out.println("Отчет создан: " + reportData);
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
                if (enrollment.students == null) {
                    continue;
                }
                if (enrollment.getMark() == null) {
                    continue;
                }
                if (!"APPROVED".equals(enrollment.getStatus())) {
                    continue;
                }

                Mark mark = enrollment.getMark();

                report.append(enrollment.students.getName());
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

        Student student = e.students;
        Course course = e.course;

        if (student == null) {
            return;
        }
        if (course == null) {
            return;
        }

        boolean alreadyInStudentList = student.getEnrollments().contains(e);
        boolean validRegistration = false;

        if (alreadyInStudentList) {
            if (course.isValidCourseType()) {
                if (student.getCurrentCredits() <= 21) {
                    validRegistration = true;
                }
            }
        } else {
            validRegistration = student.canRegisterForCourse(course);
        }

        if (!validRegistration) {
            e.reject();
            System.out.println("Регистрация отклонена.");
            return;
        }

        e.approve();

        if (!alreadyInStudentList) {
            student.getEnrollments().add(e);
        }

        course.addEnrollment(e);
        registrationQueue.remove(e);

        System.out.println("Регистрация одобрена на курс " + course.getName());
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
