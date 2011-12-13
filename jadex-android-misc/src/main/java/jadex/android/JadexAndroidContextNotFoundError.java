package jadex.android;

public class JadexAndroidContextNotFoundError extends Error {
@Override
public String getMessage() {
	return "Could not find Android Application Context!\nMake sure your (Main) Activity extends 'JadexAndroidActivity'!";
}
}
