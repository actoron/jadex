package jadex.android.controlcenter.settings;

import jadex.android.controlcenter.JadexBooleanPreference;
import jadex.android.controlcenter.JadexStringPreference;
import jadex.bridge.service.IService;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.commons.future.DefaultResultListener;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.view.Menu;
import android.view.MenuItem;

public class SecuritySettings extends AServiceSettings {

	private ISecurityService secService;

	public SecuritySettings(IService secservice) {
		super(secservice);
		this.secService = (ISecurityService) service;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return false;
	}

	@Override
	protected void createPreferenceHierarchy(PreferenceScreen screen) {
		final JadexBooleanPreference usePw = new JadexBooleanPreference(screen.getContext());
		usePw.setTitle("Use Password");
		secService.isUsePassword().addResultListener(new DefaultResultListener<Boolean>() {
			@Override
			public void resultAvailable(Boolean result) {
				usePw.setValue(result);
			}
		});
		usePw.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				secService.setUsePassword((Boolean) newValue);
				return true;
			}
		});
		screen.addPreference(usePw);

		final JadexStringPreference password = new JadexStringPreference(screen.getContext());
		password.setTitle("Password");
		secService.getLocalPassword().addResultListener(new DefaultResultListener<String>() {
			@Override
			public void resultAvailable(String result) {
				password.setValue(result);
			}
		});
		password.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				secService.setLocalPassword((String) newValue);
				return true;
			}
		});
		screen.addPreference(password);
	}
}
