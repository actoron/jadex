package jadex.android.controlcenter.settings;

import android.preference.PreferenceScreen;
import android.view.Menu;
import android.view.MenuItem;

public interface ISettings {
	void setPreferenceScreen(PreferenceScreen screen);
	
	String getTitle();
	
	boolean onCreateOptionsMenu(Menu menu);
	
	public boolean onOptionsItemSelected(MenuItem item);
}
