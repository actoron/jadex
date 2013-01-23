package jadex.android.classloading.metaservice;

import android.content.Intent;
import android.os.IBinder;

public interface IUserService
{
	public void onCreate();

	public android.os.IBinder onBind(android.content.Intent intent);

	public void onStart(android.content.Intent intent, int startId);

	public int onStartCommand(android.content.Intent intent, int flags, int startId);
	public void onDestroy();
	public void onConfigurationChanged(android.content.res.Configuration newConfig);
	public void onLowMemory();

	public boolean onUnbind(android.content.Intent intent);
	public void onRebind(android.content.Intent intent);
	
	
}
