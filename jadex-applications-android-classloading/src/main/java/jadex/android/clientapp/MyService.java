package jadex.android.clientapp;

import jadex.android.standalone.clientapp.JadexClientAppService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class MyService extends JadexClientAppService
{
	
	public class Result
	{
		public String result;
		public Result(String value)
		{
			this.result = value;
		}
	}

	abstract class MyBinder extends Binder {
		public abstract Result getResultObject();			
	}
	
	@Override
	public void onCreate()
	{
		System.out.println("MyService.onCreate()");
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		System.out.println("MyService.onBind()");
		String stringExtra = intent.getStringExtra("myExtra");
		System.out.println("myExtra: " + stringExtra);
		return new MyBinder() {

			@Override
			public Result getResultObject()
			{
				return new Result("blub");
			}
		};
	}

}
