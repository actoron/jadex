package jadex.simulation.environment;

import jadex.application.space.envsupport.dataview.IDataView;
import jadex.application.space.envsupport.environment.AbstractEnvironmentSpace;
import jadex.application.space.envsupport.environment.ISpaceExecutor;
import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.application.space.envsupport.environment.ISpaceProcess;
import jadex.application.space.envsupport.environment.SpaceObject;
import jadex.application.space.envsupport.evaluation.ITableDataConsumer;
import jadex.bdi.runtime.impl.ExternalAccessFlyweight;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentListener;
import jadex.bridge.IComponentManagementService;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.commons.IFuture;
import jadex.commons.SimplePropertyObject;
import jadex.commons.concurrent.IExecutable;
import jadex.commons.concurrent.IResultListener;
import jadex.service.IServiceContainer;
import jadex.service.clock.IClockService;
import jadex.service.clock.ITimedObject;
import jadex.service.clock.ITimer;
import jadex.service.execution.IExecutionService;
import jadex.simulation.helper.AgentMethods;
import jadex.simulation.helper.Constants;
import jadex.simulation.model.ObservedEvent;
import jadex.simulation.model.Observer;
import jadex.simulation.model.SimulationConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Space executor that connects to a clock service and reacts on time deltas. It
 * is specially designed for the automated simulation execution, since it
 * observes and collects data according to the simulation configuration.
 */
// Todo: immediate execution of agent actions and percepts?
public class DeltaTimeExecutor4Simulation extends SimplePropertyObject implements ISpaceExecutor {
	// -------- attributes --------

	/** Current time stamp */
	protected long timestamp;

	/** The platform. */
	protected IServiceContainer container;

	/** The clock listener. */
	protected IChangeListener clocklistener;

	/** The tick timer. */
	protected ITimer timer;

	/** The flag indicating that the executor is terminated. */
	protected boolean terminated;

	// ---Variables need for simulation evaluation

	/** Dilation counter . */
	protected int dilationCounter = 0;

	/** The IComponentIdentifier of the clientSimulationAgent. **/
	protected IComponentIdentifier clientSimulationAgent;

	/** The HashMap with all ObservedEvents. **/
	private ConcurrentHashMap<Long, ArrayList<ObservedEvent>> allObservedEventsMap;

	// /** Thread Pool to observe agents.*/
	// protected ExecutorService executor = Executors.newCachedThreadPool();

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
		this.container = space.getContext().getServiceContainer();
		final IClockService clockservice = (IClockService) container.getService(IClockService.class);
		final IExecutionService exeservice = (IExecutionService) container.getService(IExecutionService.class);

		// needed for observation of simulation experiment
//		final SimulationConfiguration simConf = (SimulationConfiguration) ((Map) space.getContext().getArguments().get("Simulation_Facts_For_Client")).get("Simulation_Facts_For_Client");
//		final ArrayList<Observer> observerList = (ArrayList<Observer>) simConf.getObservers().getObserver();

