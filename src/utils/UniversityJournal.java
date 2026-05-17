package utils;

import models.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import core.LocalizationManager;

public class UniversityJournal implements Serializable{
	private static final long serialVersionUID = 1L;
    private String name;
    private List<User> subscribers = new ArrayList<>();
    private List<ResearchPaper> papers = new ArrayList<>();

    public UniversityJournal(String name) {
        this.name = name;
    }

    public void addSubscriber(User user) {
        if (!subscribers.contains(user)) subscribers.add(user);
    }

    public void removeSubscriber(User user) { subscribers.remove(user); }

    public void publishPaper(ResearchPaper paper) {
        papers.add(paper);
        notifySubscribers(paper.getTitle());
    }

    private void notifySubscribers(String paperTitle) {
        for (User user : subscribers) {
        	String notificationMessage = LocalizationManager.getString("journal_new_paper_notification", name, paperTitle);
            user.update(notificationMessage);
        }
    }
}
