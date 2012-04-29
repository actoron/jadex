package jadex.android.controlcenter;

import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.view.View;
import android.view.View.OnClickListener;

public class JadexPreferenceCategory extends PreferenceCategory implements
		OnClickListener {

	private String provider;
	private Map<String, ?> prefs;
	private OnPreferenceChangeListener prefListener;

	public JadexPreferenceCategory(Context context, String provider,
			Map<String, ?> prefs) {
		super(context, null);
		this.provider = provider;
		this.prefs = prefs;
		this.setTitle(provider);
	}

	@Override
	protected void onAttachedToActivity() {
		super.onAttachedToActivity();
		createPreferenceHierarchy(prefs);
	}

	private void createPreferenceHierarchy(Map<String, ?> prefs2) {
		JadexStringPreference editTextPreference;
		JadexBooleanPreference booleanPreference;

		for (Entry<String, ?> entry : prefs.entrySet()) {
			Object value = entry.getValue();
			if (provider.equals(entry.getKey().split("\\.")[0])) {
				if (value instanceof String) {
					if (value.equals("false") || value.equals("true")) {
						booleanPreference = new JadexBooleanPreference(
								getContext());
						booleanPreference.setKey(entry.getKey());
						booleanPreference.setTitle(entry.getKey());
						booleanPreference.setChecked(value.equals("true"));
						 booleanPreference
						 .setOnPreferenceChangeListener(defaultListener);
						this.addPreference(booleanPreference);
					} else {
						editTextPreference = new JadexStringPreference(
								getContext());

						editTextPreference.setKey(entry.getKey());
						editTextPreference.setTitle(entry.getKey());
						editTextPreference.setValue((String) entry.getValue());
						editTextPreference.setSummary("Aktueller Wert: "
								+ (String) entry.getValue());
						editTextPreference
								.setOnPreferenceChangeListener(defaultListener);
						this.addPreference(editTextPreference);
					}
				}
			}
		}
	}
	
	@Override
	public void setOnPreferenceChangeListener(
			OnPreferenceChangeListener onPreferenceChangeListener) {
		super.setOnPreferenceChangeListener(onPreferenceChangeListener);
		this.prefListener = onPreferenceChangeListener;
	}

	@Override
	public void onClick(View v) {
	}

	private OnPreferenceChangeListener defaultListener = new OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
//			return JadexPreferenceCategory.this.prefListener
//					.onPreferenceChange(preference, newValue);
			if (JadexPreferenceCategory.this.prefListener != null) {
				return JadexPreferenceCategory.this.prefListener.onPreferenceChange(preference, newValue);
			} else {
				return false;
			}
		}
	};
}
