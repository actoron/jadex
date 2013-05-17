package jadex.android.standalone.clientapp;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

public abstract class JadexClientAppService extends Service
{


	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();
	}

	@Override
	public void startActivity(Intent intent)
	{
		super.startActivity(intent);
	}

	@Override
	public ComponentName startService(Intent service)
	{
		return super.startService(service);
	}

	@Override
	public boolean stopService(Intent name)
	{
		return super.stopService(name);
	}

	@Override
	public boolean onUnbind(Intent intent)
	{
		return true;
	}

	@Override
	public void unbindService(ServiceConnection conn)
	{
	}
	
	@Override
	public Context getApplicationContext()
	{
		System.out.println("JadexClientAppService: getApplicationContext()");
		return super.getApplicationContext();
	}

}
