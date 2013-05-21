package jadex.android.standalone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public abstract class JadexApplication extends Activity
{
	public static final String EXTRA_KEY_ACTIVITYCLASS = "extra_activityclass";
	public static final String EXTRA_KEY_APPLICATIONPATH = "extra_applicationpath";
	public static final String EXTRA_KEY_APPLICATIONPACKAGE = "extra_applicationpackage";
	
	public static final String INTENT_ACTION_LOADAPP = "net.sourceforge.jadex.LOAD_APPLICATION";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (getClassName() == null || getAppPath() == null) {
			throw new Error("className or appPath missing");
		}
		
		Intent intent = new Intent() {{
			setAction(INTENT_ACTION_LOADAPP);
			putExtra(EXTRA_KEY_APPLICATIONPATH, getAppPath());
			putExtra(EXTRA_KEY_ACTIVITYCLASS, getClassName());
			putExtra(EXTRA_KEY_APPLICATIONPACKAGE, getAppPackage());
		}};
		
		intent.putExtras(getIntent());
		
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
