package jadex.android.controlcenter.settings;

import jadex.android.controlcenter.IChildPreferenceScreen;
import jadex.bridge.IComponentIdentifier;

/**
 * Interface used for Settings that are shown in Android Control Center
 * Sub-PreferenceScreens.
 */
public interface ISettings extends IChildPreferenceScreen {

	/**
	 * Sets the platformId of the platform to be configured.
	 * @param platformId
	 */
	void setPlatformId(IComponentIdentifier platformId);
}
