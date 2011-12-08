package jadex.android.bluetooth;

import jadex.android.JadexAndroidActivity;
import jadex.android.bluetooth.service.ConnectionService;
import android.content.Intent;
import android.os.Bundle;

public class JadexBluetoothActivity extends JadexAndroidActivity {

	public JadexBluetoothActivity() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = new Intent(this, ConnectionService.class);
		this.stopService(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (!isFinishing()) {
			Intent intent = new Intent(this, ConnectionService.class);
			this.startService(intent);
		}
	}
}
