package jadex.android.clientappdemo;

import android.content.Intent;
import jadex.android.standalone.JadexClientLauncherActivity;

/**
 * This class marks the entry point for a Jadex Client Application.
 */
public class MyApplication extends JadexClientLauncherActivity
{
	/**
	 * Based on the Intent this application was called with, different fragment classes are returned.
	 * The Intent will be passed to them upon instantiation.
	 * 
	 * In this case, if the application is called from another through a "send"-action (e.g. from a file manager), 
	 * it will launch an activity to send the file.
	 */
	@Override
	protected String getInitialFragmentClassName()
	{
		Intent intent = getIntent();
		String action = intent.getAction();
		if (Intent.ACTION_SEND.equals(action)) {
			return "jadex.android.clientappdemo.SendActivity";
		} else {
			return "jadex.android.clientappdemo.ServiceActivity";
		}
	}

	@Override
	protected int[] getWindowFeatures()
	{
		return null;
	}

}
