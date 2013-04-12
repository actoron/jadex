package jadex.android.classloading;

import jadex.android.standalone.clientapp.JadexClientAppFragment;
import jadex.bridge.IExternalAccess;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class MyActivity extends JadexClientAppFragment
{
	public MyActivity()
	{
		setPlatformAutostart(true);
		setPlatformName("classloadingtest");
	}

	@Override
	public void onPrepare(Activity act)
	{
		System.out.println("MyActivity onPrepare");
		super.onPrepare(act);
	}

	@Override
	public void onStart()
	{
		System.out.println("MyActivity onStart");
		super.onStart();

	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		System.out.println("MyActivity onCreate");
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume()
	{
		System.out.println("MyActivity onResume");
		super.onResume();
	}

	@Override
	public void onAttach(Activity activity)
	{
		System.out.println("MyActivity onAttach");
		super.onAttach(activity);
	}
	
	@Override
	protected void onPlatformStarted(IExternalAccess result)
	{
		System.out.println("MyActivity onPlatformStarted");
		Toast.makeText(getActivity(), "Jadex Platform started!", Toast.LENGTH_LONG);
		super.onPlatformStarted(result);
	}
}
