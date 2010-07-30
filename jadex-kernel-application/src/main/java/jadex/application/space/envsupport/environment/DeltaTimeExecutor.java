package jadex.application.space.envsupport.environment;

import jadex.application.space.envsupport.dataview.IDataView;
import jadex.application.space.envsupport.evaluation.ITableDataConsumer;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentListener;
import jadex.bridge.IComponentManagementService;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.commons.SimplePropertyObject;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.IExecutable;
import jadex.service.IServiceProvider;
import jadex.service.SServiceProvider;
import jadex.service.clock.IClockService;
import jadex.service.clock.ITimedObject;
import jadex.service.clock.ITimer;
import jadex.service.execution.IExecutionService;

import java.util.Iterator;
import java.util.Map;

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
	protected IServiceProvider container;
	
	/** The clock listener. */
	protected IChangeListener clocklistener;
	
	/** The tick timer. */
	protected ITimer timer;
	
	/** The flag indicating that the executor is terminated. */
	protected boolean terminated;
	
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
		setProperty("tick", new Boolean(tick));
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
		this.container	= space.getContext().getServiceProvider();
		
		SServiceProvider.getService(container, IClockService.class).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				final IClockService clockservice = (IClockService)result;
				SServiceProvider.getService(container, IExecutionService.class).addResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						final IExecutionService exeservice = (IExecutionService)result;
						
						final IExecutable	executable	= new IExecutable()
						{
							public boolean execute()
							{
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
								
								return false;
							}
						};
						
						timestamp = clockservice.getTime();
						
						// Start the processes.
						Object[] procs = space.getProcesses().toArray();
						for(int i = 0; i < procs.length; ++i)
						{
							ISpaceProcess process = (ISpaceProcess) procs[i];
							process.start(clockservice, space);
						}

						if(tick)
						{
							timer = clockservice.createTickTimer(new ITimedObject()
							{
								public void timeEventOccurred(long currenttime)
								{
//									boolean t = false;
//									synchronized(DeltaTimeExecutor.this)
//									{
//										t = terminated;
//									}
//									if(t)
//									{
										if(!terminated)
										{
											exeservice.execute(executable);
											timer = clockservice.createTickTimer(this);
										}
//									}
								}
							});
						}
						else
						{
							clocklistener = new IChangeListener()
							{
								public void changeOccurred(ChangeEvent e)
								{
									exeservice.execute(executable);
								}
							};
							clockservice.addChangeListener(clocklistener);
						}
						
						// Add the executor as context listener on the application.
						SServiceProvider.getServiceUpwards(container, IComponentManagementService.class).addResultListener(new DefaultResultListener()
						{
							public void resultAvailable(Object source, Object result)
							{
								IComponentManagementService	cms	= (IComponentManagementService)result;
								cms.addComponentListener(space.getContext().getComponentIdentifier(), new IComponentListener()
								{
									
									public void componentRemoved(IComponentDescription desc, Map results)
									{
										terminate();
									}
									
									public void componentChanged(IComponentDescription desc)
									{
									}
									
									public void componentAdded(IComponentDescription desc)
									{
									}
								});
							}
						});
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
		SServiceProvider.getService(container, IClockService.class).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				IClockService clockservice = (IClockService)result;
				if(clocklistener!=null)
				{
					clockservice.removeChangeListener(clocklistener);
				}
				else
				{
					terminated = true;
					if(timer!=null)
						timer.cancel();
				}
			}
		});
	}
}
