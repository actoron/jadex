package jadex.simulation.environment;

import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.clock.ITimedObject;
import jadex.bridge.service.types.clock.ITimer;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.commons.SimplePropertyObject;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.dataview.IDataView;
import jadex.extension.envsupport.environment.AbstractEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceExecutor;
import jadex.extension.envsupport.environment.ISpaceProcess;
import jadex.extension.envsupport.environment.SpaceObject;
import jadex.extension.envsupport.evaluation.ITableDataConsumer;
import jadex.simulation.model.ObservedEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Space executor that connects to a clock service and reacts on time deltas.
 */
// Todo: immediate execution of component actions and percepts?
public class DeltaTimeExecutor4Simulation extends SimplePropertyObject implements ISpaceExecutor {
	// -------- attributes --------

	/** Current time stamp */
	protected long timestamp;

	/** The platform. */
	protected IServiceProvider container;

	/** The clock listener. */
	protected IChangeListener clocklistener;

	/** The tick timer. */
	protected ITimer timer;

	/** The flag indicating that the executor is terminated. */
	protected boolean terminated;

	/** Flag that a step was scheduled. */
	protected boolean scheduled;

	// ---Variables need for simulation evaluation

	/** Dilation counter . */
	protected int dilationCounter = 0;

	/** The IComponentIdentifier of the clientSimulationAgent. **/
	protected IComponentIdentifier clientSimulationAgent;

	/** The HashMap with all ObservedEvents. **/
	private ConcurrentHashMap<Long, ArrayList<ObservedEvent>> allObservedEventsMap;

	// -------- constructors--------

	/**
	 * Creates a new DeltaTimeExecutor
	 * 
	 * @param timecoefficient
	 *            the time coefficient
	 * @param clockservice
	 *            the clock service
	 */
	public DeltaTimeExecutor4Simulation() {
	}

	/**
	 * Creates a new DeltaTimeExecutor
	 * 
	 * @param timecoefficient
	 *            the time coefficient
	 * @param clockservice
	 *            the clock service
	 */
	public DeltaTimeExecutor4Simulation(AbstractEnvironmentSpace space, boolean tick) {
		setProperty("space", space);
		setProperty("tick", new Boolean(tick));
	}

	// -------- methods --------

	/**
	 * Start the space executor.
	 */
	public void start() {
		this.terminated = false;
		allObservedEventsMap = new ConcurrentHashMap<Long, ArrayList<ObservedEvent>>();
		final AbstractEnvironmentSpace space = (AbstractEnvironmentSpace) getProperty("space");
		final boolean tick = getProperty("tick") != null && ((Boolean) getProperty("tick")).booleanValue();
		container = space.getExternalAccess().getServiceProvider();
		
//		clock = (IClockService) SServiceProvider.getServiceUpwards(space.getExternalAccess().getServiceProvider(), IClockService.class).get(new ThreadSuspendable());		
		SServiceProvider.getService(container, IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new DefaultResultListener() {
			public void resultAvailable(Object result) {
				final IClockService clockservice = (IClockService) result;

				timestamp = clockservice.getTime();

				// Start the processes.
				Object[] procs = space.getProcesses().toArray();
				for (int i = 0; i < procs.length; ++i) {
					ISpaceProcess process = (ISpaceProcess) procs[i];
					process.start(clockservice, space);
				}

				final IComponentStep step = new IComponentStep<Void>() {
					public IFuture<Void> execute(IInternalAccess ia) {
						scheduled = false;
						long currenttime = clockservice.getTime();
						long progress = currenttime - timestamp;
						timestamp = currenttime;

						// System.out.println("step: "+timestamp+" "+progress);

						synchronized (space.getMonitor()) {

							// Update the environment objects.
							Object[] objs = space.getSpaceObjectsCollection().toArray();
							for (int i = 0; i < objs.length; i++) {
								SpaceObject obj = (SpaceObject) objs[i];
								obj.updateObject(space, progress, clockservice);
							}

							// Execute the scheduled component actions.
							space.getComponentActionList().executeActions(null, true);

							// Execute the processes.
							Object[] procs = space.getProcesses().toArray();
							for (int i = 0; i < procs.length; ++i) {
								ISpaceProcess process = (ISpaceProcess) procs[i];
								process.execute(clockservice, space);
							}

							// Update the views.
							for (Iterator it = space.getViews().iterator(); it.hasNext();) {
								IDataView view = (IDataView) it.next();
								view.update(space);
							}

							// // Execute the data consumers.
							// for(Iterator it = space.getDataConsumers().iterator(); it.hasNext(); )
							// {
							// ITableDataConsumer consumer = (ITableDataConsumer)it.next();
							// consumer.consumeData(currenttime, clockservice.getTick());
							// }

							dilationCounter += progress;

							if (dilationCounter >= 1000) {

								// consumer.consumeData(currenttime, clockservice.getTick());
								// Hack: "tick" can not be used since the tick is not reseted when a new simulation is started. Instead, it continues to run once the platform and the ClockService have
								// been started.
																
								if (space == null) {
									System.out.println("***space is null");
								}
								if (space.getProperties() == null) {
									System.out.println("***space properties are null");
								}
								if (space.getProperty("REAL_START_TIME_OF_SIMULATION") == null) {
//									System.out.println("***space REAL_START_TIME_OF_SIMULATION is null");
								} else {
									//Make sure the property "REAL_START_TIME_OF_SIMULATION" is set!
									// Execute the data consumers.
									for (Iterator it = space.getDataConsumers().iterator(); it.hasNext();) {
										ITableDataConsumer consumer = (ITableDataConsumer) it.next();
										consumer.consumeData(currenttime, (currenttime - (Long) space.getProperty("REAL_START_TIME_OF_SIMULATION")) / 1000);
									}
								}
								// reset dilationCounter
								dilationCounter = 0;
							}

							// Send the percepts to the components.
							space.getPerceptList().processPercepts(null);
						}

						final IComponentStep step = this;
						if (tick) {
							// System.out.println("tick");
							timer = clockservice.createTickTimer(new ITimedObject() {
								public void timeEventOccurred(long currenttime) {
									if (!terminated) {
										// System.out.println("scheduled step");
										try {
											space.getExternalAccess().scheduleStep(step);
										} catch (ComponentTerminatedException cte) {
										}
									}
								}
							});
						}

						return IFuture.DONE;
					}
				};

				step.execute(null);

				if (!tick) {
					// System.out.println("no tick");
					clocklistener = new IChangeListener() {
						public void changeOccurred(ChangeEvent e) {
							if (!terminated && !scheduled) {
								scheduled = true;
								// System.out.println("scheduled step");
								try {
									space.getExternalAccess().scheduleStep(step);
								} catch (ComponentTerminatedException cte) {
								}
							}
						}
					};
					clockservice.addChangeListener(clocklistener);
				}
			}
		});

	}

	/**
	 * Terminate the space executor.
	 */
	// public synchronized void terminate()
	public void terminate() {
		terminated = true;
		SServiceProvider.getService(container, IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new DefaultResultListener() {
			public void resultAvailable(Object result) {
				IClockService clockservice = (IClockService) result;
				if (clocklistener != null) {
					clockservice.removeChangeListener(clocklistener);
				} else {
					if (timer != null)
						timer.cancel();
				}
			}
		});
	}
}
