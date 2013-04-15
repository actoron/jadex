package jadex.android.standalone.metaservice;

import android.app.Service;
import android.content.Context;

public abstract class UserService extends Service
{
	
	public UserService()
	{
	}
	
	public void setBaseContext(Context base) {
		this.attachBaseContext(base);
	}

//	@Override
//	public void onCreate()
//	{
//	}
//
//	@Override
//	public abstract IBinder onBind(Intent intent);
//	
//	@Override
//	public void onStart(Intent intent, int startId)
//	{
//	}
//
//	@Override
//	public int onStartCommand(Intent intent, int flags, int startId)
//	{
//		return 0;
//	}
//
//	@Override
//	public void onDestroy()
//	{
//	}
//
//	@Override
//	public void onConfigurationChanged(Configuration newConfig)
//	{
//	}
//
//	@Override
//	public void onLowMemory()
//	{
//	}
//
//	@Override
//	public boolean onUnbind(Intent intent)
//	{
//		return false;
//	}
//
//	@Override
//	public void onRebind(Intent intent)
//	{
//	}
	

}
