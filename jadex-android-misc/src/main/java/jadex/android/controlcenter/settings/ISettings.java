package jadex.android.controlcenter.settings;

import android.app.Activity;
import android.preference.PreferenceScreen;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public interface ISettings {
	void setPreferenceScreen(PreferenceScreen screen);
	
	String getTitle();
	
	boolean onCreateOptionsMenu(Menu menu);
	
	public boolean onOptionsItemSelected(MenuItem item);
}
