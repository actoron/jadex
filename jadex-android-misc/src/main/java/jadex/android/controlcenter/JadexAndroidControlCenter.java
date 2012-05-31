package jadex.android.controlcenter;

import jadex.android.JadexAndroidContext;
import jadex.android.controlcenter.settings.AComponentSettings;
import jadex.android.controlcenter.settings.AServiceSettings;
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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;

public class JadexAndroidControlCenter extends PreferenceActivity {

	private SharedPreferences sharedPreferences;

	public JadexAndroidControlCenter() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setTitle("Control Center");
		sharedPreferences = JadexAndroidContext.getInstance().getAndroidContext()
				.getSharedPreferences(AndroidSettingsService.DEFAULT_PREFS_NAME, Context.MODE_PRIVATE);

		Map<String, ?> prefs = sharedPreferences.getAll();

		setPreferenceScreen(createPreferenceHierarchy(prefs));
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	private PreferenceScreen createPreferenceHierarchy(Map<String, ?> prefs) {
		final PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);

		final PreferenceCategory servicesCat = new PreferenceCategory(this);
		servicesCat.setTitle("Services");
		root.addPreference(servicesCat);
		final Preference dummyServicePref = new Preference(this);
		dummyServicePref.setTitle("No viewable Services.");
		dummyServicePref.setEnabled(false);
		servicesCat.addPreference(dummyServicePref);

		final PreferenceCategory componentsCat = new PreferenceCategory(this);
		componentsCat.setTitle("Components");
		root.addPreference(componentsCat);
		final Preference dummyComponentPref = new Preference(this);
		dummyComponentPref.setTitle("No viewable Components.");
		dummyComponentPref.setEnabled(false);
		componentsCat.addPreference(dummyComponentPref);

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
							servicesCat.removePreference(dummyServicePref);
						}
					}
				}

				@Override
				public void intermediateResultAvailable(IService service) {
					if (addServiceSettings(servicesCat, service)) {
						servicesCat.removePreference(dummyServicePref);
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
												Object clid = acc.getModel().getProperty(ViewableFilter.COMPONENTVIEWER_VIEWERCLASS,
														getClassLoader());

												final Class<?> clazz = getGuiClass(clid);

												if (clazz != null) {
													runOnUiThread(new Runnable() {
														@Override
														public void run() {
															if (addComponentSettings(componentsCat, acc, clazz)) {
																componentsCat.removePreference(dummyComponentPref);
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
		return root;
	}

	protected boolean addServiceSettings(PreferenceGroup root, IService service) {
		final Object clid = service.getPropertyMap() != null ? service.getPropertyMap().get(ViewableFilter.COMPONENTVIEWER_VIEWERCLASS)
				: null;
		Class<?> guiClass = getGuiClass(clid);
		if (guiClass != null) {
			try {
				AServiceSettings settings = (AServiceSettings) guiClass.getConstructor(IService.class).newInstance(service);
				PreferenceScreen screen = this.getPreferenceManager().createPreferenceScreen(this);
				root.addPreference(screen);
				settings.setPreferenceRoot(screen);
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
			PreferenceScreen screen = this.getPreferenceManager().createPreferenceScreen(this);
			root.addPreference(screen);
			settings.setPreferenceScreen(screen);
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

	// protected void reloadJadexSettings() {
	// IExternalAccess extAcc =
	// JadexAndroidContext.getInstance().getExternalPlattformAccess();
	// if (extAcc != null) {
	// SServiceProvider.getService(extAcc.getServiceProvider(),
	// ISettingsService.class, Binding.SCOPE_PLATFORM).addResultListener(
	// new DefaultResultListener<ISettingsService>() {
	//
	// @Override
	// public void resultAvailable(ISettingsService settings) {
	// settings.loadProperties().addResultListener(new
	// DefaultResultListener<Properties>() {
	//
	// @Override
	// public void resultAvailable(Properties result) {
	//
	// }
	// });
	// }
	// });
	// }
	//
	// }
	//
	// @Override
	// protected void onDestroy() {
	// super.onDestroy();
	// }
	//
	// private OnPreferenceChangeListener opcl = new
	// OnPreferenceChangeListener() {
	//
	// @Override
	// public boolean onPreferenceChange(Preference preference, Object newValue)
	// {
	// System.out.println("changed: " + newValue);
	// // ((JadexBooleanPreference)preference).setChecked((Boolean)
	// // newValue);
	// String key = preference.getKey();
	// Editor edit = sharedPreferences.edit();
	// edit.putString(key, newValue.toString());
	// edit.commit();
	// reloadJadexSettings();
	// return true;
	// }
	// };

}
