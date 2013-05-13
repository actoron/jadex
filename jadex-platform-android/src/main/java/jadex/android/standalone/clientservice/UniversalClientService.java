package jadex.android.standalone.clientservice;

import jadex.android.commons.JadexPlatformOptions;
import jadex.android.service.JadexPlatformManager;
import jadex.commons.SReflect;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class UniversalClientService extends Service 
{
	private Service clientService;
	
	private boolean isCreate;

	private IBinder clientBinder;

	@Override
	public void onCreate()
	{
		super.onCreate();
		isCreate = true;
	}
	
	@Override
	public void onStart(Intent intent, int startId)
	{
		super.onStart(intent, startId);
		
	}
	
	@Override
	public IBinder onBind(Intent intent)
	{
		ComponentName clientServiceComponent = intent.getParcelableExtra(UniversalClientBinder.CLIENT_SERVICE_COMPONENT);
		clientService = createClientService(clientServiceComponent.getClassName());
		
		if (isCreate){
			clientService.onCreate();
			isCreate = false;
		}
		clientBinder = clientService.onBind(createClientIntent(intent));
		UniversalClientBinder binderProxy = new UniversalClientBinder(clientBinder);
		return binderProxy;
	}
	
	@Override
	public boolean onUnbind(Intent intent)
	{
		super.onUnbind(intent);
		clientBinder = null;
		return clientService.onUnbind(intent);
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		clientService.onDestroy();
		clientBinder = null;
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
		ComponentName clientServiceComponent = intent.getParcelableExtra(UniversalClientBinder.CLIENT_SERVICE_COMPONENT);
		result.setComponent(clientServiceComponent);
		result.putExtras(intent.getExtras());
		result.removeExtra(UniversalClientBinder.CLIENT_SERVICE_COMPONENT);
		return result;
	}
	
}
