package views;

import models.*;
import data.Database;
import enumerations.*;
import core.LocalizationManager;
import factory.UserFactory;

import java.util.*;

public class AdminView implements View {
    private Admin admin;
    private Scanner scanner = new Scanner(System.in);
    private Database db = Database.getInstance();

    public AdminView(Admin admin) {
        this.admin = admin;
    }

    @Override
    public void showMenu() {
        System.out.println("\n--- " + LocalizationManager.getString("menu_admin_title") + " ---");
        System.out.println(LocalizationManager.getString("admin_opt1"));
        System.out.println(LocalizationManager.getString("admin_opt2"));
        System.out.println(LocalizationManager.getString("admin_opt3"));
        System.out.println(LocalizationManager.getString("admin_opt4"));
        System.out.println(LocalizationManager.getString("change_lang"));
        System.out.println(LocalizationManager.getString("logout"));
    }

    @Override
    public void handleInput(int choice) {
        switch (choice) {
            case 1 -> addUser();
            case 2 -> removeUser();
            case 3 -> viewAllUsers();
            case 4 -> viewLogs();
            case 9 -> changeLanguage();
            case 0 -> System.out.println(LocalizationManager.getString("logout_msg"));
            default -> System.out.println(LocalizationManager.getString("err_invalid"));
        }
    }

    private void addUser() {
        System.out.print(LocalizationManager.getString("msg_enter_role") + " (STUDENT/TEACHER/ADMIN/MANAGER/TECH_SUPPORT/GRADUATED_STUDENT): ");
        String role = scanner.nextLine().toUpperCase();

        try {
            String userId = readNonEmpty("User ID: ");
            String name = readNonEmpty("Name: ");
            String pwd = readNonEmpty("Password: ");
            String login = readNonEmpty("Login: ");

            User user = null;

            switch (role) {
                case "STUDENT" -> {
                    int yos = readInt("Years of study: ");
                    Faculty fac = readFaculty();
                    user = UserFactory.createUser(role, userId, name, pwd, login, fac, yos);
                }
                case "TEACHER" -> {
                    Faculty fac = readFaculty();
                    TeacherType tt = readTeacherType();
                    double sal = readDouble("Salary: ");
                    user = UserFactory.createUser(role, userId, name, pwd, login, fac, tt, sal);
                }
                case "ADMIN", "MANAGER", "TECH_SUPPORT" -> {
                    double sal = readDouble("Salary: ");
                    user = UserFactory.createUser(role, userId, name, pwd, login, sal);
                }
                case "GRADUATED_STUDENT" -> {
                    int yos = readInt("Years of study: ");
                    Faculty fac = readFaculty();
                    GraduateLevel level = readGraduateLevel();
                    String supId = readNonEmpty("Supervisor ID: ");
                    User supUser = db.findUserById(supId);
                    if (supUser == null) {
                        System.out.println(LocalizationManager.getString("err_supervisor_not_found"));
                        return;
                    }
                    ResearcherDecorator supervisor = new ResearcherDecorator(supUser);
                    if (supervisor.calculateHIndex() < 3) {
                        throw new IllegalArgumentException("Supervisor h-index < 3 – cannot supervise graduate student");
                    }
                    user = UserFactory.createUser(role, userId, name, pwd, login, fac, yos, level, supervisor);
                }
                default -> throw new IllegalArgumentException("Unknown role: " + role);
            }

            if (user != null) {
                admin.addUser(user);
//                System.out.println(LocalizationManager.getString("user_added", user.getName()));
                }
        } catch (Exception e) {
            System.out.println(LocalizationManager.getString("err_general") + e.getMessage());
        }
    }

    private void removeUser() {
        String id = readNonEmpty(LocalizationManager.getString("msg_enter_remove_id"));
        admin.removeUser(id);
    }

    private void viewAllUsers() {
        db.getUsers().forEach(System.out::println);
    }

    private void viewLogs() {
        admin.viewLogs();
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
        if (newLang != null) {
            admin.switchLanguage(newLang); 
        }
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
    private double readDouble(String prompt) {
        System.out.print(prompt);
        try {
            String input = scanner.nextLine().trim().replace(',', '.');
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private Faculty readFaculty() {
        while (true) {
            System.out.print("Faculty (e.g., FIT , BS , SEPI): ");
            try {
                return Faculty.valueOf(scanner.nextLine().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid faculty. Try again.");
            }
        }
    }

    private TeacherType readTeacherType() {
        while (true) {
            System.out.print("Teacher type (LECTOR, SENIOR_LECTOR, PROFESSOR): ");
            try {
                return TeacherType.valueOf(scanner.nextLine().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid type. Try again.");
            }
        }
    }

    private GraduateLevel readGraduateLevel() {
        while (true) {
            System.out.print("Graduate level (MASTER, PHD): ");
            try {
                return GraduateLevel.valueOf(scanner.nextLine().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid level. Try again.");
            }
        }
    }
}