		final IExecutable executable = new IExecutable() {
			public boolean execute() {
				long currenttime = clockservice.getTime();
				long progress = currenttime - timestamp;
				timestamp = currenttime;

				synchronized (space.getMonitor()) {
					// Update the environment objects.
					Object[] objs = space.getSpaceObjectsCollection().toArray();
					for (int i = 0; i < objs.length; i++) {
						SpaceObject obj = (SpaceObject) objs[i];
						obj.updateObject(space, progress, clockservice);
					}

					// Execute the scheduled agent actions.
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

//					// Execute the data consumers.
//					for (Iterator it = space.getDataConsumers().iterator(); it.hasNext();) {
//						ITableDataConsumer consumer = (ITableDataConsumer) it.next();
////						consumer.consumeData(currenttime, clockservice.getTick());
//						//Hack: "tick" can not be used since the tick is not reseted when a new simulation is started. Instead, it continues to run once the platform and the ClockService have been started.
//						if(space == null){
//							System.out.println("***space is null");
//						}
//						if(space.getProperties() == null){
//							System.out.println("***space properties are null");
//						}
//						if(space.getProperty("REAL_START_TIME_OF_SIMULATION") == null){
//							System.out.println("***space REAL_START_TIME_OF_SIMULATION is null");
//						}
//						consumer.consumeData(currenttime, (currenttime - (Long)space.getProperty("REAL_START_TIME_OF_SIMULATION"))/1000);
//					}

					dilationCounter += progress;

					if (dilationCounter >= 1000) {
						
						
						
						
						
						
						
						// Execute the data consumers.
						for (Iterator it = space.getDataConsumers().iterator(); it.hasNext();) {
							ITableDataConsumer consumer = (ITableDataConsumer) it.next();
//							consumer.consumeData(currenttime, clockservice.getTick());
							//Hack: "tick" can not be used since the tick is not reseted when a new simulation is started. Instead, it continues to run once the platform and the ClockService have been started.
							if(space == null){
								System.out.println("***space is null");
							}
							if(space.getProperties() == null){
								System.out.println("***space properties are null");
							}
							if(space.getProperty("REAL_START_TIME_OF_SIMULATION") == null){
								System.out.println("***space REAL_START_TIME_OF_SIMULATION is null");
							}
							consumer.consumeData(currenttime, (currenttime - (Long)space.getProperty("REAL_START_TIME_OF_SIMULATION"))/1000);
						}
						
						
						
						
						
						
						
						
						
						
						
						
						
//						
//						final String experimentId = (String) space.getContext().getArguments().get(Constants.EXPERIMENT_ID);
//						final String appName = space.getContext().getComponentIdentifier().getLocalName();
//						final ArrayList<ObservedEvent> observedEvents = new ArrayList<ObservedEvent>();
//
//						// System.out.println("#Executor# ID: " + appName +
//						// " - Dilation: " + dilationCounter + " timestamp: " +
//						// timestamp);
//
//						// Observe elements: ISpaceObjects, BDI-Agents,
//						// MicroAgents
//						// Handle BDI-Agents separate due asyn call
//						// TODO: Differentiate between periodical and onChange
//						// Evaluation
//
//						for (final Observer obs : observerList) {
//
//							if (obs.getData().getObjectSource().getType().equals(Constants.BDI_AGENT)) {
//								String agentType = obs.getData().getObjectSource().getName();
//
//								// for(IComponentIdentifier agentIdentifier :
//								// space.getAgents()){
//								// for (IComponentIdentifier agentIdentifier :
//								// space.getAgents()) {
//								// if
//								// (space.getContext().getComponentType(agentIdentifier).equals(agentType))
//								// {
//								IComponentIdentifier agentIdentifier = AgentMethods.getIComponentIdentifier(space, agentType);
//								// if (agentIdentifier != null) {
//								// TODO: Apply / Check if filter has been set on
//								// this observer data
//								// System.out.println("#DeltaTime4Exec# Starting get result for BDIAgent.");
//
//								IFuture fut = ((IComponentManagementService) space.getContext().getServiceContainer().getService(IComponentManagementService.class)).getExternalAccess(agentIdentifier);
//								fut.addResultListener(new IResultListener() {
//
//									@Override
//									public void resultAvailable(Object source, Object result) {
//										ExternalAccessFlyweight exta = (ExternalAccessFlyweight) result;
//										// Get Fact from Beliefbase
//										// TODO: Not only for Strings meaning:
//										// read the right class from the
//										// data-field!
//										String currentValue = exta.getBeliefbase().getBelief(obs.getData().getElementSource().getName()).getFact().toString();
//										// System.out.println("MayValue: " +
//										// currentValue);
//										observedEvents.add(new ObservedEvent(appName, experimentId, timestamp, obs.getData(), currentValue));
//
//									}
//
//									@Override
//									public void exceptionOccurred(Object source, Exception exception) {
//										// TODO Auto-generated method stub
//									}
//								});
//
//								// Observe ISpaceObject
//							} else if (obs.getData().getObjectSource().getType().equals(Constants.ISPACE_OBJECT)) {
//								ISpaceObject[] targets = space.getSpaceObjectsByType(obs.getData().getObjectSource().getName());
//								// TODO: Handle multiple occurrences of that
//								// ISpaceObject
//								String currentValue = targets[0].getProperty(obs.getData().getElementSource().getName()).toString();
//								observedEvents.add(new ObservedEvent(appName, experimentId, timestamp, obs.getData(), currentValue));
//
//							} else {
//								System.err.println("#DeltaTimeExecutor4Simulation# Error on setting type of ObjectSource " + simConf);
//							}
//
//						}
//
//						// // write result to beliefbase of client simulation
//						// agent
//						// addToBeliefBase(space, observedEvents, timestamp);
//						
//						
//						// write result to hashmap that holds all events - HAS
//						// to happen outside the for-loop to get the values for
//						// all observer at that timestamp
//						allObservedEventsMap.put(timestamp, observedEvents);
//						space.setProperty("observedEvents", allObservedEventsMap);
//
						// reset dilationCounter
						dilationCounter = 0;
					}

					// Send the percepts to the agents.
					space.getPerceptList().processPercepts(null);
				}
				return false;
			}
		};

		this.timestamp = clockservice.getTime();

