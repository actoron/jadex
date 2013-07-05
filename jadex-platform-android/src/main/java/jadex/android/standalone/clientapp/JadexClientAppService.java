package jadex.android.standalone.clientapp;

import android.app.Service;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Looper;

public abstract class JadexClientAppService extends Service 
{


	private Context applicationContext;
//	private Context baseContext;

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
		return false;
	}

	@Override
	public void unbindService(ServiceConnection conn)
	{
	}
	
	@Override
	public Object getSystemService(String name)
	{
		return super.getSystemService(name);
	}

	@Override
	public void attachBaseContext(Context baseContext)
	{
		super.attachBaseContext(baseContext);
	}
	

}
