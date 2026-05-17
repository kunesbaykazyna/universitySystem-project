package utils;
public class ApprovedState implements RequestState {
	private static final long serialVersionUID = 1L;
	@Override
    public String getStatusName() { return "Approved"; }
    @Override
    public void approve(Request request) { /* уже апрувнуто ничего не надо делать*/ }
    @Override
    public void reject(Request request) { throw new IllegalStateException("Cannot reject an approved request"); }
}

