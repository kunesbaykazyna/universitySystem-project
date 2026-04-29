package exceptions;

public class NonResearcherException extends Exception {
	private static final long serialVersionUID = 1L;

	public NonResearcherException(String message) {
        super(message);
    }
}