package utils;

public class PendingState implements RequestState {

	private static final long serialVersionUID = 1L;

	@Override
	public void approve(Request request) {
	    request.setState(new ApprovedState());
	}

	@Override
	public void reject(Request request) {
	    request.setState(new RejectedState());
	}

	public String getStatusName() { 
		return "Pending";
	} 
	
}
