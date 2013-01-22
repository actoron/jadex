package jadex.android.classloading;

import jadex.android.service.JadexPlatformService;
import android.content.Intent;
import android.os.IBinder;

public class TimeService extends JadexPlatformService 
{

	@Override
	public void onCreate()
	{
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return new TimeServiceBinder();
	}
	
}
