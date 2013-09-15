package jadex.android.testlauncher;

import android.content.Intent;
import android.view.Window;
import jadex.android.standalone.JadexApplication;

public class TestLauncherApplication extends JadexApplication
{
	@Override
	protected String getClassName()
	{
		Intent intent = getIntent();
		String action = intent.getAction();
		return "jadex.android.testlauncher.TestLauncherActivity";
	}

	@Override
	protected int[] getWindowFeatures()
	{
		return new int[]{Window.FEATURE_INDETERMINATE_PROGRESS};
	}
}
