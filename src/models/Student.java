package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import enumerations.Faculty;
import utils.Course;
import utils.Enrollment;
import utils.Mark;

public class Student extends User{
    private static final long serialVersionUID = 1L;
    private int yearsOfStudy;
    private double gpa;
    private Faculty faculty;
    private List<Enrollment> enrollments = new ArrayList<>();
    private Map<Course, Mark> marks = new HashMap<>();

    public Student(String userId, String name, String password, String login,int yos,Faculty faculty) {
        super(userId, name, password, login);
        this.faculty=faculty;
        this.yearsOfStudy=yos;

    }

    public void viewTeacherInfo(Teacher t) {
        System.out.print(t.toString());
    }

    public void viewMarks(Enrollment e) {
        if (e == null) {
            return;
        }
        if (e.getMark() == null) {
            return;
        }

        System.out.print(e.getMark().getTotal());
    }

    public void registerForCourse(Course c) {
        if (c == null) {
            return;
        }
        if (!canRegisterForCourse(c)) {
            System.out.println("Регистрация отклонена: превышен лимит кредитов или тип курса неверный.");
            return;
        }
        Enrollment newEnrollment = new Enrollment();
        newEnrollment.course = c;
        newEnrollment.students = this;
        newEnrollment.setStatus("PENDING");
        enrollments.add(newEnrollment);
        c.addEnrollment(newEnrollment);
        System.out.println("Студент отправил заявку на курс: " + c.getName());
    }

    public void rateTeacher(Teacher t, int rating) {
        System.out.println("Студент оценил преподавателя " + t.getName() + " на " + rating + "/10");
    }

    public int getYearsOfStudy() {
        return yearsOfStudy;
    }

    public double getGpa() {
        updateGpa();
        return gpa;
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public List<Enrollment> getEnrollments() {
        return enrollments;
    }

    public Map<Course, Mark> getMarks() {
        return marks;
    }

    public int getCurrentCredits() {
        int totalCredits = 0;

        for (Enrollment enrollment : enrollments) {
            if (enrollment == null) {
                continue;
            }
            if (enrollment.course == null) {
                continue;
            }
            if ("REJECTED".equals(enrollment.getStatus())) {
                continue;
            }

            totalCredits = totalCredits + enrollment.course.getCredits();
        }

        return totalCredits;
    }

    public boolean canRegisterForCourse(Course c) {
        if (c == null) {
            return false;
        }
        if (!c.isValidCourseType()) {
            return false;
        }

        return getCurrentCredits() + c.getCredits() <= 21;
    }

    public void updateGpa() {
        double total = 0;
        int credits = 0;
        marks.clear();

        for (Enrollment enrollment : enrollments) {
            if (enrollment == null) {
                continue;
            }
            if (enrollment.course == null) {
                continue;
            }
            if (enrollment.getMark() == null) {
                continue;
            }
            if (!"APPROVED".equals(enrollment.getStatus())) {
                continue;
            }

            Mark mark = enrollment.getMark();
            marks.put(enrollment.course, mark);
            total = total + mark.convertToGpa() * enrollment.course.getCredits();
            credits = credits + enrollment.course.getCredits();
        }

        if (credits == 0) {
            gpa = 0;
        } else {
            gpa = total / credits;
        }
    }

    public String generateTranscript() {
        StringBuilder transcript = new StringBuilder();
        transcript.append("Транскрипт студента ").append(getName()).append("\n");

        for (Enrollment enrollment : enrollments) {
            if (enrollment == null) {
                continue;
            }
            if (enrollment.course == null) {
                continue;
            }
            if (!"APPROVED".equals(enrollment.getStatus())) {
                continue;
            }

            transcript.append(enrollment.course.getCourseCode());
            transcript.append(" ");
            transcript.append(enrollment.course.getName());
            transcript.append(" кредиты=");
            transcript.append(enrollment.course.getCredits());

            if (enrollment.getMark() != null) {
                transcript.append(" итог=");
                transcript.append(enrollment.getMark().getTotal());
                transcript.append(" GPA=");
                transcript.append(enrollment.getMark().convertToGpa());
            }

            transcript.append("\n");
        }

        transcript.append("Средний GPA: ").append(getGpa());
        return transcript.toString();
    }

    public String viewTranscript() {
        return generateTranscript();
    }

    @Override
    public String toString() {
        return "Студент{имя='" + getName() + "', факультет=" + faculty + ", годОбучения=" + yearsOfStudy + ", gpa=" + gpa + "}";
    }


}
