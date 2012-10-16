package jadex.android.controlcenter.settings;

import jadex.android.service.IJadexPlatformBinder;
import jadex.android.service.JadexPlatformService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceActivity;


public class ServiceConnectingPreferenceActivity extends PreferenceActivity implements ServiceConnection
{

	protected IJadexPlatformBinder platformService;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Intent intent = new Intent(this, JadexPlatformService.class);
		bindService(intent, this, BIND_AUTO_CREATE);
	}
	
//	@Override
//	protected void onResume()
//	{
//		super.onResume();
//	}
//	
//	@Override
//	protected void onPause()
//	{
//		super.onPause();
//	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		unbindService(this);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service)
	{
		this.platformService = (IJadexPlatformBinder) service;
	}

	@Override
	public void onServiceDisconnected(ComponentName name)
	{
		this.platformService = null;
	}
	
	
}
