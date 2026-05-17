package models;

import java.util.ArrayList;
import java.util.List;

import data.Database;
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
