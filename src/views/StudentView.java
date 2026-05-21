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
        System.out.println("1. " + LocalizationManager.getString("student_opt1"));
        System.out.println("2. " + LocalizationManager.getString("student_opt2"));
        System.out.println("3. " + LocalizationManager.getString("student_opt3"));
        System.out.println("4. " + LocalizationManager.getString("student_opt4"));
        System.out.println("5. " + LocalizationManager.getString("student_opt5"));
        System.out.println("6. " + LocalizationManager.getString("student_opt6")); // View teacher info
        if (getResearcherDecorator() != null) {
            System.out.println("7. " + LocalizationManager.getString("student_opt_research"));
        }
        System.out.println("8. " + LocalizationManager.getString("view_news"));
        System.out.println("9. " + LocalizationManager.getString("journals_menu"));
        System.out.println("10. " + LocalizationManager.getString("student_orgs"));
        System.out.println("11. " + LocalizationManager.getString("change_lang"));
        System.out.println("0. " + LocalizationManager.getString("logout_msg"));
    }

    @Override
    public void handleInput(int choice) {
        switch (choice) {
            case 1 -> viewCourses();
            case 2 -> registerForCourse();
            case 3 -> viewMarks();
            case 4 -> viewTranscript();
            case 5 -> rateTeacher();
            case 6 -> viewTeacherInfoForCourse();
            case 7 -> {
                if (getResearcherDecorator() != null) researchMenu();
                else System.out.println(LocalizationManager.getString("err_invalid"));
            }
            case 8 -> viewNews();
            case 9 -> journalsMenu();
            case 10 -> organizationsMenu();
            case 11 -> changeLanguage();
            case 0 -> System.out.println(LocalizationManager.getString("logout_msg"));
            default -> System.out.println(LocalizationManager.getString("err_invalid"));
        }
    }

    private void viewCourses() {
        db.getCourses().forEach(System.out::println);
    }
    
    private void registerForCourse() {
        System.out.print(LocalizationManager.getString("prompt_course_code_input"));
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
            System.out.println(LocalizationManager.getString("error_credit_limit"));
            return;
        }
        long failCount = s.getEnrollments().stream()
                .filter(e -> e.getMark() != null && e.getMark().getTotal() < 50)
                .count();
        if (failCount >= 3) {
            System.out.println(LocalizationManager.getString("error_fail_count"));
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
        System.out.print(LocalizationManager.getString("prompt_teacher_id"));
        String tid = scanner.nextLine();
        User teacher = db.findUserById(tid);
        if (!(teacher instanceof Teacher)) {
            System.out.println(LocalizationManager.getString("error_teacher_not_found"));
            return;
        }
        int rating = readInt(LocalizationManager.getString("prompt_rating"));
        if (rating < 1 || rating > 10) {
            System.out.println(LocalizationManager.getString("error_rating_range"));
            return;
        }
        getStudentObject().rateTeacher((Teacher) teacher, rating);
    }
    
    private void viewTeacherInfoForCourse() {
        List<Course> allCourses = db.getCourses();
        if (allCourses.isEmpty()) {
            System.out.println(LocalizationManager.getString("no_courses_available"));
            return;
        }
        System.out.println(LocalizationManager.getString("select_course"));
        for (int i = 0; i < allCourses.size(); i++) {
            System.out.println((i + 1) + ". " + allCourses.get(i).getName());
        }
        int idx = readInt() - 1;
        if (idx < 0 || idx >= allCourses.size()) return;
        Course course = allCourses.get(idx);
        List<Teacher> teachers = course.getTeachers();
        if (teachers.isEmpty()) {
            System.out.println(LocalizationManager.getString("no_teachers_for_course", course.getName()));
            return;
        }
        System.out.println(LocalizationManager.getString("teachers_for_course", course.getName()));
        for (Teacher t : teachers) {
            System.out.println(t);
        }
    }
    
    private void researchMenu() {
        ResearcherDecorator rd = getResearcherDecorator();
        if (rd == null) return;
        boolean back = false;
        while (!back) {
            System.out.println(LocalizationManager.getString("research_menu_title"));
            System.out.println("1. "+LocalizationManager.getString("research_view_papers"));
            System.out.println("2. "+LocalizationManager.getString("research_add_paper"));
            System.out.println("3. "+LocalizationManager.getString("research_print_citations"));
            System.out.println("4. "+LocalizationManager.getString("research_print_date"));
            System.out.println("5. "+LocalizationManager.getString("research_print_pages"));
            System.out.println("6. "+LocalizationManager.getString("research_calc_hindex"));
            System.out.println("7. " + LocalizationManager.getString("publish_to_journal"));
            System.out.println("0. "+LocalizationManager.getString("research_back"));
            int ch = readInt();
            switch (ch) {
                case 1 -> rd.getPublishedPapers().forEach(System.out::println);
                case 2 -> addPaper(rd);
                case 3 -> rd.printPapers(Comparator.comparing(ResearchPaper::getCitations).reversed());
                case 4 -> rd.printPapers(Comparator.comparing(ResearchPaper::getDate).reversed());
                case 5 -> rd.printPapers(Comparator.comparingInt(ResearchPaper::getPages).reversed());
                case 6 -> System.out.println("H-index: " + rd.calculateHIndex());
                case 7 -> publishToJournal();
                case 0 -> back = true;
                default -> System.out.println(LocalizationManager.getString("err_invalid"));
            }
        }
    }

    private void publishToJournal() {
        ResearcherDecorator rd = getResearcherDecorator();
        if (rd == null) return;
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
        
        System.out.print(LocalizationManager.getString("prompt_title"));
        String title = scanner.nextLine();
        System.out.print(LocalizationManager.getString("prompt_authors"));
        String authors = scanner.nextLine();
        System.out.print(LocalizationManager.getString("prompt_journal"));
        String journalName = scanner.nextLine();
        int pages = readInt(LocalizationManager.getString("prompt_pages"));
        int citations = readInt(LocalizationManager.getString("prompt_citations"));
        System.out.print(LocalizationManager.getString("prompt_doi"));
        String doi = scanner.nextLine();
        ResearchPaper paper = new ResearchPaper(title, authors, journalName, pages, new Date(), citations, doi);
        rd.publishToJournal(journal, paper);
        System.out.println(LocalizationManager.getString("paper_published"));
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
    
    private void viewNews() {
        List<News> allNews = db.getNews();
        if (allNews.isEmpty()) {
            System.out.println(LocalizationManager.getString("no_news"));
            return;
        }
        System.out.println(LocalizationManager.getString("news_header"));
        for (News n : allNews) {
            if (n.isPinned()) {
                System.out.println(" " + LocalizationManager.getString("news_item", n.getTitle(), n.getPostDate()) + LocalizationManager.getString("pinned_news"));
                System.out.println(LocalizationManager.getString("news_content", n.getContent()));
                System.out.println("---");
            }
        }
        for (News n : allNews) {
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
                selected.addComment((User) (student instanceof User ? student : getStudentObject()), commentText);
                db.save();
                System.out.println(LocalizationManager.getString("comment_added"));
            }
        }
    }
    
    private void journalsMenu() {
        Student student = getStudentObject();
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
                case 2 -> viewMySubscriptions(student);
                case 3 -> subscribeToJournal(student);
                case 4 -> unsubscribeFromJournal(student);
               
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
    
    private void organizationsMenu() {
        Student student = getStudentObject();
        boolean back = false;
        while (!back) {
            System.out.println("\n--- " + LocalizationManager.getString("orgs_title") + " ---");
            System.out.println("1. " + LocalizationManager.getString("view_all_orgs"));
            System.out.println("2. " + LocalizationManager.getString("my_orgs"));
            System.out.println("3. " + LocalizationManager.getString("create_org"));
            System.out.println("4. " + LocalizationManager.getString("join_org"));
            System.out.println("5. " + LocalizationManager.getString("leave_org"));
            System.out.println("6. " + LocalizationManager.getString("manage_org") + " (if head)");
            System.out.println("0. " + LocalizationManager.getString("back"));
            int ch = readInt();
            switch (ch) {
                case 1 -> viewAllOrganizations();
                case 2 -> viewMyOrganizations(student);
                case 3 -> createOrganization(student);
                case 4 -> joinOrganization(student);
                case 5 -> leaveOrganization(student);
                case 6 -> manageOrganization(student);
                case 0 -> back = true;
                default -> System.out.println(LocalizationManager.getString("err_invalid"));
            }
        }
    }
    
    private void viewAllOrganizations() {
        List<StudentOrganization> orgs = db.getOrganizations();
        if (orgs.isEmpty()) System.out.println(LocalizationManager.getString("no_orgs"));
        else orgs.forEach(System.out::println);
    }

    private void viewMyOrganizations(Student s) {
        List<StudentOrganization> myOrgs = s.getMyOrganizations();
        if (myOrgs.isEmpty()) System.out.println(LocalizationManager.getString("no_my_orgs"));
        else myOrgs.forEach(System.out::println);
    }

    private void createOrganization(Student s) {
        System.out.print(LocalizationManager.getString("enter_org_name"));
        String name = scanner.nextLine();
        if (name.trim().isEmpty()) return;
        s.createOrganization(name);
        System.out.println(LocalizationManager.getString("org_created", name));
    }

    private void joinOrganization(Student s) {
        List<StudentOrganization> orgs = db.getOrganizations();
        if (orgs.isEmpty()) {
            System.out.println(LocalizationManager.getString("no_orgs"));
            return;
        }
        System.out.println(LocalizationManager.getString("select_org"));
        for (int i = 0; i < orgs.size(); i++) {
            System.out.println((i+1) + ". " + orgs.get(i).getName());
        }
        int idx = readInt() - 1;
        if (idx < 0 || idx >= orgs.size()) return;
        s.joinOrganization(orgs.get(idx));
        System.out.println(LocalizationManager.getString("joined_org"));
    }

    private void leaveOrganization(Student s) {
        List<StudentOrganization> myOrgs = s.getMyOrganizations();
        if (myOrgs.isEmpty()) {
            System.out.println(LocalizationManager.getString("no_my_orgs"));
            return;
        }
        System.out.println(LocalizationManager.getString("select_org_to_leave"));
        for (int i = 0; i < myOrgs.size(); i++) {
            System.out.println((i+1) + ". " + myOrgs.get(i).getName());
        }
        int idx = readInt() - 1;
        if (idx < 0 || idx >= myOrgs.size()) return;
        s.leaveOrganization(myOrgs.get(idx));
        System.out.println(LocalizationManager.getString("left_org"));
    }

    private void manageOrganization(Student s) {
        List<StudentOrganization> myOrgs = s.getMyOrganizations();
        StudentOrganization org = null;
        for (StudentOrganization o : myOrgs) {
            if (o.getHead().equals(s)) {
                org = o;
                break;
            }
        }
        if (org == null) {
            System.out.println(LocalizationManager.getString("not_head"));
            return;
        }
        boolean back = false;
        while (!back) {
            System.out.println("\n--- " + LocalizationManager.getString("manage_org_title", org.getName()) + " ---");
            System.out.println("1. " + LocalizationManager.getString("view_members"));
            System.out.println("2. " + LocalizationManager.getString("add_member"));
            System.out.println("3. " + LocalizationManager.getString("remove_member"));
            System.out.println("4. " + LocalizationManager.getString("transfer_head"));
            System.out.println("0. " + LocalizationManager.getString("back"));
            int ch = readInt();
            switch (ch) {
                case 1 -> org.getMembers().forEach(m -> System.out.println(m.getName()));
                case 2 -> {
                    System.out.print(LocalizationManager.getString("enter_student_id"));
                    String sid = scanner.nextLine();
                    User user = db.findUserById(sid);
                    if (user instanceof Student) org.addMember((Student) user);
                    else System.out.println(LocalizationManager.getString("err_user_not_found"));
                }
                case 3 -> {
                    System.out.print(LocalizationManager.getString("enter_student_id"));
                    String sid = scanner.nextLine();
                    User user = db.findUserById(sid);
                    if (user instanceof Student) org.removeMember((Student) user);
                    else System.out.println(LocalizationManager.getString("err_user_not_found"));
                }
                case 4 -> {
                    System.out.print(LocalizationManager.getString("new_head_id"));
                    String sid = scanner.nextLine();
                    User user = db.findUserById(sid);
                    if (user instanceof Student && org.getMembers().contains(user))
                        org.setHead((Student) user);
                    else System.out.println(LocalizationManager.getString("err_user_not_found"));
                }
                case 0 -> back = true;
            }
        }
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