package jadex.android.classloading;

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
		return "jadex.android.classloading.MyActivity";
	}
	
	@Override
	protected String getAppPath()
	{
		return getApplicationInfo().sourceDir;
	}

}
