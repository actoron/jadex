package jadex.android.exception;

/**
 * This exception is thrown if an event could not be delivered because receiver
 * and event type do not match.
 * 
 */
public class WrongEventClassError extends JadexAndroidError {
	private static final long serialVersionUID = 7406711168427252765L;
	private String message;
	private Class<?> given;
	private Class<?> expected;

	public WrongEventClassError(Class<?> expected, Class<?> given,
			String message) {
		super();
		this.expected = expected;
		this.given = given;
		this.message = message;
	}

	@Override
	public String getMessage() {
		return "Wrong event Class! Expected: " + expected + ", given: " + given
				+ ". " + message;
	}
}
