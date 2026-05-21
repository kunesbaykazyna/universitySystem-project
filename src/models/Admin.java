package models;

import data.Database;
import factory.UserFactory;
import utils.Message;

import java.util.List;

import core.LocalizationManager;

public class Admin extends Employee {
	private static final long serialVersionUID = 1L;

	public Admin(String userId, String name, String password, String login, double salary) {
        super(userId, name, password, login, salary);
    }

    public void addUser(User user) {
        Database db = Database.getInstance();
        List<User> allUsers = db.getUsers();

        boolean exists = allUsers.stream()
                .anyMatch(u -> u.getUserId().equals(user.getUserId()) || u.getLogin().equals(user.getLogin()));
        if (exists) {
        	System.out.println(LocalizationManager.getString("err_user_exists"));
            return;
        }
        allUsers.add(user);
        db.save(); //изменение в бд сохранить
        db.getLog().addEntry(this.getUserId(), LocalizationManager.getString("log_add_user", user.getUserId()));
        System.out.println(LocalizationManager.getString("user_added", user.getName()));
    }

    public void addUser(String role, String userId, String name, String password, String login, Object... extraArgs) {
        User user = UserFactory.createUser(role, userId, name, password, login, extraArgs);
        addUser(user); 
    }
    
    public void sendMessage(Employee receiver, String content) {
        Message msg = new Message(this, receiver, content);
        Database.getInstance().getMessages().add(msg);
        Database.getInstance().save();
        System.out.println(LocalizationManager.getString("message_sent", receiver.getName()));
    }

    public void removeUser(String userId) {
        Database db = Database.getInstance();
        List<User> allUsers = db.getUsers();

        User toRemove = allUsers.stream()
                .filter(u -> u.getUserId().equals(userId))
                .findFirst()
                .orElse(null);

        if (toRemove == null) {
        	System.out.println(LocalizationManager.getString("err_user_not_found", userId));
            return;
        }

        //админ не может удалить себя
        if (toRemove.getUserId().equals(this.getUserId())) {
        	System.out.println(LocalizationManager.getString("err_cannot_delete_self"));
            return;
        }

        allUsers.remove(toRemove);
        db.save();
        db.getLog().addEntry(this.getUserId(), LocalizationManager.getString("log_remove_user", userId));
        System.out.println(LocalizationManager.getString("user_removed", toRemove.getName()));
    }

    public void viewLogs() {
        Database.getInstance().getLog().printAll();
    }
}