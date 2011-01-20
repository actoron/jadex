package jadex.simulation.environment;

import jadex.application.space.envsupport.dataview.IDataView;
import jadex.application.space.envsupport.environment.AbstractEnvironmentSpace;
import jadex.application.space.envsupport.environment.ISpaceExecutor;
import jadex.application.space.envsupport.environment.ISpaceProcess;
import jadex.application.space.envsupport.environment.SpaceObject;
import jadex.application.space.envsupport.evaluation.ITableDataConsumer;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.commons.SimplePropertyObject;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.service.IServiceProvider;
import jadex.commons.service.RequiredServiceInfo;
import jadex.commons.service.SServiceProvider;
import jadex.commons.service.clock.IClockService;
import jadex.commons.service.clock.ITimedObject;
import jadex.commons.service.clock.ITimer;
import jadex.simulation.model.ObservedEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Space executor that connects to a clock service and reacts on time deltas.
 */
// Todo: immediate execution of component actions and percepts?
public class DeltaTimeExecutor4Simulation extends SimplePropertyObject implements ISpaceExecutor
{
	//-------- attributes --------
	
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
	protected boolean	scheduled;
	
	// ---Variables need for simulation evaluation

	/** Dilation counter . */
	protected int dilationCounter = 0;

	/** The IComponentIdentifier of the clientSimulationAgent. **/
	protected IComponentIdentifier clientSimulationAgent;

	/** The HashMap with all ObservedEvents. **/
	private ConcurrentHashMap<Long, ArrayList<ObservedEvent>> allObservedEventsMap;
	
	//-------- constructors--------
	
	/**
	 * Creates a new DeltaTimeExecutor
	 * @param timecoefficient the time coefficient
	 * @param clockservice the clock service
	 */
	public DeltaTimeExecutor4Simulation()
	{
	}
	
	/**
	 * Creates a new DeltaTimeExecutor
	 * @param timecoefficient the time coefficient
	 * @param clockservice the clock service
	 */
	public DeltaTimeExecutor4Simulation(AbstractEnvironmentSpace space, boolean tick)
	{
		setProperty("space", space);
		setProperty("tick", new Boolean(tick));
	}
	
	//-------- methods --------
	
