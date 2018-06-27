package jadex.android;


import jadex.android.commons.Logger;
import jadex.android.exception.WrongEventClassError;
import jadex.bridge.service.types.context.IJadexAndroidEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;

/**
 * This Class manages Android Activity Contexts to make them available for other
 * classes.
 */
public class AndroidContextManager
{
	// --------- attributes -----------
	/** The last valid android context object */
	private Context lastContext;

	/** listeners of context changes */
	private List<AndroidContextChangeListener> contextListeners;

	/** set of strings to identify the device */
	private static Set<String> identifiers;
	
	private Map<String, List<IEventReceiver<?>>> eventReceivers;

	static
	{
		identifiers = new HashSet<String>();
		identifiers.add(android.os.Build.BRAND != null ? android.os.Build.BRAND.toLowerCase(): "");
		identifiers.add(android.os.Build.MANUFACTURER != null ? android.os.Build.MANUFACTURER.toLowerCase() : "");
		if (android.os.Build.MANUFACTURER != null && !android.os.Build.MANUFACTURER.equals(android.os.Build.UNKNOWN))
		{
			// stupid full text on x86 emulator, so skip it if emulated
			identifiers.add(android.os.Build.MODEL.toLowerCase());
			identifiers.add(android.os.Build.DEVICE.toLowerCase());
		}
		identifiers.add(android.os.Build.PRODUCT != null ? android.os.Build.PRODUCT.toLowerCase() : "");
		identifiers.add(android.os.Build.PRODUCT != null ? android.os.Build.BOARD.toLowerCase() : "");
	}
	
	/**
	 * Listener Interface
	 */
	public interface AndroidContextChangeListener
	{
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
	
	private static final AndroidContextManager instance = new AndroidContextManager();
	
	/**
	 * Returns the Instance of this JadexAndroidContext
	 * 
	 */
	public static AndroidContextManager getInstance()
	{
		return instance;
	}

	// -------- constructors --------
	
	/**
	 * Private Constructor - Singleton
	 */
	private AndroidContextManager()
	{
		contextListeners = new ArrayList<AndroidContextChangeListener>();
		eventReceivers = new HashMap<String, List<IEventReceiver<?>>>();
	}

	/**
	 * Sets a new Android Application Context. Pass <code>null</code> to unset
	 * the previous application context.
	 * 
	 * @param contextProvidingActivity
	 */
	public synchronized void setAndroidContext(Context contextProvidingActivity)
	{
		if (contextProvidingActivity == null)
		{
			if (lastContext != null) {
				synchronized (lastContext) {
					informContextDestroy(lastContext);
					Logger.d("Context destroyed");
				}
			}
		} else
		{
			synchronized (contextProvidingActivity) {
				informContextCreate(contextProvidingActivity);
				Logger.d("Context created");
			}
		}
		lastContext = contextProvidingActivity;
	}

	/**
	 * Returns the last known Android Context or <code>null</code>
	 * 
	 * @return Context
	 */
	public Context getAndroidContext()
	{
		return lastContext;
	}

	/**
	 * Adds a new Context Change Listener
	 * 
	 * @param l
	 */
	public void addContextChangeListener(AndroidContextChangeListener l)
	{
		contextListeners.add(l);
		if (lastContext != null)
		{
			l.onContextCreate(lastContext);
		}
	}

	/**
	 * Removes a Context Change Listener
	 * 
	 * @param l
	 */
	public void removeContextChangeListener(AndroidContextChangeListener l)
	{
		contextListeners.remove(l);
		l.onContextDestroy(lastContext);
	}

	public String getUniqueDeviceName()
	{

		StringBuilder sb = new StringBuilder();
		for (String identifier : identifiers)
		{
			if (!identifier.equals(android.os.Build.UNKNOWN))
			{
				sb.append(identifier.replace(' ', '_'));
			}
		}
		// ** Uncomment for unique device names **
		// int deviceFingerPrint = android.os.Build.FINGERPRINT.hashCode();
		// String hexString = Integer.toHexString(deviceFingerPrint);
		// if (hexString.length() > 1) {
		// sb.append(hexString.substring(0,2));
		// } else {
		// }

		return sb.toString();
	}

	private void informContextDestroy(Context ctx)
	{
		synchronized (contextListeners)
		{
			for (AndroidContextChangeListener l : contextListeners)
			{
				l.onContextDestroy(ctx);
			}
		}
	}

	private void informContextCreate(Context ctx)
	{
		synchronized (contextListeners)
		{
			for (AndroidContextChangeListener l : contextListeners)
			{
				l.onContextCreate(ctx);
			}
		}
	}
	
	public void registerEventListener(IEventReceiver<?> rec)
	{
		List<IEventReceiver<?>> receivers = this.eventReceivers.get(rec.getType());
		if (receivers == null)
		{
			receivers = new ArrayList<IEventReceiver<?>>();
			eventReceivers.put(rec.getType(), receivers);
		}
		receivers.add(rec);
	}

	public boolean dispatchEvent(IJadexAndroidEvent event)
	{
		boolean result = false;
		List<IEventReceiver<?>> list = eventReceivers.get(event.getType());
		if (list != null)
		{
			for (IEventReceiver<?> eventReceiver : list)
			{
				Class<?> eventClass = eventReceiver.getEventClass();
				if (eventClass.equals(event.getClass()))
				{
					try
					{
						Method method = eventReceiver.getClass().getMethod("receiveEvent", eventClass);
						method.invoke(eventReceiver, eventClass.cast(event));
					} catch (SecurityException e)
					{
						e.printStackTrace();
					} catch (NoSuchMethodException e)
					{
						e.printStackTrace();
					} catch (IllegalArgumentException e)
					{
						e.printStackTrace();
					} catch (IllegalAccessException e)
					{
						e.printStackTrace();
					} catch (InvocationTargetException e)
					{
						e.printStackTrace();
					}
					result = true;
				} else
				{
					throw new WrongEventClassError(eventReceiver.getEventClass(), event.getClass(), "");
				}
			}
		}
		return result;
	}

	public boolean unregisterEventListener(IEventReceiver<?> rec)
	{
		boolean removed = false;
		List<IEventReceiver<?>> list = eventReceivers.get(rec.getType());
		if (list != null)
		{
			removed = list.remove(rec);
		}
		return removed;
	}

}
