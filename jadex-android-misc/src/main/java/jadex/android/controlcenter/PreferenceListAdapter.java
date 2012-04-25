package jadex.android.controlcenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class PreferenceListAdapter extends ArrayAdapter<Preference> {

	private OnPreferenceChangeListener prefListener;

	private List<Preference> prefList;

	public void setOnPreferenceChangeListener(OnPreferenceChangeListener opcl) {
		this.prefListener = opcl;
	}

	public PreferenceListAdapter(Context context, Map<String, ?> prefs) {
		super(context, android.R.layout.simple_list_item_1);
		prefList = new ArrayList<Preference>();
		JadexStringPreference editTextPreference;
		JadexBooleanPreference booleanPreference;

		for (Entry<String, ?> entry : prefs.entrySet()) {
			Object value = entry.getValue();
			if (value instanceof String) {
				if (value.equals("false") || value.equals("true")) {
					booleanPreference = new JadexBooleanPreference(context);
					booleanPreference.setKey(entry.getKey());
					booleanPreference.setTitle(entry.getKey());
					booleanPreference.setChecked(value.equals("true"));
					booleanPreference
							.setOnPreferenceChangeListener(defaultListener);
					prefList.add(booleanPreference);
					this.add(booleanPreference);
					// main.addView(booleanPreference.getView(null, main));
				} else {
					editTextPreference = new JadexStringPreference(context);

					editTextPreference.setKey(entry.getKey());
					editTextPreference.setTitle(entry.getKey());
					editTextPreference.setValue((String) entry.getValue());
					editTextPreference.setSummary("Aktueller Wert: "
							+ (String) entry.getValue());
					editTextPreference
							.setOnPreferenceChangeListener(defaultListener);
					prefList.add(editTextPreference);
					this.add(editTextPreference);
					// View view = editTextPreference.getView(null, main);
					// main.addView(view);
				}
			}
		}

	}

	private OnPreferenceChangeListener defaultListener = new OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			return PreferenceListAdapter.this.prefListener.onPreferenceChange(
					preference, newValue);
		}
	};

	public View getView(int position, View convertView, ViewGroup parent) {
		return prefList.get(position).getView(convertView, parent);
	};

}
