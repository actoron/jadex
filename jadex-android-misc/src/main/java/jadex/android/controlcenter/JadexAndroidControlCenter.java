package jadex.android.controlcenter;

import jadex.android.JadexAndroidContext;
import jadex.android.controlcenter.settings.AComponentSettings;
import jadex.android.controlcenter.settings.AServiceSettings;
import jadex.android.controlcenter.settings.ISettings;
import jadex.base.service.settings.AndroidSettingsService;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.search.BasicResultSelector;
import jadex.bridge.service.search.ISearchManager;
import jadex.bridge.service.search.IVisitDecider;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SReflect;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.micro.annotation.Binding;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.provider.Contacts.Settings;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;

/**
 * A Simple Control Center for Jadex-Android. Provides Access to configurable
 * Components and Services. Because Android doesn't provide a way to set Option
 * Menus for Child PreferenceScreens, this Activity is instantiated once for
 * every child PreferenceScreen that is displayed. It then displays the child
 * PreferenceScreen and delegates calls to the child Settings Implementation.
 * 
 * (See
 * http://stackoverflow.com/questions/5032141/adding-menus-to-child-preference
 * -screens)
 * 
 */
public class JadexAndroidControlCenter extends PreferenceActivity {

	private static final String EXTRA_SHOWCHILDPREFSCREEN = "showChildPrefScreen";
	private static final String EXTRA_SETTINGSKEY = "settingsKey";
	private SharedPreferences sharedPreferences;
	private PreferenceCategory servicesCat;
	private PreferenceCategory componentsCat;
	private ISettings displayedChildSettings;
	
	static private Map<String, ISettings> childSettings;
	
	static {
		childSettings = new HashMap<String, ISettings>();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Are we displaying Preferences for a child Prefscreen?
		if (getIntent().getBooleanExtra(EXTRA_SHOWCHILDPREFSCREEN, false)) {
			String settingsKey = getIntent().getStringExtra(EXTRA_SETTINGSKEY);
			displayedChildSettings = childSettings.get(settingsKey);
			if (displayedChildSettings != null) {
				// display child preferences, enables us to control the options menu
				PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);
				setPreferenceScreen(root);
				displayedChildSettings.setPreferenceScreen(root);
				this.setTitle(settingsKey);
			} else {
				// display error
			}
		} else {
			setPreferenceScreen(createPreferenceHierarchy());
			this.setTitle("Control Center");
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		if (displayedChildSettings != null) {
			// child functionality
			return displayedChildSettings.onCreateOptionsMenu(menu);
		} else {
			// main functionality
			menu.add("Refresh");
			return true;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (displayedChildSettings != null) {
			// child functionality
			return displayedChildSettings.onOptionsItemSelected(item); 
		} else {
			// main functionality
			refreshControlCenter();
			return true;
		}
	}
	
	private PreferenceScreen createPreferenceHierarchy() {
		final PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);

		servicesCat = new PreferenceCategory(this);
		servicesCat.setTitle("Services");
		root.addPreference(servicesCat);

		componentsCat = new PreferenceCategory(this);
		componentsCat.setTitle("Components");
		root.addPreference(componentsCat);

		refreshControlCenter();
		return root;
	}

