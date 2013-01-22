package jadex.android.classloading;

import jadex.android.service.JadexPlatformManager;
import jadex.bridge.IExternalAccess;
import jadex.commons.future.IFuture;

import java.util.Calendar;

import android.os.Binder;

public class TimeServiceBinder extends Binder implements ITimeService
{
	public String getTime(long timeInMillis)
	{
		Calendar instance = Calendar.getInstance();
		instance.setTimeInMillis(timeInMillis);
		return instance.getTime().toLocaleString();
	}

	@Override
	public void startJadexPlatform()
	{
		// IFuture<IExternalAccess> startPlatform = startPlatform();
		JadexPlatformManager instance = JadexPlatformManager.getInstance();
		IFuture<IExternalAccess> startJadexPlatform = instance.startJadexPlatform(JadexPlatformManager.DEFAULT_KERNELS, "classloading", "");
	}

}
