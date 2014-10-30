package jadex.android.standalone.clientapp;

import jadex.android.exception.JadexAndroidError;
import jadex.android.standalone.clientservice.UniversalClientService.UniversalClientServiceBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;

public class ClientAppFragment extends ActivityAdapterFragment
{
	private UniversalClientServiceBinder universalService;
	
	/** ApplicationInfo, set on instantiating this Fragment */
	private ApplicationInfo appInfo;

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

	protected Context getContext()
	{
		return getActivity().getApplicationContext();
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
