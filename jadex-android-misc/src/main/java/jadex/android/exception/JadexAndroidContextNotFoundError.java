package jadex.android.exception;

/**
 * This Error is thrown if no Android Context could be found.
 * 
 * @author Julian Kalinowski
 * 
 */
public class JadexAndroidContextNotFoundError extends JadexAndroidError {
	private static final long serialVersionUID = 3257231190621629872L;

	@Override
	public String getMessage() {
		return "Could not find Android Application Context!\nMake sure your (Main) Activity extends 'JadexAndroidActivity'!";
	}
}
