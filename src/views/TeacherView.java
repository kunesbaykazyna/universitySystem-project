package views;

import models.*;
import utils.*;
import data.Database;
import enumerations.*;
import core.LocalizationManager;

import java.util.*;

public class TeacherView implements View {
    private UserComponent teacher;
    private Scanner scanner = new Scanner(System.in);
    private Database db = Database.getInstance();

    public TeacherView(UserComponent teacher) {
        this.teacher = teacher;
    }

    private Teacher getTeacherObject() {
        if (teacher instanceof Teacher) return (Teacher) teacher;
        if (teacher instanceof UserDecorator) {
            UserComponent wrapped = ((UserDecorator) teacher).getWrappedUser();
            if (wrapped instanceof Teacher) return (Teacher) wrapped;
        }
        throw new IllegalStateException("Component is not a Teacher");
    }

    private ResearcherDecorator getResearcherDecorator() {
        if (teacher instanceof ResearcherDecorator) return (ResearcherDecorator) teacher;
        return null;
    }

    @Override
    public void showMenu() {
        System.out.println("\n--- " + LocalizationManager.getString("menu_teacher_title") + " ---");
        System.out.println("1. " + LocalizationManager.getString("teacher_opt1"));
        System.out.println("2. " + LocalizationManager.getString("teacher_opt2"));
        System.out.println("3. " + LocalizationManager.getString("teacher_opt3"));
        System.out.println("4. " + LocalizationManager.getString("teacher_opt4"));
        System.out.println("5. " + LocalizationManager.getString("teacher_opt5"));
        System.out.println("6. " + LocalizationManager.getString("teacher_opt6"));
        if (getResearcherDecorator() != null) {
            System.out.println("7. " + LocalizationManager.getString("teacher_opt_research"));
        }
        System.out.println("8. " + LocalizationManager.getString("view_inbox"));
        System.out.println("9. " + LocalizationManager.getString("view_news"));
        System.out.println("10. " + LocalizationManager.getString("change_lang"));
        System.out.println("0. " + LocalizationManager.getString("logout_msg"));
    }

    @Override
    public void handleInput(int choice) {
        switch (choice) {
            case 1 -> viewMyCourses();
            case 2 -> putMark();
            case 3 -> markAttendance();
            case 4 -> sendComplaint();
            case 5 -> viewStudentInfo();
            case 6 -> sendMessageToEmployee();
            case 7 -> {
                if (getResearcherDecorator() != null) researchMenu();
                else System.out.println(LocalizationManager.getString("err_invalid"));
            }
            case 8 -> viewInbox();
            case 9 -> viewNews();
            case 10 -> changeLanguage();
            case 0 -> System.out.println(LocalizationManager.getString("logout_msg"));
            default -> System.out.println(LocalizationManager.getString("err_invalid"));
        }
    }

    private void viewMyCourses() {
        List<Course> myCourses = getTeacherObject().getMyCourses();
        if (myCourses.isEmpty()) {
            System.out.println(LocalizationManager.getString("no_courses"));
        } else {
            myCourses.forEach(System.out::println);
        }
    }

    private void viewInbox() {
        List<Message> inbox = getTeacherObject().getInbox();
        if (inbox.isEmpty()) {
            System.out.println(LocalizationManager.getString("no_messages"));
            return;
        }
        System.out.println(LocalizationManager.getString("inbox_title"));
        for (Message m : inbox) {
            System.out.println(m);
        }
    }

    private void viewNews() {
        List<News> allNews = db.getNews();
        if (allNews.isEmpty()) {
            System.out.println(LocalizationManager.getString("no_news"));
            return;
        }
        System.out.println(LocalizationManager.getString("news_header"));
        for (News n : allNews) {
            if (n.isPinned()) {
                System.out.println(" "+ LocalizationManager.getString("news_item", n.getTitle(), n.getPostDate()) + LocalizationManager.getString("pinned_news"));
                System.out.println(LocalizationManager.getString("news_content", n.getContent()));
                System.out.println("---");
            }
        }
        for (News n : allNews) {
            if (!n.isPinned()) {
                System.out.println(LocalizationManager.getString("news_item", n.getTitle(), n.getPostDate()));
                System.out.println(LocalizationManager.getString("news_content", n.getContent()));
                System.out.println("---");
            }
        }
    }
    
    private void putMark() {
        List<Course> myCourses = getTeacherObject().getMyCourses();
        if (myCourses.isEmpty()) {
            System.out.println(LocalizationManager.getString("no_courses"));
            return;
        }
        System.out.println(LocalizationManager.getString("select_course"));
        for (int i = 0; i < myCourses.size(); i++) {
            System.out.println((i + 1) + ". " + myCourses.get(i).getName());
        }
        int idx = readInt() - 1;
        if (idx < 0 || idx >= myCourses.size()) return;
        Course course = myCourses.get(idx);

        List<Student> students = getTeacherObject().viewStudents(course);
        if (students.isEmpty()) {
            System.out.println(LocalizationManager.getString("no_students"));
            return;
        }
        System.out.println(LocalizationManager.getString("select_student"));
        for (int i = 0; i < students.size(); i++) {
            System.out.println((i + 1) + ". " + students.get(i).getName());
        }
        int sIdx = readInt() - 1;
        if (sIdx < 0 || sIdx >= students.size()) return;
        Student student = students.get(sIdx);

        Enrollment enrollment = course.getE().stream()
                .filter(e -> e.getStudent().equals(student) && "APPROVED".equals(e.getStatus()))
                .findFirst().orElse(null);
        if (enrollment == null) {
            System.out.println(LocalizationManager.getString("err_enrollment_not_approved"));
            return;
        }
        double a1 = readDouble(LocalizationManager.getString("enter_att1"));
        double a2 = readDouble(LocalizationManager.getString("enter_att2"));
        double fin = readDouble(LocalizationManager.getString("enter_final"));
        Mark mark = new Mark(a1, a2, fin);
        getTeacherObject().putMark(enrollment, mark);
    }

