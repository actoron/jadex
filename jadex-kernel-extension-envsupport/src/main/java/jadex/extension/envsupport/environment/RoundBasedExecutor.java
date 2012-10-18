package jadex.extension.envsupport.environment;

import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClock;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.clock.ITimedObject;
import jadex.bridge.service.types.clock.ITimer;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.execution.IExecutionService;
import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.commons.ICommand;
import jadex.commons.SimplePropertyObject;
import jadex.commons.concurrent.IExecutable;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.dataview.IDataView;
import jadex.extension.envsupport.environment.ComponentActionList.ActionEntry;
import jadex.extension.envsupport.evaluation.ITableDataConsumer;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

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
	
	/** The execution monitor (if any). */
	protected IChangeListener<Object>	mon;
	
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
		final IServiceProvider provider	= space.getExternalAccess().getServiceProvider();
		
		if(Boolean.TRUE.equals(getProperty(PROPERTY_EXECUTION_MONITORING)!=null))
		{
			mon	= addExecutionMonitor(space.getExternalAccess());			
		}

		SServiceProvider.getService(provider, IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				final IClockService clockservice = (IClockService)result;

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
					ISpaceProcess process = (ISpaceProcess) procs[i];
					process.start(clockservice, space);
				}

				new IComponentStep<Void>()
				{
					boolean first = true;

					public IFuture<Void> execute(IInternalAccess ia)
					{
		//				System.out.println("---+++--- New round: "+currenttime+" ---+++---");
						
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
		if(mon!=null)
		{
			removeExecutionMonitor(((AbstractEnvironmentSpace)getProperty("space")).getExternalAccess(), mon);
		}
	}
	
	/**
	 *  Add a listener to the clock and check if no agent is running whenever the clock advances.
	 */
	protected static IChangeListener<Object>	addExecutionMonitor(final IExternalAccess ea)
	{
		final IClockService[]	clock	= new IClockService[1];
		final IExecutionService[]	exe	= new IExecutionService[1];
		
		final IChangeListener<Object>	ret	= new IChangeListener<Object>()
		{
			long last	= 0;
			public void changeOccurred(ChangeEvent<Object> event)
			{
				long	cur	= clock[0].getTime();
				if(cur!=last && IClock.EVENT_TYPE_NEXT_TIMEPOINT.equals(event.getType()))
				{
					IExecutable[]	tasks	= exe[0].getTasks();
					for(IExecutable task: tasks)
					{
						// Only print warning for sub-components
						if(task instanceof IComponentAdapter)
						{
							IComponentIdentifier	cid	= ((IComponentAdapter)task).getComponentIdentifier();
							IComponentIdentifier	test	= cid;
							while(test!=null && !test.equals(ea.getComponentIdentifier()))
							{
								test	= test.getParent();
							}
							if(test!=null)
							{
								System.out.println("Non-idle component at time switch: "+cid);
							}
						}
					}
				}
				last	= cur;
			}
		};
		
		SServiceProvider.getService(ea.getServiceProvider(), IExecutionService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new DefaultResultListener<IExecutionService>()
		{
			public void resultAvailable(IExecutionService result)
			{
				exe[0]	= result;
				SServiceProvider.getService(ea.getServiceProvider(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(new DefaultResultListener<IClockService>()
				{
					public void resultAvailable(IClockService result)
					{
						clock[0]	= result;
						clock[0].addChangeListener(ret);
					}
				});
			}
		});
		
		return ret;
	}
	
	/**
	 *  Remove the clock listener.
	 */
	protected static void	removeExecutionMonitor( IExternalAccess ea, final IChangeListener<Object> mon)
	{
		SServiceProvider.getService(ea.getServiceProvider(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new DefaultResultListener<IClockService>()
		{
			public void resultAvailable(final IClockService clock)
			{
				clock.removeChangeListener(mon);
			}
		});
	}
}
