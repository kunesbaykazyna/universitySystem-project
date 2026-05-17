package views;

import models.*;
import utils.*;
import enumerations.*;
import data.Database;
import java.util.*;
import core.LocalizationManager;

public class StudentView implements View {
    private UserComponent student;
    private Scanner scanner = new Scanner(System.in);
    private Database db = Database.getInstance();

    public StudentView(UserComponent student) {
        this.student = student;
    }

    private Student getStudentObject() {
        if (student instanceof Student) return (Student) student;
        if (student instanceof UserDecorator) {
            UserComponent wrapped = ((UserDecorator) student).getWrappedUser();
            if (wrapped instanceof Student) return (Student) wrapped;
        }
        throw new IllegalStateException("Component is not a Student");
    }

    private ResearcherDecorator getResearcherDecorator() {
        if (student instanceof ResearcherDecorator) return (ResearcherDecorator) student;
        return null;
    }

    @Override
    public void showMenu() {
        System.out.println("\n--- " + LocalizationManager.getString("menu_student_title") + " ---");
        System.out.println(LocalizationManager.getString("student_opt1"));
        System.out.println(LocalizationManager.getString("student_opt2"));
        System.out.println(LocalizationManager.getString("student_opt3"));
        System.out.println(LocalizationManager.getString("student_opt4"));
        System.out.println(LocalizationManager.getString("student_opt5"));
        if (getResearcherDecorator() != null) {
            System.out.println(LocalizationManager.getString("student_opt_research"));
        }
        System.out.println(LocalizationManager.getString("change_lang"));
        System.out.println(LocalizationManager.getString("logout"));
    }

    @Override
    public void handleInput(int choice) {
        switch (choice) {
            case 1 -> viewCourses();
            case 2 -> registerForCourse();
            case 3 -> viewMarks();
            case 4 -> viewTranscript();
            case 5 -> rateTeacher();
            case 6 -> {
                if (getResearcherDecorator() != null) researchMenu();
                else System.out.println(LocalizationManager.getString("err_invalid"));
            }
            case 9 -> changeLanguage();
            case 0 -> System.out.println(LocalizationManager.getString("logout_msg"));
            default -> System.out.println(LocalizationManager.getString("err_invalid"));
        }
    }

    private void viewCourses() {
        db.getCourses().forEach(System.out::println);
    }

    private void registerForCourse() {
        System.out.print("Enter course code: ");
        String code = scanner.nextLine();
        Course course = db.getCourses().stream()
                .filter(c -> c.getCourseCode().equals(code))
                .findFirst().orElse(null);
        if (course == null) {
            System.out.println(LocalizationManager.getString("err_course_not_found"));
            return;
        }
        Semester semester = Semester.FALL; 
        Student s = getStudentObject();
        int totalCredits = s.getEnrollments().stream()
                .filter(e -> "APPROVED".equals(e.getStatus()))
                .mapToInt(e -> e.getCourse().getCredits())
                .sum();
        if (totalCredits + course.getCredits() > 21) {
            System.out.println("Cannot register: exceeds 21 credit limit.");
            return;
        }
        long failCount = s.getEnrollments().stream()
                .filter(e -> e.getMark() != null && e.getMark().getTotal() < 50)
                .count();
        if (failCount >= 3) {
            System.out.println("Cannot register: you have failed 3 or more courses.");
            return;
        }
        s.registerForCourse(course, semester);
    }

    private void viewMarks() {
        for (Enrollment e : getStudentObject().getEnrollments()) {
            if ("APPROVED".equals(e.getStatus()) && e.getMark() != null) {
                System.out.println(e.getCourse().getName() + ": " + e.getMark().getTotal());
            }
        }
    }

    private void viewTranscript() {
        System.out.println(getStudentObject().generateTranscript());
    }

    private void rateTeacher() {
        System.out.print("Enter teacher ID: ");
        String tid = scanner.nextLine();
        User teacher = db.findUserById(tid);
        if (!(teacher instanceof Teacher)) {
            System.out.println("Teacher not found.");
            return;
        }
        int rating = readInt("Rating (1-10): ");
        if (rating < 1 || rating > 10) {
            System.out.println("Rating must be 1-10.");
            return;
        }
        getStudentObject().rateTeacher((Teacher) teacher, rating);
    }

    private void researchMenu() {
        ResearcherDecorator rd = getResearcherDecorator();
        if (rd == null) return;
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Research Menu ---");
            System.out.println("1. View my papers");
            System.out.println("2. Add paper");
            System.out.println("3. Print papers (by citations)");
            System.out.println("4. Print papers (by date)");
            System.out.println("5. Print papers (by pages)");
            System.out.println("6. Calculate H-index");
            System.out.println("0. Back");
            int ch = readInt();
            switch (ch) {
                case 1 -> rd.getPublishedPapers().forEach(System.out::println);
                case 2 -> addPaper(rd);
                case 3 -> rd.printPapers(Comparator.comparing(ResearchPaper::getCitations).reversed());
                case 4 -> rd.printPapers(Comparator.comparing(ResearchPaper::getDate).reversed());
                case 5 -> rd.printPapers(Comparator.comparingInt(ResearchPaper::getPages).reversed());
                case 6 -> System.out.println("H-index: " + rd.calculateHIndex());
                case 0 -> back = true;
                default -> System.out.println(LocalizationManager.getString("err_invalid"));
            }
        }
    }

    private void addPaper(ResearcherDecorator rd) {
        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Authors: ");
        String authors = scanner.nextLine();
        System.out.print("Journal: ");
        String journal = scanner.nextLine();
        int pages = readInt("Pages: ");
        int citations = readInt("Citations: ");
        System.out.print("DOI: ");
        String doi = scanner.nextLine();
        ResearchPaper paper = new ResearchPaper(title, authors, journal, pages, new Date(), citations, doi);
        rd.addPaper(paper);
        System.out.println(LocalizationManager.getString("paper_added"));
    }

    private void changeLanguage() {
        System.out.println(LocalizationManager.getString("change_lang"));
        int choice = readInt();
        Language newLang = switch (choice) {
            case 1 -> Language.EN;
            case 2 -> Language.RU;
            case 3 -> Language.KZ;
            default -> null;
        };
        if (newLang != null) {
            User user = (User) (student instanceof User ? student : getStudentObject());
            user.switchLanguage(newLang);
        }
    }

    private int readInt() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private int readInt(String prompt) {
        System.out.print(prompt);
        return readInt();
    }
}