package utils;

import models.User;
import java.util.ArrayList;
import java.util.List;

public class UniversityJournal {
    private List<User> subscribers = new ArrayList<>();

    public void addSubscriber(User user) {
        if (!subscribers.contains(user)) {
            subscribers.add(user);
        }
    }

    public void removeSubscriber(User user) {
        subscribers.remove(user);
    }

    public void notifySubscribers(String newsTitle) {
        for (User user : subscribers) {
            user.update("Вышла новая статья в журнале: " + newsTitle);
        }
    }
}
