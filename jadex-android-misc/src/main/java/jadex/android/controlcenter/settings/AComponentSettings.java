package jadex.android.controlcenter.settings;

import android.preference.PreferenceScreen;
import jadex.bridge.IExternalAccess;

public abstract class AComponentSettings {

	protected IExternalAccess extAcc;

	public AComponentSettings(IExternalAccess component) {
		this.extAcc = component;
	}
	
	protected abstract void createPreferenceHierarchy(PreferenceScreen screen);

	public void setPreferenceRoot(PreferenceScreen screen) {
		screen.setTitle(extAcc.getComponentIdentifier().getLocalName());
		createPreferenceHierarchy(screen);
	}

}
