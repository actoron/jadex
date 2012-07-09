package jadex.android.controlcenter.settings;

import jadex.android.JadexAndroidContext;
import jadex.android.controlcenter.preference.JadexBooleanPreference;
import jadex.android.controlcenter.preference.JadexStringPreference;
import jadex.android.controlcenter.preference.LongClickablePreference;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.IService;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.awareness.DiscoveryInfo;
import jadex.bridge.service.types.awareness.IAwarenessManagementService;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.commons.future.DefaultResultListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * Settings implementation for {@link ISecurityService}.
 */
public class SecuritySettings extends AServiceSettings
{
	/** The security service **/
	private ISecurityService secService;

	/** Handler to change UI objects from non-ui threads. */
	private Handler uiHandler;

	/** Remote Platform passwords */
	private Map<String, String> platformPasswords;
	
	/** Id of the platform to be configured. */
	private IComponentIdentifier platformId;

	private Map<String, String> networkPasswords;
	
	// UI members
	private JadexBooleanPreference usePw;
	private JadexStringPreference password;
	private JadexBooleanPreference trulan;
	private PreferenceCategory platformPasswordsCat;
	private PreferenceCategory networkPasswordsCat;


	public SecuritySettings(IService secservice)
	{
		super(secservice);
		this.secService = (ISecurityService) service;
		this.platformPasswords = new HashMap<String, String>();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add("Add remote platform password");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		final AlertDialog.Builder builder = new AlertDialog.Builder(platformPasswordsCat.getContext());

		SServiceProvider.getService(JadexAndroidContext.getInstance().getExternalPlatformAccess(platformId).getServiceProvider(),
				IAwarenessManagementService.class).addResultListener(new DefaultResultListener<IAwarenessManagementService>()
		{

			@Override
			public void resultAvailable(IAwarenessManagementService result)
			{
				result.getKnownPlatforms().addResultListener(new DefaultResultListener<Collection<DiscoveryInfo>>()
				{
					@Override
					public void resultAvailable(Collection<DiscoveryInfo> platforms)
					{
						final ArrayList<DiscoveryInfo> platformList = new ArrayList<DiscoveryInfo>(platforms.size());
						ArrayList<String> items = new ArrayList<String>();

						for (DiscoveryInfo dis : platforms) {
							String platformPrefix = dis.getComponentIdentifier().getPlatformPrefix();
							if (!platformPasswords.containsKey(platformPrefix) && !items.contains(platformPrefix)) {
								platformList.add(dis);
								items.add(platformPrefix);
							}
						}

						if (items.isEmpty()) {
							uiHandler.post(new Runnable()
							{

								@Override
								public void run()
								{
									Toast.makeText(platformPasswordsCat.getContext(), "No further platforms found!", Toast.LENGTH_LONG)
											.show();
								}
							});
						} else {
							builder.setTitle("Choose platform to add password for");
							builder.setItems(items.toArray(new String[0]), new DialogInterface.OnClickListener()
							{
								public void onClick(DialogInterface dialog, final int item)
								{
									final IComponentIdentifier cid = platformList.get(item).getComponentIdentifier();
									String platformPrefix = cid.getPlatformPrefix();
									final PasswordDialog pwDialog = new PasswordDialog(platformPasswordsCat.getContext());
									pwDialog.setTitle("Enter password for platform " + platformPrefix);
									pwDialog.show();
									pwDialog.setOnApplyListener(new OnClickListener()
									{
										@Override
										public void onClick(View v)
										{
											String password = pwDialog.getPassword();
											secService.setPlatformPassword(cid, password);
											refresh();
										}
									});

								}
							});
							uiHandler.post(new Runnable()
							{

								@Override
								public void run()
								{
									AlertDialog alert = builder.create();
									alert.show();
								}
							});
						}

					}

					@Override
					public void exceptionOccurred(Exception exception)
					{
						exception.printStackTrace();
					}
				});
			}
		});

		return true;
	}

