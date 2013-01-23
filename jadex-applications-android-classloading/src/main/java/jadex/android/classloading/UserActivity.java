package jadex.android.classloading;

import jadex.android.classloading.ITimeService.Listener;
import jadex.android.classloading.metaservice.ActivityUsingMetaService;
import jadex.android.classloading.metaservice.IUserService;

import java.util.Calendar;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.TextView;

public class UserActivity extends ActivityUsingMetaService
{

	private TextView label;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		try
		{
			Thread.sleep(5000);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		this.setContentView(R.layout.userlayout);
		label = (TextView) findViewById(R.id.label);
		label.setText("activity created");
		Intent intent = new Intent("jadex.android.classloading.TimeService");
		bindService(intent, this, BIND_AUTO_CREATE);
	}

	@Override
	protected void onCustomServiceConnected(ComponentName name, IBinder service)
	{
		if (service instanceof IBinder) {
			System.out.println("is ibinder");
		}
		if (service instanceof ITimeService) {
			System.out.println("is ITimeService");
		}
		if (service instanceof IUserService) {
			System.out.println("is IUserService");
		}
		ITimeService ts = (ITimeService) service;
		label.setText("service connected");
		label.setText(ts.getTime(Calendar.getInstance().getTimeInMillis()));
		
		ts.setListener(new Listener()
		{
			
			@Override
			public void platformStarted()
			{
				label.setText("done");
			}
		});
		ts.startJadexPlatform();
	}

	@Override
	protected void onCustomServiceDisconnected(ComponentName name)
	{
		label.setText("service disconnected");
	}

}
