package utils;

import java.io.Serializable;

import core.LocalizationManager;

public class Request implements Serializable{
	private static final long serialVersionUID = 1L;
	private RequestState state; 
	private String description;

    public Request() {
        this.state = new PendingState();
    }

    public void approve() {
        if (!(state instanceof PendingState)) {
            System.out.println(LocalizationManager.getString("err_cannot_approve_status", state.getStatusName()));
            return;
        }
        setState(new ApprovedState());
        System.out.println(LocalizationManager.getString("request_approved"));
    }

    public void reject() {
        if (!(state instanceof PendingState)) {
            System.out.println(LocalizationManager.getString("err_cannot_reject_status", state.getStatusName()));
            return;
        }
        setState(new RejectedState());
        System.out.println(LocalizationManager.getString("request_rejected"));
    }
    
    public void setState(RequestState state) {
        this.state = state;
    }
    
    public void setDescription(String desc) { this.description = desc; }
	public String getDescription() { return description; }

    
    public String getCurrentStatus() {
        return state.getStatusName();
    } 
    
    @Override
    public String toString() {
        return LocalizationManager.getString("request_info", "User", state.getStatusName());
    }
}
