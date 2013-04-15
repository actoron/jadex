package jadex.android.standalone.clientapp;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class JadexClientAppService extends Service
{

	@Override
	public IBinder onBind(Intent intent)
	{
		System.out.println("JadexClientAppService.onBind()");
		return new Binder();
	}

}
