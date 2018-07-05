package jadex.android.controlcenter.settings;

import jadex.android.controlcenter.preference.DiscoveryPreference;
import jadex.android.controlcenter.preference.JadexBooleanPreference;
import jadex.android.controlcenter.preference.JadexIntegerPreference;
import jadex.android.controlcenter.preference.LongClickablePreference;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.modelinfo.SubcomponentTypeInfo;
import jadex.bridge.service.types.awareness.DiscoveryInfo;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.ThreadSuspendable;
import jadex.platform.service.awareness.management.AwarenessManagementAgent;
import jadex.platform.service.awareness.management.AwarenessManagementAgentHelper;
import jadex.platform.service.awareness.management.AwarenessSettingsData;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Handler;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * Settings implementation for {@link AwarenessManagementAgent}.
 */
public class AwarenessSettings extends AComponentSettings implements OnPreferenceChangeListener
{

	/** Handler to change UI objects from non-ui threads. */
	private Handler uiHandler;

	/** Id of the platform to be configured. */
	private IComponentIdentifier platformId;

	// UI members
	private JadexBooleanPreference cbautoCreate;
	private JadexBooleanPreference cbautoDelete;
	private JadexIntegerPreference spdelay;
	private JadexBooleanPreference cbfast;
	private JadexBooleanPreference[] cbmechanisms;
	private PreferenceCategory infoCat;
	private PreferenceScreen screen;

	private AwarenessManagementAgentHelper helper;

	private List<String> excludes;
	private List<String> includes;

	private PreferenceCategory inexcludeCat;

	/** Enum for preference keys */
	private enum PREFKEYS
	{
		FAST, DELAY, AUTOCREATE, AUTODELETE, INEXCLUDES
	}

