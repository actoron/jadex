package jadex.android.controlcenter;

import jadex.bridge.service.types.awareness.DiscoveryInfo;
import android.content.Context;
import android.preference.Preference;

public class DiscoveryPreference extends Preference {

	private DiscoveryInfo info;

	public DiscoveryPreference(Context context, DiscoveryInfo info) {
		super(context);
		this.info = info;
		setTitle(info.getComponentIdentifier().getName());
		setSummary("Last info: " + info.getTime());
	}

}
