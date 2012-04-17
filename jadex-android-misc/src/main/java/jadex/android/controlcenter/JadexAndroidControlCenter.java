package jadex.android.controlcenter;

import jadex.android.JadexAndroidContext;
import jadex.android.JadexAndroidContext.AndroidContextChangeListener;
import jadex.base.service.settings.AndroidSettingsService;

import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class JadexAndroidControlCenter extends Activity implements AndroidContextChangeListener {
	public JadexAndroidControlCenter() {
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		JadexAndroidContext.getInstance().addContextChangeListener(this);
		SharedPreferences sharedPreferences = JadexAndroidContext.getInstance().getAndroidContext().getSharedPreferences(AndroidSettingsService.DEFAULT_PREFS_NAME, Context.MODE_PRIVATE);
		
		LinearLayout main = new LinearLayout(this);
		main.setOrientation(LinearLayout.VERTICAL);
		
		CheckBox box;
		EditText text;
		TextView label;
		LinearLayout subLayout;
		
		Map<String, ?> prefs = sharedPreferences.getAll();
		
		for (Entry<String, ?> entry : prefs.entrySet()) {
			Object value = entry.getValue();
			if (value instanceof String) {
				if (value.equals("false") || value.equals("true")) {
					box = new CheckBox(this);
					box.setText(entry.getKey());
					box.setChecked(value.equals("true"));
					//box.setOnCheckedChangeListener(occl);
					main.addView(box);
				} else {
//					subLayout = new LinearLayout(this);
//					subLayout.setOrientation(LinearLayout.VERTICAL);
//					
//					label = new TextView(this);
//					label.setText(entry.getKey());
//					text = new EditText(this);
//					text.setText(entry.getValue().toString());
//					
//					subLayout.addView(label);
//					subLayout.addView(text);
//					main.addView(subLayout);
					
					final JadexStringPreference editTextPreference = new JadexStringPreference(this);
					editTextPreference.setDefaultValue(entry.getValue().toString());
					editTextPreference.setText(entry.getValue().toString());
					
					
					editTextPreference.setSummary(entry.getKey());
					editTextPreference.setKey(entry.getKey());
					editTextPreference.setTitle(entry.getKey());

					editTextPreference.setEnabled(true);
					editTextPreference.setSelectable(true);
					editTextPreference.setPersistent(false);
//					editTextPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
//						
//						@Override
//						public boolean onPreferenceClick(Preference preference) {
//							editTextPreference.getDialog().show();
//							return true;
//						}
//					});
					
					//editTextPreference.setDialogLayoutResource(5);
					//editTextPreference.setDialogTitle("test");
					//editTextPreference.setDialogMessage("bla");
					
					View view = editTextPreference.getView(null, main);
					view.setEnabled(true);
					view.setClickable(true);
					view.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							editTextPreference.showDialog();
						}
					});
					main.addView(view);
				}
			}
		}
		
		setContentView(main);
	}
	
//	@Override
//	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
//			Preference preference) {
//		System.out.println("preference clicked: " + preference.getKey());
//		return super.onPreferenceTreeClick(preferenceScreen, preference);
//	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		JadexAndroidContext.getInstance().removeContextChangeListener(this);
	}

	@Override
	public void onContextDestroy(Context ctx) {
	}

	@Override
	public void onContextCreate(Context ctx) {
	}
	
	
//	private PreferenceScreen createJadexCorePrefScreen() {
//		PreferenceScreen screen = PreferenceScreen.
//		
//		return screen;
//	}
	
	private OnCheckedChangeListener occl = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			System.out.println("checked: " + isChecked);
		}
		
	};
}
