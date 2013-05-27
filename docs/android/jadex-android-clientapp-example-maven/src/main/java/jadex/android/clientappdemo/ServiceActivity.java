package jadex.android.clientappdemo;

import jadex.android.clientappdemo.PlatformService.PlatformBinder;
import jadex.android.clientappdemo.PlatformService.PlatformListener;
import jadex.android.standalone.clientapp.ClientAppFragment;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.future.DefaultResultListener;
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
import android.widget.Toast;

/**
 * This is a Sample Client App Fragment.
 * 
 * It connects to a Service which provides access to a jadex platform.
 */
public class ServiceActivity extends ClientAppFragment implements ServiceConnection, PlatformListener
{
	private TextView statusTextView;

	private Button startAgentButton;

	private PlatformBinder service;

	protected boolean platformRunning;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Intent intent = new Intent(getContext(), PlatformService.class);
		bindService(intent, this, BIND_AUTO_CREATE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		setTitle(R.string.app_title);
		// inflate must be called with attachToRoot: false!
		View view = inflater.inflate(R.layout.defaultactivity, container, false);
		statusTextView = (TextView) view.findViewById(R.id.statusTextView);
		startAgentButton = (Button) view.findViewById(R.id.startAgentButton);
		startAgentButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (service != null && platformRunning) {
					service.startAgent().addResultListener(new DefaultResultListener<IComponentIdentifier>()
					{

						@Override
						public void resultAvailable(IComponentIdentifier result)
						{
							System.out.println("Agent Started");
						}
						
					});
				} else {
					runOnUiThread(new Runnable()
					{

						@Override
						public void run()
						{
							Toast.makeText(getContext(), "Platform not running yet", Toast.LENGTH_LONG).show();
						}
					});
				}
			}
		});
		return view;
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if (service != null)
		{
			unbindService(this);
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
				statusTextView.setText(R.string.status_started);
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
				statusTextView.setText(R.string.status_loading);
			}
		});
	}
}
