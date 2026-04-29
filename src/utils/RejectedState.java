package utils;

public class RejectedState implements RequestState{

	@Override
	public String getStatusName() {
		return "Rejected";
	}

	@Override
	public void handle(Request request) {		
	}

}
