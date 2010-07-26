package jadex.application.space.envsupport.environment;

import jadex.application.space.envsupport.dataview.IDataView;
import jadex.application.space.envsupport.environment.ComponentActionList.ActionEntry;
import jadex.application.space.envsupport.evaluation.ITableDataConsumer;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentListener;
import jadex.bridge.IComponentManagementService;
import jadex.commons.ICommand;
import jadex.commons.SimplePropertyObject;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.IExecutable;
import jadex.service.IServiceProvider;
import jadex.service.SServiceProvider;
import jadex.service.clock.IClockService;
import jadex.service.clock.ITimedObject;
import jadex.service.clock.ITimer;
import jadex.service.execution.IExecutionService;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

/**
 *  Synchronized execution of all actions in rounds based on clock ticks.
 */
public class RoundBasedExecutor extends SimplePropertyObject implements ISpaceExecutor
{
	//-------- constants --------
	
	/** The property for the action execution mode. */
	public static final String	PROPERTY_MODE	= "mode";
	
	/** The value for the last action execution mode. */
	public static final String	MODE_LASTACTION	= "lastaction";
	
	//-------- attributes --------
	
	/** Last time stamp. */
	protected long timestamp;
	
	/** Current time. */
	protected long currenttime;
	
	/** The tick timer. */
	protected ITimer timer;
	
	/** The flag indicating that the executor is terminated. */
	protected boolean terminated;
	
	//-------- constructors--------
	
	/**
	 *  Creates a new round based executor.
	 */
	public RoundBasedExecutor()
	{
	}
	
	/**
	 *  Creates a new round based executor.
	 *  @param space	The space.
	 *  @param clockservice	The clock service.
	 */
	public RoundBasedExecutor(AbstractEnvironmentSpace space)
	{
		this(space, null);
	}
	
	/**
	 *  Creates a new round based executor.
	 *  @param space	The space.
	 *  @param clockservice	The clock service.
	 *  @param acomp	The action comparator.
	 */
	public RoundBasedExecutor(AbstractEnvironmentSpace space, Comparator acomp)
	{
		setProperty("space", space);
		setProperty("comparator", acomp);
	}
	
	//-------- methods --------
	
	/**
	 *  Start the space executor.
	 */
	public void start()
	{
		final AbstractEnvironmentSpace space = (AbstractEnvironmentSpace)getProperty("space");
		final IServiceProvider provider	= space.getContext().getServiceProvider();

		SServiceProvider.getService(provider, IClockService.class).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				final IClockService clockservice = (IClockService)result;
				SServiceProvider.getService(provider, IExecutionService.class).addResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						final IExecutionService exeservice = (IExecutionService)result;
					
						Comparator comp = (Comparator)getProperty("comparator");
						if(comp!=null)
						{
							space.getComponentActionList().setOrdering(comp);
						}
						if(MODE_LASTACTION.equals(getProperty(PROPERTY_MODE)))
						{
							space.getComponentActionList().setScheduleCommand(new ICommand()
							{
								public void execute(Object args)
								{
									ActionEntry	entry	= (ActionEntry)args;
									ActionEntry	entries[]	= space.getComponentActionList().getActionEntries();
									
									if(entries.length>0)
									{
										IComponentIdentifier	actor	= entry.parameters!=null ?
											(IComponentIdentifier)entry.parameters.get(ISpaceAction.ACTOR_ID) : null;
										
										for(int i=0; actor!=null && i<entries.length; i++)
										{
											IComponentIdentifier	actor2	= entries[i].parameters!=null ?
												(IComponentIdentifier)entries[i].parameters.get(ISpaceAction.ACTOR_ID) : null;
											if(actor.equals(actor2))
											{
				//								System.out.println("Removing duplicate action: "+entries[i]);
												space.getComponentActionList().removeComponentAction(entries[i]);
											}
										}
									}
									space.getComponentActionList().addComponentAction(entry);
								}
							});
						}
						
						final IExecutable	executable	= new IExecutable()
						{
							boolean first = true;
							
							public boolean execute()
							{
				//				System.out.println("---+++--- New round: "+currenttime+" ---+++---");
								
								long progress = currenttime - timestamp;
								timestamp = currenttime;
								
								synchronized(space.getMonitor())
								{
									if(!first)
									{
										// Update the environment objects.
										Object[] objs = space.getSpaceObjectsCollection().toArray();
										for(int i = 0; i < objs.length; ++i)
										{
											SpaceObject obj = (SpaceObject)objs[i];
											obj.updateObject(space, progress, clockservice);
										}
										
										// Execute the scheduled component actions.
										space.getComponentActionList().executeActions(null, false);
										
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
											IDataView view = (IDataView) it.next();
											view.update(space);
										}
										
										// Execute the data consumers.
										for(Iterator it = space.getDataConsumers().iterator(); it.hasNext(); )
										{
											ITableDataConsumer consumer = (ITableDataConsumer)it.next();
											consumer.consumeData(currenttime, clockservice.getTick());
										}
									}
									
									// Send the percepts to the components.
									space.getPerceptList().processPercepts(null);
				
									// Wakeup the components.
									space.getComponentActionList().wakeupComponents(null);
									
									first = false;
								}
				//				System.out.println("-------------------------------------------");
								
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
						
						// In the first round only percepts are distributed.
						timer = clockservice.createTickTimer(new ITimedObject()
						{
							public void timeEventOccurred(long currenttime)
							{
								if(!terminated)
								{
									RoundBasedExecutor.this.currenttime = currenttime;
									exeservice.execute(executable);
									clockservice.createTickTimer(this);
								}
							}
						});
						
						// Add the executor as context listener on the application.
						SServiceProvider.getServiceUpwards(provider, IComponentManagementService.class).addResultListener(new DefaultResultListener()
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
		terminated = true;
		if(timer!=null)
			timer.cancel();
	}
}
