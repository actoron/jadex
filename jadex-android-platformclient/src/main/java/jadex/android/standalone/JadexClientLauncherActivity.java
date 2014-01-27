package jadex.android.standalone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * This class marks the entry point for a Jadex Client Application.
 * Subclasses can define the main Fragment (which will be loaded on application startup)
 * by implementing getClassName(). 
 */
public abstract class JadexClientLauncherActivity extends Activity
{
	public static final String EXTRA_KEY_ACTIVITYCLASS = "net.sourceforge.jadex.EXTRA_KEY_ACTIVITYCLASS";
	public static final String EXTRA_KEY_ORIGINALACTION = "net.sourceforge.jadex.EXTRA_KEY_ORIGINALACTION";
	public static final String EXTRA_KEY_APPLICATIONINFO = "net.sourceforge.jadex.EXTRA_KEY_APPLICATIONINFO";
	public static final String EXTRA_KEY_WINDOWFEATURES = "net.sourceforge.jadex.EXTRA_KEY_WINDOWFEATURES";
	
	public static final String INTENT_ACTION_LOADAPP = "net.sourceforge.jadex.LOAD_APPLICATION";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		final String className = getInitialFragmentClassName();
		if (className == null) {
			throw new Error("ClassName missing. Please implement getInitialFragmentClassName() correctly.");
		}
		
		Intent intent = new Intent() {{
			setAction(INTENT_ACTION_LOADAPP);
			putExtra(EXTRA_KEY_ACTIVITYCLASS, className);
			putExtra(EXTRA_KEY_APPLICATIONINFO, getApplicationInfo());
		}};
		
		intent.putExtras(getIntent());
		intent.putExtra(EXTRA_KEY_ORIGINALACTION, getIntent().getAction());
		intent.putExtra(EXTRA_KEY_WINDOWFEATURES, getWindowFeatures());
		
//		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		startActivity(intent);
		finish();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
	}
	
	/**
	 * Please provide the name of the class which represents your main
	 * activity here.
	 * The given Class should inherit from ClientAppFragment.
	 * @return the class name
	 */
	protected abstract String getInitialFragmentClassName();
	
	/**
	 * Due to the nature of android, window features can only be requested
	 * before ClientAppFragments are instantiated.
	 * So, if you need window features such as Window.FEATURE_INDETERMINATE_PROGRESS,
	 * please return them here in an array.
	 */
	protected abstract int[] getWindowFeatures();
}