	public AwarenessSettings(IExternalAccess extAcc)
	{
		super(extAcc);
		helper = new AwarenessManagementAgentHelper(extAcc);
		includes = new ArrayList<String>();
		excludes = new ArrayList<String>();
	}

	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add("Refresh");
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		refreshSettings();
		refreshDiscoveryMechanisms();
		refreshDiscoveryInfos();
		return true;
	}
	
	public void onOptionsMenuClosed(Menu menu)
	{
	}

	public void onDestroy()
	{
	}

	protected void createPreferenceHierarchy(final PreferenceScreen screen)
	{
		this.screen = screen;
		uiHandler = new Handler();

		// --- Proxy Settings ---
		PreferenceCategory proxyCat = new PreferenceCategory(screen.getContext());
		screen.addPreference(proxyCat);
		proxyCat.setTitle("Proxy Settings");

		cbautoCreate = new JadexBooleanPreference(screen.getContext());
		cbautoCreate.setTitle("Create On Discovery");
		cbautoCreate.setKey(PREFKEYS.FAST.toString());
		cbautoCreate.setOnPreferenceChangeListener(this);
		cbautoCreate.setEnabled(false);
		proxyCat.addPreference(cbautoCreate);

		cbautoDelete = new JadexBooleanPreference(screen.getContext());
		cbautoDelete.setTitle("Delete On Disappearance");
		cbautoDelete.setKey(PREFKEYS.AUTODELETE.toString());
		cbautoDelete.setOnPreferenceChangeListener(this);
		cbautoDelete.setEnabled(false);
		proxyCat.addPreference(cbautoDelete);

		// --- Discovery Settings ---
		PreferenceCategory discoveryCat = new PreferenceCategory(screen.getContext());
		screen.addPreference(discoveryCat);
		discoveryCat.setTitle("Discovery Settings");

		spdelay = new JadexIntegerPreference(screen.getContext());
		spdelay.setTitle("Info send delay");
		spdelay.setKey(PREFKEYS.DELAY.toString());
		spdelay.setOnPreferenceChangeListener(this);
		spdelay.setEnabled(false);
		discoveryCat.addPreference(spdelay);

		cbfast = new JadexBooleanPreference(screen.getContext());
		cbfast.setTitle("Fast startup awareness");
		cbfast.setKey(PREFKEYS.FAST.toString());
		cbfast.setOnPreferenceChangeListener(this);
		cbfast.setEnabled(false);
		discoveryCat.addPreference(cbfast);

		// --- Discovery Mechanisms ---
		PreferenceScreen mechanismsCat = screen.getPreferenceManager().createPreferenceScreen(screen.getContext());
		// PreferenceCategory mechanismsCat = new
		// PreferenceCategory(screen.getContext());
		screen.addPreference(mechanismsCat);
		mechanismsCat.setTitle("Discovery Mechanisms");

		SubcomponentTypeInfo[] dis = extAcc.getModel().getSubcomponentTypes();
		cbmechanisms = new JadexBooleanPreference[dis.length];
		for (int i = 0; i < dis.length; i++)
		{
			final JadexBooleanPreference disMechanism = new JadexBooleanPreference(screen.getContext());
			disMechanism.setEnabled(false);
			cbmechanisms[i] = disMechanism;
			final String disType = dis[i].getName();
			disMechanism.setTitle(disType);
			disMechanism.setKey(disType);
			disMechanism.setOnPreferenceClickListener(new OnPreferenceClickListener()
			{

				public boolean onPreferenceClick(Preference preference)
				{
					disMechanism.setEnabled(false);
					return true;
				}
			});
			disMechanism.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
			{
				public boolean onPreferenceChange(Preference preference, Object newValue)
				{
					final boolean on = (Boolean) newValue;
					helper.setDiscoveryMechanismState(disType, on).addResultListener(new DefaultResultListener<Void>()
					{
						public void resultAvailable(Void result)
						{
							uiHandler.post(new Runnable()
							{
								public void run()
								{
									disMechanism.setEnabled(true);
								}
							});
						}

						@Override
						public void exceptionOccurred(Exception exception)
						{
							uiHandler.post(new Runnable()
							{
								public void run()
								{
									disMechanism.setEnabled(true);
									disMechanism.setChecked(!on);
									Toast.makeText(screen.getContext(), "Could not start Discovery Mechanism: " + disType, Toast.LENGTH_SHORT).show();
								}
							});
						}
					});

					return true;
				}
			});

			mechanismsCat.addPreference(disMechanism);
		}

		inexcludeCat = new PreferenceCategory(screen.getContext());
		screen.addPreference(inexcludeCat);
		inexcludeCat.setTitle("Includes/Excludes");
		inexcludeCat.setKey(PREFKEYS.INEXCLUDES.toString());

		infoCat = new PreferenceCategory(screen.getContext());
		screen.addPreference(infoCat);
		infoCat.setTitle("Discovery Info");

		refreshSettings();
		refreshDiscoveryMechanisms();
		refreshDiscoveryInfos();
	}

	/**
	 * Apply Discovery Mechanism settings to GUI.
	 */
	protected void refreshDiscoveryMechanisms()
	{
		for (JadexBooleanPreference mechanism : cbmechanisms)
		{
			mechanism.setEnabled(false);
		}
		helper.getActiveDiscoveryMechanisms().addResultListener(new DefaultResultListener<Set<String>>()
		{
			public void resultAvailable(final Set<String> localtypes)
			{
				uiHandler.post(new Runnable()
				{
					public void run()
					{
						for (int i = 0; i < cbmechanisms.length; i++)
						{
							cbmechanisms[i].setChecked(localtypes.contains(cbmechanisms[i].getTitle()));
							cbmechanisms[i].setEnabled(true);
						}
					}
				});
			}
		});
	}

	/**
	 * Apply settings to GUI.
	 */
	protected void refreshSettings()
	{
		cbautoCreate.setEnabled(false);
		cbautoDelete.setEnabled(false);
		spdelay.setEnabled(false);
		cbfast.setEnabled(false);

		AwarenessSettingsData settings = helper.getSettings().get();

		cbautoCreate.setValue(settings.autocreate);
		cbautoDelete.setValue(settings.autodelete);

		long value = settings.delay / 1000;
		spdelay.setValue((int) value);
		cbfast.setChecked(settings.fast);
		// includes.setEntries(settings.includes);
		// excludes.setEntries(settings.excludes);

		cbautoCreate.setEnabled(true);
		cbautoDelete.setEnabled(true);
		spdelay.setEnabled(true);
		cbfast.setEnabled(true);

		this.includes.clear();
		this.excludes.clear();

		for (int i = 0; i < settings.includes.length; i++)
		{
			addInOrExclude(settings.includes[i], true);
		}
		for (int i = 0; i < settings.excludes.length; i++)
		{
			addInOrExclude(settings.excludes[i], false);
		}
	}

	private void addInOrExclude(String platform, boolean included)
	{
		LongClickablePreference includePref = new LongClickablePreference(inexcludeCat.getContext());
		includePref.setTitle(platform);
		if (included) {
			includes.add(platform);
			includePref.setSummary("included");
		} else {
			excludes.add(platform);
			includePref.setSummary("excluded");
		}
		includePref.setOnPreferenceLongClickListener(onInExcludeLongClickListener);
		includePref.setKey(platform);
		inexcludeCat.addPreference(includePref);
	}

	/**
	 * Refresh the discovery infos.
	 */
	protected void refreshDiscoveryInfos()
	{
		helper.getDiscoveryInfos().addResultListener(new DefaultResultListener<DiscoveryInfo[]>()
		{

			@Override
			public void exceptionOccurred(Exception exception)
			{
				// sprefresh.setValue(Integer.valueOf(0));
			}

			public void resultAvailable(final DiscoveryInfo[] ds)
			{
				List<Preference> newDisPrefs = new ArrayList<Preference>();
				if (ds.length == 0)
				{
					final Preference dummyPref = new Preference(infoCat.getContext());
					dummyPref.setTitle("No Discovery Infos available.");
					newDisPrefs.add(dummyPref);
				} else
				{
					for (int i = 0; i < ds.length; i++)
					{
						final DiscoveryInfo info = ds[i];
						DiscoveryPreference disPref = new DiscoveryPreference(infoCat.getContext(), info);
						disPref.setOnPreferenceLongClickListener(onDiscoveryInfoLongClickListener);
						newDisPrefs.add(disPref);
					}
				}
				infoCat.removeAll();
				for (Preference disPref : newDisPrefs)
				{
					infoCat.addPreference(disPref);
				}
			}
		});
	}

	public boolean onPreferenceChange(Preference preference, Object newValue)
	{
		// local variable for XML transfer
		final AwarenessSettingsData settings = new AwarenessSettingsData();
		settings.delay = ((long) spdelay.getInt()) * 1000;
		settings.fast = cbfast.isChecked();
		settings.autocreate = cbautoCreate.isChecked();
		settings.autodelete = cbautoDelete.isChecked();
		settings.includes = this.includes.toArray(new String[this.includes.size()]);
		settings.excludes = this.excludes.toArray(new String[this.excludes.size()]);

		switch (PREFKEYS.valueOf(preference.getKey()))
		{
		case FAST:
			settings.fast = (Boolean) newValue;
			break;
		case AUTOCREATE:
			settings.autocreate = (Boolean) newValue;
			break;
		case AUTODELETE:
			settings.autodelete = (Boolean) newValue;
			break;
		case DELAY:
			settings.delay = ((Integer) newValue).longValue() * 1000;
			break;
		case INEXCLUDES:
			break;
		default:
			break;
		}

		helper.setSettings(settings);
		return true;
	}

	public void setPlatformId(IComponentIdentifier platformId)
	{
		this.platformId = platformId;
	}

	private OnPreferenceClickListener onDiscoveryInfoLongClickListener = new OnPreferenceClickListener()
	{

		public boolean onPreferenceClick(Preference preference)
		{
			DiscoveryPreference pref = (DiscoveryPreference) preference;
			final DiscoveryInfo info = pref.getDiscoveryInfo();

			Builder builder = new AlertDialog.Builder(pref.getContext());
			String addRemoveProxyText;
			if (info.getProxy() == null)
			{
				addRemoveProxyText = "Add local Proxy";
			} else
			{
				addRemoveProxyText = "Remove local Proxy";
			}
			CharSequence[] items = 
					new CharSequence[] { addRemoveProxyText, "Add to excludes", "Add to includes" };
			
			builder.setItems(items, new DialogInterface.OnClickListener()
			{

				public void onClick(DialogInterface dialog, int which)
				{
					switch (which)
					{
					case 0: // add/remove proxy
						final Boolean create = (info.getProxy() == null);

						final IComponentIdentifier proxy = info.getProxy() != null && info.getProxy().isDone() && info.getProxy().getException() == null ? info
								.getProxy().get() : null;
						if (create && info.getProxy() == null || !create && proxy != null)
						{
							// setting changed -> create or
							// delete proxy
							helper.createOrDeleteProxy(info.getIdentifier(), create).addResultListener(new DefaultResultListener<Void>()
							{
								public void exceptionOccurred(Exception exception)
								{
									exception.printStackTrace();
									uiHandler.post(new Runnable()
									{
										public void run()
										{
											Toast.makeText(screen.getContext(), "Could not start/stop Proxy for " + info.getIdentifier(),
													Toast.LENGTH_LONG).show();
										}
									});
								};

								public void resultAvailable(Void result)
								{
									refreshDiscoveryInfos();
								}
							});
						}
						break;
					case 1: // add to excludes
						addInOrExclude(info.getIdentifier().getPlatformPrefix(), false);
						onPreferenceChange(inexcludeCat, null);
						break;
					case 2: // add to includes
						addInOrExclude(info.getIdentifier().getPlatformPrefix(), true);
						onPreferenceChange(inexcludeCat, null);
						break;
					default:
						break;
					}
				}

			});
			AlertDialog dialog = builder.create();
			dialog.show();

			return true;
		}
	};

	private OnPreferenceClickListener onInExcludeLongClickListener = new OnPreferenceClickListener()
	{

		public boolean onPreferenceClick(Preference p)
		{
			Builder builder = new AlertDialog.Builder(p.getContext());
			final CharSequence platformPrefix = p.getTitle();
			CharSequence summary = p.getSummary();
			builder.setMessage("Delete " + summary.subSequence(0, summary.length()-1) + " for " + platformPrefix + "?").setCancelable(false)
					.setPositiveButton("Yes", new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int id)
						{
							inexcludeCat.removePreference(inexcludeCat.findPreference(platformPrefix));
							includes.remove(platformPrefix);
							excludes.remove(platformPrefix);
							onPreferenceChange(inexcludeCat, null);
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
}
