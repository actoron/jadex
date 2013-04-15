package jadex.android.classloading;

import jadex.android.standalone.clientapp.JadexClientAppService;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class MyService extends JadexClientAppService
{

	@Override
	public IBinder onBind(Intent intent)
	{
		System.out.println("MyService.onBind()");
		return new Binder() {
		};
	}

}
