package jadex.android.controlcenter.settings;

import jadex.android.controlcenter.preference.JadexDoublePreference;
import jadex.android.controlcenter.preference.JadexIntegerPreference;
import jadex.android.service.JadexPlatformManager;
import jadex.base.SRemoteClock.ClockState;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.RemoteChangeListenerHandler;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.clock.IClock;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.simulation.ISimulationService;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.commons.IRemoteChangeListener;
import jadex.commons.TimeFormat;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import android.os.Handler;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Settings Implementation for {@link ISimulationService}.
 */
public class SimulationSettings extends AServiceSettings {

	/**
	 * The SimulationService
	 */
	private ISimulationService simService;
	
	/**
	 * Formatter
	 */
	private SimpleDateFormat dateformat;
	
	/**
	 * Formatter
	 */
	private DecimalFormat numberformat;

	private int timemode;

	/**
	 * Handler to change UI objects from non-ui threads.
	 */
	private Handler uiHandler;
	private ClockState lastClockState;

	/** The last clocktype. */
	private String lastclocktype;
	
	/** Id of the platform to be configured. */
	private IComponentIdentifier platformId;

	// UI members
	private PreferenceCategory clockCat;
	private ListPreference executionMode;
	private Preference startTime;
	private JadexIntegerPreference tickSize;
	private JadexDoublePreference dilation;
	private Preference modelTime;
	private Preference tickCount;
	private Preference sysTime;
	private PreferenceCategory controlCat;
	private Preference ctrl_pause;
	private Preference ctrl_play;

	public SimulationSettings(IService service) {
		super(service);
		simService = (ISimulationService) service;
		dateformat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss S");
		numberformat = new DecimalFormat("#######0.####");
		timemode = 2;
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Refresh");
		return false;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// refreshClockSettings();
		return true;
	}

	public void onDestroy()
	{
	}
	
	public void onOptionsMenuClosed(Menu menu)
	{
	}
	
	protected void createPreferenceHierarchy(PreferenceScreen screen) {
		uiHandler = new Handler();

		clockCat = new PreferenceCategory(screen.getContext());
		clockCat.setTitle("Clock Settings");
		screen.addPreference(clockCat);

		executionMode = new ListPreference(screen.getContext());
		executionMode.setTitle("Execution mode");
		executionMode.setEntries(new String[] { "System", "Continuous", "Time Stepped", "Event Driven" });
		executionMode.setEnabled(false);
		clockCat.addPreference(executionMode);

		startTime = new Preference(screen.getContext());
		startTime.setTitle("Start time");
		startTime.setEnabled(false);
		clockCat.addPreference(startTime);

		tickSize = new JadexIntegerPreference(screen.getContext());
		tickSize.setTitle("Tick size");
		tickSize.setEnabled(false);
		clockCat.addPreference(tickSize);

		dilation = new JadexDoublePreference(screen.getContext());
		dilation.setTitle("Dilation");
		dilation.setEnabled(false);
		clockCat.addPreference(dilation);

		modelTime = new Preference(screen.getContext());
		modelTime.setTitle("Model time");
		modelTime.setEnabled(false);
		clockCat.addPreference(modelTime);

		tickCount = new Preference(screen.getContext());
		tickCount.setTitle("Tick count");
		tickCount.setEnabled(false);
		clockCat.addPreference(tickCount);

		sysTime = new Preference(screen.getContext());
		sysTime.setTitle("System time");
		sysTime.setEnabled(false);
		clockCat.addPreference(sysTime);

		controlCat = new PreferenceCategory(screen.getContext());
		controlCat.setTitle("Execution Control");
		screen.addPreference(controlCat);

		ctrl_pause = new Preference(screen.getContext());
		ctrl_pause.setTitle("Pause");
		ctrl_pause.setEnabled(false);

		ctrl_play = new Preference(screen.getContext());
		ctrl_play.setTitle("Start");
		ctrl_play.setEnabled(false);

		controlCat.addPreference(ctrl_play);
		controlCat.addPreference(ctrl_pause);
		
		
		
		ctrl_pause.setOnPreferenceClickListener(pauseClickListener);
		ctrl_play.setOnPreferenceClickListener(playClickListener);

		getProperties();
		refreshClockSettings();
	}
	
	private OnPreferenceClickListener pauseClickListener = new OnPreferenceClickListener() {
		
		public boolean onPreferenceClick(Preference preference) {
			
			return true;
		}
	};
	
	private OnPreferenceClickListener playClickListener = new OnPreferenceClickListener() {
		
		public boolean onPreferenceClick(Preference preference) {
			
			return true;
		}
	};

	/**
	 * Update the view.
	 */
	public void updateClockView() {
		if (lastClockState != null) {
			updateClockView(lastClockState);
		}
	}

	/**
	 * Update the view.
	 */
	public void updateClockView(final ClockState state) {
		lastClockState = state;

		uiHandler.post(new Runnable() {

			public void run() {
				executionMode.setEnabled(state.changeallowed);
				startTime.setSummary(formatTime(state.starttime));
				tickSize.setValue((int) state.delta);
				tickCount.setSummary("" + state.tick);
				sysTime.setSummary("" + formatTime(System.currentTimeMillis()));
				modelTime.setSummary("" + formatTime(state.time));
				dilation.setValue(state.dilation);

				if (lastclocktype == null || !lastclocktype.equals(state.type)) {
					lastclocktype = state.type;
					if (lastclocktype.equals(IClock.TYPE_SYSTEM)) {
						executionMode.setValue("System");
					} else if (lastclocktype.equals(IClock.TYPE_CONTINUOUS)) {
						executionMode.setValue("Continuous");
					} else if (lastclocktype.equals(IClock.TYPE_TIME_DRIVEN)) {
						executionMode.setValue("Time Stepped");
					} else if (lastclocktype.equals(IClock.TYPE_EVENT_DRIVEN)) {
						executionMode.setValue("Event Driven");
					}
				}

				if (lastclocktype.equals(IClock.TYPE_CONTINUOUS)) {
					dilation.setEnabled(true);
				} else {
					dilation.setEnabled(false);
				}
			}
		});
	}

