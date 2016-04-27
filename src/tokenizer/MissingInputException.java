package tokenizer;

public class MissingInputException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1711804591490561035L;

	public MissingInputException() {
		super();
	}
	
	public MissingInputException(String message) {
		super(message);
	}
	
	public MissingInputException(String message, Throwable cause) {
		super(message, cause);
	}
}
