package utils;

public class Request {
	private RequestState state; 

    public Request() {
        this.state = new PendingState();
    }

    public void setState(RequestState state) {
        this.state = state;
    }

    public void approve() {
        setState(new ApprovedState());
        System.out.println("Запрос одобрен.");
    }

    public void reject() {
        setState(new RejectedState());
        System.out.println("Запрос отклонен.");
    }

    public String getCurrentStatus() {
        return state.getStatusName();
    }
}
