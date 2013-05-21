package jadex.android.clientapp;

import jadex.android.standalone.JadexApplication;

public class MyApplication extends JadexApplication
{
	@Override
	protected String getClassName()
	{
		return "jadex.android.clientapp.MySimpleActivity";
	}
}