    private void markAttendance() {
        List<Course> myCourses = getTeacherObject().getMyCourses();
        if (myCourses.isEmpty()) {
            System.out.println(LocalizationManager.getString("no_courses"));
            return;
        }
        System.out.println(LocalizationManager.getString("select_course"));
        for (int i = 0; i < myCourses.size(); i++) {
            System.out.println((i + 1) + ". " + myCourses.get(i).getName());
        }
        int idx = readInt() - 1;
        if (idx < 0 || idx >= myCourses.size()) return;
        Course course = myCourses.get(idx);

        System.out.print(LocalizationManager.getString("prompt_lesson_type"));
        LessonType type = LessonType.valueOf(scanner.nextLine().toUpperCase());
        Lesson lesson = new Lesson(type, 2);
        lesson.setCourse(course);

        List<Student> students = getTeacherObject().viewStudents(course);
        if (students.isEmpty()) {
            System.out.println(LocalizationManager.getString("no_students"));
            return;
        }
        for (Student s : students) {
            System.out.print(s.getName() + " " + LocalizationManager.getString("present") + " (y/n): ");
            String ans = scanner.nextLine();
            boolean present = "y".equalsIgnoreCase(ans);
            getTeacherObject().markAttendance(lesson, s, present);
        }
        System.out.println(LocalizationManager.getString("attendance_saved"));
    }

    private void sendComplaint() {
        System.out.print(LocalizationManager.getString("enter_student_id"));
        String sid = scanner.nextLine();
        User student = db.findUserById(sid);
        if (!(student instanceof Student)) {
            System.out.println(LocalizationManager.getString("err_user_not_found"));
            return;
        }
        System.out.print(LocalizationManager.getString("complaint_text"));
        String text = scanner.nextLine();
        UrgencyLevel urgency = readUrgency();
        Manager manager = (Manager) db.getUsers().stream()
                .filter(u -> u instanceof Manager).findFirst().orElse(null);
        if (manager == null) {
            System.out.println(LocalizationManager.getString("error_no_manager"));
            return;
        }
        getTeacherObject().sendComplaint((Student) student, text, urgency, manager);
    }

    private void viewStudentInfo() {
        List<Course> myCourses = getTeacherObject().getMyCourses();
        if (myCourses.isEmpty()) {
            System.out.println(LocalizationManager.getString("no_courses"));
            return;
        }
        System.out.println(LocalizationManager.getString("select_course"));
        for (int i = 0; i < myCourses.size(); i++) {
            System.out.println((i + 1) + ". " + myCourses.get(i).getName());
        }
        int idx = readInt() - 1;
        if (idx < 0 || idx >= myCourses.size()) return;
        Course course = myCourses.get(idx);
        List<Student> students = getTeacherObject().viewStudents(course);
        if (students.isEmpty()) {
            System.out.println(LocalizationManager.getString("no_students"));
            return;
        }
        System.out.println(LocalizationManager.getString("students_in_course", course.getName()));
        for (Student s : students) {
            System.out.println(s);
        }
    }

    private void sendMessageToEmployee() {
        List<Employee> employees = db.getUsers().stream()
                .filter(u -> u instanceof Employee && !u.equals(getTeacherObject()))
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
        getTeacherObject().sendMessage(receiver, content);
    }

    private void researchMenu() {
        ResearcherDecorator rd = getResearcherDecorator();
        if (rd == null) return;
        boolean back = false;
        while (!back) {
            System.out.println(LocalizationManager.getString("research_menu_title"));
            System.out.println("1. " + LocalizationManager.getString("research_view_papers"));
            System.out.println("2. " + LocalizationManager.getString("research_add_paper"));
            System.out.println("3. " + LocalizationManager.getString("research_print_citations"));
            System.out.println("4. " + LocalizationManager.getString("research_print_date"));
            System.out.println("5. " + LocalizationManager.getString("research_print_pages"));
            System.out.println("6. " + LocalizationManager.getString("research_calc_hindex"));
            System.out.println("0. " + LocalizationManager.getString("research_back"));
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
        System.out.print(LocalizationManager.getString("prompt_title"));
        String title = scanner.nextLine();
        System.out.print(LocalizationManager.getString("prompt_authors"));
        String authors = scanner.nextLine();
        System.out.print(LocalizationManager.getString("prompt_journal"));
        String journal = scanner.nextLine();
        int pages = readInt(LocalizationManager.getString("prompt_pages"));
        int citations = readInt(LocalizationManager.getString("prompt_citations"));
        System.out.print(LocalizationManager.getString("prompt_doi"));
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
            User user = (User) (teacher instanceof User ? teacher : getTeacherObject());
            user.switchLanguage(newLang);
        }
    }

    private UrgencyLevel readUrgency() {
    	System.out.println(LocalizationManager.getString("urgency_level"));
        System.out.println(LocalizationManager.getString("urgency_menu"));
        int choice = readInt();
        return switch (choice) {
            case 1 -> UrgencyLevel.LOW;
            case 2 -> UrgencyLevel.MEDIUM;
            default -> UrgencyLevel.HIGH;
        };
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
            return Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}