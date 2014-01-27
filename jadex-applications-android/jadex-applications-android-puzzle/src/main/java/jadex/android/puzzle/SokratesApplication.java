package jadex.android.puzzle;

import android.content.Intent;
import android.view.Window;
import jadex.android.standalone.JadexClientLauncherActivity;

public class SokratesApplication extends JadexClientLauncherActivity
{
	@Override
	protected String getInitialFragmentClassName()
	{
		Intent intent = getIntent();
		String action = intent.getAction();
		return "jadex.android.puzzle.SokratesLoaderActivity";
	}

	@Override
	protected int[] getWindowFeatures()
	{
		return new int[]{Window.FEATURE_INDETERMINATE_PROGRESS};
	}
}
