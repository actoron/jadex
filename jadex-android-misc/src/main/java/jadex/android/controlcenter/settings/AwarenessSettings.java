package jadex.android.controlcenter.settings;

import jadex.android.controlcenter.preference.DiscoveryPreference;
import jadex.android.controlcenter.preference.JadexBooleanPreference;
import jadex.android.controlcenter.preference.JadexIntegerPreference;
import jadex.base.service.awareness.management.AwarenessManagementAgent;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.modelinfo.SubcomponentTypeInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.awareness.DiscoveryInfo;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ThreadSuspendable;
import jadex.commons.transformation.annotations.Classname;
import jadex.commons.transformation.annotations.IncludeFields;
import jadex.tools.awareness.AwarenessSettingsData;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
public class AwarenessSettings extends AComponentSettings implements OnPreferenceChangeListener {
	
	/**
	 * Handler to change UI objects from non-ui threads.
	 */
	private Handler uiHandler;
	
	// UI members
	private JadexBooleanPreference cbautoCreate;
	private JadexBooleanPreference cbautoDelete;
	private JadexIntegerPreference spdelay;
	private JadexBooleanPreference cbfast;
	private JadexBooleanPreference[] cbmechanisms;
	private PreferenceCategory infoCat;
	private PreferenceScreen screen;

	/**
	 * Enum for preference keys
	 */
	private enum PREFKEYS {
		FAST, DELAY, AUTOCREATE, AUTODELETE
	}

	public AwarenessSettings(IExternalAccess extAcc) {
		super(extAcc);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Refresh");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		refreshSettings();
		refreshDiscoveryMechanisms();
		refreshDiscoveryInfos();
		return true;
	}