	private void getProperties() {
		// TODO: get timeMode preference from settings service
		getComponentForService().addResultListener(new DefaultResultListener<IExternalAccess>() {

			public void resultAvailable(IExternalAccess result) {

				result.scheduleStep(new IComponentStep<Void>() {

					public IFuture<Void> execute(IInternalAccess ia) {
						Future<Void> ret = new Future<Void>();
						// @Override
						// public void resultAvailable(Properties ps) {
						// final int timemode = ps.getProperty("timemode")!=null
						// ? ps.getIntProperty("timemode") : 2;
						// uiHandler.post(new Runnable() {
						//
						// @Override
						// public void run() {
						// SimulationSettings.this.timemode = timemode;
						// }
						// });
						// updateView();
						// }
						return ret;
					}
				});
			}
		});
	}

	private void refreshClockSettings() {

		final IRemoteChangeListener rcl = new IRemoteChangeListener() {
			public IFuture changeOccurred(ChangeEvent event) {
				handleEvent(event);
				return IFuture.DONE;
			}

			public void handleEvent(ChangeEvent event) {
				if (RemoteChangeListenerHandler.EVENT_BULK.equals(event.getType())) {
					Collection events = (Collection) event.getValue();
					for (Iterator it = events.iterator(); it.hasNext();) {
						handleEvent((ChangeEvent) it.next());
					}
				} else {
					updateClockView((ClockState) event.getValue());
				}
			}
		};

		final String id = "ClockPanel" + SimulationSettings.this.hashCode() + "@" + simService.getClockService().getServiceIdentifier();

		getComponentForService().addResultListener(new DefaultResultListener<IExternalAccess>() {

			public void resultAvailable(IExternalAccess result) {

				result.scheduleStep(new IComponentStep<Void>() {

					public IFuture<Void> execute(IInternalAccess ia) {
						RemoteClockChangeListener rccl = new RemoteClockChangeListener(id, ia, rcl, simService);
						Future<Void> ret = new Future<Void>();
						simService.addChangeListener(rccl);
						simService.getClockService().addChangeListener(rccl);

						// Initial event.
						rccl.changeOccurred(null);
						return ret;
					}
				});
			}
		});

	}

	/**
	 * Get the host component of a service.
	 */
	public IFuture<IExternalAccess> getComponentForService() {
		final Future<IExternalAccess> ret = new Future<IExternalAccess>();

		JadexPlatformManager.getInstance().getExternalPlatformAccess(platformId).searchService(new ServiceQuery<>(
			IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)).addResultListener(
				new ExceptionDelegationResultListener<IComponentManagementService, IExternalAccess>(ret) {
					public void customResultAvailable(IComponentManagementService cms) {
						// IComponentManagementService cms =
						// (IComponentManagementService)result;
						cms.getExternalAccess((IComponentIdentifier) ((IService)simService).getServiceIdentifier().getProviderId()).addResultListener(
								new DelegationResultListener<IExternalAccess>(ret));
					}
				});

		return ret;
	}

	/**
	 * Format a time.
	 * 
	 * @return The formatted time string.
	 */
	public String formatTime(long time) {
		String ret;

		if (timemode == 0)
			ret = "" + time;
		else if (timemode == 1)
			ret = TimeFormat.format(time);
		else
			ret = dateformat.format(new Date(time));

		return ret;
	}
	
	public void setPlatformId(IComponentIdentifier platformId)
	{
		this.platformId = platformId;
	}

	/**
	 * The remote clock change listener.
	 */
	public static class RemoteClockChangeListener extends RemoteChangeListenerHandler implements IChangeListener {
		// -------- attributes --------

		/** The simulation service. */
		protected ISimulationService simservice;

		// -------- constructors --------

		/**
		 * Create a BPMN listener.
		 */
		public RemoteClockChangeListener(String id, IInternalAccess instance, IRemoteChangeListener rcl, ISimulationService simservice) {
			super(id, instance, rcl);
			this.simservice = simservice;
		}

		// -------- IChangeListener interface --------

		/**
		 * Called when the process executes.
		 */
		public void changeOccurred(ChangeEvent event) {
			// Code in component result listener as clock runs on its own
			// thread.
			
			simservice.isExecuting().addResultListener(instance.getFeature(IExecutionFeature.class).createResultListener(new IResultListener<Boolean>() {
				public void resultAvailable(Boolean result) {
					try {
						boolean executing = result.booleanValue();
						IClockService cs = simservice.getClockService();
						elementChanged("clock",
								new ClockState(cs.getClockType(), cs.getTime(), cs.getTick(), cs.getStarttime(), cs.getDelta(),
										IClock.TYPE_CONTINUOUS.equals(cs.getClockType()) ? cs.getDilation() : 0, !executing));
					} catch (Exception e) {
						exceptionOccurred(e);
					}
				}

				public void exceptionOccurred(Exception exception) {
					dispose();
				}
			}));
		}

		/**
		 * Remove local listeners.
		 */
		protected void dispose() {
			super.dispose();
			try {
				simservice.removeChangeListener(this);
				simservice.getClockService().removeChangeListener(this);
			} catch (Exception e) {

			}
			// System.out.println("dispose: "+id);
		}
	}

}
