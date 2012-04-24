package jadex.android.controlcenter;

import jadex.android.JadexAndroidContext;
import jadex.android.JadexAndroidContext.AndroidContextChangeListener;
import jadex.base.service.settings.AndroidSettingsService;

import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class JadexAndroidControlCenter extends Activity {
	
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
		
		LinearLayout main = new LinearLayout(this);
		main.setOrientation(LinearLayout.VERTICAL);
		
		JadexStringPreference editTextPreference;
		JadexBooleanPreference booleanPreference;

		Map<String, ?> prefs = sharedPreferences.getAll();

		for (Entry<String, ?> entry : prefs.entrySet()) {
			Object value = entry.getValue();
			if (value instanceof String) {
				if (value.equals("false") || value.equals("true")) {
					booleanPreference = new JadexBooleanPreference(this);
					booleanPreference.setKey(entry.getKey());
					booleanPreference.setTitle(entry.getKey());
					booleanPreference.setChecked(value.equals("true"));
					booleanPreference.setOnPreferenceChangeListener(opcl);
					main.addView(booleanPreference.getView(null, main));
				} else {
					editTextPreference = new JadexStringPreference(
							this);

					editTextPreference.setKey(entry.getKey());
					editTextPreference.setTitle(entry.getKey());
					editTextPreference.setValue((String) entry.getValue());
					editTextPreference.setSummary("Aktueller Wert: "
							+ (String) entry.getValue());
					editTextPreference.setOnPreferenceChangeListener(opcl);

					View view = editTextPreference.getView(null, main);
					main.addView(view);
				}
			}
		}

		setContentView(main);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}


	private OnPreferenceChangeListener opcl = new OnPreferenceChangeListener() {

		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			System.out.println("changed: " + newValue);
			//((JadexBooleanPreference)preference).setChecked((Boolean) newValue);
			String key = preference.getKey();
			Editor edit = sharedPreferences.edit();
			edit.putString(key, newValue.toString());
			edit.commit();
			return true;
		}
	};
	
}
