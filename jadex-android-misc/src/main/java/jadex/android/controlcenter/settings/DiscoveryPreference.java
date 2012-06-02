package jadex.android.controlcenter.settings;

import jadex.bridge.service.types.awareness.DiscoveryInfo;

import java.text.SimpleDateFormat;

import android.content.Context;
import android.preference.Preference;
import android.text.format.DateFormat;

public class DiscoveryPreference extends Preference {

	private DiscoveryInfo info;

	public DiscoveryPreference(Context context, DiscoveryInfo info) {
		super(context);
		this.info = info;
		setTitle(info.getComponentIdentifier().getName());
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		
		setSummary("Last info: " + dateFormat.format(info.getTime()) + "\nRemote excluded:" + info.remoteexcluded);
	}
	
	

}
