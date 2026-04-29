package models;

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

}
