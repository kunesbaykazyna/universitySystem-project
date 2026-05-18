package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.LocalizationManager;
import data.Database;
import enumerations.Faculty;
import enumerations.Semester;
import utils.Course;
import utils.Enrollment;
import utils.Mark;
import utils.Request;

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

    public void registerForCourse(Course c, Semester semester) {
        if (c == null) return;
        if (!canRegisterForCourse(c)) {
            System.out.println(LocalizationManager.getString("err_reg_denied"));
            return;
        }
        Enrollment newEnrollment = new Enrollment(c, this, semester);
        enrollments.add(newEnrollment);
        c.addEnrollment(newEnrollment);
        
        Manager manager = (Manager) Database.getInstance().getUsers().stream()
                .filter(u -> u instanceof Manager)
                .findFirst()
                .orElse(null);
        if (manager != null) {
            manager.addRegistration(newEnrollment);
        }
        System.out.println(LocalizationManager.getString("student_applied", c.getName()));
    }
    public void rateTeacher(Teacher t, int rating) {
        System.out.println(LocalizationManager.getString("student_rated_teacher", t.getName(), rating));
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
            if (enrollment == null || enrollment.getCourse() == null) continue;
            if ("APPROVED".equals(enrollment.getStatus())) { 
                totalCredits += enrollment.getCourse().getCredits();
            }
        }
        return totalCredits;
    }

    public int getTotalFailCount() {
        int fails = 0;
        for (Enrollment enrollment : enrollments) {
            if (enrollment.getMark() != null && enrollment.getMark().getTotal() < 50) {
                fails++;
            }
        }
        return fails;
    }

    public void requestResearcherStatus() {
        Request request = new Request();
        request.setDescription("User " + getName() + " (ID: " + getUserId() + ") requests researcher status.");
        Database.getInstance().getRequests().add(request);
        System.out.println(LocalizationManager.getString("request_sent"));
    }
    
    public boolean canRegisterForCourse(Course course) {
        if (course == null) return false;
        if (this.getCurrentCredits() + course.getCredits() > 21) return false;
        if (this.getTotalFailCount() >= 3) return false;
        if (!course.isOfferedForFaculty(this.getFaculty())) return false;
        return true;
    }

    public void updateGpa() {
        double total = 0;
        int credits = 0;
        marks.clear();

        for (Enrollment enrollment : enrollments) {
            if (enrollment == null) {
                continue;
            }
            if (enrollment.getCourse() == null) {
                continue;
            }
            if (enrollment.getMark() == null) {
                continue;
            }
            if (!"APPROVED".equals(enrollment.getStatus())) {
                continue;
            }

            Mark mark = enrollment.getMark();
            marks.put(enrollment.getCourse(), mark);
            total = total + mark.convertToGpa() * enrollment.getCourse().getCredits();
            credits = credits + enrollment.getCourse().getCredits();
        }

        if (credits == 0) {
            gpa = 0;
        } else {
            gpa = total / credits;
        }
    }

    public String generateTranscript() {
        StringBuilder transcript = new StringBuilder();
        transcript.append(LocalizationManager.getString("transcript_title", getName()));

        for (Enrollment enrollment : enrollments) {
            if (enrollment == null) {
                continue;
            }
            if (enrollment.getCourse() == null) {
                continue;
            }
            if (!"APPROVED".equals(enrollment.getStatus())) {
                continue;
            }

            String courseLine = LocalizationManager.getString("transcript_course_line", 
                    enrollment.getCourse().getCourseCode(), 
                    enrollment.getCourse().getName(), 
                    enrollment.getCourse().getCredits());
            transcript.append(courseLine);

            if (enrollment.getMark() != null) {
                String markPart = LocalizationManager.getString("transcript_mark_part", 
                        enrollment.getMark().getTotal(), 
                        enrollment.getMark().convertToGpa());
                transcript.append(markPart);
            }
            transcript.append("\n");
        }

        transcript.append(LocalizationManager.getString("transcript_gpa_footer", getGpa()));
        return transcript.toString();
    }

    public String viewTranscript() {
        return generateTranscript();
    }

    @Override
    public String toString() {
        return LocalizationManager.getString("student_info", getName(), faculty, yearsOfStudy, getGpa());
    }


}
