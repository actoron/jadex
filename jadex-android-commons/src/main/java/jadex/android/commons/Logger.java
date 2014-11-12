package jadex.android.commons;

import android.util.Log;

/**
 * Wraps the android.util.Log class.
 * Accepts objects, so it can replace default System.out.print().
 */
public class Logger {

	private static String LOG_TAG = "jadex-android";

	public static void i(Object o) {
		i(String.valueOf(o));
	}

	public static void i(String s) {
		Log.i(LOG_TAG, s);
	}

	public static void d(Object o) {
		d(String.valueOf(o));
	}

	public static void d(String s) {
		Log.d(LOG_TAG, s);
	}

	public static void e(Object o) {
		e(String.valueOf(o));
	}

	public static void e(String s) {
		Log.e(LOG_TAG, s);
	}
	
	public static void w(Object o) {
		w(String.valueOf(o));
	}
	
	public static void w(String s) {
		Log.w(LOG_TAG, s);
	}

}
