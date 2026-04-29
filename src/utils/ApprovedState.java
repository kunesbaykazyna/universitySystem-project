package utils;

public class ApprovedState implements RequestState{

	@Override
	public String getStatusName() {
		return "Approved";
	}

	@Override
	public void handle(Request request) {		
	}

}