	@Override
	protected void createPreferenceHierarchy(final PreferenceScreen screen) {
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
		for (int i = 0; i < dis.length; i++) {
			final JadexBooleanPreference disMechanism = new JadexBooleanPreference(screen.getContext());
			disMechanism.setEnabled(false);
			cbmechanisms[i] = disMechanism;
			final String disType = dis[i].getName();
			disMechanism.setTitle(disType);
			disMechanism.setKey(disType);
			disMechanism.setOnPreferenceClickListener(new OnPreferenceClickListener() {
				
				@Override
				public boolean onPreferenceClick(Preference preference) {
					disMechanism.setEnabled(false);
					return true;
				}
			});
			disMechanism.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					final boolean on = (Boolean) newValue;
					extAcc.scheduleStep(new IComponentStep<Void>() {
						@Classname("deoractivateDiscoveryMechanism")
						public IFuture<Void> execute(final IInternalAccess ia) {
							final Future<Void> ret = new Future<Void>();
							ia.getChildrenAccesses().addResultListener(
									ia.createResultListener(new ExceptionDelegationResultListener<Collection<IExternalAccess>, Void>(ret) {
										public void customResultAvailable(Collection<IExternalAccess> subs) {
											IComponentIdentifier found = null;
											for (Iterator<IExternalAccess> it = subs.iterator(); it.hasNext();) {
												IExternalAccess exta = it.next();
												if (disType.equals(exta.getLocalType())) {
													found = exta.getComponentIdentifier();
													break;
												}
											}

											// Start relay mechanism agent
											if (on && found == null) {
												SServiceProvider.getService(ia.getServiceContainer(), IComponentManagementService.class,
														RequiredServiceInfo.SCOPE_GLOBAL).addResultListener(
														ia.createResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret) {
															public void customResultAvailable(IComponentManagementService cms) {
																CreationInfo info = new CreationInfo(ia.getComponentIdentifier());
																cms.createComponent(null, disType, info, null).addResultListener(
																		new ExceptionDelegationResultListener<IComponentIdentifier, Void>(ret) {
																			public void customResultAvailable(IComponentIdentifier result) {
																				ret.setResult(null);
																			}
																		});
															};
														}));
											}

											// Stop relay mechanism agent
											else if (!on && found != null) {
												final IComponentIdentifier cid = found;
												SServiceProvider.getService(ia.getServiceContainer(), IComponentManagementService.class,
														RequiredServiceInfo.SCOPE_GLOBAL).addResultListener(
														ia.createResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret) {
															public void customResultAvailable(IComponentManagementService cms) {
																cms.destroyComponent(cid).addResultListener(
																		new ExceptionDelegationResultListener<Map<String, Object>, Void>(ret) {
																			public void customResultAvailable(Map<String, Object> result) {
																				ret.setResult(null);
																			}
																		});
															};
														}));
											}

											// No change required.
											else {
												ret.setResult(null);
											}
										};
									}));
							return ret;
						}
					}).addResultListener(new DefaultResultListener<Void>() {

						@Override
						public void resultAvailable(Void result) {
							uiHandler.post(new Runnable() {
								@Override
								public void run() {
									disMechanism.setEnabled(true);
								}
							});
						}

						@Override
						public void exceptionOccurred(Exception exception) {
							uiHandler.post(new Runnable() {

								@Override
								public void run() {
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

		infoCat = new PreferenceCategory(screen.getContext());
		// screen.getPreferenceManager().createPreferenceScreen(screen.getContext());
		screen.addPreference(infoCat);
		infoCat.setTitle("Discovery Info");

		refreshSettings();
		refreshDiscoveryMechanisms();
		refreshDiscoveryInfos();
	}

	/**
	 * Apply Discovery Mechanism settings to GUI.
	 */
	protected void refreshDiscoveryMechanisms() {
		for (JadexBooleanPreference mechanism : cbmechanisms) {
			mechanism.setEnabled(false);
		}
		extAcc.scheduleStep(new IComponentStep<Set<String>>() {
			@Classname("getDiscoveryMechanisms")
			public IFuture<Set<String>> execute(IInternalAccess ia) {
				final Future<Set<String>> ret = new Future<Set<String>>();

				ia.getChildrenAccesses().addResultListener(
						ia.createResultListener(new ExceptionDelegationResultListener<Collection<IExternalAccess>, Set<String>>(ret) {
							public void customResultAvailable(Collection<IExternalAccess> result) {
								Set<String> res = new HashSet<String>();
								for (Iterator<IExternalAccess> it = result.iterator(); it.hasNext();) {
									IExternalAccess child = it.next();
									// System.out.println("child: "+child.getLocalType()+" "+child.getComponentIdentifier());
									res.add(child.getLocalType());
								}
								ret.setResult(res);
							}
						}));

				return ret;
			}
		}).addResultListener(new DefaultResultListener<Set<String>>() {
			public void customExceptionOccurred(Exception exception) {
				// sprefresh.setValue(new Integer(0));
			}

			@Override
			public void resultAvailable(Set<String> localtypes) {
				for (int i = 0; i < cbmechanisms.length; i++) {
					// System.out.println("test: "+cbmechanisms[i].getText()+" "+localtypes);
					cbmechanisms[i].setChecked(localtypes.contains(cbmechanisms[i].getTitle()));
					cbmechanisms[i].setEnabled(true);
				}
			}
		});
	}

	/**
	 * Apply settings to GUI.
	 */
	protected void refreshSettings() {
		cbautoCreate.setEnabled(false);
		cbautoDelete.setEnabled(false);
		spdelay.setEnabled(false);
		cbfast.setEnabled(false);
		
		ThreadSuspendable sus = new ThreadSuspendable();
		AwarenessSettingsData settings = extAcc.scheduleStep(new IComponentStep<AwarenessSettingsData>() {
			@Override
			public IFuture<AwarenessSettingsData> execute(IInternalAccess ia) {
				AwarenessManagementAgent agent = (AwarenessManagementAgent) ia;
				AwarenessSettingsData ret = new AwarenessSettingsData();
				ret.delay = agent.getDelay();
				ret.fast = agent.isFastAwareness();
				ret.autocreate = agent.isAutoCreateProxy();
				ret.autodelete = agent.isAutoDeleteProxy();
				ret.includes = agent.getIncludes();
				ret.excludes = agent.getExcludes();
				return new Future<AwarenessSettingsData>(ret);
			}
		}).get(sus);

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
	}

	/**
	 * Refresh the discovery infos.
	 */
	protected void refreshDiscoveryInfos() {
		extAcc.scheduleStep(new IComponentStep<DiscoveryInfo[]>() {
			@Classname("getDiscoveryInfos")
			public IFuture<DiscoveryInfo[]> execute(IInternalAccess ia) {
				AwarenessManagementAgent agent = (AwarenessManagementAgent) ia;
				return new Future<DiscoveryInfo[]>(agent.getDiscoveryInfos());
			}
		}).addResultListener(new DefaultResultListener<DiscoveryInfo[]>() {

			@Override
			public void exceptionOccurred(Exception exception) {
				// sprefresh.setValue(new Integer(0));
			}

			@Override
			public void resultAvailable(final DiscoveryInfo[] ds) {
				List<Preference> newDisPrefs = new ArrayList<Preference>();
				if (ds.length == 0) {
					final Preference dummyPref = new Preference(infoCat.getContext());
					dummyPref.setTitle("No Discovery Infos available.");
					newDisPrefs.add(dummyPref);
				} else {

					for (int i = 0; i < ds.length; i++) {
						final DiscoveryInfo info = ds[i];
						DiscoveryPreference disPref = new DiscoveryPreference(infoCat.getContext(), info);
						disPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

							@Override
							public boolean onPreferenceChange(Preference preference, Object value) {
								final Boolean create = (Boolean) value;
								final IComponentIdentifier proxy = info.getProxy() != null && info.getProxy().isDone()
										&& info.getProxy().getException() == null ? info.getProxy().get(null) : null;
								if (create && info.getProxy() == null || !create && proxy != null) {
									// setting changed -> create or
									// delete proxy
									extAcc.scheduleStep(new IComponentStep<Void>() {
										@Classname("createDeleteProxy")
										public IFuture<Void> execute(IInternalAccess ia) {
											IFuture<Void> ret;
											AwarenessManagementAgent agent = (AwarenessManagementAgent) ia;
											DiscoveryInfo dif = agent.getDiscoveryInfo(info.getComponentIdentifier());
											if (create && dif != null) {
												final Future<Void> fut = new Future<Void>();
												ret = fut;
												agent.createProxy(dif).addResultListener(
														new ExceptionDelegationResultListener<IComponentIdentifier, Void>(fut) {
															public void customResultAvailable(IComponentIdentifier result) {
																fut.setResult(null);
															}
														});
											} else if (dif != null) {
												ret = agent.deleteProxy(dif);
											} else {
												ret = IFuture.DONE;
											}
											return ret;
										}
									}).addResultListener(new DefaultResultListener<Void>() {
										public void exceptionOccurred(Exception exception) {
											exception.printStackTrace();
											uiHandler.post(new Runnable() {
												@Override
												public void run() {
													Toast.makeText(screen.getContext(), "Could not start/stop Proxy for " + info.getComponentIdentifier(),
															Toast.LENGTH_LONG).show();
												}
											});
										};

										@Override
										public void resultAvailable(Void result) {
											refreshDiscoveryInfos();
										}
									});
								}
								return true;
							}
						});
						newDisPrefs.add(disPref);
					}
				}
				for (Preference disPref : newDisPrefs) {
					infoCat.removeAll();
					infoCat.addPreference(disPref);
				}
			}
		});
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// local variable for XML transfer
		final AwarenessSettingsData settings = new AwarenessSettingsData();
		settings.delay = ((long) spdelay.getInt()) * 1000;
		settings.fast = cbfast.isChecked();
		settings.autocreate = cbautoCreate.isChecked();
		settings.autodelete = cbautoDelete.isChecked();

		switch (PREFKEYS.valueOf(preference.getKey())) {
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
		default:
			break;
		}

		extAcc.scheduleStep(new IComponentStep<Void>() {
			@Classname("applySettings")
			public IFuture<Void> execute(IInternalAccess ia) {
				AwarenessManagementAgent agent = (AwarenessManagementAgent) ia;
				// agent.setAddressInfo(settings.address, settings.port);
				agent.setDelay(settings.delay);
				agent.setFastAwareness(settings.fast);
				agent.setAutoCreateProxy(settings.autocreate);
				agent.setAutoDeleteProxy(settings.autodelete);
				// agent.setIncludes(settings.includes);
				// agent.setExcludes(settings.excludes);
				return IFuture.DONE;
			}
		});
		return true;
	}

}
