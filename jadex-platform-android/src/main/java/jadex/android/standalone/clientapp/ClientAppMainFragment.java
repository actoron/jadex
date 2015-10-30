package jadex.android.standalone.clientapp;

import jadex.android.exception.JadexAndroidError;
import jadex.android.standalone.clientservice.UniversalClientService.UniversalClientServiceBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ClientAppMainFragment extends ActivityAdapterFragment
{
	/**
	 * Extra key to alter back stack behavior when switching to another Fragment via startActivity().
	 * Set to true (default) to include the previous fragment in the back stack,
	 * or to false to not include it.
	 */
	public static final String EXTRA_KEY_BACKSTACK = "jadex.android.standalone.clientapp.ClientAppFragment.backstack";
	
	private UniversalClientServiceBinder universalService;
	
	/** ApplicationInfo, set on instantiating this Fragment */
	private ApplicationInfo appInfo;

	/** indicates whether this fragment should be finished upon next attach **/
	protected boolean finished;

	/**
	 * This method is called upon instantiation of the Fragment and before the
	 * default Fragment Lifecycle comes into play. 
	 * 
	 * Note that getActivity() will return null during this method, use the given parameter
	 * instead.
	 * 
	 * If you need an options menu, be sure to call setHasOptionsMenu in onCreate!
	 * 
	 * @param mainActivity
	 */
	public void onPrepare(Activity mainActivity)
	{
	}

	@Override
	public Context getContext()
	{
//		return getActivity().getApplicationContext();
		return super.getContext();
	}
	
	/**
	 * Finishes the fragment, e.g. pops the back stack to display
	 * the previous fragment again.
	 */
	protected void finishFragment() {
		finished = true;
	}
	
	@Override
	public void onStart() {
		if (finished) {
			getActivity().getSupportFragmentManager().popBackStack();
		}
		super.onStart();
	}
	
	@Override
	public boolean bindService(final Intent service, final ServiceConnection conn, final int flags)
	{
		boolean result = false;
		ComponentName originalComponent = service.getComponent();
		
		if (originalComponent == null) {
			// external service, pass-through intent
			result = super.bindService(service, conn, flags);
		}
		else 
		{
			String clientServiceName = originalComponent.getClassName();
			if (universalService.isClientServiceConnection(conn))
			{
				// TODO: check for valid clientServiceName
				throw new JadexAndroidError("already bound: " + clientServiceName);
			}
			else
			{
				result = universalService.bindClientService(service, conn, flags, appInfo);	
			}
		}
		return result;
	}

	@Override
	public void unbindService(ServiceConnection conn)
	{
		if (universalService.isClientServiceConnection(conn))
		{
			universalService.unbindClientService(conn);
		}
		else
		{
			super.unbindService(conn);
		}
	}

	@Override
	public void startService(Intent service)
	{
		ComponentName originalComponent = service.getComponent();
		if (originalComponent == null) {
			// external service, pass-through intent
			super.startService(service);
		} else {
			// individual user service requested
			universalService.startClientService(service, appInfo);
		}
	}

	@Override
	public boolean stopService(Intent service)
	{
		if (universalService.isClientServiceStarted(service))
		{
			return universalService.stopClientService(service);
		}
		else
		{
			return super.stopService(service);
		}
	}
	
	public void setUniversalClientService(UniversalClientServiceBinder service)
	{
		this.universalService = service;
	}
	
	public void setApplicationInfo(ApplicationInfo appInfo) 
	{
		this.appInfo = appInfo;
	}
	
	public ApplicationInfo getApplicationInfo()
	{
		return appInfo;
	}
	
}
