package utils;

public interface RequestState {
	public String getStatusName();
    public void handle(Request request);
}
