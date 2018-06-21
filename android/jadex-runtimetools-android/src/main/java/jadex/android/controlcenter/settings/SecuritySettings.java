package jadex.android.controlcenter.settings;

import jadex.android.controlcenter.preference.JadexBooleanPreference;
import jadex.android.controlcenter.preference.JadexStringPreference;
import jadex.android.controlcenter.preference.LongClickablePreference;
import jadex.android.service.JadexPlatformManager;
import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.IService;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.awareness.DiscoveryInfo;
import jadex.bridge.service.types.awareness.IAwarenessManagementService;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.commons.ChangeEvent;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;

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
	private static final int OPTIONS_ADD_TRUSTED_NET = 1;

	private static final int OPTIONS_ADD_REMOTE_PW = 0;

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
	private JadexBooleanPreference usePlatformSecret;
	private JadexStringPreference platformSecret;
//	private JadexBooleanPreference trulan;
	private PreferenceCategory platformPasswordsCat;
	private PreferenceCategory networkPasswordsCat;

	/**
	 * Android Implementation for Security Service settings.
	 * 
	 * @param secservice
	 *            the security service to be administrated
	 */
	public SecuritySettings(IService secservice)
	{
		super(secservice);
		this.secService = (ISecurityService) service;
		this.platformPasswords = new HashMap<String, String>();
	}

	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuItem addRemotePw = menu.add(0, OPTIONS_ADD_REMOTE_PW, 0, "Add remote platform platformSecret");
		addRemotePw.setIcon(android.R.drawable.ic_menu_add);
		MenuItem addTrustedNet = menu.add(0, OPTIONS_ADD_TRUSTED_NET, 1, "Add trusted network");
		addTrustedNet.setIcon(android.R.drawable.ic_menu_add);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		boolean result = false;

		switch (item.getItemId())
		{
		case OPTIONS_ADD_REMOTE_PW:

			final AlertDialog.Builder builder = new AlertDialog.Builder(platformPasswordsCat.getContext());
			SServiceProvider.getService(JadexPlatformManager.getInstance().getExternalPlatformAccess(platformId),
					IAwarenessManagementService.class).addResultListener(new DefaultResultListener<IAwarenessManagementService>()
			{
				public void resultAvailable(IAwarenessManagementService result)
				{
					result.getKnownPlatforms().addResultListener(new DefaultResultListener<Collection<DiscoveryInfo>>()
					{
						public void resultAvailable(Collection<DiscoveryInfo> platforms)
						{
							final ArrayList<DiscoveryInfo> platformList = new ArrayList<DiscoveryInfo>(platforms.size());
							ArrayList<String> items = new ArrayList<String>();

							for (DiscoveryInfo dis : platforms)
							{
								String platformPrefix = dis.getComponentIdentifier().getPlatformPrefix();
								if (!platformPasswords.containsKey(platformPrefix) && !items.contains(platformPrefix))
								{
									platformList.add(dis);
									items.add(platformPrefix);
								}
							}

							if (items.isEmpty())
							{
								uiHandler.post(new Runnable()
								{
									public void run()
									{
										Toast.makeText(platformPasswordsCat.getContext(), "No further platforms found!", Toast.LENGTH_LONG).show();
									}
								});
							} else
							{
								builder.setTitle("Choose platform to add platformSecret for");
								builder.setItems(items.toArray(new String[0]), new DialogInterface.OnClickListener()
								{
									public void onClick(DialogInterface dialog, final int item)
									{
										final IComponentIdentifier cid = platformList.get(item).getComponentIdentifier();
										String platformPrefix = cid.getPlatformPrefix();
										final EditTextDialog pwDialog = new EditTextDialog(platformPasswordsCat.getContext());
										pwDialog.setTitle("Enter platformSecret for platform " + platformPrefix);
										pwDialog.show();
										pwDialog.setOnApplyListener(new OnClickListener()
										{
											public void onClick(View v)
											{
												String password = pwDialog.getText();
												secService.setPlatformSecret(cid, password);
											}
										});

									}
								});
								uiHandler.post(new Runnable()
								{
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
			result = true;
			break;
		case OPTIONS_ADD_TRUSTED_NET:
			final EditTextDialog netNameDialog = new EditTextDialog(networkPasswordsCat.getContext());
			netNameDialog.setTitle("Enter new network name");
			netNameDialog.show();
			netNameDialog.setOnApplyListener(new OnClickListener()
			{

				public void onClick(View v)
				{
					String netName = netNameDialog.getText();
					secService.setNetwork(netName, "");
				}
			});
			result = true;
			break;
		default:
			result = false;
		}

		return result;
	}
	
	public void onOptionsMenuClosed(Menu menu)
	{
	}

	public void setPlatformId(IComponentIdentifier platformId)
	{
		this.platformId = platformId;
	}
	
	public void onDestroy()
	{
		subscribtion.terminate();
	}

	protected void createPreferenceHierarchy(final PreferenceScreen screen)
	{
		uiHandler = new Handler();

		PreferenceCategory localCat = new PreferenceCategory(screen.getContext());
		localCat.setTitle("Local Password Settings");
		screen.addPreference(localCat);

		usePlatformSecret = new JadexBooleanPreference(screen.getContext());
		usePlatformSecret.setTitle("Use Password");
		usePlatformSecret.setEnabled(false);
		localCat.addPreference(usePlatformSecret);

		platformSecret = new JadexStringPreference(screen.getContext());
		platformSecret.setTitle("Password");
		platformSecret.setEnabled(false);
		platformSecret.setDialogTitle("Set new platformSecret");
		localCat.addPreference(platformSecret);

//		trulan = new JadexBooleanPreference(screen.getContext());
//		trulan.setTitle("Trust local networks");
//		localCat.addPreference(trulan);

		platformPasswordsCat = new PreferenceCategory(screen.getContext());
		platformPasswordsCat.setTitle("Remote Passwords");
		screen.addPreference(platformPasswordsCat);

		networkPasswordsCat = new PreferenceCategory(screen.getContext());
		networkPasswordsCat.setTitle("Network Names");
		screen.addPreference(networkPasswordsCat);

		usePlatformSecret.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
		{
			public boolean onPreferenceChange(Preference preference, Object newValue)
			{
				secService.setUsePlatformSecret((Boolean) newValue);
				return true;
			}
		});

		platformSecret.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
		{
			public boolean onPreferenceChange(Preference preference, Object newValue)
			{
				secService.setPlatformSecret(platformId, (String) newValue);
				return true;
			}
		});

//		trulan.setOnPreferenceClickListener(new OnPreferenceClickListener()
//		{
//			public boolean onPreferenceClick(Preference preference)
//			{
//
//				return true;
//			}
//		});
//
//		trulan.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
//		{
//			public boolean onPreferenceChange(Preference preference, Object newValue)
//			{
//				final Boolean newState = (Boolean) newValue;
//				if (newState)
//				{
//					Builder builder = new AlertDialog.Builder(screen.getContext());
//					builder.setMessage("Access from trusted Platforms is not platformSecret protected by default!\n You can set a platformSecret manually.")
//							.setPositiveButton("Ok", new DialogInterface.OnClickListener()
//							{
//								public void onClick(DialogInterface dialog, int which)
//								{
//									secService.setTrustedLanMode(newState);
//									refresh();
//								}
//
//							}).setNegativeButton("Cancel", new DialogInterface.OnClickListener()
//							{
//								public void onClick(DialogInterface dialog, int which)
//								{
//									trulan.setChecked(false);
//								}
//							}).create().show();
//				} else
//				{
//					secService.setTrustedLanMode(newState);
//					refresh();
//				}
//				return true;
//			}
//		});

//		subscribtion = secService.subscribeToEvents();
//		subscribtion.addResultListener(refreshListener);
		refresh();
	}

//	private IIntermediateResultListener<ChangeEvent<Object>> refreshListener = new IIntermediateResultListener<jadex.commons.ChangeEvent<Object>>()
//	{
//
//		public void exceptionOccurred(Exception exception)
//		{
//		}
//
//		public void resultAvailable(Collection<ChangeEvent<Object>> result)
//		{
//		}
//
//		public void intermediateResultAvailable(final ChangeEvent<Object> event)
//		{
//			if (event == null) {
//				return;
//			}
//			System.out.println("event: " + event.getType() + " " + event.getValue());
//			uiHandler.post(new Runnable()
//			{
//				@SuppressWarnings("unchecked")
//				public void run()
//				{
//					if (ISecurityService.PROPERTY_USEPASS.equals(event.getType()))
//					{
//						usePlatformSecret.setValue((Boolean) event.getValue());
//
//					} else if (ISecurityService.PROPERTY_TRUSTEDLAN.equals(event.getType()))
//					{
//						trulan.setChecked((Boolean) event.getValue());
//					} else if (ISecurityService.PROPERTY_LOCALPASS.equals(event.getType()))
//					{
//						platformSecret.setText((String)event.getValue());
//					} else if (ISecurityService.PROPERTY_PLATFORMPASS.equals(event.getType()))
//					{
//						setPlatformPasswords((Map<String, String>) event.getValue());
//					} else if (ISecurityService.PROPERTY_NETWORKPASS.equals(event.getType()))
//					{
//						setNetworkPasswords((Map<String, String>) event.getValue());
//					} else if (ISecurityService.PROPERTY_KEYSTORESETTINGS.equals(event.getType()))
//					{
////						String[] info = (String[]) event.getValue();
////						tfstorepath.setText(info[0]);
////						tfstorepass.setText(info[1]);
////						tfkeypass.setText(info[2]);
//					} else if (ISecurityService.PROPERTY_KEYSTORESETTINGS.equals(event.getType()))
//					{
////						String[] info = (String[]) event.getValue();
////						tfstorepath.setText(info[0]);
////						tfstorepass.setText(info[1]);
////						tfkeypass.setText(info[2]);
//					} else if (ISecurityService.PROPERTY_KEYSTOREENTRIES.equals(event.getType()))
//					{
//						// Map<String, KeyStoreEntry> entries = (Map<String,
//						// KeyStoreEntry>)event.getValue();
////						updateact.run();
//					} else if (ISecurityService.PROPERTY_SELECTEDMECHANISM.equals(event.getType()))
//					{
////						acp.setSelectedMechanism(((Integer) event.getValue()).intValue());
//					} else if (ISecurityService.PROPERTY_MECHANISMPARAMETER.equals(event.getType()))
//					{
////						Object[] data = (Object[]) event.getValue();
////						acp.setParameterValue(((Class<?>) event.getSource()).getName(), (String) data[0], data[1]);
//					}
//				}
//			});
//		}
//
//		public void finished()
//		{
//		}
//
//	};

	private void refresh()
	{
		secService.isUsePlatformSecret().addResultListener(new DefaultResultListener<Boolean>()
		{
			public void resultAvailable(final Boolean result)
			{
				uiHandler.post(new Runnable()
				{
					public void run()
					{
						usePlatformSecret.setValue(result);
						usePlatformSecret.setEnabled(true);
					}
				});
			}
		});

		secService.getPlatformSecret(platformId).addResultListener(new DefaultResultListener<String>()
		{
			public void resultAvailable(final String result)
			{
				uiHandler.post(new Runnable()
				{
					public void run()
					{
						platformSecret.setValue(result);
						platformSecret.setEnabled(true);
					}
				});
			}
		});

		// TODO: refresh all platform passwords


//		secService.getPlatformPasswords().addResultListener(new DefaultResultListener<Map<String, String>>()
//		{
//			public void resultAvailable(Map<String, String> result)
//			{
//				setPlatformPasswords(result);
//			}
//		});
//
//		secService.getNetworkPasswords().addResultListener(new DefaultResultListener<Map<String, String>>()
//		{
//			public void resultAvailable(Map<String, String> result)
//			{
//				setNetworkPasswords(result);
//			}
//		});
//
//		secService.isTrustedLanMode().addResultListener(new DefaultResultListener<Boolean>()
//		{
//			public void resultAvailable(Boolean result)
//			{
//				trulan.setChecked(result);
//			}
//		});
	}

	protected void setNetworkPasswords(Map<String, String> result)
	{
		this.networkPasswords = result;
		networkPasswordsCat.removeAll();
		if (result.isEmpty())
		{
			Preference dummyPref = new Preference(platformPasswordsCat.getContext());
			dummyPref.setTitle("No network names set");
			dummyPref.setSummary("Press menu to add a new network name.");
			networkPasswordsCat.addPreference(dummyPref);
		} else
		{

			for (Entry<String, String> entry : result.entrySet())
			{
				LongClickablePreference pwPref = new LongClickablePreference(networkPasswordsCat.getContext());
				pwPref.setTitle(entry.getKey());
				pwPref.setSummary("Password: " + entry.getValue());
				pwPref.setOnPreferenceClickListener(onNetworkPasswordClickListener);
				pwPref.setOnPreferenceLongClickListener(onNetworkPasswordLongClickListener);
				networkPasswordsCat.addPreference(pwPref);
			}
		}
	}

	protected void setPlatformPasswords(Map<String, String> result)
	{
		this.platformPasswords = result;
		platformPasswordsCat.removeAll();
		if (result.isEmpty())
		{
			Preference dummyPref = new Preference(platformPasswordsCat.getContext());
			dummyPref.setTitle("No passwords set");
			dummyPref.setSummary("Press menu to add a new remote platform platformSecret.");
			platformPasswordsCat.addPreference(dummyPref);
		} else
		{
			for (Entry<String, String> entry : result.entrySet())
			{
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
	 * Creates a dialog to ask the user for a new platformSecret for a platform.
	 */
	protected OnPreferenceClickListener onPlatformPasswordClickListener = new OnPreferenceClickListener()
	{
		public boolean onPreferenceClick(Preference preference)
		{
			final CharSequence platformPrefix = preference.getTitle();
			final EditTextDialog pwDialog = new EditTextDialog(preference.getContext());
			pwDialog.setTitle("Enter new platformSecret for platform:\n" + platformPrefix);

			String oldPw = platformPasswords.get(platformPrefix);
			if (oldPw != null)
			{
				pwDialog.setText(oldPw);
			}
			pwDialog.setOnApplyListener(new OnClickListener()
			{
				public void onClick(View v)
				{
					BasicComponentIdentifier cid = new BasicComponentIdentifier(platformPrefix.toString());
					secService.setPlatformSecret(cid, pwDialog.getText());
				}
			});
			pwDialog.show();
			return true;
		}
	};

	/**
	 * Creates a dialog to confirm the deletion of a remote platform platformSecret.
	 */
	protected OnPreferenceClickListener onPlatformPasswordLongClickListener = new OnPreferenceClickListener()
	{
		public boolean onPreferenceClick(Preference p)
		{
			Builder builder = new AlertDialog.Builder(p.getContext());
			final CharSequence platformPrefix = p.getTitle();
			builder.setMessage("Delete platformSecret for " + platformPrefix + "?").setCancelable(false)
					.setPositiveButton("Yes", new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int id)
						{
							BasicComponentIdentifier cid = new BasicComponentIdentifier(platformPrefix.toString());
							secService.setPlatformSecret(cid, null);
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
	 * Creates a dialog to confirm the deletion of a trusted network.
	 */
	protected OnPreferenceClickListener onNetworkPasswordLongClickListener = new OnPreferenceClickListener()
	{
		public boolean onPreferenceClick(Preference p)
		{
			Builder builder = new AlertDialog.Builder(p.getContext());
			final CharSequence networkName = p.getTitle();
			builder.setMessage("Delete network " + networkName + "?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int id)
				{
					secService.setNetwork(networkName.toString(), null);
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
	 * Creates a dialog to ask the user for a new platformSecret for a network.
	 */
	protected OnPreferenceClickListener onNetworkPasswordClickListener = new OnPreferenceClickListener()
	{
		public boolean onPreferenceClick(Preference preference)
		{
			final CharSequence network = preference.getTitle();
			final EditTextDialog pwDialog = new EditTextDialog(preference.getContext());
			pwDialog.setTitle("Enter new platformSecret for network: " + network);

			String oldPw = networkPasswords.get(network);
			if (oldPw != null)
			{
				pwDialog.setText(oldPw);
			}

			pwDialog.setOnApplyListener(new OnClickListener()
			{
				public void onClick(View v)
				{
					secService.setNetwork(network.toString(), pwDialog.getText());
				}
			});
			pwDialog.show();
			return true;
		}
	};

	private ISubscriptionIntermediateFuture<ChangeEvent<Object>> subscribtion;

	// --- inner classes ---

	/**
	 * Dialog to ask for new (remote) Platform Password.
	 */
	public static class EditTextDialog extends Dialog
	{
		private EditText editText;
		private Button okButton;

		public EditTextDialog(Context context)
		{
			super(context);

			LayoutParams lparams = new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			LayoutParams rlparams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			android.widget.RelativeLayout.LayoutParams okButtonParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
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
				public void onClick(View v)
				{
					dismiss();
				}
			});

			setContentView(ll);
		}

		/**
		 * Return the platformSecret entered in the Dialog.
		 * 
		 * @return the platformSecret
		 */
		public String getText()
		{
			return editText.getText().toString();
		}

		/**
		 * Sets a platformSecret to be displayed.
		 * 
		 * @param pw
		 *            the new platformSecret
		 */
		public void setText(String pw)
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
				public void onClick(View v)
				{
					l.onClick(v);
					cancel();
				}
			});
		}
	}
}
