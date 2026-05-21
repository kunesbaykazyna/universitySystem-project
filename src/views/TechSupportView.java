package views;

import models.*;
import data.Database;
import enumerations.Language;
import core.LocalizationManager;
import utils.Message;
import utils.News;
import utils.Request;
import utils.ResearchPaper;
import utils.UniversityJournal;

import java.util.*;

public class TechSupportView implements View {
    private TechSupportSpecialist specialist;
    private Scanner scanner = new Scanner(System.in);
    private Database db = Database.getInstance();

    public TechSupportView(TechSupportSpecialist specialist) {
        this.specialist = specialist;
    }

    @Override
    public void showMenu() {
        System.out.println("\n--- " + LocalizationManager.getString("menu_tech_title") + " ---");
        System.out.println("1. " + LocalizationManager.getString("tech_opt1"));
        System.out.println("2. " + LocalizationManager.getString("tech_opt2"));
        System.out.println("3. " + LocalizationManager.getString("send_message"));
        System.out.println("4. " + LocalizationManager.getString("view_inbox"));
        System.out.println("5. " + LocalizationManager.getString("view_news"));
        System.out.println("6. " + LocalizationManager.getString("journals_menu"));
        System.out.println("7. " + LocalizationManager.getString("change_lang"));
        System.out.println("0. " + LocalizationManager.getString("logout_msg"));
    }

    @Override
    public void handleInput(int choice) {
        switch (choice) {
            case 1 -> viewPendingRequests();
            case 2 -> processRequest();
            case 3 -> sendMessageToEmployee();
            case 4 -> viewInbox();
            case 5 -> viewNews();
            case 6 -> journalsMenu();
            case 7 -> changeLanguage();
            case 0 -> System.out.println(LocalizationManager.getString("logout_msg"));
            default -> System.out.println(LocalizationManager.getString("err_invalid"));
        }
    }
    
    private void viewPendingRequests() {
        List<Request> pending = db.getRequests().stream()
                .filter(r -> "Pending".equals(r.getCurrentStatus()))
                .toList();
        if (pending.isEmpty()) {
            System.out.println(LocalizationManager.getString("no_pending_requests"));
        } else {
            pending.forEach(System.out::println);
        }
    }
    
    private void processRequest() {
        List<Request> pending = db.getRequests().stream()
                .filter(r -> "Pending".equals(r.getCurrentStatus()))
                .toList();
        if (pending.isEmpty()) {
            System.out.println(LocalizationManager.getString("no_pending_requests"));
            return;
        }
        System.out.println(LocalizationManager.getString("select_request"));
        for (int i = 0; i < pending.size(); i++) {
            System.out.println((i + 1) + ". " + pending.get(i));
        }
        int idx = readInt() - 1;
        if (idx < 0 || idx >= pending.size()) return;
        Request req = pending.get(idx);
        System.out.print(LocalizationManager.getString("prompt_approve"));
        String ans = scanner.nextLine();
        boolean approve = "y".equalsIgnoreCase(ans);
        specialist.manageRequests(req, approve);
        db.save();
    }
    
    private void sendMessageToEmployee() {
        List<Employee> employees = db.getUsers().stream()
                .filter(u -> u instanceof Employee && !u.equals(specialist))
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
        specialist.sendMessage(receiver, content);
    }
    
    private void viewInbox() {
        List<Message> inbox = specialist.getInbox();
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
                selected.addComment((User) (specialist instanceof User ? specialist :specialist), commentText);
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
                case 2 -> viewMySubscriptions(specialist);
                case 3 -> subscribeToJournal(specialist);
                case 4 -> unsubscribeFromJournal(specialist);
               
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
        if (newLang != null) specialist.switchLanguage(newLang);
    }

    
    private int readInt() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}