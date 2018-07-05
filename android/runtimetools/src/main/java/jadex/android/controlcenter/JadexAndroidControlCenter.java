package jadex.android.controlcenter;

import jadex.android.controlcenter.componentViewer.ComponentViewer;
import jadex.android.controlcenter.settings.AComponentSettings;
import jadex.android.controlcenter.settings.AServiceSettings;
import jadex.android.controlcenter.settings.ISettings;
import jadex.android.service.JadexPlatformService;
import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
import jadex.bridge.service.IService;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.search.ServiceRegistry;
import jadex.bridge.service.search.IServiceRegistry;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.platform.IJadexPlatformBinder;
import jadex.commons.SReflect;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.IntermediateExceptionDelegationResultListener;
import jadex.commons.future.SResultListener;
import jadex.micro.annotation.Binding;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * A Simple Control Center for Jadex-Android. Provides Access to configurable
 * Components and Services.
 * 
 * Can be instantiated by creating an Intent an passing the ComponentIdentifier
 * of the Platform to be configured as Extra with the Key:
 * JadexAndroidControlCenter.EXTRA_PLATFORMID
 */

public class JadexAndroidControlCenter extends OptionsMenuDelegatingPreferenceActivity implements ServiceConnection
{

	protected IJadexPlatformBinder platformService;

	public static final String EXTRA_PLATFORMID = "platformId";

	private PreferenceCategory servicesCat;
	private PreferenceCategory componentsCat;

