package jadex.android.bluetooth.util;

import android.util.Log;

public class Helper {

	public static final String LOG_TAG = "android-bluetooth";
	
	public static int jLog(String msg) {
		return Log.i(LOG_TAG, msg);
	}
	
	public static int jError(String msg) {
		return Log.e(LOG_TAG, msg);
	}
}
