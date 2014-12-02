package jadex.android.standalone.clientservice;

import jadex.android.commons.Logger;
import jadex.android.exception.JadexAndroidError;
import jadex.android.service.JadexPlatformManager;
import jadex.android.service.JadexPlatformService;
import jadex.commons.SReflect;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

public class UniversalClientService extends Service
{
	/**
	 * Maps ClassNames to Service instances.
	 */
	private Map<String, Service> serviceInstances;

	/**
	 * Maps ServiceConnections to Service instances.
	 */
	private Map<ServiceConnection, Service> serviceConnections;
	
	/**
	 * Maps ServiceConnections to intents for unbinding.
	 */
	private Map<ServiceConnection, Intent> clientIntents;

	/**
	 * Maps ServiceConnections to ComponentNames (currently debug only).
	 */
	private Map<ServiceConnection, ComponentName> componentNames;
	
	/**
	 * Contains state of a service. True = started.
	 */
	private Map<Service, Boolean> startedServices;

	/**
	 * Handler to execute tasks.
	 */
	private Handler backgroundHandler;
	
	private static Method attachBaseContextMethod; 
	
	public UniversalClientService()
	{
		super();
		if (attachBaseContextMethod == null) {
			try
			{
				attachBaseContextMethod = ContextWrapper.class.getDeclaredMethod("attachBaseContext", new Class[]{Context.class});
				attachBaseContextMethod.setAccessible(true);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		serviceInstances = new HashMap<String, Service>();
		serviceConnections = new HashMap<ServiceConnection, Service>();
		clientIntents = new HashMap<ServiceConnection, Intent>();
		componentNames = new HashMap<ServiceConnection, ComponentName>();
		startedServices = new HashMap<Service, Boolean>();
		backgroundHandler = new Handler();
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return new UniversalClientServiceBinder();
	}

	@Override
	public void onDestroy()
	{
		//TODO: destroy all services
		super.onDestroy();
	}
	
	@Override
	public void onLowMemory()
	{
		// TODO: inform all
		super.onLowMemory();
	}
	
	private Service createClientService(String className, ApplicationInfo appInfo)
	{
		Service result;
		ClassLoader cl = JadexPlatformManager.getInstance().getClassLoader(appInfo.sourceDir);
		Class<?> clientServiceClass = SReflect.classForName0(className, cl);
		try
		{
			Logger.d("Creating new Client Service: " + className);
			result = (Service) clientServiceClass.newInstance();
			Context baseContext = getBaseContext();
			if (result instanceof JadexPlatformService) {
				JadexPlatformService jadexPlatformService = (JadexPlatformService) result;
				jadexPlatformService.attachBaseContext(baseContext);
				jadexPlatformService.setApplicationInfo(appInfo);
			} else {
				attachBaseContextMethod.invoke(result, baseContext);
			}
		}
		catch (Exception e)
		{
			throw new JadexAndroidError(e);
		}
		return result;
	}

	public class UniversalClientServiceBinder extends Binder
	{
		public UniversalClientServiceBinder()
		{
		}

		public boolean bindClientService(final Intent intent, final ServiceConnection conn, int flags, final ApplicationInfo appInfo)
		{
			final ComponentName clientServiceComponent = intent.getComponent();
			final String clientServiceClassName = clientServiceComponent.getClassName();
			
			backgroundHandler.post(new Runnable()
			{
				@Override
				public void run()
				{
					// TODO: handle flags

					Service clientService = serviceInstances.get(clientServiceClassName);
					// if the service instance doesn't exist yet, create it
					if (clientService == null)
					{
						clientService = createClientService(clientServiceClassName, appInfo);
						Logger.d("Creating new Service instance: " + clientServiceClassName);
						clientService.onCreate();
						serviceInstances.put(clientServiceClassName, clientService);
					} else {
						ClassLoader rightCl = JadexPlatformManager.getInstance().getClassLoader(appInfo.sourceDir);
						try {
							Class<?> loadClass = rightCl.loadClass(clientServiceClassName);
							rightCl = loadClass.getClassLoader();
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
						ClassLoader actualCl = clientService.getClass().getClassLoader();
						if (!actualCl.equals(rightCl)) {
							Logger.d("Service instance found, but classloaders differ: " + rightCl + "\n" + actualCl);
							Logger.d("Application must have been updated. Terminating previous service instance.");
							clientService.stopSelf();
							serviceInstances.remove(clientServiceClassName);
							Logger.d("Creating new Service instance: " + clientServiceClassName);
							clientService = createClientService(clientServiceClassName, appInfo);
							clientService.onCreate();
							serviceInstances.put(clientServiceClassName, clientService);
						} else {
							Logger.d("Service instance found: " + clientServiceClassName);
						}
					}

					// has the service already been bound with the given connection?
					if (!serviceConnections.containsKey(conn))
					{
						IBinder clientBinder = clientService.onBind(intent);

						// we need this intent for unbinding too, but no extras:
						Intent intentCopy = intent.cloneFilter(); 
						clientIntents.put(conn, intentCopy);
						serviceConnections.put(conn, clientService);
						componentNames.put(conn, clientServiceComponent);

						Logger.d("Binding Client Service: " + clientServiceClassName + " with binder object: " + clientBinder);
						conn.onServiceConnected(clientServiceComponent, clientBinder);
					}
					else
					{
						throw new JadexAndroidError("Service already bound!");
					}
				}
			});
			
			ClassLoader cl = JadexPlatformManager.getInstance().getClassLoader(appInfo.sourceDir);
			Class<?> clientServiceClass = SReflect.classForName0(clientServiceClassName, cl);
			if (clientServiceClass != null) {
				return true;
			} else {
				return false;
			}
		}

		public boolean unbindClientService(ServiceConnection conn)
		{
			boolean result = false;
			Service service = serviceConnections.get(conn);
			if (service != null)
			{
				Intent clientIntent = clientIntents.get(conn);
				ComponentName componentName = componentNames.get(conn);
				Logger.d("Unbinding Client Service: " + componentName);
				boolean onUnbind = service.onUnbind(clientIntent);
				// onUnbind returns false by default, so don't respect it
				conn.onServiceDisconnected(componentName);
				serviceConnections.remove(conn);
				componentNames.remove(conn);
				clientIntents.remove(conn);
				
				checkDestroyService(service);
				result = onUnbind;
			}
			else
			{
			}
			return result;
		}

		public boolean isClientServiceConnection(ServiceConnection conn)
		{
			return serviceConnections.containsKey(conn);
		}
		
		public void startClientService(Intent service, ApplicationInfo appInfo) {
			String clientServiceClassName = service.getComponent().getClassName();

			Service clientService = serviceInstances.get(clientServiceClassName);
			// do we already have an instance?
			if (clientService == null)
			{
				clientService = createClientService(clientServiceClassName, appInfo);
				clientService.onCreate();
				serviceInstances.put(clientServiceClassName, clientService);
			}
			
			clientService.onStart(service, 0);
			startedServices.put(clientService, Boolean.TRUE);
		}
		
		public boolean stopClientService(Intent intent) {
			boolean result = false;
			Boolean b = startedServices.remove(serviceInstances.get(intent.getComponent().getClassName()));
			if (b != null && b) {
				String clientServiceClassName = intent.getComponent().getClassName();
				Service clientService = serviceInstances.get(clientServiceClassName);
				
				checkDestroyService(clientService);
				
				result = true;
			}
			return result;
		}

		private void checkDestroyService(Service clientService)
		{
			Boolean started = startedServices.get(clientService);
			if (started == null || !started) { 
				// check if clientService is bound anywhere, else destroy.
				boolean isBound = false;
				Set<Entry<ServiceConnection,Service>> entrySet = serviceConnections.entrySet();
				for (Entry<ServiceConnection, Service> entry : entrySet)
				{
					if (entry.getValue() == clientService) {
						isBound = true;
						break;
					}
				}
				if (!isBound) {
					Logger.d("Terminating Client Service: " + clientService);
					clientService.onDestroy();
					serviceInstances.remove(clientService.getClass().getName());
				}
			}
		}

		public boolean isClientServiceStarted(Intent intent)
		{
			boolean result = false;
			Service instance = serviceInstances.get(intent.getComponent().getClassName());
			if (instance != null) {
				Boolean started = startedServices.get(instance);
				if (started != null && started) {
					result = true;
				}
			}

			return result;
		}
	}

}