	/** The platformID to display preferences for */
	private IComponentIdentifier platformId;

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Serializable platformId = getIntent().getSerializableExtra(EXTRA_PLATFORMID);
		if (platformId != null)
		{
			this.platformId = (IComponentIdentifier) platformId;
		}
	}

	public void onServiceConnected(ComponentName name, IBinder service)
	{
		this.platformService = (IJadexPlatformBinder) service;

		if (platformId == null)
		{
			Log.d("jadex-android", "ControlCenter: No platformId passed, using a random started platform...");
			this.platformId = null;
		}
		else
		{
			this.platformId = (IComponentIdentifier) platformId;
		}
		if (!isDisplayingChildScreen())
		{
			refreshControlCenter();
		}
	}

	public void onServiceDisconnected(ComponentName name)
	{
		this.platformService = null;
		onDestroy(); // ?
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		Intent intent = new Intent(this, JadexPlatformService.class);
		bindService(intent, this, BIND_AUTO_CREATE);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		unbindService(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		boolean result = super.onCreateOptionsMenu(menu);
		if (!result)
		{
			// main functionality
			menu.add("Refresh");
			result = true;
		}
		else
		{
		}
		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		boolean result = super.onOptionsItemSelected(item);
		if (!result)
		{
			// main functionality
			refreshControlCenter();
			result = true;
		}
		return result;
	}

	@Override
	protected boolean updateView()
	{
		// Are we displaying Preferences for a child Prefscreen?
		if (!super.updateView())
		{
			// no, so set up main screen
			setPreferenceScreen(createPreferenceHierarchy());
			this.setTitle("Control Center");
		}

		// allow long clicks on items
		getListView().setOnItemLongClickListener(new OnItemLongClickListener()
		{
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
			{
				ListView listView = (ListView) parent;
				ListAdapter listAdapter = listView.getAdapter();
				Object obj = listAdapter.getItem(position);
				if (obj != null && obj instanceof View.OnLongClickListener)
				{
					View.OnLongClickListener longListener = (View.OnLongClickListener) obj;
					return longListener.onLongClick(view);
				}
				return false;
			}
		});
		return true;
	}

	/**
	 * Creates the root preference Hierarchy.
	 * 
	 * @return root {@link PreferenceScreen}.
	 */
	private PreferenceScreen createPreferenceHierarchy()
	{
		final PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);

		servicesCat = new PreferenceCategory(this);
		servicesCat.setTitle("Services");
		root.addPreference(servicesCat);

		componentsCat = new PreferenceCategory(this);
		componentsCat.setTitle("Components");
		root.addPreference(componentsCat);
		
		Preference componentViewer = new Preference(this);
		componentViewer.setTitle("Component Viewer");
		Intent intent = new Intent(this, ComponentViewer.class);
		intent.putExtra(EXTRA_PLATFORMID, (BasicComponentIdentifier) platformId);
		componentViewer.setIntent(intent);
		root.addPreference(componentViewer);

		// refreshControlCenter();
		return root;
	}

	/**
	 * Initiates a lookup for Services and Components which have an available
	 * GUIClass Annotation and list them.
	 */
	private void refreshControlCenter()
	{
		servicesCat.removeAll();
		componentsCat.removeAll();
		resetChildScreens();
		addDummyPrefs();

		if (platformService.isPlatformRunning(platformId))
		{
			IExternalAccess extAcc = platformService.getExternalPlatformAccess(platformId);
			// add all viewable services
			addViewableServices(extAcc);

			// add all viewable components
			addViewableComponents(extAcc);
		}
	}

	private void addViewableServices(final IExternalAccess extAcc)
	{
		extAcc.scheduleStep(new IComponentStep<Void>() {
			@Override
			public IFuture<Void> execute(IInternalAccess ia) {
				final Future<Void> ret = new Future<>();
				IServiceRegistry registry = ServiceRegistry.getRegistry(ia);
				ServiceQuery<IService> query = new ServiceQuery<IService>((Class)null, Binding.SCOPE_PLATFORM, null, null,	ViewableFilter.VIEWABLE_FILTER);
				ISubscriptionIntermediateFuture<IService> services = registry.searchServicesAsync(query);
//				ISubscriptionIntermediateFuture services = registry.searchServicesAsync(null, extAcc.getIdentifier(), Binding.SCOPE_PLATFORM, ViewableFilter.VIEWABLE_FILTER);

				services.addResultListener(new IntermediateExceptionDelegationResultListener<IService, Void>(ret) {

					@Override
					public void resultAvailable(Collection<IService> result) {
						for (IService service : result) {
							intermediateResultAvailable(service);
						}
//						ret.setResult(null);
					}

					@Override
					public void intermediateResultAvailable(IService service) {
						if (addServiceSettings(servicesCat, service)) {
							Preference dummyPref = servicesCat.findPreference("dummy");
							if (dummyPref != null)
								servicesCat.removePreference(dummyPref);
						}
					}

					@Override
					public void finished() {
						ret.setResult(null);
					}
				});

				return ret;
			}
		});
	}

	private void addViewableComponents(IExternalAccess extAcc)
	{
		extAcc.searchService( new ServiceQuery<>( IComponentManagementService.class)).addResultListener(
				new DefaultResultListener<IComponentManagementService>()
				{
					public void resultAvailable(final IComponentManagementService cms)
					{
						cms.getComponentIdentifiers().addResultListener(new DefaultResultListener<IComponentIdentifier[]>()
						{
							public void resultAvailable(IComponentIdentifier[] result)
							{
								for (IComponentIdentifier cid : result)
								{
									cms.getExternalAccess(cid).addResultListener(new DefaultResultListener<IExternalAccess>()
									{
										public void resultAvailable(final IExternalAccess acc)
										{
											Object clid = acc.getModel().getProperty(ViewableFilter.COMPONENTVIEWER_VIEWERCLASS,
													getClassLoader());

											final Class<?> clazz = getGuiClass(clid);

											if (clazz != null)
											{
												runOnUiThread(new Runnable()
												{
													public void run()
													{
														if (addComponentSettings(componentsCat, acc, clazz))
														{
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

	/**
	 * Adds dummy preferences to show that no services/components are found.
	 */
	private void addDummyPrefs()
	{
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

	protected boolean addServiceSettings(PreferenceGroup root, IService service)
	{
		final Object clid = service.getPropertyMap() != null
				? service.getPropertyMap().get(ViewableFilter.COMPONENTVIEWER_VIEWERCLASS)
				: null;
		Class<?> guiClass = getGuiClass(clid);
		if (guiClass != null)
		{
			try
			{
				AServiceSettings settings = (AServiceSettings) guiClass.getConstructor(IService.class).newInstance(service);
				addSettings(root, settings);
				return true;
			}
			catch (InstantiationException e)
			{
				e.printStackTrace();
			}
			catch (IllegalAccessException e)
			{
				e.printStackTrace();
			}
			catch (IllegalArgumentException e)
			{
				e.printStackTrace();
			}
			catch (SecurityException e)
			{
				e.printStackTrace();
			}
			catch (InvocationTargetException e)
			{
				e.printStackTrace();
			}
			catch (NoSuchMethodException e)
			{
				e.printStackTrace();
			}
		}
		return false;
	}

	protected boolean addComponentSettings(PreferenceGroup root, IExternalAccess component, Class<?> guiClass)
	{
		try
		{
			AComponentSettings settings = (AComponentSettings) guiClass.getConstructor(IExternalAccess.class).newInstance(component);
			addSettings(root, settings);
			return true;
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (SecurityException e)
		{
			e.printStackTrace();
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch (InvocationTargetException e)
		{
			e.printStackTrace();
		}
		catch (NoSuchMethodException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	protected void addSettings(PreferenceGroup root, ISettings settings)
	{
		settings.setPlatformId(this.platformId);
		PreferenceScreen screen = createSubPreferenceScreen(settings);
		root.addPreference(screen);
	}

	/**
	 * Returns the class with the given Class name or the first available class
	 * from a Class name Array.
	 * 
	 * @param clid
	 *            Name of the class or Array of names of classes.
	 * @return The first found Class or <code>null</code>.
	 */
	private Class<?> getGuiClass(final Object clid)
	{
		Class<?> guiClass = null;
		if (clid instanceof String)
		{
			Class<?> clazz = SReflect.classForName0((String) clid, getClassLoader());
			if (clazz != null)
			{
				guiClass = clazz;
			}
		}
		else if (clid instanceof String[])
		{
			for (String className : (String[]) clid)
			{
				Class<?> clazz = SReflect.classForName0(className, getClassLoader());
				if (clazz != null)
				{
					guiClass = clazz;
					break;
				}
			}
		}
		return guiClass;
	}
}
