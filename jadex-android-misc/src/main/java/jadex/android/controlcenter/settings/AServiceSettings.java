package jadex.android.controlcenter.settings;

import jadex.bridge.service.IService;
import android.preference.PreferenceScreen;

public abstract class AServiceSettings implements ISettings {

	protected IService service;
	private String title;

	public AServiceSettings(IService service) {
		this.service = service;
		title = service.getServiceIdentifier().getServiceType().getType().getSimpleName();
	}

	protected abstract void createPreferenceHierarchy(PreferenceScreen screen);

	@Override
	public void setPreferenceScreen(PreferenceScreen screen) {
		screen.setTitle(title);
		createPreferenceHierarchy(screen);
	}
	
	@Override
	public String getTitle() {
		return title;
	}

}
