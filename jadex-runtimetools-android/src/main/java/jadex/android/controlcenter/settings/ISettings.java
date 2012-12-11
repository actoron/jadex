package jadex.android.controlcenter.settings;

import jadex.bridge.IComponentIdentifier;

import java.io.Serializable;

import android.preference.PreferenceScreen;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Interface used for Settings that are shown in Android Control Center
 * Sub-PreferenceScreens.
 */
public interface ISettings {

	/**
	 * Sets the PreferenceScreen this Settings object can display its
	 * preferences on.
	 * 
	 * @param screen
	 */
	void setPreferenceScreen(PreferenceScreen screen);
	
	/**
	 * Called when the PreferenceScreen is switched.
	 */
	void onDestroy();

	/**
	 * Returns the Title of this Settings Implementation. Must be unique.
	 * 
	 * @return {@link String}
	 */
	String getTitle();

	/**
	 * Called from the Control Center when an Options Menu is requested.
	 * 
	 * @param menu
	 *            The Menu where this Settings Implementation can add its
	 *            entries to.
	 * @return true, if an Option menu should be shown, else false
	 */
	boolean onCreateOptionsMenu(Menu menu);

	/**
	 * Called from the Control Center when a {@link MenuItem} is selected.
	 * 
	 * @param item
	 *            The selected {@link MenuItem}
	 * @return true if menu should be closed
	 */
	boolean onOptionsItemSelected(MenuItem item);

	/**
	 * Sets the platformId of the platform to be configured.
	 * @param platformId
	 */
	void setPlatformId(IComponentIdentifier platformId);
}
