package jadex.android.standalone.clientapp;

import android.app.Activity;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;

/**
 * This class provides some Activity functionality to a Fragment by calling the
 * corresponding methods on the parent activity.
 * 
 * @author Julian Kalinowski
 * 
 */
class ActivityAdapterFragment extends android.support.v4.app.Fragment
{
	
	public static final int BIND_AUTO_CREATE = Activity.BIND_AUTO_CREATE;
	public static final int BIND_DEBUG_UNBIND = Activity.BIND_DEBUG_UNBIND;
	public static final int BIND_NOT_FOREGROUND = Activity.BIND_NOT_FOREGROUND;
	private Intent intent;

	/**
	 * Calls getActivity().bindService()
	 */
	public boolean bindService(Intent service, ServiceConnection conn, int flags)
	{
		boolean success = getActivity().bindService(service, conn, flags);
		System.out.println("Bind success: " + success);
		return success;
	}

	/**
	 * Calls getActivity().unbindService()
	 */
	public void unbindService(ServiceConnection conn)
	{
		getActivity().unbindService(conn);
	}
	
	/**
	 * Calls getActivity().startService();
	 */
	public void startService(Intent service) {
		getActivity().startService(service);
	}
	
	/**
	 * Calls getActivity().stopService();
	 * @return 
	 */
	public boolean stopService(Intent service) {
		return getActivity().stopService(service);
	}
	
	/**
	 * Calls getActivity().finish();
	 */
	protected void finish() {
		getActivity().finish();
	}

	/**
	 * Calls getActivity().setProgressBarIndeterminateVisibility()
	 */
	public void setProgressBarIndeterminateVisibility(boolean indeterminate)
	{
		getActivity().setProgressBarIndeterminate(indeterminate);
	}
	
	public void setProgressBarIndeterminate(boolean value)
	{
		getActivity().setProgressBarIndeterminate(value);
	}

	/**
	 * Calls getActivity().runOnUiThread()
	 */
	public void runOnUiThread(Runnable runnable)
	{
		getActivity().runOnUiThread(runnable);
	}
	
	/**
	 * Calls getActivity().setTitle()
	 */
	public void setTitle(CharSequence title) {
		getActivity().setTitle(title);
	}
	
	/**
	 * Calls getActivity().setTitle()
	 */
	public void setTitle(int titleId) {
		getActivity().setTitle(titleId);
	}
	
	/**
	 * Calls getActivity().managedQuery()
	 */
	public final Cursor managedQuery(Uri uri,
            String[] projection,
            String selection,
            String[] selectionArgs,
            String sortOrder)
	{
		return getActivity().managedQuery(uri, projection, selection, selectionArgs, sortOrder);
	}
	
	public Intent getIntent()
	{
		return intent;
	}
	
	public void setIntent(Intent intent) {
		this.intent = intent;
	}
	
	
}