	private void refreshControlCenter() {
		servicesCat.removeAll();
		componentsCat.removeAll();
		childSettings.clear();
		addDummyPrefs();

		if (JadexAndroidContext.getInstance().isJadexRunning()) {
			// get all viewable services
			IExternalAccess extAcc = JadexAndroidContext.getInstance().getExternalPlattformAccess();

			IServiceProvider sp = extAcc.getServiceProvider();
			ISearchManager manager = SServiceProvider.getSearchManager(true, Binding.SCOPE_PLATFORM);
			IVisitDecider decider = SServiceProvider.getVisitDecider(false, Binding.SCOPE_PLATFORM);

			BasicResultSelector selector = new BasicResultSelector(ViewableFilter.VIEWABLE_FILTER, false);
			IIntermediateFuture<IService> services = sp.getServices(manager, decider, selector);

			services.addResultListener(new IntermediateDefaultResultListener<IService>() {

				@Override
				public void resultAvailable(Collection<IService> result) {
					for (IService service : result) {
						if (addServiceSettings(servicesCat, service)) {
							Preference dummyPref = servicesCat.findPreference("dummy");
							if (dummyPref != null)
								servicesCat.removePreference(dummyPref);
						}
					}
				}

				@Override
				public void intermediateResultAvailable(IService service) {
					if (addServiceSettings(servicesCat, service)) {
						servicesCat.removePreference(servicesCat.findPreference("dummy"));
					}
				}

			});

			// get all viewable components
			SServiceProvider.getServiceUpwards(extAcc.getServiceProvider(), IComponentManagementService.class).addResultListener(
					new DefaultResultListener<IComponentManagementService>() {
						@Override
						public void resultAvailable(final IComponentManagementService cms) {
							cms.getComponentIdentifiers().addResultListener(new DefaultResultListener<IComponentIdentifier[]>() {
								@Override
								public void resultAvailable(IComponentIdentifier[] result) {
									for (IComponentIdentifier cid : result) {
										cms.getExternalAccess(cid).addResultListener(new DefaultResultListener<IExternalAccess>() {
											@Override
											public void resultAvailable(final IExternalAccess acc) {
												Object clid = acc.getModel().getProperty(ViewableFilter.COMPONENTVIEWER_VIEWERCLASS, getClassLoader());

												final Class<?> clazz = getGuiClass(clid);

												if (clazz != null) {
													runOnUiThread(new Runnable() {
														@Override
														public void run() {
															if (addComponentSettings(componentsCat, acc, clazz)) {
																Preference dummyPref = componentsCat.findPreference("dummy");
																if (dummyPref != null)
																	componentsCat.removePreference(dummyPref);
															}
														}
													});
												}
											}
										});
									}
								}
							});
						}
					});
		}
	}

	private void addDummyPrefs() {
		servicesCat.removeAll();
		componentsCat.removeAll();
		final Preference dummyServicePref = new Preference(this);
		dummyServicePref.setTitle("No viewable Services.");
		dummyServicePref.setKey("dummy");
		dummyServicePref.setEnabled(false);
		servicesCat.addPreference(dummyServicePref);
		final Preference dummyComponentPref = new Preference(this);
		dummyComponentPref.setTitle("No viewable Components.");
		dummyComponentPref.setKey("dummy");
		dummyComponentPref.setEnabled(false);
		componentsCat.addPreference(dummyComponentPref);
	}

	protected boolean addServiceSettings(PreferenceGroup root, IService service) {
		final Object clid = service.getPropertyMap() != null ? service.getPropertyMap().get(ViewableFilter.COMPONENTVIEWER_VIEWERCLASS) : null;
		Class<?> guiClass = getGuiClass(clid);
		if (guiClass != null) {
			try {
				AServiceSettings settings = (AServiceSettings) guiClass.getConstructor(IService.class).newInstance(service);
				addSettings(root,settings);
				return true;
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	protected boolean addComponentSettings(PreferenceGroup root, IExternalAccess component, Class<?> guiClass) {
		try {
			AComponentSettings settings = (AComponentSettings) guiClass.getConstructor(IExternalAccess.class).newInstance(component);
			addSettings(root,settings);
			return true;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	protected void addSettings(PreferenceGroup root, ISettings settings) {
		PreferenceScreen screen = this.getPreferenceManager().createPreferenceScreen(this);
		root.addPreference(screen);
		Intent i = new Intent(this, JadexAndroidControlCenter.class);
		i.putExtra(EXTRA_SHOWCHILDPREFSCREEN, true);
		i.putExtra(EXTRA_SETTINGSKEY, settings.getTitle());
		childSettings.put(settings.getTitle(), settings);
		screen.setIntent(i);
		screen.setKey(settings.getTitle());
		screen.setTitle(settings.getTitle());
		//settings.setPreferenceScreen(screen);
	}

	private Class<?> getGuiClass(final Object clid) {
		Class<?> guiClass = null;
		if (clid instanceof String) {
			Class<?> clazz = SReflect.classForName0((String) clid, getClassLoader());
			if (clazz != null) {
				guiClass = clazz;
			}
		} else if (clid instanceof String[]) {
			for (String className : (String[]) clid) {
				Class<?> clazz = SReflect.classForName0(className, getClassLoader());
				if (clazz != null) {
					guiClass = clazz;
					break;
				}
			}
		}
		return guiClass;
	}
}
