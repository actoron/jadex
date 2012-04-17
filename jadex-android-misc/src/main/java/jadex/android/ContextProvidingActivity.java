package jadex.android;

import android.app.Activity;
import android.os.Bundle;

/**
 * This abstract base Activity Class passes the current Android Context to the
 * Jadex Android Services.
 * 
 * @author Julian Kalinowski
 * 
 */
public abstract class ContextProvidingActivity extends Activity {

	private JadexAndroidContext jadexAndroidContext;

	public ContextProvidingActivity() {
		this.jadexAndroidContext = JadexAndroidContext.getInstance();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		jadexAndroidContext.setAndroidContext(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		jadexAndroidContext.setAndroidContext(null);
	}

}
