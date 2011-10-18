package jadex.android.bluetooth.shadows;

import android.util.Log;

import com.xtremelabs.robolectric.internal.Implementation;
import com.xtremelabs.robolectric.internal.Implements;

@Implements(Log.class)
public class MyShadowLog {
	 @Implementation
	 public static int i(String tag, String msg) {
		 System.out.println("[" + tag + "] " + msg);
		 return 0;
	 }
	 
	 @Implementation
	 public static int d(String tag, String msg) {
		 System.out.println("debug: [" + tag + "] " + msg);
		 return 0;
	 }
	 
	 @Implementation
	 public static int e(String tag, String msg) {
		 System.err.println("[" + tag + "] " + msg);
		 return 0;
	 }
}
