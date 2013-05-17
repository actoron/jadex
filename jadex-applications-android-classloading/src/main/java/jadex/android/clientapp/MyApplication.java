package jadex.android.clientapp;

import jadex.android.standalone.JadexApplication;
import android.os.Bundle;

public class MyApplication extends JadexApplication
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	protected String getClassName()
	{
//		return MyActivity.class.getCanonicalName();
		return "jadex.android.clientapp.MyActivity";
	}
}
