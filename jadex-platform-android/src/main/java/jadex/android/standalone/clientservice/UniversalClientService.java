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
		intent.removeExtra(UniversalClientBinder.CLIENT_SERVICE_COMPONENT);
		
		ClassLoader cl = JadexPlatformManager.getInstance().getClassLoader(null);
		Class<Service> clientServiceClass = SReflect.classForName0(clientServiceComponent.getClassName(), cl);
		try
		{
			clientService = clientServiceClass.newInstance();
			// re-set component info:
			intent.setComponent(clientServiceComponent);
			if (isCreate){
				clientService.onCreate();
			}
			clientBinder = clientService.onBind(intent);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		isCreate = false;
		return new ClientBinder(clientBinder);
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
	
	
	static class ClientBinder extends Binder implements UniversalClientBinder {

		private IBinder clientBinder;

		public ClientBinder(IBinder clientBinder)
		{
			this.clientBinder = clientBinder;
		}

		public IBinder getClientBinder()
		{
			return clientBinder;
		}

	}

}
