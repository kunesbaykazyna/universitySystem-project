package models;

import java.io.Serializable;
import java.util.*;
import core.LocalizationManager;
import enumerations.Language;
import utils.News;
import utils.ResearchPaper;
import utils.ResearchProject;
import utils.UniversityJournal;
import data.Database;

public abstract class User implements UserComponent, Serializable {
    private static final long serialVersionUID = 1L;
    private String userId;
    private String login;
    private String name;
    private String password;
    public Language currentLanguage = Language.RU;
    private boolean isResearcher = false;
    private List<UniversityJournal> subscribedJournals = new ArrayList<>();
    private List<ResearchPaper> publishedPapers = new ArrayList<>();
    private List<ResearchProject> researchProjects = new ArrayList<>();

    public User(String userId, String name, String password, String login) {
        this.name = name;
        this.password = password;
        this.login = login;
        this.userId = userId;
        this.publishedPapers = new ArrayList<>();
        this.researchProjects = new ArrayList<>();
        this.subscribedJournals = new ArrayList<>();
    }

    public boolean login(String enteredLogin, String enteredPassword) {
        if (this.login.equals(enteredLogin) && this.password.equals(enteredPassword)) {
            System.out.println(LocalizationManager.getString("welcome_user", name));
            return true;
        }
        return false;
    }

    public void switchLanguage(Language newLanguage) {
        LocalizationManager.setLanguage(newLanguage);
        this.currentLanguage = newLanguage;
        System.out.println(LocalizationManager.getString("language.changed"));
    }

    public void subscribeToJournal(UniversityJournal journal) {
        if (!subscribedJournals.contains(journal)) {
            subscribedJournals.add(journal);
            journal.addSubscriber(this);
        }
    }

    public void unsubscribeFromJournal(UniversityJournal journal) {
        if (subscribedJournals.remove(journal)) {
            journal.removeSubscriber(this);
        }
    }

    public void addPaper(ResearchPaper paper) {
        publishedPapers.add(paper);
        News news = new News("Research", getName() + " published a new paper: " + paper.getTitle(), true);
        Database.getInstance().getNews().add(news);
        Database.getInstance().announceTopResearcher();
        Database.getInstance().save();
    }

    public void addResearchProject(ResearchProject project) {
        if (project != null && !researchProjects.contains(project)) {
            researchProjects.add(project);
        }
    }

    public int calculateHIndex() {
        if (publishedPapers.isEmpty()) return 0;
        List<Integer> citations = new ArrayList<>();
        for (ResearchPaper p : publishedPapers) citations.add(p.getCitations());
        citations.sort(Collections.reverseOrder());
        int h = 0;
        for (int i = 0; i < citations.size(); i++) {
            if (citations.get(i) >= i + 1) h = i + 1;
            else break;
        }
        return h;
    }

    public void printPapers(Comparator<ResearchPaper> comparator) {
        publishedPapers.sort(comparator);
        publishedPapers.forEach(System.out::println);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId) && Objects.equals(login, user.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, login);
    }

    @Override
    public String toString() {
        return String.format("User[ID='%s', Name='%s']", userId, name);
    }

    public void update(String newsMessage) {
        System.out.println(LocalizationManager.getString("user_notification", getName(), newsMessage));
    }

    public List<UniversityJournal> getSubscribedJournals() {
        return subscribedJournals;
    }

    public List<ResearchPaper> getPublishedPapers() { 
    	return publishedPapers; 
    }
    
    public List<ResearchProject> getResearchProjects() {
    	return researchProjects; 
    }
    public String getUserId() { 
    	return userId; 
    }
    
    public String getLogin() {
    	return login; 
    }
    
    public String getName() { 
    	return name;
    }
    
    public String getPassword() {
    	return password; 
    }
    
    public boolean isResearcher() { 
    	return isResearcher; 
    }
    
    public void setResearcher(boolean isResearcher) { 
    	this.isResearcher = isResearcher; 
    }
}