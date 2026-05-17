package utils;

import core.LocalizationManager;

public class RejectedState implements RequestState {
    
	private static final long serialVersionUID = 1L;
	@Override
    public String getStatusName() { return "Rejected"; }
    @Override
    public void approve(Request request) {throw new IllegalStateException(LocalizationManager.getString("err_cannot_approve_rejected")); }
    @Override
    public void reject(Request request) { 
    	/* уже отклонена ничего не делаем*/ 
    }
}
