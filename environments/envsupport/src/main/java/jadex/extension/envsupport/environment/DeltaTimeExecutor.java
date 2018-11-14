package jadex.extension.envsupport.environment;

import java.util.Iterator;

import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.clock.ITimedObject;
import jadex.bridge.service.types.clock.ITimer;
import jadex.bridge.service.types.execution.IExecutionService;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.commons.SimplePropertyObject;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.dataview.IDataView;
import jadex.extension.envsupport.evaluation.ITableDataConsumer;

/**
 * Space executor that connects to a clock service and reacts on time deltas.
 */
// Todo: immediate execution of component actions and percepts?
public class DeltaTimeExecutor extends SimplePropertyObject implements ISpaceExecutor
{
	//-------- attributes --------
	
	/** Current time stamp */
	protected long timestamp;
	
	/** The platform. */
	protected IExternalAccess container;
	
	/** The clock listener. */
	protected IChangeListener clocklistener;
	
	/** The tick timer. */
	protected ITimer timer;
	
	/** The flag indicating that the executor is terminated. */
	protected boolean terminated;
	
	/** Flag that a step was scheduled. */
	protected boolean	scheduled;
	
	//-------- constructors--------
	
	/**
	 * Creates a new DeltaTimeExecutor
	 * @param timecoefficient the time coefficient
	 * @param clockservice the clock service
	 */
	public DeltaTimeExecutor()
	{
	}
	
	/**
	 * Creates a new DeltaTimeExecutor
	 * @param timecoefficient the time coefficient
	 * @param clockservice the clock service
	 */
	public DeltaTimeExecutor(AbstractEnvironmentSpace space, boolean tick)
	{
		setProperty("space", space);
		setProperty("tick", Boolean.valueOf(tick));
	}
	
	//-------- methods --------
	
	/**
	 *  Start the space executor.
	 */
	public void start()
	{	
		this.terminated = false;

		final AbstractEnvironmentSpace space = (AbstractEnvironmentSpace)getProperty("space");
		final boolean tick = getProperty("tick")!=null && ((Boolean)getProperty("tick")).booleanValue();
		this.container	= space.getExternalAccess();

		container.searchService( new ServiceQuery<>( IExecutionService.class, RequiredServiceInfo.SCOPE_PLATFORM))
			.addResultListener(new DefaultResultListener<IExecutionService>()
		{
			public void resultAvailable(final IExecutionService exeservice)
			{
				container.searchService( new ServiceQuery<>( IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM))
					.addResultListener(new DefaultResultListener<IClockService>()
				{
					public void resultAvailable(final IClockService clockservice)
					{
						timestamp = clockservice.getTime();
						
						// Start the processes.
						Object[] procs = space.getProcesses().toArray();
						for(int i = 0; i < procs.length; ++i)
						{
							ISpaceProcess process = (ISpaceProcess) procs[i];
							process.start(clockservice, space);
						}

						final IComponentStep step = new IComponentStep<Void>()
						{
							public IFuture<Void> execute(IInternalAccess ia)
							{
								if(Boolean.TRUE.equals(getProperty(PROPERTY_EXECUTION_MONITORING)))
								{
									RoundBasedExecutor.monitorExecution(space.getExternalAccess(), exeservice);			
								}
								
								scheduled	= false;
								long currenttime = clockservice.getTime();
								long progress = currenttime - timestamp;
								timestamp = currenttime;

//								System.out.println("step: "+timestamp+" "+progress);
					
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

									// Execute the data consumers.
									for(Iterator it = space.getDataConsumers().iterator(); it.hasNext(); )
									{
										ITableDataConsumer consumer = (ITableDataConsumer)it.next();
										consumer.consumeData(currenttime, clockservice.getTick());
									}
									
									// Send the percepts to the components.
									space.getPerceptList().processPercepts(null);
								}
								
								final IComponentStep step = this;
								if(tick)
								{
//									System.out.println("tick");
									timer = clockservice.createTickTimer(new ITimedObject()
									{
										public void timeEventOccurred(long currenttime)
										{
											if(!terminated)
											{
//												System.out.println("scheduled step");
												try
												{
													space.getExternalAccess().scheduleStep(step);
												}
												catch(ComponentTerminatedException cte)
												{
												}
											}
										}
										
										@Override
										public String toString()
										{
											return "DeltaTimeExecutor for "+container.getId();
										}
									});
								}
								
								return IFuture.DONE;
							}
						};

						step.execute(null);

						if(!tick)
						{
//							System.out.println("no tick");
							clocklistener = new IChangeListener()
							{
								public void changeOccurred(ChangeEvent e)
								{
									if(!terminated && !scheduled)
									{
										scheduled	= true;
//										System.out.println("scheduled step");
										try
										{
											space.getExternalAccess().scheduleStep(step);
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
		});
	}
	
	/**
	 *  Terminate the space executor.
	 */
//	public synchronized void terminate()
	public void terminate()
	{
		terminated = true;
		container.searchService( new ServiceQuery<>( IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)).addResultListener(new DefaultResultListener()
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
}
