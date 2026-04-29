package models;

import java.util.List;

public class Admin extends Employee{
	private static final long serialVersionUID = 1L;

	public Admin(String userId, String name, String password, String login, double salary) {
		super(userId, name, password, login, salary);
	}
	
	public void addUser(List<User> users, User u) {
        users.add(u);
    }

    public void removeUser(List<User> users, User u) {
        users.remove(u);
    }

    public void viewLogs() {
        System.out.println("Отображение логов системы...");
    }
	
}
