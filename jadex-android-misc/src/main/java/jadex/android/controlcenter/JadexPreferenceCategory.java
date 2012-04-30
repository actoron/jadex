package jadex.android.controlcenter;

import java.util.HashMap;
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
	
	private static Map<String,String> CATEGORY_TITLES;
	
	static {
		CATEGORY_TITLES = new HashMap<String, String>();
		CATEGORY_TITLES.put("securityservice", "Security Service");
		CATEGORY_TITLES.put("clockservice", "Clock Service");
		CATEGORY_TITLES.put("simulationservice", "Simulation Service");
		CATEGORY_TITLES.put("awa", "Awareness");
	}

	public JadexPreferenceCategory(Context context, String provider,
			Map<String, ?> prefs) {
		super(context, null);
		this.provider = provider;
		this.prefs = prefs;
		
		if (CATEGORY_TITLES.containsKey(provider)) {
			this.setTitle(CATEGORY_TITLES.get(provider));
		} else {
			this.setTitle(provider);
		}
	}

	@Override
	protected void onAttachedToActivity() {
		super.onAttachedToActivity();
		createPreferenceHierarchy(prefs);
	}

	private void createPreferenceHierarchy(Map<String, ?> prefs2) {
		JadexStringPreference stringPreference;
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
						stringPreference = new JadexStringPreference(
								getContext());

						stringPreference.setKey(entry.getKey());
						stringPreference.setTitle(entry.getKey());
						stringPreference.setText((String) entry.getValue());
				
						stringPreference
								.setOnPreferenceChangeListener(defaultListener);
						this.addPreference(stringPreference);
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
