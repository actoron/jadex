package jadex.android.standalone.clientapp;

import jadex.android.exception.JadexAndroidError;
import jadex.android.standalone.clientservice.UniversalClientService.UniversalClientServiceBinder;
import android.app.Activity;
import android.app.LauncherActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Window;

public class ClientAppFragment extends ActivityAdapterFragment
{
	private UniversalClientServiceBinder universalService;

	public ClientAppFragment()
	{
		super();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}

	/**
	 * This method is called upon instantiation of the Fragment and before the
	 * default Fragment Lifecycle comes into play. 
	 * 
	 * Note that getActivity() will return null during this method, use the given parameter
	 * instead.
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
		ComponentName originalComponent = service.getComponent();
		String clientServiceName = originalComponent.getClassName();
		if (clientServiceName.equals("jadex.android.service.JadexPlatformService"))
		{
			return super.bindService(service, conn, flags);
		}
		else
		{
			// individual user service requested
			if (universalService.isClientServiceConnection(conn))
			{
				// TODO: check for valid clientServiceName
				throw new JadexAndroidError("already bound: " + clientServiceName);
			}
			else
			{
				final Handler handler = new Handler();
				handler.post(new Runnable()
				{
					@Override
					public void run()
					{
						universalService.bindClientService(service, conn, flags);	
					}
				});
			}
			return true;
		}
	}

	@Override
	public void unbindService(ServiceConnection conn)
	{
		if (universalService.isClientServiceConnection(conn))
		{
			boolean unbindClientService = universalService.unbindClientService(conn);
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
		String clientServiceName = originalComponent.getClassName();
		if (clientServiceName.equals("jadex.android.service.JadexPlatformService"))
		{
			super.startService(service);
		}
		else
		{
			// individual user service requested
			universalService.startClientService(service);
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
	
	@Override
	public LayoutInflater getLayoutInflater(Bundle savedInstanceState)
	{
		LayoutInflater layoutInflater = super.getLayoutInflater(savedInstanceState);
//		LayoutInflater userInflater = LayoutInflater.from(getContext());
//		
//		userInflater.setFactory(layoutInflater.getFactory());
		return layoutInflater;
	}
	
	@Override
	public void startActivity(Intent intent)
	{
		startActivityForResult(intent, -1);
	}
	
	@Override
	public void startActivityForResult(Intent intent, int requestCode)
	{
		
		super.startActivityForResult(intent, requestCode);
	}
	
}
