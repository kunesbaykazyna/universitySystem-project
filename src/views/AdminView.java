package views;

import models.*;
import utils.Message;
import utils.News;
import utils.Request;
import utils.ResearchPaper;
import utils.UniversityJournal;
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
        System.out.println("1. " + LocalizationManager.getString("admin_opt1"));
        System.out.println("2. " + LocalizationManager.getString("admin_opt2"));
        System.out.println("3. " + LocalizationManager.getString("admin_opt3"));
        System.out.println("4. " + LocalizationManager.getString("admin_opt4"));
        System.out.println("5. " + LocalizationManager.getString("send_message"));
        System.out.println("6. " + LocalizationManager.getString("view_inbox"));
        System.out.println("7. " + LocalizationManager.getString("view_news"));
        System.out.println("8. " + LocalizationManager.getString("journals_menu"));
        System.out.println("9. " + LocalizationManager.getString("send_support_request"));
        System.out.println("10. " + LocalizationManager.getString("change_lang"));
        System.out.println("0. " + LocalizationManager.getString("logout_msg"));
    }

    @Override
    public void handleInput(int choice) {
        switch (choice) {
            case 1 -> addUser();
            case 2 -> removeUser();
            case 3 -> viewAllUsers();
            case 4 -> viewLogs();
            case 5 -> sendMessageToEmployee();
            case 6 -> viewInbox();
            case 7 -> viewNews();
            case 8 -> journalsMenu();
            case 9 -> sendSupportRequest();
            case 10 -> changeLanguage();
            case 0 -> System.out.println(LocalizationManager.getString("logout_msg"));
            default -> System.out.println(LocalizationManager.getString("err_invalid"));
        }
    }
    
    private void addUser() {
        System.out.print(LocalizationManager.getString("msg_enter_role") + " (STUDENT/TEACHER/ADMIN/MANAGER/TECH_SUPPORT/GRADUATED_STUDENT): ");
        String role = scanner.nextLine().toUpperCase();

        try {
            String userId = readNonEmpty(LocalizationManager.getString("prompt_user_id"));
            String name = readNonEmpty(LocalizationManager.getString("prompt_name"));
            String pwd = readNonEmpty(LocalizationManager.getString("prompt_password"));
            String login = readNonEmpty(LocalizationManager.getString("prompt_login"));

            User user = null;

            switch (role) {
                case "STUDENT" -> {
                    int yos = readInt(LocalizationManager.getString("prompt_years_of_study"));
                    Faculty fac = readFaculty();
                    user = UserFactory.createUser(role, userId, name, pwd, login, fac, yos);
                }
                case "TEACHER" -> {
                    Faculty fac = readFaculty();
                    TeacherType tt = readTeacherType();
                    double sal = readDouble(LocalizationManager.getString("prompt_salary"));
                    user = UserFactory.createUser(role, userId, name, pwd, login, fac, tt, sal);
                }
                case "ADMIN", "TECH_SUPPORT" -> {
                    double sal = readDouble(LocalizationManager.getString("prompt_salary"));
                    user = UserFactory.createUser(role, userId, name, pwd, login, sal);
                }
                case "MANAGER" -> {
                    double sal = readDouble(LocalizationManager.getString("prompt_salary"));
                    ManagerTypes mType = readManagerType(); // запрос типа менеджера
                    user = UserFactory.createUser(role, userId, name, pwd, login, sal, mType);
                }
                case "GRADUATED_STUDENT" -> {
                    int yos = readInt(LocalizationManager.getString("prompt_years_of_study"));
                    Faculty fac = readFaculty();
                    GraduateLevel level = readGraduateLevel();
                    String supId = readNonEmpty(LocalizationManager.getString("prompt_supervisor_id"));
                    User supUser = db.findUserById(supId);
                    if (supUser == null) {
                        System.out.println(LocalizationManager.getString("err_supervisor_not_found"));
                        return;
                    }
                    ResearcherDecorator supervisor = new ResearcherDecorator(supUser);
                    if (supervisor.calculateHIndex() < 3) {
                        System.out.println(LocalizationManager.getString("error_supervisor_hindex"));
                        return;
                    }
                    user = UserFactory.createUser(role, userId, name, pwd, login, fac, yos, level, supervisor);
                }
                default -> System.out.println(LocalizationManager.getString("error_unknown_role", role));
            }

            if (user != null) {
                admin.addUser(user);
            }
        } catch (Exception e) {
            System.out.println(LocalizationManager.getString("err_general") + e.getMessage());
        }
    }

    private void removeUser() {
        String id = readNonEmpty(LocalizationManager.getString("msg_enter_remove_id"));
//        user.getName()
        admin.removeUser(id);
    }

    private void viewAllUsers() {
        db.getUsers().forEach(System.out::println);
    }
    
    private void viewLogs() {
        admin.viewLogs();
    }
    
    private void sendMessageToEmployee() {
        List<Employee> employees = db.getUsers().stream()
                .filter(u -> u instanceof Employee && !u.equals(admin))
                .map(u -> (Employee) u)
                .toList();
        if (employees.isEmpty()) {
            System.out.println(LocalizationManager.getString("no_other_employees"));
            return;
        }
        System.out.println(LocalizationManager.getString("select_receiver"));
        for (int i = 0; i < employees.size(); i++) {
            System.out.println((i + 1) + ". " + employees.get(i).getName());
        }
        int idx = readInt() - 1;
        if (idx < 0 || idx >= employees.size()) return;
        Employee receiver = employees.get(idx);
        System.out.print(LocalizationManager.getString("enter_message_text"));
        String content = scanner.nextLine();
        if (content.trim().isEmpty()) return;
        admin.sendMessage(receiver, content);
    }

    private void viewInbox(){
        List<Message> inbox = admin.getInbox();
        if (inbox.isEmpty()) {
            System.out.println(LocalizationManager.getString("no_messages"));
            return;
        }
        System.out.println(LocalizationManager.getString("inbox_title"));
        for (Message m : inbox) System.out.println(m);
    }

    private void viewNews() {
        List<News> allNews = db.getNews();
        if (allNews.isEmpty()) {
            System.out.println(LocalizationManager.getString("no_news"));
            return;
        }
        System.out.println(LocalizationManager.getString("news_header"));
        List<News> sorted = new ArrayList<>(allNews);
        sorted.sort((a,b) -> {
            if (a.isPinned() != b.isPinned())
                return Boolean.compare(b.isPinned(), a.isPinned());
            return b.getPostDate().compareTo(a.getPostDate());
        });
        for (News n : sorted) {
            System.out.println(n);
            System.out.println("--- Comments ---");
            for (News.Comment c : n.getComments()) {
                System.out.println(c);
            }
        }
        
        System.out.println(LocalizationManager.getString("add_comment_prompt"));
        String choice = scanner.nextLine();
        if ("y".equalsIgnoreCase(choice)) {
            System.out.print(LocalizationManager.getString("enter_comment"));
            String commentText = scanner.nextLine();
            System.out.println(LocalizationManager.getString("select_news_index"));
            int idx = readInt() - 1;
            if (idx >= 0 && idx < allNews.size()) {
                News selected = allNews.get(idx);
                selected.addComment((User) (admin instanceof User ? admin : admin), commentText);
                db.save();
                System.out.println(LocalizationManager.getString("comment_added"));
            }
            
        }
    }
    
    private void journalsMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- " + LocalizationManager.getString("journals_title") + " ---");
            System.out.println("1. " + LocalizationManager.getString("view_all_journals"));
            System.out.println("2. " + LocalizationManager.getString("my_subscriptions"));
            System.out.println("3. " + LocalizationManager.getString("subscribe_to_journal"));
            System.out.println("4. " + LocalizationManager.getString("unsubscribe_from_journal"));
           
            System.out.println("0. " + LocalizationManager.getString("back"));
            int ch = readInt();
            switch (ch) {
                case 1 -> viewAllJournals();
                case 2 -> viewMySubscriptions(admin);
                case 3 -> subscribeToJournal(admin);
                case 4 -> unsubscribeFromJournal(admin);
               
                case 0 -> back = true;
                default -> System.out.println(LocalizationManager.getString("err_invalid"));
            }
        }
    }
    private void viewAllJournals() {
        List<UniversityJournal> journals = db.getJournals();
        if (journals.isEmpty()) {
            System.out.println(LocalizationManager.getString("no_journals"));
            return;
        }
        for (UniversityJournal j : journals) {
            System.out.println(j.getName() + " (papers: " + j.getPapers().size() + ")");
        }
    }

    private void viewMySubscriptions(User user) {
        List<UniversityJournal> subs = user.getSubscribedJournals();
        if (subs.isEmpty()) {
            System.out.println(LocalizationManager.getString("no_subscriptions"));
            return;
        }
        for (UniversityJournal j : subs) {
            System.out.println(j.getName());
            System.out.println(LocalizationManager.getString("papers_in_journal"));
            for (ResearchPaper p : j.getPapers()) {
                System.out.println("  " + p.getTitle());
            }
        }
    }

    private void subscribeToJournal(User user) {
        List<UniversityJournal> journals = db.getJournals();
        if (journals.isEmpty()) {
            System.out.println(LocalizationManager.getString("no_journals"));
            return;
        }
        System.out.println(LocalizationManager.getString("select_journal"));
        for (int i = 0; i < journals.size(); i++) {
            System.out.println((i+1) + ". " + journals.get(i).getName());
        }
        int idx = readInt() - 1;
        if (idx < 0 || idx >= journals.size()) return;
        UniversityJournal journal = journals.get(idx);
        user.subscribeToJournal(journal);
        System.out.println(LocalizationManager.getString("subscribed", journal.getName()));
    }

    private void unsubscribeFromJournal(User user) {
        List<UniversityJournal> subs = user.getSubscribedJournals();
        if (subs.isEmpty()) {
            System.out.println(LocalizationManager.getString("no_subscriptions"));
            return;
        }
        System.out.println(LocalizationManager.getString("select_subscription"));
        for (int i = 0; i < subs.size(); i++) {
            System.out.println((i+1) + ". " + subs.get(i).getName());
        }
        int idx = readInt() - 1;
        if (idx < 0 || idx >= subs.size()) return;
        UniversityJournal journal = subs.get(idx);
        user.unsubscribeFromJournal(journal);
        System.out.println(LocalizationManager.getString("unsubscribed", journal.getName()));
    }
    
    private void sendSupportRequest() {
        System.out.print(LocalizationManager.getString("enter_problem_description"));
        String description = scanner.nextLine();
        if (description.trim().isEmpty()) {
            System.out.println(LocalizationManager.getString("err_invalid"));
            return;
        }
        Request request = new Request();
        request.setDescription(description);
        db.getRequests().add(request);
        db.save();
        System.out.println(LocalizationManager.getString("request_sent"));
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
        if (newLang != null) {
            admin.switchLanguage(newLang);
        }
    }
    
    private ManagerTypes readManagerType() {
        while (true) {
            System.out.print(LocalizationManager.getString("prompt_manager_type"));
            try {
                return ManagerTypes.valueOf(scanner.nextLine().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println(LocalizationManager.getString("error_invalid_manager_type"));
            }
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
            System.out.print(LocalizationManager.getString("prompt_faculty"));
            try {
                return Faculty.valueOf(scanner.nextLine().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println(LocalizationManager.getString("error_invalid_faculty"));
            }
        }
    }

    private TeacherType readTeacherType() {
        while (true) {
            System.out.print(LocalizationManager.getString("prompt_teacher_type"));
            try {
                return TeacherType.valueOf(scanner.nextLine().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println(LocalizationManager.getString("error_invalid_teacher_type"));
            }
        }
    }

    private GraduateLevel readGraduateLevel() {
        while (true) {
            System.out.print(LocalizationManager.getString("prompt_graduate_level"));
            try {
                return GraduateLevel.valueOf(scanner.nextLine().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println(LocalizationManager.getString("error_invalid_level"));
            }
        }
    }
}