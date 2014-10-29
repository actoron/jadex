package jadex.android.applications.demos;

import jadex.android.standalone.JadexClientLauncherActivity;

public class DemoApplication extends JadexClientLauncherActivity {

	@Override
	protected String getInitialFragmentClassName() {
		return "jadex.android.applications.demos.DemoChooserActivity";
	}

}
