package jadex.android.controlcenter.settings;

import jadex.bridge.service.IService;
import android.preference.PreferenceScreen;

public abstract class AServiceSettings {

	protected IService service;

	public AServiceSettings(IService service) {
		this.service = service;
	}

	protected abstract void createPreferenceHierarchy(PreferenceScreen screen);

	public void setPreferenceRoot(PreferenceScreen screen) {
		screen.setTitle(service.getServiceIdentifier().getServiceName());
		createPreferenceHierarchy(screen);
	}

}
