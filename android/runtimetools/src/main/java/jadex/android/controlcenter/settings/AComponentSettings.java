package jadex.android.controlcenter.settings;

import jadex.bridge.IExternalAccess;
import android.preference.PreferenceScreen;

/**
 * Basic Settings Implementation for Components.
 * Sets the Title and Service.
 */
public abstract class AComponentSettings implements ISettings {

	protected IExternalAccess extAcc;
	private String title;

	public AComponentSettings(IExternalAccess component) {
		this.extAcc = component;
		this.title = extAcc.getModel().getName();
	}

	/**
	 * This Method is called when the Settings' Preference Hierarchy will be
	 * added to the parent PreferenceScreen.
	 * Add your whole Preference Hierarchy here.
	 * 
	 * @param screen
	 */
	protected abstract void createPreferenceHierarchy(PreferenceScreen screen);

	public void setPreferenceScreen(PreferenceScreen screen) {
		screen.setTitle(title);
		createPreferenceHierarchy(screen);
	}

	public String getTitle() {
		return title;
	}
}
