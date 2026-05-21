package models;

import java.util.ArrayList;
import java.util.List;

import core.LocalizationManager;
import data.Database;
import utils.Message;
import utils.Request;

public class TechSupportSpecialist extends Employee{
	private static final long serialVersionUID = 1L;

	public TechSupportSpecialist(String userId, String name, String password, String login, double salary) {
		super(userId, name, password, login, salary);
	}
	
	public void manageRequests(Request request, boolean isOk) {
        if (isOk) {
            request.approve();
        } else {
            request.reject();
        }
    }
	
	public void sendMessage(Employee receiver, String content) {
        Message msg = new Message(this, receiver, content);
        Database.getInstance().getMessages().add(msg);
        Database.getInstance().save();
        System.out.println(LocalizationManager.getString("message_sent", receiver.getName()));
    }
	
	public List<Request> viewPendingRequests() {
	    Database db = Database.getInstance();
	    List<Request> pending = new ArrayList<>();
	    for (Request r : db.getRequests()) {
	        if (r.getCurrentStatus().equals("Pending")) {
	            pending.add(r);
	        }
	    }
	    return pending;
	}
}
