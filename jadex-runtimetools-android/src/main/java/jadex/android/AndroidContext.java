package jadex.android;

import jadex.android.JadexAndroidContext.AndroidContextChangeListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;

/**
 * This Class manages Android Activity Contexts to make them available for other
 * classes.
 */
public class AndroidContext
{
	private Context lastContext;

	private List<AndroidContextChangeListener> contextListeners;

	private static Set<String> identifiers;

	static
	{
		identifiers = new HashSet<String>();
		identifiers.add(android.os.Build.BRAND.toLowerCase());
		identifiers.add(android.os.Build.MANUFACTURER.toLowerCase());
		if (!android.os.Build.MANUFACTURER.equals(android.os.Build.UNKNOWN))
		{
			// stupid full text on x86 emulator, so skip it if emulated
			identifiers.add(android.os.Build.MODEL.toLowerCase());
			identifiers.add(android.os.Build.DEVICE.toLowerCase());
		}
		identifiers.add(android.os.Build.PRODUCT.toLowerCase());
		identifiers.add(android.os.Build.BOARD.toLowerCase());
	}

	// -------- constructors --------
	public AndroidContext()
	{
		contextListeners = new ArrayList<AndroidContextChangeListener>();
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
			informContextDestroy(lastContext);
			System.out.println("Context destroy");
		} else
		{
			informContextCreate(lastContext);
			System.out.println("Context create");
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

	protected String getUniqueDeviceName()
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

}