		// Start the processes.
		Object[] procs = space.getProcesses().toArray();
		for (int i = 0; i < procs.length; ++i) {
			ISpaceProcess process = (ISpaceProcess) procs[i];
			process.start(clockservice, space);
		}

		if (tick) {
			timer = clockservice.createTickTimer(new ITimedObject() {
				public void timeEventOccurred(long currenttime) {
					// boolean t = false;
					// synchronized(DeltaTimeExecutor.this)
					// {
					// t = terminated;
					// }
					// if(t)
					// {
					if (!terminated) {
						exeservice.execute(executable);
						timer = clockservice.createTickTimer(this);
					}
					// }
				}
			});
		} else {
			clocklistener = new IChangeListener() {
				public void changeOccurred(ChangeEvent e) {
					exeservice.execute(executable);
				}
			};
			clockservice.addChangeListener(clocklistener);
		}

		// Add the executor as context listener on the application.
		IComponentManagementService ces = (IComponentManagementService) container.getService(IComponentManagementService.class);
		ces.addComponentListener(space.getContext().getComponentIdentifier(), new IComponentListener() {

			public void componentRemoved(IComponentDescription desc, Map results) {
				terminate();
			}

			public void componentChanged(IComponentDescription desc) {
			}

			public void componentAdded(IComponentDescription desc) {
			}
		});
	}

	/**
	 * Terminate the space executor.
	 */
	// public synchronized void terminate()
	public void terminate() {		
		IClockService clockservice = (IClockService) container.getService(IClockService.class);

		if (clocklistener != null) {
			clockservice.removeChangeListener(clocklistener);
		} else {
			terminated = true;
			if (timer != null)
				timer.cancel();
		}
	}

	private void addToBeliefBase(final AbstractEnvironmentSpace space, final ArrayList<ObservedEvent> observedEvents, final long timestamp) {
		if (clientSimulationAgent == null) {
			clientSimulationAgent = AgentMethods.getIComponentIdentifier(space, Constants.CLIENT_SIMULATION_AGENT);
		}

		// final ArrayList<ObservedEvent> observedEventsCopy = new
		// ArrayList<ObservedEvent>();
		// Collections.copy(observedEvents, observedEventsCopy);

		// ((IComponentManagementService)
		// space.getContext().getServiceContainer().getService(IComponentManagementService.class)).getExternalAccess(clientSimulationAgent,
		// new IResultListener() {
		IFuture fut = ((IComponentManagementService) space.getContext().getServiceContainer().getService(IComponentManagementService.class)).getExternalAccess(clientSimulationAgent);
		fut.addResultListener(new IResultListener() {

			@Override
			public void resultAvailable(Object source, Object result) {
				ExternalAccessFlyweight exta = (ExternalAccessFlyweight) result;
				// System.out.println("#ObserveBDIAgentThread# Got exta ---> " +
				// exta.getAgentName() + timestamp);
				// System.out.println("bels: "+exta.getBeliefbase().getBelief(Constants.OBSERVED_EVENTS_MAP));
				HashMap resultsMap = (HashMap) exta.getBeliefbase().getBelief(Constants.OBSERVED_EVENTS_MAP).getFact();
				resultsMap.put(timestamp, observedEvents);
				// System.out.println("TMP: " + observedEvents.size());
				exta.getBeliefbase().getBelief(Constants.OBSERVED_EVENTS_MAP).setFact(resultsMap);
				// ((Map)
				// space.getContext().getArguments()).put(Constants.OBSERVED_EVENTS_MAP,
				// resultsMap);
				//				
				//				
				//				
				// observedEvents.add(new ObservedEvent(appName, experimentId,
				// timestamp, obs.getData(), exta.getAgentName()));

			}

			@Override
			public void exceptionOccurred(Object source, Exception exception) {
				// TODO Auto-generated method stub
			}
		});
	}

//	public ConcurrentHashMap<Long, ArrayList<ObservedEvent>> getAllObservedValues(){
//		return this.allObservedEventsMap;
//	}
	// private void addToSpace(AbstractEnvironmentSpace space,
	// ArrayList<ObservedEvent> observedEvents, long timestamp) {
	// HashMap<Long, ArrayList<ObservedEvent>> results;
	//
	// if (space.getProperty(Constants.OBSERVED_EVENTS_MAP) == null) {
	// results = new HashMap<Long, ArrayList<ObservedEvent>>();
	// } else {
	// results = (HashMap<Long, ArrayList<ObservedEvent>>)
	// space.getProperty(Constants.OBSERVED_EVENTS_MAP);
	// }
	// results.put(new Long(timestamp), observedEvents);
	// space.setProperty(Constants.OBSERVED_EVENTS_MAP, results);
	// }
}