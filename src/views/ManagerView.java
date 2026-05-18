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
        System.out.println("1. " + LocalizationManager.getString("manager_opt1"));
        System.out.println("2. " + LocalizationManager.getString("manager_opt2"));
        System.out.println("3. " + LocalizationManager.getString("manager_opt3"));
        System.out.println("4. " + LocalizationManager.getString("manager_opt4"));
        System.out.println("5. " + LocalizationManager.getString("manager_opt5"));
        System.out.println("6. " + LocalizationManager.getString("manager_opt6"));
        System.out.println("7. " + LocalizationManager.getString("manager_opt7")); 
        System.out.println("8. " + LocalizationManager.getString("manager_opt8")); 
        System.out.println("9. " + LocalizationManager.getString("manager_opt9"));
        System.out.println("10. Manage news");    
        System.out.println("11. " + LocalizationManager.getString("change_lang"));
        System.out.println("0. " + LocalizationManager.getString("logout_msg"));
    }

    @Override
    public void handleInput(int choice) {
        switch (choice) {
            case 1 -> addCourse();
            case 2 -> assignTeacherToCourse();
            case 3 -> approveRegistrations();
            case 4 -> createReport();
            case 5 -> viewAllCourses();
            case 6 -> makeResearcher();
            case 7 -> viewAllStudentsSorted();
            case 8 -> viewAllTeachersSorted();
            case 9 -> sendMessageToEmployee();
            case 10 -> manageNews();
            case 11 -> changeLanguage();
            case 0 -> System.out.println(LocalizationManager.getString("logout_msg"));
            default -> System.out.println(LocalizationManager.getString("err_invalid"));
        }
    }

    private void manageNews() {
    	System.out.println(LocalizationManager.getString("manage_news_title"));
        System.out.println(LocalizationManager.getString("manage_news_view"));
        System.out.println(LocalizationManager.getString("manage_news_add"));
        System.out.print(LocalizationManager.getString("manage_news_choice"));
        int ch = readInt();
        if (ch == 1) {
            List<News> allNews = db.getNews();
            if (allNews.isEmpty()) {
                System.out.println(LocalizationManager.getString("no_news"));
            } else {
                for (News n : allNews) {
                    System.out.println(n);
                    System.out.println("--- Comments ---");
                    for (News.Comment c : n.getComments()) {
                        System.out.println(c);
                    }
                    System.out.println("----------------");
                }
            }
        } else if (ch == 2) {
            System.out.print(LocalizationManager.getString("prompt_news_title"));
            String title = scanner.nextLine();
            System.out.print(LocalizationManager.getString("prompt_news_content"));
            String content = scanner.nextLine();
            System.out.print(LocalizationManager.getString("prompt_news_pin"));
            boolean pin = Boolean.parseBoolean(scanner.nextLine());
            manager.manageNews(title, content, pin);
            System.out.println(LocalizationManager.getString("news_added"));
        } else {
            System.out.println(LocalizationManager.getString("err_invalid"));
        }
    }

    private void makeResearcher() {
    	System.out.print(LocalizationManager.getString("prompt_make_researcher"));
        String userId = scanner.nextLine();
        User user = db.findUserById(userId);
        if (user == null) {
            System.out.println(LocalizationManager.getString("err_user_not_found", userId));
            return;
        }
        if (user.isResearcher()) {
            System.out.println(LocalizationManager.getString("user_already_researcher"));
        } else {
            user.setResearcher(true);
            db.save();
            System.out.println(LocalizationManager.getString("user_now_researcher", user.getName()));
        }
    }

    private void addCourse() {
        String code = readNonEmpty(LocalizationManager.getString("prompt_course_code"));
        String name = readNonEmpty(LocalizationManager.getString("prompt_course_name"));
        int credits = readInt(LocalizationManager.getString("prompt_credits"));
        if (credits <= 0) {
            System.out.println(LocalizationManager.getString("error_credits_positive"));
            return;
        }
        CourseType type = readCourseType();
        Faculty faculty = readFaculty();
        Course course = new Course(code, name, credits, type, faculty);
        manager.addCourse(course);
    }

    private void sendMessageToEmployee() {
        List<Employee> employees = db.getUsers().stream()
                .filter(u -> u instanceof Employee && !u.equals(manager))
                .map(u -> (Employee) u)
                .toList();
        if (employees.isEmpty()) {
            System.out.println(LocalizationManager.getString("no_other_employees"));
            return;
        }
        System.out.println(LocalizationManager.getString("select_receiver"));
        for (int i = 0; i < employees.size(); i++) {
            System.out.println((i + 1) + ". " + employees.get(i).getName() + " (ID: " + employees.get(i).getUserId() + ")");
        }
        int idx = readInt() - 1;
        if (idx < 0 || idx >= employees.size()) return;
        Employee receiver = employees.get(idx);
        System.out.print(LocalizationManager.getString("enter_message_text"));
        String content = scanner.nextLine();
        if (content.trim().isEmpty()) {
            System.out.println(LocalizationManager.getString("err_invalid"));
            return;
        }
        manager.sendMessage(receiver, content);
    }

    private void assignTeacherToCourse() {
        List<Course> courses = db.getCourses();
        if (courses.isEmpty()) {
            System.out.println(LocalizationManager.getString("error_no_courses"));
            return;
        }
        System.out.println(LocalizationManager.getString("select_course"));
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
            System.out.println(LocalizationManager.getString("error_no_teachers"));
            return;
        }
        System.out.println(LocalizationManager.getString("select_teacher"));
        for (int i = 0; i < teachers.size(); i++) {
            System.out.println((i + 1) + ". " + teachers.get(i).getName());
        }
        int tIdx = readInt() - 1;
        if (tIdx < 0 || tIdx >= teachers.size()) return;
        Teacher teacher = teachers.get(tIdx);
        manager.assignTeacher(teacher, course);
    }

    private void viewAllStudentsSorted() {
        List<Student> students = db.getUsers().stream()
                .filter(u -> u instanceof Student)
                .map(u -> (Student) u)
                .toList();
        if (students.isEmpty()) {
            System.out.println(LocalizationManager.getString("no_students"));
            return;
        }
        System.out.println(LocalizationManager.getString("sort_by"));
        System.out.println("1. " + LocalizationManager.getString("sort_by_name"));
        System.out.println("2. " + LocalizationManager.getString("sort_by_gpa"));
        int choice = readInt();
        List<Student> sorted = new ArrayList<>(students);
        if (choice == 1) {
            sorted.sort(Comparator.comparing(Student::getName));
        } else if (choice == 2) {
            sorted.sort(Comparator.comparingDouble(Student::getGpa).reversed());
        } else {
            System.out.println(LocalizationManager.getString("err_invalid"));
            return;
        }
        for (Student s : sorted) {
            System.out.println(s);
        }
    }

    private void viewAllTeachersSorted() {
        List<Teacher> teachers = db.getUsers().stream()
                .filter(u -> u instanceof Teacher)
                .map(u -> (Teacher) u)
                .toList();
        if (teachers.isEmpty()) {
            System.out.println(LocalizationManager.getString("no_teachers"));
            return;
        }
        System.out.println(LocalizationManager.getString("sort_by"));
        System.out.println("1. " + LocalizationManager.getString("sort_by_name"));
        System.out.println("2. " + LocalizationManager.getString("sort_by_salary"));
        int choice = readInt();
        List<Teacher> sorted = new ArrayList<>(teachers);
        if (choice == 1) {
            sorted.sort(Comparator.comparing(Teacher::getName));
        } else if (choice == 2) {
            sorted.sort(Comparator.comparingDouble(Teacher::getSalary).reversed());
        } else {
            System.out.println(LocalizationManager.getString("err_invalid"));
            return;
        }
        for (Teacher t : sorted) {
            System.out.println(t);
        }
    }

    private void approveRegistrations() {
        List<Enrollment> queue = manager.getRegistrationQueue();
        if (queue.isEmpty()) {
            System.out.println(LocalizationManager.getString("error_no_pending"));
            return;
        }
        List<Enrollment> copy = new ArrayList<>(queue);
        for (Enrollment e : copy) {
            System.out.println(e);
            System.out.print(LocalizationManager.getString("prompt_approve"));
            String ans = scanner.nextLine();
            if ("y".equalsIgnoreCase(ans)) {
                manager.approveRegistration(e);
            } else {
                e.reject();
                System.out.println(LocalizationManager.getString("status_rejected"));
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
        System.out.println(LocalizationManager.getString("language_choice_menu"));
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
            System.out.print(LocalizationManager.getString("prompt_course_type"));
            try {
                return CourseType.valueOf(scanner.nextLine().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println(LocalizationManager.getString("error_invalid_type"));
            }
        }
    }

    private Faculty readFaculty() {
        while (true) {
            System.out.print(LocalizationManager.getString("prompt_faculty"));
            try {
                return Faculty.valueOf(scanner.nextLine().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println(LocalizationManager.getString("error_invalid_faculty"));
            }
        }
    }
}