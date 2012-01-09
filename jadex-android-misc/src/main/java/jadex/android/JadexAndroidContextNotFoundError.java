package jadex.android;

/**
 * This Error is thrown if no Android Context could be found.
 * @author Julian Kalinowski
 *
 */
public class JadexAndroidContextNotFoundError extends Error {
@Override
public String getMessage() {
	return "Could not find Android Application Context!\nMake sure your (Main) Activity extends 'JadexAndroidActivity'!";
}
}
