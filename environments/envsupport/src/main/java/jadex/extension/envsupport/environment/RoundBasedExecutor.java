package jadex.extension.envsupport.environment;

import java.util.Comparator;
import java.util.Iterator;

import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.impl.ExecutionComponentFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.clock.ITimedObject;
import jadex.bridge.service.types.clock.ITimer;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.execution.IExecutionService;
import jadex.commons.ICommand;
import jadex.commons.SimplePropertyObject;
import jadex.commons.concurrent.IExecutable;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.dataview.IDataView;
import jadex.extension.envsupport.environment.ComponentActionList.ActionEntry;
import jadex.extension.envsupport.evaluation.ITableDataConsumer;

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
		
		SServiceProvider.searchService(space.getExternalAccess(), new ServiceQuery<>( IExecutionService.class, RequiredServiceInfo.SCOPE_PLATFORM))
			.addResultListener(new DefaultResultListener<IExecutionService>()
		{
			public void resultAvailable(final IExecutionService exeservice)
			{
				SServiceProvider.searchService(space.getExternalAccess(), new ServiceQuery<>( IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM))
					.addResultListener(new DefaultResultListener<IClockService>()
				{
					public void resultAvailable(final IClockService clockservice)
					{
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
										IComponentDescription actor	= entry.parameters!=null ?
											(IComponentDescription)entry.parameters.get(ISpaceAction.ACTOR_ID) : null;
										
										for(int i=0; actor!=null && i<entries.length; i++)
										{
											IComponentDescription actor2 = entries[i].parameters!=null ?
												(IComponentDescription)entries[i].parameters.get(ISpaceAction.ACTOR_ID) : null;
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
						
						timestamp = clockservice.getTime();
						
						// Start the processes.
						Object[] procs = space.getProcesses().toArray();
						for(int i = 0; i < procs.length; ++i)
						{
							ISpaceProcess process = (ISpaceProcess)procs[i];
							process.start(clockservice, space);
						}
	
						new IComponentStep<Void>()
						{
							boolean first = true;
	
							public IFuture<Void> execute(IInternalAccess ia)
							{
//								System.out.println("---+++--- New round: "+currenttime+" ---+++---");
								
								if(Boolean.TRUE.equals(getProperty(PROPERTY_EXECUTION_MONITORING)))
								{
									monitorExecution(space.getExternalAccess(), exeservice);			
								}
								
								long progress = currenttime - timestamp;
								timestamp = currenttime;
								
								synchronized(space.getMonitor())
								{
									// In the first round only percepts are distributed.
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
								
								final IComponentStep step = this;
								timer = clockservice.createTickTimer(new ITimedObject()
								{
									public void timeEventOccurred(long currenttime)
									{
										if(!terminated)
										{
											RoundBasedExecutor.this.currenttime = currenttime;
											try
											{
												space.getExternalAccess().scheduleStep(step);
											}
											catch(ComponentTerminatedException cte)
											{
											}
										}
									}
								});
								return IFuture.DONE;
							}
						}.execute(null);				
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
		{
			timer.cancel();
		}
	}
	
	/**
	 *  Check if no agent is running whenever the clock advances.
	 */
	protected static void	monitorExecution(IExternalAccess ea, IExecutionService exe)
	{
//		System.out.println("Monitoring execution...");

		IExecutable[]	tasks	= exe.getRunningTasks();
		for(IExecutable task: tasks)
		{
			// Only print warning for sub-components
			if(task instanceof ExecutionComponentFeature)
			{
				IComponentIdentifier	cid	= ((ExecutionComponentFeature)task).getComponent().getIdentifier();
				IComponentIdentifier	test	= cid;
				while(test!=null && !test.equals(ea.getComponentIdentifier()))
				{
					test	= test.getParent();
				}
				if(test!=null && !cid.equals(ea.getComponentIdentifier()))
				{
					System.out.println("Non-idle component at time switch: "+cid);
				}
			}
		}
	}
}
