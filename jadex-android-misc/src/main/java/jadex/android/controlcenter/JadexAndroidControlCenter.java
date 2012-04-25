package jadex.android.controlcenter;

import jadex.android.JadexAndroidContext;
import jadex.base.service.settings.AndroidSettingsService;

import java.util.Map;

import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;

public class JadexAndroidControlCenter extends ListActivity {

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

		// LinearLayout main = new LinearLayout(this);
		// main.setOrientation(LinearLayout.VERTICAL);
		Map<String, ?> prefs = sharedPreferences.getAll();
		// ListView main = new ListView(this);
		PreferenceListAdapter adapter = new PreferenceListAdapter(this, prefs);
		adapter.setOnPreferenceChangeListener(opcl);
		// main.setAdapter(adapter);
		// setContentView(main);
		setListAdapter(adapter);
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
