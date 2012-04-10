package jadex.android;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

/**
 * This abstract base Activity Class passes the current Android Context to the
 * Jadex Android Services.
 * 
 * @author Julian Kalinowski
 * 
 */
public abstract class ContextProvidingActivity extends Activity {
	private static List<AndroidContextChangeListener> listeners = new ArrayList<AndroidContextChangeListener>();

	/**
	 * Listener Interface
	 */
	public interface AndroidContextChangeListener {
		/**
		 * Called when an Android Context is destroyed
		 * 
		 * @param ctx
		 */
		void onContextDestroy(Context ctx);

		/**
		 * Called when an Android Context is created
		 * 
		 * @param ctx
		 */
		void onContextCreate(Context ctx);
	}

	private static Context lastContext;

	/**
	 * Adds a new Context Change Listener
	 * 
	 * @param l
	 */
	public static void addContextChangeListener(AndroidContextChangeListener l) {
		listeners.add(l);
		if (lastContext != null) {
			l.onContextCreate(lastContext);
		}
	}

	/**
	 * Removes a Context Change Listener
	 * 
	 * @param l
	 */
	public static void removeContextChangeListener(
			AndroidContextChangeListener l) {
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
