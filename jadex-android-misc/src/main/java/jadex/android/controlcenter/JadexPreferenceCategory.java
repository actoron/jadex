package jadex.android.controlcenter;

import jadex.xml.bean.JavaReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.view.View;
import android.view.View.OnClickListener;

public class JadexPreferenceCategory extends PreferenceCategory implements
		OnClickListener {

	private String provider;
	private Map<String, ?> prefs;
	private OnPreferenceChangeListener prefListener;

	private static Map<String, String> CATEGORY_TITLES;

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
//		this.provider = provider;
//		this.prefs = prefs;
//
//		if (CATEGORY_TITLES.containsKey(provider)) {
//			this.setTitle(CATEGORY_TITLES.get(provider));
//		} else {
//			this.setTitle(provider);
//		}
	}

	@Override
	protected void onAttachedToActivity() {
		super.onAttachedToActivity();
		//createPreferenceHierarchy(prefs);
	}

	private void createPreferenceHierarchy(Map<String, ?> prefs2) {
		 JadexStringPreference sp;
		 JadexBooleanPreference bp;

		for (Entry<String, ?> entry : prefs.entrySet()) {
			Object value = entry.getValue();
			String[] split = entry.getKey().split("\\.");
			if (provider.equals(split[0])) {
				String key = split[1];
				if (value instanceof String) {
					if (value.equals("false") || value.equals("true")) {
						bp = new JadexBooleanPreference(getContext());
						bp.setKey(entry.getKey());
						bp.setTitle(key);
						bp.setValue(value);
						bp.setOnPreferenceChangeListener(defaultListener);
						this.addPreference(bp);
					} else if (((String) value).startsWith("<?xml")) {
						Object o = JavaReader.objectFromXML((String) value,
								this.getClass().getClassLoader());
						if (o != null) {
							if (o instanceof Map) {
								Map<String,String> map = (Map<String,String>) o;
								PreferenceScreen subScreen = getPreferenceManager().createPreferenceScreen(getContext());
								subScreen.setTitle(key);
								JadexMapPreference.fillPreferenceScreen(getContext(), subScreen, entry.getKey(), map, defaultListener);
								addPreference(subScreen);
							}
						}
					} else {
						sp = new JadexStringPreference(getContext());
						sp.setKey(entry.getKey());
						sp.setTitle(key);
						sp.setValue(value);
						sp.setOnPreferenceChangeListener(defaultListener);
						this.addPreference(sp);
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
			// return JadexPreferenceCategory.this.prefListener
			// .onPreferenceChange(preference, newValue);
			if (JadexPreferenceCategory.this.prefListener != null) {
				return JadexPreferenceCategory.this.prefListener
						.onPreferenceChange(preference, newValue);
			} else {
				return false;
			}
		}
	};
}
