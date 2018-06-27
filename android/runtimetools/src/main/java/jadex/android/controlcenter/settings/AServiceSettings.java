package jadex.android.controlcenter.settings;

import jadex.bridge.service.IService;
import jadex.commons.SReflect;
import android.preference.PreferenceScreen;

/**
 * Basic Settings Implementation for Services. Sets the Title and Service.
 */
public abstract class AServiceSettings implements ISettings {

	protected IService service;
	private String title;

	public AServiceSettings(IService service) {
		this.service = service;
		title = SReflect.getUnqualifiedTypeName(service.getServiceIdentifier().getServiceType().getTypeName());
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