	@Override
	protected void createPreferenceHierarchy(final PreferenceScreen screen)
	{
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
		password.setDialogTitle("Set new password");
		localCat.addPreference(password);

		trulan = new JadexBooleanPreference(screen.getContext());
		trulan.setTitle("Trust local networks");
		localCat.addPreference(trulan);

		platformPasswordsCat = new PreferenceCategory(screen.getContext());
		platformPasswordsCat.setTitle("Remote Platform Password Settings");
		screen.addPreference(platformPasswordsCat);

		networkPasswordsCat = new PreferenceCategory(screen.getContext());
		networkPasswordsCat.setTitle("Network Password Settings");
		screen.addPreference(networkPasswordsCat);

		usePw.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
		{
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue)
			{
				secService.setUsePassword((Boolean) newValue);
				return true;
			}
		});

		password.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
		{
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue)
			{
				secService.setLocalPassword((String) newValue);
				return true;
			}
		});

		trulan.setOnPreferenceClickListener(new OnPreferenceClickListener()
		{

			@Override
			public boolean onPreferenceClick(Preference preference)
			{

				return true;
			}
		});

		trulan.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
		{

			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue)
			{
				final Boolean newState = (Boolean) newValue;
				if (newState) {
					Builder builder = new AlertDialog.Builder(screen.getContext());
					builder.setMessage(
							"Access from trusted Platforms is not password protected by default!\n You can add a password below.")
							.setPositiveButton("Ok", new DialogInterface.OnClickListener()
							{

								@Override
								public void onClick(DialogInterface dialog, int which)
								{
									secService.setTrustedLanMode(newState);
									refresh();
								}

							}).setNegativeButton("Cancel", new DialogInterface.OnClickListener()
							{
								
								@Override
								public void onClick(DialogInterface dialog, int which)
								{
									trulan.setChecked(false);
								}
							}).create().show();
				} else {
					secService.setTrustedLanMode(newState);
					refresh();
				}
				return true;
			}
		});

		refresh();
	}

	private void refresh()
	{
		secService.isUsePassword().addResultListener(new DefaultResultListener<Boolean>()
		{
			@Override
			public void resultAvailable(final Boolean result)
			{
				uiHandler.post(new Runnable()
				{
					@Override
					public void run()
					{
						usePw.setValue(result);
						usePw.setEnabled(true);
					}
				});
			}
		});

		secService.getLocalPassword().addResultListener(new DefaultResultListener<String>()
		{
			@Override
			public void resultAvailable(final String result)
			{
				uiHandler.post(new Runnable()
				{

					@Override
					public void run()
					{
						password.setValue(result);
						password.setEnabled(true);
					}
				});
			}
		});

		secService.getPlatformPasswords().addResultListener(new DefaultResultListener<Map<String, String>>()
		{

			@Override
			public void resultAvailable(Map<String, String> result)
			{
				setPlatformPasswords(result);
			}
		});

		secService.getNetworkPasswords().addResultListener(new DefaultResultListener<Map<String, String>>()
		{

			@Override
			public void resultAvailable(Map<String, String> result)
			{
				setNetworkPasswords(result);
			}
		});

		secService.isTrustedLanMode().addResultListener(new DefaultResultListener<Boolean>()
		{

			@Override
			public void resultAvailable(Boolean result)
			{
				trulan.setChecked(result);
			}
		});
	}

	protected void setNetworkPasswords(Map<String, String> result)
	{
		this.networkPasswords = result;
		networkPasswordsCat.removeAll();
		if (result.isEmpty()) {
			Preference dummyPref = new Preference(platformPasswordsCat.getContext());
			dummyPref.setTitle("Not applicable");
			dummyPref.setSummary("You can set network passwords here if trusted lan mode is enabled.");
			networkPasswordsCat.addPreference(dummyPref);
		} else {

			for (Entry<String, String> entry : result.entrySet()) {
				Preference pwPref = new Preference(networkPasswordsCat.getContext());
				pwPref.setTitle(entry.getKey());
				pwPref.setSummary("Password: " + entry.getValue());
				pwPref.setOnPreferenceClickListener(onNetworkPasswordClickListener);
				networkPasswordsCat.addPreference(pwPref);
			}
		}
	}

	protected void setPlatformPasswords(Map<String, String> result)
	{
		this.platformPasswords = result;
		platformPasswordsCat.removeAll();
		if (result.isEmpty()) {
			Preference dummyPref = new Preference(platformPasswordsCat.getContext());
			dummyPref.setTitle("No passwords set");
			dummyPref.setSummary("Press menu to add a new remote platform password.");
			platformPasswordsCat.addPreference(dummyPref);
		} else {
			for (Entry<String, String> entry : result.entrySet()) {
				LongClickablePreference pwPref = new LongClickablePreference(platformPasswordsCat.getContext());
				pwPref.setTitle(entry.getKey());
				pwPref.setSummary("Password: " + entry.getValue());
				pwPref.setOnPreferenceClickListener(onPlatformPasswordClickListener);
				pwPref.setOnPreferenceLongClickListener(onPlatformPasswordLongClickListener);
				platformPasswordsCat.addPreference(pwPref);
			}
		}
	}

	/**
	 * Creates a dialog to ask the user for a new password for a platform.
	 */
	protected OnPreferenceClickListener onPlatformPasswordClickListener = new OnPreferenceClickListener()
	{
		@Override
		public boolean onPreferenceClick(Preference preference)
		{
			final CharSequence platformPrefix = preference.getTitle();
			final PasswordDialog pwDialog = new PasswordDialog(preference.getContext());
			pwDialog.setTitle("Enter new password for platform: " + platformPrefix);

			String oldPw = platformPasswords.get(platformPrefix);
			if (oldPw != null) {
				pwDialog.setPassword(oldPw);
			}
			pwDialog.setOnApplyListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					ComponentIdentifier cid = new ComponentIdentifier(platformPrefix.toString());
					secService.setPlatformPassword(cid, pwDialog.getPassword());
					refresh();
				}
			});
			pwDialog.show();
			return true;
		}
	};

	protected OnPreferenceClickListener onPlatformPasswordLongClickListener = new OnPreferenceClickListener()
	{

		@Override
		public boolean onPreferenceClick(Preference p)
		{
			Builder builder = new AlertDialog.Builder(p.getContext());
			final CharSequence platformPrefix = p.getTitle();
			builder.setMessage("Delete password for " + platformPrefix + "?").setCancelable(false)
					.setPositiveButton("Yes", new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int id)
						{
							ComponentIdentifier cid = new ComponentIdentifier(platformPrefix.toString());
							secService.setPlatformPassword(cid, null);
							refresh();
						}
					}).setNegativeButton("No", new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int id)
						{
							dialog.cancel();
						}
					});

			builder.create().show();

			return true;

		}
	};

	/**
	 * Creates a dialog to ask the user for a new password for a network.
	 */
	protected OnPreferenceClickListener onNetworkPasswordClickListener = new OnPreferenceClickListener()
	{
		@Override
		public boolean onPreferenceClick(Preference preference)
		{
			final CharSequence network = preference.getTitle();
			final PasswordDialog pwDialog = new PasswordDialog(preference.getContext());
			pwDialog.setTitle("Enter new password for network: " + network);

			String oldPw = networkPasswords.get(network);
			if (oldPw != null) {
				pwDialog.setPassword(oldPw);
			}

			pwDialog.setOnApplyListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					secService.setNetworkPassword(network.toString(), pwDialog.getPassword());
					refresh();
				}
			});
			pwDialog.show();
			return true;
		}
	};


	// --- inner classes ---

	/**
	 * Dialog to ask for new (remote) Platform Password.
	 */
	public static class PasswordDialog extends Dialog
	{
		private EditText editText;
		private Button okButton;

		public PasswordDialog(Context context)
		{
			super(context);

			LayoutParams lparams = new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			LayoutParams rlparams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			android.widget.RelativeLayout.LayoutParams okButtonParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			android.widget.RelativeLayout.LayoutParams cancelButtonParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);

			LinearLayout ll = new LinearLayout(context);
			ll.setLayoutParams(lparams);
			ll.setOrientation(LinearLayout.VERTICAL);

			editText = new EditText(context);
			editText.setText("");
			editText.setLayoutParams(lparams);

			RelativeLayout rl = new RelativeLayout(context);
			rl.setLayoutParams(rlparams);

			okButton = new Button(context);
			okButton.setText("Ok");
			okButton.setWidth(150);
			okButton.setId(1);
			okButtonParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			okButton.setLayoutParams(okButtonParams);

			Button cancelButton = new Button(context);
			cancelButton.setText("Cancel");
			cancelButton.setWidth(120);
			cancelButton.setId(2);
			cancelButtonParams.addRule(RelativeLayout.RIGHT_OF, okButton.getId());
			cancelButton.setLayoutParams(cancelButtonParams);

			rl.addView(okButton);
			rl.addView(cancelButton);

			ll.addView(editText);
			ll.addView(rl);

			cancelButton.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					dismiss();
				}
			});

			setContentView(ll);
		}

		/**
		 * Return the password entered in the Dialog.
		 * 
		 * @return the password
		 */
		public String getPassword()
		{
			return editText.getText().toString();
		}

		/**
		 * Sets a password to be displayed.
		 * 
		 * @param pw
		 *            the new password
		 */
		public void setPassword(String pw)
		{
			editText.setText(pw);
		}

		/**
		 * Set a listener which is called when the user clicks on "Ok".
		 * 
		 * @param l
		 *            the Listener
		 */
		public void setOnApplyListener(final android.view.View.OnClickListener l)
		{
			okButton.setOnClickListener(new View.OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					l.onClick(v);
					cancel();
				}
			});
		}
	}

	@Override
	public void setPlatformId(IComponentIdentifier platformId)
	{
		this.platformId = platformId;
	}
}
