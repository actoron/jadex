package jadex.android.standalone;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

public abstract class JadexApplication extends Activity
{
	public static final String EXTRA_KEY_ACTIVITYCLASS = "net.sourceforge.jadex.EXTRA_KEY_ACTIVITYCLASS";
	public static final String EXTRA_KEY_ORIGINALACTION = "net.sourceforge.jadex.EXTRA_KEY_ORIGINALACTION";
	public static final String EXTRA_KEY_APPLICATIONINFO = "net.sourceforge.jadex.EXTRA_KEY_APPLICATIONINFO";
	
	public static final String INTENT_ACTION_LOADAPP = "net.sourceforge.jadex.LOAD_APPLICATION";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		final String className = getClassName();
		if (className == null || getAppPath() == null) {
			throw new Error("className or appPath missing");
		}
		
		Intent intent = new Intent() {{
			setAction(INTENT_ACTION_LOADAPP);
			putExtra(EXTRA_KEY_ACTIVITYCLASS, className);
			putExtra(EXTRA_KEY_APPLICATIONINFO, getApplicationInfo());
		}};
		
		intent.putExtras(getIntent());
		intent.putExtra(EXTRA_KEY_ORIGINALACTION, getIntent().getAction());
		
//		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		startActivity(intent);
		finish();
	}
	
	/**
	 * Please provide the name of the class which represents your main
	 * activity here.
	 * The given Class should inherit from ClientAppFragment.
	 * @return the class name
	 */
	protected abstract String getClassName();
	
	protected String getAppPackage() {
		return getApplicationInfo().packageName;
	};
	
	protected String getAppPath() {
		return getApplicationInfo().sourceDir;
	}
	
}
