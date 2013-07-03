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

	private ServiceConnection sc1;

	private MyPlatformService.PlatformBinder service;

	protected boolean platformRunning;

	private Button startDemoButton;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		System.out.println("MyActivity onCreate");
		Intent intent = new Intent(getContext(), MyPlatformService.class);

		bindService(intent, this, BIND_AUTO_CREATE);
		startService(intent);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		System.out.println("MyActivity onCreateView");
		setTitle(R.string.app_title);
		int userlayout = R.layout.mainapp;
		View view = inflater.inflate(userlayout, container, false);
		statusTextView = (TextView) view.findViewById(R.id.statusTextView);
		callServicesButton = (Button) view.findViewById(R.id.callServiceButton);
		callServicesButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (service != null && platformRunning) {
					service.startAgent();
				}
			}
		});
		
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
		return view;
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
