package jadex.android.testlauncher;

import android.content.Intent;
import android.view.Window;
import jadex.android.standalone.JadexClientLauncherActivity;

public class TestLauncherApplication extends JadexClientLauncherActivity
{
	@Override
	protected String getInitialFragmentClassName()
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
