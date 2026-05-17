package views;

import models.*;
import utils.*;
import data.Database;
import enumerations.*;
import core.LocalizationManager;

import java.util.*;

public class ManagerView implements View {
    private Manager manager;
    private Scanner scanner = new Scanner(System.in);
    private Database db = Database.getInstance();

    public ManagerView(Manager manager) {
        this.manager = manager;
    }

    @Override
    public void showMenu() {
        System.out.println("\n--- " + LocalizationManager.getString("menu_manager_title") + " ---");
        System.out.println(LocalizationManager.getString("manager_opt1"));
        System.out.println(LocalizationManager.getString("manager_opt2"));
        System.out.println(LocalizationManager.getString("manager_opt3"));
        System.out.println(LocalizationManager.getString("manager_opt4"));
        System.out.println(LocalizationManager.getString("manager_opt5"));
        System.out.println(LocalizationManager.getString("change_lang"));
        System.out.println(LocalizationManager.getString("logout"));
    }

    @Override
    public void handleInput(int choice) {
        switch (choice) {
            case 1 -> addCourse();
            case 2 -> assignTeacherToCourse();
            case 3 -> approveRegistrations();
            case 4 -> createReport();
            case 5 -> viewAllCourses();
            case 9 -> changeLanguage();
            case 0 -> System.out.println(LocalizationManager.getString("logout_msg"));
            default -> System.out.println(LocalizationManager.getString("err_invalid"));
        }
    }

    private void addCourse() {
        String code = readNonEmpty("Course code: ");
        String name = readNonEmpty("Course name: ");
        int credits = readInt("Credits: ");
        if (credits <= 0) {
            System.out.println("Credits must be positive.");
            return;
        }
        CourseType type = readCourseType();
        Faculty faculty = readFaculty();
        Course course = new Course(code, name, credits, type, faculty);
        manager.addCourse(course);
        System.out.println(LocalizationManager.getString("success_course"));
    }

    private void assignTeacherToCourse() {
        List<Course> courses = db.getCourses();
        if (courses.isEmpty()) {
            System.out.println("No courses available.");
            return;
        }
        System.out.println("Select course:");
        for (int i = 0; i < courses.size(); i++) {
            System.out.println((i + 1) + ". " + courses.get(i).getName());
        }
        int cIdx = readInt() - 1;
        if (cIdx < 0 || cIdx >= courses.size()) return;
        Course course = courses.get(cIdx);

        List<Teacher> teachers = db.getUsers().stream()
                .filter(u -> u instanceof Teacher)
                .map(u -> (Teacher) u)
                .toList();

        if (teachers.isEmpty()) {
            System.out.println("No teachers found.");
            return;
        }
        System.out.println("Select teacher:");
        for (int i = 0; i < teachers.size(); i++) {
            System.out.println((i + 1) + ". " + teachers.get(i).getName());
        }
        int tIdx = readInt() - 1;
        if (tIdx < 0 || tIdx >= teachers.size()) return;
        Teacher teacher = teachers.get(tIdx);
        manager.assignTeacher(teacher, course);
    }

    private void approveRegistrations() {
        List<Enrollment> queue = manager.getRegistrationQueue();
        if (queue.isEmpty()) {
            System.out.println("No pending registrations.");
            return;
        }

        List<Enrollment> copy = new ArrayList<>(queue);
        for (Enrollment e : copy) {
            System.out.println(e);
            System.out.print("Approve? (y/n): ");
            String ans = scanner.nextLine();
            if ("y".equalsIgnoreCase(ans)) {
                manager.approveRegistration(e);
            } else {
                e.reject();
                System.out.println("Rejected.");
            }
        }
    }

    private void createReport() {
        List<Course> courses = db.getCourses();
        String report = manager.createReport(courses);
        System.out.println(report);
    }

    private void viewAllCourses() {
        db.getCourses().forEach(System.out::println);
    }

    private void changeLanguage() {
        System.out.println(LocalizationManager.getString("change_lang"));
        System.out.println("1. English  2. Русский  3. Қазақша");
        int choice = readInt();
        Language newLang = switch (choice) {
            case 1 -> Language.EN;
            case 2 -> Language.RU;
            case 3 -> Language.KZ;
            default -> null;
        };
        if (newLang != null) manager.switchLanguage(newLang);
    }

    private String readNonEmpty(String prompt) {
        String input;
        do {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
        } while (input.isEmpty());
        return input;
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

    private CourseType readCourseType() {
        while (true) {
            System.out.print("Course type (MAJOR/MINOR/FREE_ELECTIVE): ");
            try {
                return CourseType.valueOf(scanner.nextLine().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid type. Try again.");
            }
        }
    }

    private Faculty readFaculty() {
        while (true) {
            System.out.print("Faculty (FIT , BS , SEPI): ");
            try {
                return Faculty.valueOf(scanner.nextLine().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid faculty. Try again.");
            }
        }
    }
}