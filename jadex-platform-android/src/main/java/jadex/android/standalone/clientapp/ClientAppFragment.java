package jadex.android.standalone.clientapp;

import jadex.android.standalone.clientservice.ServiceConnectionProxy;
import jadex.android.standalone.clientservice.UniversalClientBinder;
import jadex.android.standalone.clientservice.UniversalClientService;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.Window;

public class ClientAppFragment extends ActivityAdapterFragment
{
	private ServiceConnectionProxy serviceConnectionProxy;
	
	private Map<ComponentName, ServiceConnection> serviceConnections = new HashMap<ComponentName, ServiceConnection>();

	public ClientAppFragment() {
		super();
	}
	
	/**
	 * This method is called upon instantiation of the Fragment. Tasks that
	 * should be run before the layout of the Activity is set should be
	 * performed here.
	 * 
	 * Note that getActivity() will return null during this method.
	 * 
	 * @param mainActivity
	 */
	public void onPrepare(Activity mainActivity)
	{
		mainActivity.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
	}
	
	protected Context getContext()
	{
		return getActivity().getApplicationContext();
	}
	
	@Override
	public boolean bindService(Intent service, ServiceConnection conn, int flags)
	{
		ComponentName originalComponent = service.getComponent();
		String clientServiceName = originalComponent.getClassName();
		if (clientServiceName.equals("jadex.android.service.JadexPlatformService")) {
			return super.bindService(service, conn, flags);
		} else {
			// individual user service requested
			service = convertIntent(service);
			serviceConnections.put(originalComponent, conn);
			
			serviceConnectionProxy = new ServiceConnectionProxy(conn, originalComponent);
			return super.bindService(service, serviceConnectionProxy, flags);
		}
	}
	
	@Override
	public void unbindService(ServiceConnection conn)
	{
		super.unbindService(serviceConnectionProxy);
		serviceConnections.values().remove(conn);
	}
	
	@Override
	public void startService(Intent service)
	{
		super.startService(convertIntent(service));
	}
	
	@Override
	public boolean stopService(Intent service)
	{
		return super.stopService(convertIntent(service));
	}
	
	
	private Intent convertIntent(Intent service) {
		ComponentName originalComponent = service.getComponent();
		service.putExtra(UniversalClientBinder.CLIENT_SERVICE_COMPONENT, originalComponent);
		ComponentName universalComponent = new ComponentName(this.getActivity(), UniversalClientService.class);
		service.setComponent(universalComponent);
		return service;
	}
	
	
	
	
}
