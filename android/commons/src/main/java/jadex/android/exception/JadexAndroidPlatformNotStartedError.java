package jadex.android.exception;

/**
 * This Error is thrown when a method is called that requires a running Jadex
 * platform, but Jadex is not running.
 * 
 * @author Julian Kalinowski
 */
public class JadexAndroidPlatformNotStartedError extends JadexAndroidError {
	private static final long serialVersionUID = 274715626051336618L;
	private String calledMethod;

	/**
	 * Constructor
	 * @param calledMethod
	 */
	public JadexAndroidPlatformNotStartedError(String calledMethod) {
		this.calledMethod = calledMethod;
	}
	
	@Override
	public String getMessage() {
		return "Jadex Platform was not running when " + calledMethod + " was called.";
	}
}
