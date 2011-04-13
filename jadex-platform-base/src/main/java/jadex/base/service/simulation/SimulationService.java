package jadex.base.service.simulation;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ISettingsService;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.clock.IClock;
import jadex.bridge.service.clock.IClockService;
import jadex.bridge.service.clock.ITimer;
import jadex.bridge.service.execution.IExecutionService;
import jadex.bridge.service.threadpool.IThreadPoolService;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.commons.IPropertiesProvider;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.collection.SCollection;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

import java.util.List;
import java.util.Map;

/**
 *  The execution control is the access point for controlling the
 *  execution of one application. It provides basic features for
 *  starting, stopping and stepwise execution.
 */
public class SimulationService extends BasicService implements ISimulationService, IPropertiesProvider
{		
	//-------- attributes --------

	/** The containing component. */
	protected IInternalAccess access;
	
	/** The execution mode. */
	protected String mode;
	
	/** The executing flag. */
	protected boolean executing;
	
	/** The listeners. */
	protected List listeners;

	/** The time of a time step. */
	protected long timesteptime;
		
	/** The clock service. */
	protected IClockService clockservice;
	
	/** The execution service. */
	protected IExecutionService exeservice;
	
	/** The future (if any) indicating when a step is finished. */
	protected Future	stepfuture;
	
	/** The idle future listener. */
	protected IdleListener	idlelistener;
	
	/** Flag to indicate that simulation should be started after service is inited. */
	protected boolean	startoninit;
	
	//-------- constructors --------

	/**
	 *  Create a new execution control.
	 */
	public SimulationService(IInternalAccess access)
	{
		this(access, null);
	}
	
	/**
	 *  Create a new execution control.
	 */
	public SimulationService(IInternalAccess access, Map properties)
	{
		super(access.getServiceContainer().getId(), ISimulationService.class, properties);

		this.access = access;
		this.mode = MODE_NORMAL;
		this.startoninit	= true;
		this.listeners = SCollection.createArrayList();
	}
	