	/**
	 *  Start the space executor.
	 */
	public void start()
	{	
		this.terminated = false;
		allObservedEventsMap = new ConcurrentHashMap<Long, ArrayList<ObservedEvent>>();
		final AbstractEnvironmentSpace space = (AbstractEnvironmentSpace)getProperty("space");
		final boolean tick = getProperty("tick")!=null && ((Boolean)getProperty("tick")).booleanValue();
		this.container	= space.getContext().getServiceProvider();
		
		SServiceProvider.getService(container, IClockService.class,RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				final IClockService clockservice = (IClockService)result;
				
				timestamp = clockservice.getTime();
				
				// Start the processes.
				Object[] procs = space.getProcesses().toArray();
				for(int i = 0; i < procs.length; ++i)
				{
					ISpaceProcess process = (ISpaceProcess) procs[i];
					process.start(clockservice, space);
				}

				final IComponentStep step = new IComponentStep()
				{
					public Object execute(IInternalAccess ia)
					{
						scheduled	= false;
						long currenttime = clockservice.getTime();
						long progress = currenttime - timestamp;
						timestamp = currenttime;

//						System.out.println("step: "+timestamp+" "+progress);
			
						synchronized(space.getMonitor())
						{
							
							// Update the environment objects.
							Object[] objs = space.getSpaceObjectsCollection().toArray();
							for(int i=0; i<objs.length; i++)
							{
								SpaceObject obj = (SpaceObject)objs[i];
								obj.updateObject(space, progress, clockservice);
							}
							
							// Execute the scheduled component actions.
							space.getComponentActionList().executeActions(null, true);
							
							// Execute the processes.
							Object[] procs = space.getProcesses().toArray();
							for(int i = 0; i < procs.length; ++i)
							{
								ISpaceProcess process = (ISpaceProcess) procs[i];
								process.execute(clockservice, space);
							}
							
							// Update the views.
							for(Iterator it = space.getViews().iterator(); it.hasNext(); )
							{
								IDataView view = (IDataView)it.next();
								view.update(space);
							}

//							// Execute the data consumers.
//							for(Iterator it = space.getDataConsumers().iterator(); it.hasNext(); )
//							{
//								ITableDataConsumer consumer = (ITableDataConsumer)it.next();
//								consumer.consumeData(currenttime, clockservice.getTick());
//							}
							
							dilationCounter += progress;

							if (dilationCounter >= 1000) {
								
								
								
								
								
								
								
								// Execute the data consumers.
								for (Iterator it = space.getDataConsumers().iterator(); it.hasNext();) {
									ITableDataConsumer consumer = (ITableDataConsumer) it.next();
//									consumer.consumeData(currenttime, clockservice.getTick());
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
//								final String experimentId = (String) space.getContext().getArguments().get(Constants.EXPERIMENT_ID);
//								final String appName = space.getContext().getComponentIdentifier().getLocalName();
//								final ArrayList<ObservedEvent> observedEvents = new ArrayList<ObservedEvent>();
		//
//								// System.out.println("#Executor# ID: " + appName +
//								// " - Dilation: " + dilationCounter + " timestamp: " +
//								// timestamp);
		//
//								// Observe elements: ISpaceObjects, BDI-Agents,
//								// MicroAgents
//								// Handle BDI-Agents separate due asyn call
//								// TODO: Differentiate between periodical and onChange
//								// Evaluation
		//
//								for (final Observer obs : observerList) {
		//
//									if (obs.getData().getObjectSource().getType().equals(Constants.BDI_AGENT)) {
//										String agentType = obs.getData().getObjectSource().getName();
		//
//										// for(IComponentIdentifier agentIdentifier :
//										// space.getAgents()){
//										// for (IComponentIdentifier agentIdentifier :
//										// space.getAgents()) {
//										// if
//										// (space.getContext().getComponentType(agentIdentifier).equals(agentType))
//										// {
//										IComponentIdentifier agentIdentifier = AgentMethods.getIComponentIdentifier(space, agentType);
//										// if (agentIdentifier != null) {
//										// TODO: Apply / Check if filter has been set on
//										// this observer data
//										// System.out.println("#DeltaTime4Exec# Starting get result for BDIAgent.");
		//
//										IFuture fut = ((IComponentManagementService) space.getContext().getServiceContainer().getService(IComponentManagementService.class)).getExternalAccess(agentIdentifier);
//										fut.addResultListener(new IResultListener() {
		//
//											@Override
//											public void resultAvailable(Object source, Object result) {
//												ExternalAccessFlyweight exta = (ExternalAccessFlyweight) result;
//												// Get Fact from Beliefbase
//												// TODO: Not only for Strings meaning:
//												// read the right class from the
//												// data-field!
//												String currentValue = exta.getBeliefbase().getBelief(obs.getData().getElementSource().getName()).getFact().toString();
//												// System.out.println("MayValue: " +
//												// currentValue);
//												observedEvents.add(new ObservedEvent(appName, experimentId, timestamp, obs.getData(), currentValue));
		//
//											}
		//
//											@Override
//											public void exceptionOccurred(Object source, Exception exception) {
//												// TODO Auto-generated method stub
//											}
//										});
		//
//										// Observe ISpaceObject
//									} else if (obs.getData().getObjectSource().getType().equals(Constants.ISPACE_OBJECT)) {
//										ISpaceObject[] targets = space.getSpaceObjectsByType(obs.getData().getObjectSource().getName());
//										// TODO: Handle multiple occurrences of that
//										// ISpaceObject
//										String currentValue = targets[0].getProperty(obs.getData().getElementSource().getName()).toString();
//										observedEvents.add(new ObservedEvent(appName, experimentId, timestamp, obs.getData(), currentValue));
		//
//									} else {
//										System.err.println("#DeltaTimeExecutor4Simulation# Error on setting type of ObjectSource " + simConf);
//									}
		//
//								}
		//
//								// // write result to beliefbase of client simulation
//								// agent
//								// addToBeliefBase(space, observedEvents, timestamp);
//								
//								
//								// write result to hashmap that holds all events - HAS
//								// to happen outside the for-loop to get the values for
//								// all observer at that timestamp
//								allObservedEventsMap.put(timestamp, observedEvents);
//								space.setProperty("observedEvents", allObservedEventsMap);
		//
								// reset dilationCounter
								dilationCounter = 0;
							}
							
							// Send the percepts to the components.
							space.getPerceptList().processPercepts(null);
						}
						
						final IComponentStep step = this;
						if(tick)
						{
//							System.out.println("tick");
							timer = clockservice.createTickTimer(new ITimedObject()
							{
								public void timeEventOccurred(long currenttime)
								{
									if(!terminated)
									{
//										System.out.println("scheduled step");
										try
										{
											space.getContext().scheduleStep(step);
										}
										catch(ComponentTerminatedException cte)
										{
										}
									}
								}
							});
						}
						
						return null;
					}
				};

				step.execute(null);

				if(!tick)
				{
//					System.out.println("no tick");
					clocklistener = new IChangeListener()
					{
						public void changeOccurred(ChangeEvent e)
						{
							if(!terminated && !scheduled)
							{
								scheduled	= true;
//								System.out.println("scheduled step");
								try
								{
									space.getContext().scheduleStep(step);
								}
								catch(ComponentTerminatedException cte)
								{
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
	 *  Terminate the space executor.
	 */
//	public synchronized void terminate()
	public void terminate()
	{
		terminated = true;
		SServiceProvider.getService(container, IClockService.class,RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				IClockService clockservice = (IClockService)result;
				if(clocklistener!=null)
				{
					clockservice.removeChangeListener(clocklistener);
				}
				else
				{
					if(timer!=null)
						timer.cancel();
				}
			}
		});
	}
	
//	private void addToBeliefBase(final AbstractEnvironmentSpace space, final ArrayList<ObservedEvent> observedEvents, final long timestamp) {
//		if (clientSimulationAgent == null) {
//			clientSimulationAgent = AgentMethods.getIComponentIdentifier(space, Constants.CLIENT_SIMULATION_AGENT);
//		}
//	
//		IFuture fut = ((IComponentManagementService) space.getContext().getServiceContainer().getService(IComponentManagementService.class)).getExternalAccess(clientSimulationAgent);
//		IFuture fut = ((IComponentManagementService)SServiceProvider.getService(getScope().getServiceProvider(), IComponentManagementService.class)).getExternalAccess(clientSimulationAgent);
//		fut.addResultListener(new IResultListener() {
//
//			@Override
//			public void resultAvailable(Object source, Object result) {
//				ExternalAccessFlyweight exta = (ExternalAccessFlyweight) result;
//				HashMap resultsMap = (HashMap) exta.getBeliefbase().getBelief(Constants.OBSERVED_EVENTS_MAP).getFact();
//				resultsMap.put(timestamp, observedEvents);
//
//				exta.getBeliefbase().getBelief(Constants.OBSERVED_EVENTS_MAP).setFact(resultsMap);
//			}
//			@Override
//			public void exceptionOccurred(Object source, Exception exception) {
//				// TODO Auto-generated method stub
//			}
//		});
//	}
}
