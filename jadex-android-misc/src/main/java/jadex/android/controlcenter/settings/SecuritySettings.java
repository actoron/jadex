package jadex.android.controlcenter.settings;

import jadex.android.controlcenter.preference.JadexBooleanPreference;
import jadex.android.controlcenter.preference.JadexStringPreference;
import jadex.bridge.service.IService;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.commons.future.DefaultResultListener;
import android.os.Handler;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Settings implementation for {@link ISecurityService}.
 */
public class SecuritySettings extends AServiceSettings {

	private ISecurityService secService;
	private Handler uiHandler;
	private JadexBooleanPreference usePw;
	private JadexStringPreference password;
	private JadexBooleanPreference trulan;
	private PreferenceCategory remoteCat;
	private PreferenceCategory networkCat;

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
		uiHandler = new Handler();
		
		PreferenceCategory localCat = new PreferenceCategory(screen.getContext());
		localCat.setTitle("Local Password Settings");
		screen.addPreference(localCat);
		
		usePw = new JadexBooleanPreference(screen.getContext());
		usePw.setTitle("Use Password");
		usePw.setEnabled(false);
		localCat.addPreference(usePw);

		password = new JadexStringPreference(screen.getContext());
		password.setTitle("Password");
		password.setEnabled(false);
		localCat.addPreference(password);
		
		trulan = new JadexBooleanPreference(screen.getContext());
		trulan.setTitle("Trust local networks");
		trulan.setSummary("Access from trusted Platforms is not password protected by default (caution).");
		localCat.addPreference(trulan);
		
		remoteCat = new PreferenceCategory(screen.getContext());
		remoteCat.setTitle("Remote Platform Password Settings");
		remoteCat.setSummary("Press Menu to add a new remote Plattform");
		screen.addPreference(remoteCat);
		
		networkCat = new PreferenceCategory(screen.getContext());
		networkCat.setTitle("Network Password Settings");
		screen.addPreference(networkCat);

		usePw.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				secService.setUsePassword((Boolean) newValue);
				return true;
			}
		});
		
		password.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				secService.setLocalPassword((String) newValue);
				return true;
			}
		});
		
		trulan.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				secService.setTrustedLanMode((Boolean) newValue);
				return true;
			}
		});
		
		refresh();
	}
	
	private void refresh() {
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
	}
}
