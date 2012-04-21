package jadex.android;

import jadex.bridge.IExternalAccess;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

public class JadexAndroidContext {

	private static final JadexAndroidContext instance = new JadexAndroidContext();
	private IExternalAccess extAcc;
	private Context lastContext;

	private List<AndroidContextChangeListener> contextListeners;
	private Map<String, List<EventReceiver<?>>> eventReceivers;

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

	/**
	 * Private Constructor - Singleton
	 */
	private JadexAndroidContext() {
		contextListeners = new ArrayList<AndroidContextChangeListener>();
		eventReceivers = new HashMap<String, List<EventReceiver<?>>>();
	}

	/**
	 * Returns the Instance of this JadexAndroidContext
	 * 
	 * @return
	 */
	public static JadexAndroidContext getInstance() {
		return instance;
	}

	/**
	 * Sets the External Access for the Jadex Platform
	 * 
	 * @param extAcc
	 *            IExternalAccess
	 */
	public void setExternalPlattformAccess(IExternalAccess extAcc) {
		this.extAcc = extAcc;
	}

	/**
	 * Returns the Jadex External Platfrom Access object
	 * 
	 * @return IExternalAccess
	 */
	public IExternalAccess getExternalPlattformAccess() {
		return this.extAcc;
	}

	// public IFuture getAndroidContextService() {
	// IFuture<IAndroidContextService> service = SServiceProvider.getService(
	// access.getServiceContainer(), IAndroidContextService.class);
	// service.addResultListener(new
	// DefaultResultListener<IAndroidContextService>() {
	// @Override
	// public void resultAvailable(IAndroidContextService result) {
	// contextService = result;
	// }
	// });
	// }

	public boolean isJadexRunning() {
		return this.extAcc != null;
	}

	/**
	 * Sets a new Android Application Context. Pass <code>null</code> to unset
	 * the previous application context.
	 * 
	 * @param contextProvidingActivity
	 */
	public void setAndroidContext(Context contextProvidingActivity) {
		if (contextProvidingActivity == null) {
			informContextDestroy(lastContext);
		} else {
			informContextCreate(lastContext);
		}
		lastContext = contextProvidingActivity;
	}

	/**
	 * Returns the last known Android Context or <code>null</code>
	 * 
	 * @return Context
	 */
	public Context getAndroidContext() {
		return lastContext;
	}

	/**
	 * Adds a new Context Change Listener
	 * 
	 * @param l
	 */
	public void addContextChangeListener(AndroidContextChangeListener l) {
		contextListeners.add(l);
		if (lastContext != null) {
			l.onContextCreate(lastContext);
		}
	}

	/**
	 * Removes a Context Change Listener
	 * 
	 * @param l
	 */
	public void removeContextChangeListener(AndroidContextChangeListener l) {
		contextListeners.remove(l);
		l.onContextDestroy(lastContext);
	}

	private void informContextDestroy(Context ctx) {
		synchronized (contextListeners) {
			for (AndroidContextChangeListener l : contextListeners) {
				l.onContextDestroy(ctx);
			}
		}
	}

	private void informContextCreate(Context ctx) {
		synchronized (contextListeners) {
			for (AndroidContextChangeListener l : contextListeners) {
				l.onContextCreate(ctx);
			}
		}
	}

	public void registerEventListener(String eventName, EventReceiver<?> rec) {
		List<EventReceiver<?>> receivers = this.eventReceivers.get(eventName);
		if (receivers == null) {
			receivers = new ArrayList<EventReceiver<?>>();
			eventReceivers.put(eventName, receivers);
		}
		receivers.add(rec);
	}

	public boolean dispatchEvent(String eventName, Object event) {
		boolean result = false;
		List<EventReceiver<?>> list = eventReceivers.get(eventName);
		if (list != null) {
			for (EventReceiver<?> eventReceiver : list) {
				Class eventClass = eventReceiver.getEventClass();
				try {
					Method method = eventReceiver.getClass().getMethod(
							"receiveEvent", eventClass);
					Object cast = eventClass.cast(event);
					method.invoke(eventReceiver, cast);
					result = true;
				} catch (Exception e) {
				}
			}
		}
		return result;
	}

	public boolean unregisterEventListener(String eventName,
			EventReceiver<?> rec) {
		boolean removed = false;
		List<EventReceiver<?>> list = eventReceivers.get(eventName);
		if (list != null) {
			removed = list.remove(rec);
		}
		return removed;
	}

}
