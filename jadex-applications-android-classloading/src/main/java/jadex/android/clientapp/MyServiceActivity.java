package jadex.android.clientapp;

import jadex.android.clientapp.MyPlatformService.PlatformBinder;
import jadex.android.clientapp.MyPlatformService.PlatformListener;
import jadex.android.standalone.clientapp.ClientAppFragment;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class MyServiceActivity extends ClientAppFragment implements ServiceConnection, PlatformListener
{
	private TextView statusTextView;

	private Button callServicesButton;

	private MyPlatformService.PlatformBinder service;

	protected boolean platformRunning;

	private Button startDemoButton;

	private Intent serviceIntent; 

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		serviceIntent = new Intent(getContext(), MyPlatformService.class);
		startService(serviceIntent);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		setTitle(R.string.app_title);
		int userlayout = R.layout.mainapp;
		View view = inflater.inflate(userlayout, container, false);
		return view;
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		View view = getView();
		statusTextView = (TextView) view.findViewById(R.id.statusTextView);
		
		startDemoButton = (Button) view.findViewById(R.id.startDemoButton);
		startDemoButton.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				Intent i = new Intent(getContext(),SokratesActivity.class);
				startActivity(i);
 			}
		});
		
		startDemoButton.setEnabled(false);
		callServicesButton.setEnabled(false);
		statusTextView.setText("Connecting to Service...");
		bindService(serviceIntent, this, 0);
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		if (service != null) {
			unbindService(this);
		}
	}
	

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if (service != null)
		{
			unbindService(this);
			Intent intent = new Intent(getContext(), MyPlatformService.class);
			stopService(intent);
		}
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service)
	{
		this.service = (PlatformBinder) service;
		this.service.setPlatformListener(this);
		
		statusTextView.setText("Connected.");
		this.service.startPlatform();
	}

	@Override
	public void onServiceDisconnected(ComponentName name)
	{
		this.service = null;
	}

	@Override
	public void platformStarted()
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				startDemoButton.setEnabled(true);
				callServicesButton.setEnabled(true);
				statusTextView.setText("Platform started.");
			}
		});
		platformRunning = true;
	}

	@Override
	public void platformStarting()
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				statusTextView.setText("Platform Starting");
			}
		});
	}
}
