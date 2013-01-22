package jadex.android.classloading.metaservice;

import android.os.IBinder;

public interface IUserService
{
	public void onCreate();

	public IBinder onBind();
}
