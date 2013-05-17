package jadex.android.standalone.clientservice;

import jadex.android.exception.JadexAndroidError;
import jadex.android.service.JadexPlatformManager;
import jadex.commons.SReflect;

import java.util.HashMap;
import java.util.Map;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;

public class UniversalClientService extends Service
{
	/**
	 * Maps ClassNames to Service instances.
	 */
	private Map<String, Service> serviceInstances;

	private Map<ServiceConnection, Service> serviceConnections;
	
	private Map<ServiceConnection, Intent> clientIntents;

	private Map<ServiceConnection, ComponentName> componentNames;

	@Override
	public void onCreate()
	{
		super.onCreate();
		serviceInstances = new HashMap<String, Service>();
		serviceConnections = new HashMap<ServiceConnection, Service>();
		clientIntents = new HashMap<ServiceConnection, Intent>();
		componentNames = new HashMap<ServiceConnection, ComponentName>();
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return new UniversalClientServiceBinder();
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		// TODO: destroy all client services
	}

	private static Service createClientService(String className)
	{
		Service result;
		ClassLoader cl = JadexPlatformManager.getInstance().getClassLoader(null);
		@SuppressWarnings("unchecked")
		Class<Service> clientServiceClass = SReflect.classForName0(className, cl);
		try
		{
			result = clientServiceClass.newInstance();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return result;
	}

	private static Intent createClientIntent(Intent intent)
	{
		Intent result = new Intent();
		ComponentName clientServiceComponent = intent.getParcelableExtra(ClientBinderProxy.CLIENT_SERVICE_COMPONENT);
		result.setComponent(clientServiceComponent);
		result.putExtras(intent.getExtras());
		result.removeExtra(ClientBinderProxy.CLIENT_SERVICE_COMPONENT);
		return result;
	}

	public class UniversalClientServiceBinder extends Binder
	{
		public UniversalClientServiceBinder()
		{

		}

		public ClientBinderProxy bindClientService(Intent intent, ServiceConnection conn)
		{
			ComponentName clientServiceComponent = intent.getComponent();

			String clientServiceClassName = clientServiceComponent.getClassName();
			String clientServicePackageName = clientServiceComponent.getPackageName();

			Service clientService = serviceInstances.get(clientServiceClassName);
			// do we already have an instance?
			if (clientService == null)
			{
				clientService = createClientService(clientServiceClassName);
				clientService.onCreate();
				serviceInstances.put(clientServiceClassName, clientService);
			}

			Service service = serviceConnections.get(conn);
			// has the service already been bound with the given connection?
			if (service == null)
			{
				Intent clientIntent = createClientIntent(intent);
				IBinder clientBinder = clientService.onBind(clientIntent);

				clientIntents.put(conn, clientIntent);
				serviceConnections.put(conn, clientService);
				componentNames.put(conn, clientServiceComponent);

				ClientBinderProxy binderProxy = new ClientBinderProxy(clientBinder);
				return binderProxy;
			}
			else
			{
				throw new JadexAndroidError("Service already bound!");
			}
		}

		public boolean unbindClientService(ServiceConnection conn)
		{
			Service service = serviceConnections.get(conn);
			if (service != null)
			{
				Intent clientIntent = clientIntents.get(conn);
				boolean isUnbound = service.onUnbind(clientIntent);
				conn.onServiceDisconnected(componentNames.get(conn));
				serviceConnections.remove(conn);
				componentNames.remove(conn);
				clientIntents.remove(conn);
				return isUnbound;
			}
			else
			{
				return false;
			}
			// TODO: check for last binding, then destroy service
		}

		public boolean isClientServiceConnection(ServiceConnection conn)
		{
			return serviceConnections.containsKey(conn);
		}
	}

}
