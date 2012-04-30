package jadex.android.controlcenter;

import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.preference.PreferenceScreen;

public class JadexMapPreference extends JadexStringPreference {

	public JadexMapPreference(Context context) {
		super(context);
	}

	public static void fillPreferenceScreen(Context context,
			PreferenceScreen subScreen, String key, Map<?, ?> map,
			OnPreferenceChangeListener listener) {

		for (Entry<?, ?> entry : map.entrySet()) {
			String entryKey = (String) entry.getKey();
			JadexMapPreference p = new JadexMapPreference(context);
			p.setKey(key + "." + entryKey);
			p.setTitle(entryKey);
			p.setValue(entry.getValue());
			p.setOnPreferenceChangeListener(listener);
			subScreen.addPreference(p);
		}
	}

}
