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
        System.out.println("9. " + LocalizationManager.getString("change_lang"));
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
            case 9 -> changeLanguage();
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
            if (!n.isPinned()) {
                System.out.println(LocalizationManager.getString("news_item", n.getTitle(), n.getPostDate()));
                System.out.println(LocalizationManager.getString("news_content", n.getContent()));
                System.out.println("---");
            }
        }
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
            System.out.println("0. "+LocalizationManager.getString("research_back"));
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