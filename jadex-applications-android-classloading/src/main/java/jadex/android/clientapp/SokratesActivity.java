package jadex.android.clientapp;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import jadex.android.clientapp.MyPlatformService.PlatformBinder;
import jadex.android.standalone.clientapp.ClientAppFragment;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;

public class SokratesActivity extends ClientAppFragment implements ServiceConnection
{
	private PlatformBinder service;
	private TextView statusTextView;
	private SokratesView sokratesView;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Intent i = new Intent(getContext(), MyPlatformService.class);
		bindService(i, this, 0);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		setTitle(R.string.demo_title);
		View view = inflater.inflate(R.layout.sokrates, container, false);
		
		sokratesView = (SokratesView) view.findViewById(R.id.sokrates_gameView);
		statusTextView = (TextView) view.findViewById(R.id.sokrates_statusTextView);
		statusTextView.setText("starting Platform...");
		return view;
	}
	

	@Override
	public void onServiceConnected(ComponentName name, IBinder binder)
	{
		this.service = (MyPlatformService.PlatformBinder) binder;
		statusTextView.setText("starting Game...");
		IFuture<IComponentIdentifier> startSokrates = this.service.startSokrates();
		startSokrates.addResultListener(new DefaultResultListener<IComponentIdentifier>()
		{

			@Override
			public void resultAvailable(IComponentIdentifier result)
			{
				runOnUiThread(new Runnable()
				{
					
					@Override
					public void run()
					{
						statusTextView.setText("Game started!");
					}
				});
			}
		});
	}

	@Override
	public void onServiceDisconnected(ComponentName name)
	{
		this.service = null;
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if (service != null) {
			unbindService(this);
		}
	}
	
	
}
