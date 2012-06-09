package jadex.android.controlcenter.settings;

import jadex.android.controlcenter.preference.JadexBooleanPreference;
import jadex.android.controlcenter.preference.JadexStringPreference;
import jadex.bridge.service.IService;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.commons.future.DefaultResultListener;
import android.os.Handler;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Settings implementation for {@link ISecurityService}.
 */
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
		final Handler uiHandler = new Handler();
		final JadexBooleanPreference usePw = new JadexBooleanPreference(screen.getContext());
		usePw.setTitle("Use Password");
		usePw.setEnabled(false);
		secService.isUsePassword().addResultListener(new DefaultResultListener<Boolean>() {
			@Override
			public void resultAvailable(final Boolean result) {
				uiHandler.post(new Runnable() {
					@Override
					public void run() {
						usePw.setValue(result);
						usePw.setEnabled(true);
					}
				});
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
		password.setEnabled(false);
		secService.getLocalPassword().addResultListener(new DefaultResultListener<String>() {
			@Override
			public void resultAvailable(final String result) {
				uiHandler.post(new Runnable() {
					
					@Override
					public void run() {
						password.setValue(result);
						password.setEnabled(true);
					}
				});
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
