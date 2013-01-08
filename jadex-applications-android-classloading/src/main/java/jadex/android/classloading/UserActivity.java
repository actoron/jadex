package jadex.android.classloading;

import jadex.android.JadexAndroidActivity;
import jadex.bridge.IExternalAccess;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class UserActivity extends JadexAndroidActivity 
{
	private TextView label;
	
	public UserActivity()
	{
		setPlatformAutostart(true);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.userlayout);
		label = (TextView) findViewById(R.id.label);
	}

	@Override
	protected void onPlatformStarting()
	{
		super.onPlatformStarting();
		label.setText("starting");
	}

	@Override
	protected void onPlatformStarted(IExternalAccess result)
	{
		super.onPlatformStarted(result);
		label.setText("started");
	}
	
}
