package data;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import models.ResearcherDecorator;
import models.User;
import utils.*;
import models.UserComponent;
public class Database implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String DATA_FILE = "database.ser";
    private static volatile Database instance;

    private List<User>    users    = new ArrayList<>();
    private List<Course>  courses  = new ArrayList<>();
    private List<News>    news     = new ArrayList<>();
    private List<Message> messages = new ArrayList<>();
    private List<Request> requests = new ArrayList<>();
    private Log           log      = new Log();

    private Database() {}

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
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(this);
        } catch (IOException e) {
            System.err.println("Сақтау қатесі: " + e.getMessage());
        }
    }

    public ResearcherDecorator getTopCitedResearcher() {
        ResearcherDecorator top = null;
        int maxH = -1;
        for (User u : users) {
            UserComponent comp = u;
            if (comp instanceof ResearcherDecorator) {
                ResearcherDecorator rd = (ResearcherDecorator) comp;
                int h = rd.calculateHIndex();
                if (h > maxH) { maxH = h; top = rd; }
            } 
//            else if (comp instanceof UserDecorator && ((UserDecorator) comp).getWrappedUser() instanceof ResearcherDecorator) {
//            }
        }
        return top;
    }

    public void announceTopResearcher() {
        ResearcherDecorator top = getTopCitedResearcher();
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

    public List<User>    getUsers()    { return users; }
    public List<Course>  getCourses()  { return courses; }
    public List<News>    getNews()     { return news; }
    public List<Message> getMessages() { return messages; }
    public List<Request> getRequests() { return requests; }
    public Log           getLog()      { return log; }

    public void setUsers(List<User> users)       { this.users = users; }
    public void setCourses(List<Course> courses) { this.courses = courses; }
    public void setNews(List<News> news)         { this.news = news; }

    public User findUserByLogin(String login) {
        return users.stream()
                .filter(u -> u.getLogin().equals(login))
                .findFirst()
                .orElse(null);
    }

    public User findUserById(String id) {
        return users.stream()
                .filter(u -> u.getUserId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
