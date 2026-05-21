package data;

import java.io.*;

import java.util.ArrayList;
import java.util.List;

import models.User;
import utils.*;
public class Database implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String DATA_FILE = "database.ser";
    private static volatile Database instance;

    private List<Enrollment> registrationQueue = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private List<Course>courses= new ArrayList<>();
    private List<News> news = new ArrayList<>();
    private List<Message> messages = new ArrayList<>();
    private List<Request> requests = new ArrayList<>();
    private Log log= new Log();
    private List<UniversityJournal> journals = new ArrayList<>();
    private List<StudentOrganization> organizations = new ArrayList<>();
    private List<Lesson> lessons = new ArrayList<>();
    
    private Database() {
        if (journals == null) {
            journals = new ArrayList<>();
        }
        if (journals.isEmpty()) {
            journals.add(new UniversityJournal("University Research Journal"));
            journals.add(new UniversityJournal("Computer Science Review"));
            journals.add(new UniversityJournal("Applied Mathematics"));
        }
    }

    public static Database getInstance() {
        if (instance == null) {
            synchronized (Database.class) {
                if (instance == null) {
                    instance = load();
                }
            }
        }
        return instance;
    }

    public void save() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(this);
        } catch (IOException e) {
            System.err.println("Сақтау қатесі: " + e.getMessage());
        }
    }

    public User getTopCitedResearcher() {
        User top = null;
        int maxH = -1;
        for (User u : users) {
            if (u.isResearcher()) {
                int h = u.calculateHIndex();
                if (h > maxH) {
                    maxH = h;
                    top = u;
                }
            }
        }
        return top;
    }

    public void announceTopResearcher() {
        User top = getTopCitedResearcher();
        if (top != null) {
            News newsItem = new News(
                "Top Researcher",
                top.getName() + " has the highest H-index (" + top.calculateHIndex() + ") in the university!",
                true
            );
            news.add(newsItem);
            save();
        }
    }
    
    private static Database load() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return new Database();
        
        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(file))) {
            return (Database) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
            return new Database();
        }
    }

    public User findUserByLogin(String login) {
        return users.stream().filter(u -> u.getLogin().equals(login)).findFirst().orElse(null);
    }

    public User findUserById(String id) {
        return users.stream().filter(u -> u.getUserId().equals(id)).findFirst().orElse(null);
    }

    public List<Enrollment> getRegistrationQueue() {
        if (registrationQueue == null) {
            registrationQueue = new ArrayList<>();
        }
        return registrationQueue;
    }
   
    public List<UniversityJournal> getJournals() { 
    	return journals; 
    }
    
    public void setJournals(List<UniversityJournal> journals) {
    	this.journals = journals; 
    }
    
    public List<StudentOrganization> getOrganizations() {
    	return organizations; 
    }
    
    public List<Lesson> getLessons() { 
    	return lessons;
    }
    
    public void setLessons(List<Lesson> lessons) { 
    	this.lessons = lessons; 
    }
    
    public List<User> getUsers(){ 
    	return users; 
    }
    
    public List<Course> getCourses(){
    	return courses; 
    }
    
    public List<News>getNews(){ 
    	return news; 
    }
    
    public List<Message> getMessages() { 
    	return messages; 
    }
    
    public List<Request> getRequests() { 
    	return requests; 
    }
    
    public Log getLog(){
    	return log; 
    }

    public void setUsers(List<User> users){ 
    	this.users = users;
    }
    
    public void setCourses(List<Course> courses){ 
    	this.courses = courses;
    }
    
    public void setNews(List<News> news){ 
    	this.news = news; 
    }
}

