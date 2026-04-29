package exceptions;

public class LowHIndexException extends Exception {
	private static final long serialVersionUID = 1L;

	public LowHIndexException(String message) {
        super(message);
    }
}