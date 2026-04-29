package utils;

public class PendingState implements RequestState {

	@Override
	public String getStatusName() { 
		return "Pending"; 
	}

	@Override
	public void handle(Request request) {
		//реализовать метод так чтобы в след state переходил типо так
		request.setState(new ApprovedState());
		
		
	}
	
}
