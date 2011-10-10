package jadex.android.bluetooth;

import android.app.Activity;
import android.content.Context;

public class JadexBluetoothActivity extends Activity {
	
	public static Context application_context;
	
	public JadexBluetoothActivity() {
		JadexBluetoothActivity.application_context = this;
	}
}
