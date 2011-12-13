package jadex.android;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

public class JadexAndroidActivity extends Activity {

	private static List<AndroidContextChangeListener> listeners = new ArrayList<AndroidContextChangeListener>();
	private static Context lastContext;

	public static void addContextChangeListener(AndroidContextChangeListener l) {
		listeners.add(l);
		if (lastContext != null) {
			l.onContextCreate(lastContext);
		}
	}
	
	public static void removeContextChangeListener(AndroidContextChangeListener l) {
		listeners.remove(l);
		l.onContextDestroy(lastContext);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		lastContext = this;
		informContextCreate(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
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