	/**
	 *  Shutdown the service.
	 *  @param listener The listener.
	 */
	public IFuture	shutdownService()
	{
		final Future	deregistered	= new Future();
		SServiceProvider.getService(access.getServiceContainer(), ISettingsService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(access.createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				ISettingsService	settings	= (ISettingsService)result;
				settings.deregisterPropertiesProvider("simulationservice")
					.addResultListener(access.createResultListener(new DelegationResultListener(deregistered)));
			}
			
			public void exceptionOccurred(Exception exception)
			{
				// No settings service: ignore.
				deregistered.setResult(null);
			}
		}));
			
		final Future	ret	= new Future();
		deregistered.addResultListener(access.createResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				IFuture	stopped;
				if(executing)
				{
					stopped	= pause();
				}
				else
				{
					stopped	= IFuture.DONE;
				}
				
				stopped.addResultListener(access.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						SimulationService.super.shutdownService().addResultListener(
							access.createResultListener(new DelegationResultListener(ret)));
					}
				}));
			}
		}));
		
		return ret;
	}
	
	//-------- methods --------
	
	/**
	 *  Start (and run) the execution. 
	 */
	public IFuture	startService()
	{
		final Future	ret	= new Future();
		
		super.startService().addResultListener(access.createResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				SServiceProvider.getService(access.getServiceContainer(), ISettingsService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(access.createResultListener(new IResultListener()
				{
					public void resultAvailable(Object result)
					{
						ISettingsService	settings	= (ISettingsService)result;
						settings.registerPropertiesProvider("simulationservice", SimulationService.this)
							.addResultListener(access.createResultListener(new DelegationResultListener(ret)
						{
							public void customResultAvailable(Object result)
							{
								proceed();
							}
							public void exceptionOccurred(Exception exception)
							{
								super.exceptionOccurred(exception);
							}
						}));
					}
					
					public void exceptionOccurred(Exception exception)
					{
						// No settings service: ignore.
						proceed();
					}
					
					public void proceed()
					{
						final boolean[]	services	= new boolean[2];

						SServiceProvider.getService(access.getServiceContainer(), IExecutionService.class, RequiredServiceInfo.SCOPE_PLATFORM)
							.addResultListener(access.createResultListener(new DelegationResultListener(ret)
						{
							public void customResultAvailable(Object result)
							{
								exeservice = (IExecutionService)result;
								services[0]	= true;
								if(services[0] && services[1])
								{
									if(startoninit)
									{
										startoninit	= false;
										start().addResultListener(access.createResultListener(new DelegationResultListener(ret)
										{
											public void customResultAvailable(Object result)
											{
												super.customResultAvailable(getServiceIdentifier());
											}
										}));
									}
									else
									{
										ret.setResult(getServiceIdentifier());
									}
								}
							}
						}));
								
						SServiceProvider.getService(access.getServiceContainer(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
							.addResultListener(access.createResultListener(new DelegationResultListener(ret)
						{
							public void customResultAvailable(Object result)
							{
								clockservice = (IClockService)result;
								services[1]	= true;
								if(services[0] && services[1])
								{
									if(startoninit)
									{
										startoninit	= false;
										start().addResultListener(access.createResultListener(new DelegationResultListener(ret)
										{
											public void customResultAvailable(Object result)
											{
												super.customResultAvailable(getServiceIdentifier());
											}
										}));
									}
									else
									{
										ret.setResult(getServiceIdentifier());
									}
								}
							}
						}));						
					}
				}));				
			}
		}));

		return ret;
	}
	
	/**
	 *  Pause the execution (can be resumed via start or step).
	 */
	public IFuture pause()
	{
		IFuture	ret;
		if(!executing)
		{
//			System.out.println("Not pausing");
			ret	= new Future(new IllegalStateException("Simulation not running."));
		}
		else
		{
//			System.out.println("Pausing");
			setExecuting(false);
			getClockService().stop();
			ret	= IFuture.DONE;
			
			if(idlelistener!=null)
			{
				idlelistener.outdated	= true;
				idlelistener	= null;
			}
		}
		return ret;
	}
	
	/**
	 *  Restart the execution after pause.
	 */
	public IFuture start()
	{
		IFuture	ret;
		if(executing)
		{
//			System.out.println("Not starting");
			ret	= new Future(new IllegalStateException("Simulation already running."));
		}
		else
		{
//			System.out.println("Starting");
			setMode(MODE_NORMAL);
			getClockService().start();
			setExecuting(true);
			scheduleAdvanceClock();
			ret	= IFuture.DONE;
		}
		return ret;
	}
	
	/**
	 *  Perform one event.
	 */
	public IFuture stepEvent()
	{
		IFuture	ret;
		if(executing)
		{
//			System.out.println("Not stepping");
			ret	= new Future(new IllegalStateException("Simulation already running."));
		}
		else if(IClock.TYPE_CONTINUOUS.equals(getClockService().getClockType())
				|| IClock.TYPE_SYSTEM.equals(getClockService().getClockType()))
		{
//			System.out.println("Not stepping");
			ret	= new Future(new IllegalStateException("Step only possible in simulation mode."));
		}
		else
		{
//			System.out.println("Stepping");
			setMode(MODE_ACTION_STEP);
			getClockService().start();
			setExecuting(true);
			assert stepfuture==null;
			stepfuture	= new Future();
			ret	= stepfuture;
			scheduleAdvanceClock();
		}
		return ret;
	}
	
	/**
	 *  Perform all actions belonging to one time point.
	 */
	public IFuture stepTime()
	{
		IFuture	ret;
		if(executing)
		{
//			System.out.println("Not stepping");
			ret	= new Future(new IllegalStateException("Simulation already running."));
		}
		else if(IClock.TYPE_CONTINUOUS.equals(getClockService().getClockType())
				|| IClock.TYPE_SYSTEM.equals(getClockService().getClockType()))
		{
//			System.out.println("Not stepping");
			ret	= new Future(new IllegalStateException("Step only possible in simulation mode."));
		}
		else
		{
//			System.out.println("Stepping");
			setMode(MODE_TIME_STEP);
			ITimer	next	= getClockService().getNextTimer();
			if(next!=null)
			{
				timesteptime	= next.getNotificationTime();
//				System.out.println("time step: "+timesteptime);
				getClockService().start();
				setExecuting(true);
				assert stepfuture==null;
				stepfuture	= new Future();
				ret	= stepfuture;
				scheduleAdvanceClock();
			}
			else
			{
				ret	= IFuture.DONE;
			}
		}
		return ret;
	}
	
	/**
	 *  Get the execution mode.
	 *  @return The mode.
	 */
	public IFuture getMode()
	{
		return new Future(mode);
	}
	
	/**
	 *  Set the execution mode.
	 *  @param mode The mode.
	 */
	public void setMode(String mode)
	{
		this.mode = mode;
	}
	
	/**
	 *  Set the clock type.
	 *  @param type The clock type.
	 */
	public IFuture setClockType(final String type)
	{
		IFuture	ret;
		if(executing)
		{
//			System.out.println("Not setting clock");
			ret	= new Future(new IllegalStateException("Change clock not allowed during execution."));
		}
		else
		{
			String oldtype = clockservice.getClockType();
			if(!type.equals(oldtype))
			{
//				System.out.println("Setting clock");
				final Future	fut	= new Future();
				ret	= fut;
				SServiceProvider.getService(access.getServiceContainer(), IThreadPoolService.class)
					.addResultListener(access.createResultListener(new DelegationResultListener(fut)
				{
					public void customResultAvailable(Object result)
					{
						IThreadPoolService	tps	= (IThreadPoolService)result;
						clockservice.setClock(type, tps);
						notifyListeners(new ChangeEvent(this, "clock_type", type));
						fut.setResult(null);
					}
				}));
			}
			else
			{
//				System.out.println("Not setting clock");
				ret	= IFuture.DONE;
			}
		}
		return ret;
	}
		
	/**
	 *  Test if context is executing.
	 */
	public IFuture isExecuting()
	{
		return new Future(executing ? Boolean.TRUE : Boolean.FALSE);
	}
	
	/**
	 *  Add a change listener.
	 *  @param listener The change listener.
	 */
	public void addChangeListener(IChangeListener listener)
	{
		listeners.add(listener);
	}
	
	/**
	 *  Remove a change listener.
	 *  @param listener The change listener.
	 */
	public void removeChangeListener(IChangeListener listener)
	{
		listeners.remove(listener);
	}
	
	/**
	 *  Set the executing state.
	 */
	public void setExecuting(boolean executing)
	{
//		System.out.println("Set executing: "+executing);
		assert executing!=this.executing;
		this.executing = executing;
		notifyListeners(new ChangeEvent(this, "executing", executing? Boolean.TRUE: Boolean.FALSE));
	}
	
	/**
	 *  Notify the listeners.
	 */
	protected void notifyListeners(ChangeEvent event)
	{
		IChangeListener[]	alisteners	= listeners.isEmpty() ? null
			: (IChangeListener[])listeners.toArray(new IChangeListener[listeners.size()]);
		for(int i=0; alisteners!=null && i<alisteners.length; i++)
		{
			alisteners[i].changeOccurred(event);
		}
	}
	
	/**
	 *  Get the platform clock.
	 *  @return The clock.
	 */
	public IClockService getClockService()
	{
		return clockservice;
	}
	
	/**
	 *  Get the executor service.
	 *  @return The executor service.
	 */
	public IExecutionService getExecutorService()
	{
		return exeservice;
	}

	/**
	 *  Stop execution.
	 */
	protected void setIdle()
	{
//		System.out.println("Set idle");
		setExecuting(false);
		getClockService().stop();
		if(stepfuture!=null)
		{
			stepfuture.setResult(null);
			stepfuture	= null;
		}
	}

	/**
	 *  Start clock execution.
	 */
	protected void scheduleAdvanceClock()
	{
		if(idlelistener!=null)
			idlelistener.outdated	= true;
		idlelistener	= new IdleListener();
		getExecutorService().getNextIdleFuture().addResultListener(access.createResultListener(idlelistener));
	}
	
	/**
	 *  Trigger clock execution.
	 */
	protected void advanceClock()
	{
		String	type	= getClockService().getClockType();			
		if(IClock.TYPE_EVENT_DRIVEN.equals(type) || IClock.TYPE_TIME_DRIVEN.equals(type))
		{
			ITimer t = getClockService().getNextTimer();
			if(MODE_TIME_STEP.equals(mode) && (t==null || t.getNotificationTime()>timesteptime))
			{
//				System.out.println("Not advancing clock");
				setIdle();
			}
			else
			{
//				System.out.println("Advancing clock");
				if(getClockService().advanceEvent())
				{
					if(idlelistener==null)
						idlelistener	= new IdleListener();
					getExecutorService().getNextIdleFuture().addResultListener(access.createResultListener(idlelistener));
				}
				else
				{
//					System.out.println("Clock not advanced");

					// Simulation stopped due to no more entries
					// -> listen on clock until new entries available.
					if(MODE_NORMAL.equals(mode))
					{
						getClockService().addChangeListener(new IChangeListener()
						{
							public void changeOccurred(ChangeEvent event)
							{
								if(IClock.EVENT_TYPE_TIMER_ADDED.equals(event.getType()))
								{
									getClockService().removeChangeListener(this);
									access.getExternalAccess().scheduleStep(new IComponentStep()
									{
										public Object execute(IInternalAccess ia)
										{
											// Resume execution if still executing.
											if(MODE_NORMAL.equals(mode) && executing)
											{
												scheduleAdvanceClock();
											}
											return null;
										}
									});
								}
							}
						});
					}
					
					// Step finished.
					else
					{
						setIdle();
					}
				}
			}
		}
		// else do nothing for continuous clock as it executes itself.
//		else
//		{
//			System.out.println("Not advancing clock");
//		}
	}
	
	//-------- helper classes --------
	
	/**
	 *  Listener on the execution service.
	 */
	public class IdleListener implements IResultListener
	{
		//-------- attributes --------
		
		/** Flag to indicate an outdated listener that should not do anything.
		 *  Required, because future does not support remove listener. */
		protected boolean	outdated;
		
		/** Flag indicating an a continued execution.
		 *  If in action step mode, clock must not be advanced. */
		protected boolean	continued;
		
		//-------- IResultListener interface --------
		
		/**
		 *  Called when an exception occurs.
		 */
		public void exceptionOccurred(Exception exception)
		{
			// ignore (happens when execution service terminates).
		}

		/**
		 *  Called when the execution service is idle.
		 */
		public void resultAvailable(Object result)
		{
//			System.out.println("Executor idle");
			if(executing && !outdated)
			{
				if(MODE_NORMAL.equals(mode) || MODE_TIME_STEP.equals(mode) || !continued)
				{
					continued	= true;
					advanceClock();
				}
				else if(MODE_ACTION_STEP.equals(mode))
				{
					setIdle();
				}
			}
		}
	}

	//-------- IPropertiesProvider interface --------
	
	/**
	 *  Update from given properties.
	 */
	public IFuture setProperties(Properties props)
	{
		final boolean	exe	= props.getBooleanProperty("executing");
		return access.getExternalAccess().scheduleImmediate(new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
				// startoninit==false means service already running
				if(exe && !executing && !startoninit)
				{
					start();
				}
				else if(!exe && executing)
				{
					pause();
				}
				else if(!exe)
				{
					startoninit	= false;
				}
				
				return null;
			}
		});
	}
	
	/**
	 *  Write current state into properties.
	 */
	public IFuture getProperties()
	{
		Properties	props	= new Properties();
		// Only save as executing when in normal mode.
		props.addProperty(new Property("executing", ""+(executing && MODE_NORMAL.equals(mode))));
		return new Future(props);
	}
}
