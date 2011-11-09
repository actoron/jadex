package jadex.android.bluetooth;

import jadex.android.bluetooth.service.ConnectionService;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class JadexBluetoothActivity extends Activity {

	public static List<AndroidContextChangeListener> listeners = new ArrayList<AndroidContextChangeListener>();
	private static Context lastContext;

	public JadexBluetoothActivity() {
	}

	public static void addContextChangeListener(AndroidContextChangeListener l) {
		listeners.add(l);
		if (lastContext != null) {
			l.onContextCreate(lastContext);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		lastContext = this;
		informContextCreate(this);
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
		informContextDestroy(this);
	}

	private void informContextDestroy(Context ctx) {
		synchronized (listeners) {
			for (AndroidContextChangeListener l : listeners) {
				l.onContextDestroy(ctx);
			}
		}
	}

	private void informContextCreate(Context ctx) {
		synchronized (listeners) {
			for (AndroidContextChangeListener l : listeners) {
				l.onContextCreate(ctx);
			}
		}
	}
}
