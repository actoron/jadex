package jadex.android.clientapp;

import android.content.Intent;
import jadex.android.standalone.JadexApplication;

public class MyApplication extends JadexApplication
{
	@Override
	protected String getClassName()
	{
		Intent intent = getIntent();
		String action = intent.getAction();
		if (Intent.ACTION_SEND.equals(action)) {
			return "jadex.android.clientapp.MySendActivity";
		} else {
			return "jadex.android.clientapp.MyServiceActivity";
		}
	}
}
