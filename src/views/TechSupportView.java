package views;

import models.*;
import data.Database;
import enumerations.Language;
import core.LocalizationManager;
import utils.Request;
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
        System.out.println("\n--- TECH SUPPORT MENU ---");
        System.out.println("1. View pending requests");
        System.out.println("2. Process a request");
        System.out.println("9. Change language");
        System.out.println("0. Logout");
    }

    @Override
    public void handleInput(int choice) {
        switch (choice) {
            case 1 -> viewPendingRequests();
            case 2 -> processRequest();
            case 9 -> changeLanguage();
            case 0 -> System.out.println(LocalizationManager.getString("logout_msg"));
            default -> System.out.println(LocalizationManager.getString("err_invalid"));
        }
    }

    private void viewPendingRequests() {
        List<Request> pending = db.getRequests().stream()
                .filter(r -> "Pending".equals(r.getCurrentStatus()))
                .toList();
        if (pending.isEmpty()) {
            System.out.println("No pending requests.");
        } else {
            pending.forEach(System.out::println);
        }
    }

    private void processRequest() {
        List<Request> pending = db.getRequests().stream()
                .filter(r -> "Pending".equals(r.getCurrentStatus()))
                .toList();
        if (pending.isEmpty()) {
            System.out.println("No pending requests.");
            return;
        }
        System.out.println("Select request:");
        for (int i = 0; i < pending.size(); i++) {
            System.out.println((i + 1) + ". " + pending.get(i));
        }
        int idx = readInt() - 1;
        if (idx < 0 || idx >= pending.size()) return;
        Request req = pending.get(idx);
        System.out.print("Approve? (y/n): ");
        String ans = scanner.nextLine();
        boolean approve = "y".equalsIgnoreCase(ans);
        specialist.manageRequests(req, approve);
        db.save();   
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