package core;

import data.Database;
import models.*;
import views.*;
import enumerations.Language;
import enumerations.TeacherType;
import views.View;
import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static Database db = Database.getInstance(); 

    public static void main(String[] args) {
        LocalizationManager.setLanguage(Language.RU);
        
        if (db.getUsers() == null || db.getUsers().isEmpty()) {
            createDefaultUsers();
        }
        
        System.out.println("Choose language: 1 - English, 2 - Русский, 3 - Қазақша");
        int langChoice = readInt();
        Language lang = switch (langChoice) {
            case 1 -> Language.EN;
            case 2 -> Language.RU;
            case 3 -> Language.KZ;
            default -> Language.EN;
        };
        LocalizationManager.setLanguage(lang);

        while (true) {
        	System.out.println("\n=== " + LocalizationManager.getString("welcome") + " ===");
            
            User currentUser = authenticate();
            if (currentUser == null) {
                System.out.println(LocalizationManager.getString("err_invalid"));
                continue;
            }

            UserComponent userComponent = wrapIfResearcher(currentUser);
            
            View view = createView(userComponent);
            if (view == null) {
                System.out.println("Unknown role.");
                continue;
            }

            int choice;
            do {
                view.showMenu();
                choice = readInt();
                view.handleInput(choice);
            } while (choice != 0);
            
            db.save();
        }
    }

    private static User authenticate() {
        System.out.print(LocalizationManager.getString("login") + " ");
        String inputLogin = scanner.nextLine().trim();
        System.out.print("Password: "); 
        String password = scanner.nextLine().trim();
        
        User user = db.findUserByLogin(inputLogin);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
    
    private static void createDefaultUsers() {
        try {
            Admin admin = new Admin("A001", "Admin", "admin123", "admin", 50000.0);
            db.getUsers().add(admin);
            db.save();
//            System.out.println("[админ по деволту успешно создан..!]");
//            System.out.println("Логин: admin | Пароль: admin123 | ID: A001");
        } catch (Exception e) {
            System.err.println("ошибка при создании дефолтного админа: " + e.getMessage());
        }
    }

    // если юзер должен быть ресерчером то декорирует и если просто студент или тичер то админ должен дать апрув
    private static UserComponent wrapIfResearcher(User user) {
        if (user.isResearcher()) {
            return new ResearcherDecorator(user);
        }
        if (user instanceof Teacher && ((Teacher) user).getTeacherType() == TeacherType.PROFESSOR) {
            return new ResearcherDecorator(user);
        }
        if (user instanceof GraduatedStudent) {
            return new ResearcherDecorator(user);
        }
        return user;
    }
    private static View createView(UserComponent user) {
        UserComponent baseUser = user;
        if (user instanceof UserDecorator) {
            baseUser = ((UserDecorator) user).getWrappedUser();
        }

        if (baseUser instanceof Student) 
            return new StudentView(user);
        if (baseUser instanceof Teacher) 
            return new TeacherView(user);
        if (baseUser instanceof Manager) 
            return new ManagerView((Manager) baseUser);
        if (baseUser instanceof Admin) 
            return new AdminView((Admin) baseUser);
        if (baseUser instanceof TechSupportSpecialist) 
            return new TechSupportView((TechSupportSpecialist) baseUser);
        return null;
    }

    private static int readInt() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}