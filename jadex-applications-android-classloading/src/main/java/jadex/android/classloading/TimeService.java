package jadex.android.classloading;

import jadex.android.classloading.metaservice.UserService;
import jadex.android.service.JadexPlatformManager;
import jadex.bridge.IExternalAccess;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;

import java.util.Calendar;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class TimeService extends UserService 
{
	
	class MyBinder extends Binder implements ITimeService
	{
		private Listener listener;

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
			startJadexPlatform.addResultListener(new DefaultResultListener<IExternalAccess>()
			{

				@Override
				public void resultAvailable(IExternalAccess result)
				{
					if (listener != null) {
						listener.platformStarted();
					}
				}
			});
		}

		@Override
		public void setListener(Listener l)
		{
			this.listener = l;
		}

	}


	@Override
	public void onCreate()
	{
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return new MyBinder();
	}

	
}
