package jadex.android.controlcenter;

import jadex.android.JadexAndroidContext;
import jadex.base.service.settings.AndroidSettingsService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;

public class JadexAndroidControlCenter extends PreferenceActivity {

	private SharedPreferences sharedPreferences;

	public JadexAndroidControlCenter() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sharedPreferences = JadexAndroidContext
				.getInstance()
				.getAndroidContext()
				.getSharedPreferences(
						AndroidSettingsService.DEFAULT_PREFS_NAME,
						Context.MODE_PRIVATE);

		Map<String, ?> prefs = sharedPreferences.getAll();
		
		setPreferenceScreen(createPreferenceHierarchy(prefs));
	}

	private PreferenceScreen createPreferenceHierarchy(Map<String, ?> prefs) {
        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);
        
        JadexPreferenceCategory pGroup;
		List<String> addedProviders = new ArrayList<String>();
		for (Entry<String, ?> entry : prefs.entrySet()) {
			String key = entry.getKey();
			String provider = ((String) key).split("\\.")[0];
			if (!addedProviders.contains(provider)) {
				addedProviders.add(provider);
				pGroup = new JadexPreferenceCategory(this, provider, prefs);
				pGroup.setOnPreferenceChangeListener(opcl);
				root.addPreference(pGroup);
			}
		}
        
        return root;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private OnPreferenceChangeListener opcl = new OnPreferenceChangeListener() {

		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			System.out.println("changed: " + newValue);
			// ((JadexBooleanPreference)preference).setChecked((Boolean)
			// newValue);
			String key = preference.getKey();
			Editor edit = sharedPreferences.edit();
			edit.putString(key, newValue.toString());
			edit.commit();
			return true;
		}
	};

}
