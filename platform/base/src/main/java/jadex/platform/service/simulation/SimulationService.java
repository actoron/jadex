package jadex.platform.service.simulation;

import java.util.ArrayList;
import java.util.List;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ImmediateComponentStep;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.component.IInternalRequiredServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.clock.IClock;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.clock.ITimer;
import jadex.bridge.service.types.execution.IExecutionService;
import jadex.bridge.service.types.settings.ISettingsService;
import jadex.bridge.service.types.simulation.ISimulationService;
import jadex.bridge.service.types.threadpool.IThreadPoolService;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.commons.IPropertiesProvider;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.collection.SCollection;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.FutureBarrier;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/**
 *  The execution control is the access point for controlling the
 *  execution of one application. It provides basic features for
 *  starting, stopping and stepwise execution.
 */
@Service
public class SimulationService	implements ISimulationService, IPropertiesProvider
{		
	//-------- attributes --------

	/** The containing component. */
	@ServiceComponent
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
	
	/** Blockers that prevent the clock from advancing. */
	protected List<IFuture<?>> advanceblockers = new ArrayList<>();
	
	/** The idle future listener. */
	protected IdleListener	idlelistener;
	
	/** Flag to indicate that simulation should be started after service is inited. */
	protected boolean	startoninit;
	
	//-------- constructors --------

	/**
	 *  Create a new execution control.
	 */
	public SimulationService()
	{
		this.mode = MODE_NORMAL;
		this.startoninit	= true;
		this.listeners = SCollection.createArrayList();
	}
	
	//-------- service methods --------

	/**
	 *  Shutdown the service.
	 *  @param listener The listener.
	 */
	@ServiceShutdown
	public IFuture<Void>	shutdownService()
	{
		final Future<Void>	deregistered	= new Future<Void>();
		access.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>( ISettingsService.class, RequiredServiceInfo.SCOPE_PLATFORM))
			.addResultListener(access.getFeature(IExecutionFeature.class).createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				ISettingsService	settings	= (ISettingsService)result;
				settings.deregisterPropertiesProvider("simulationservice")
					.addResultListener(access.getFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener(deregistered)));
			}
			
			public void exceptionOccurred(Exception exception)
			{
				// No settings service: ignore.
				deregistered.setResult(null);
			}
		}));
			
		final Future	ret	= new Future();
		deregistered.addResultListener(access.getFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener(ret)
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
				
				stopped.addResultListener(access.getFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener(ret)));
			}
		}));
		
		return ret;
	}
	
	//-------- methods --------
	
	/**
	 *  Start (and run) the execution. 
	 */
	@ServiceStart
	public IFuture<Void>	startService()
	{
		final Future<Void>	ret	= new Future<Void>();
		
		ISettingsService	settings	= access.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(ISettingsService.class));
		settings.registerPropertiesProvider("simulationservice", SimulationService.this)
			.addResultListener(access.getFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
			{
				exeservice	= ((IInternalRequiredServicesFeature)access.getFeature(IRequiredServicesFeature.class)).getRawService(IExecutionService.class);
				clockservice	= ((IInternalRequiredServicesFeature)access.getFeature(IRequiredServicesFeature.class)).getRawService(IClockService.class);
				if(startoninit)
				{
					startoninit	= false;
					start().addResultListener(access.getFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<Void>(ret)));
				}
				else
				{
					ret.setResult(null);
				}
			}
		}));

		return ret;
	}
	
	/**
	 *  Pause the execution (can be resumed via start or step).
	 */
	public IFuture<Void> pause()
	{
		IFuture<Void>	ret;
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
	public IFuture<Void> start()
	{
		IFuture<Void>	ret;
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
	public IFuture<Void> stepEvent()
	{
		IFuture<Void>	ret;
		if(executing)
		{
//			System.out.println("Not stepping");
			ret	= new Future<Void>(new IllegalStateException("Simulation already running."));
		}
		else if(IClock.TYPE_CONTINUOUS.equals(getClockService().getClockType())
				|| IClock.TYPE_SYSTEM.equals(getClockService().getClockType()))
		{
//			System.out.println("Not stepping");
			ret	= new Future<Void>(new IllegalStateException("Step only possible in simulation mode."));
		}
		else
		{
//			System.out.println("Stepping");
			setMode(MODE_ACTION_STEP);
			getClockService().start();
			setExecuting(true);
			assert stepfuture==null;
			stepfuture	= new Future<Void>();
			ret	= stepfuture;
			scheduleAdvanceClock();
		}
		return ret;
	}
	
	/**
	 *  Perform all actions belonging to one time point.
	 */
	public IFuture<Void> stepTime()
	{
		IFuture<Void>	ret;
		if(executing)
		{
//			System.out.println("Not stepping");
			ret	= new Future<Void>(new IllegalStateException("Simulation already running."));
		}
		else if(IClock.TYPE_CONTINUOUS.equals(getClockService().getClockType())
				|| IClock.TYPE_SYSTEM.equals(getClockService().getClockType()))
		{
//			System.out.println("Not stepping");
			ret	= new Future<Void>(new IllegalStateException("Step only possible in simulation mode."));
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
	public IFuture<String> getMode()
	{
		return new Future<String>(mode);
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
	public IFuture<Void> setClockType(final String type)
	{
		IFuture<Void>	ret;
		if(executing)
		{
//			System.out.println("Not setting clock");
			ret	= new Future<Void>(new IllegalStateException("Change clock not allowed during execution."));
		}
		else
		{
			String oldtype = clockservice.getClockType();
			if(!type.equals(oldtype))
			{
//				System.out.println("Setting clock");
				final Future	fut	= new Future();
				ret	= fut;
				IThreadPoolService	tps	= ((IInternalRequiredServicesFeature)access.getFeature(IRequiredServicesFeature.class)).getRawService(IThreadPoolService.class);
				clockservice.setClock(type, tps);
				notifyListeners(new ChangeEvent(this, "clock_type", type));
				fut.setResult(null);
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
	public IFuture<Boolean> isExecuting()
	{
		return new Future<Boolean>(executing ? Boolean.TRUE : Boolean.FALSE);
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
	 *  Adds a blocker to the clock that prevents the clock from
	 *  advancing until the future is triggered either by result
	 *  or exception.
	 *  
	 *  @param blocker The blocking future.
	 *  @return Null, when added.
	 */
	public IFuture<Void> addAdvanceBlocker(IFuture<?> blocker)
	{
		advanceblockers.add(blocker);
		return IFuture.DONE;
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
		getExecutorService().getNextIdleFuture().addResultListener(access.getFeature(IExecutionFeature.class).createResultListener(idlelistener));
//		System.out.println("Wait2");
//		waitForBlockers().addResultListener(new IResultListener<Void>()
//		{
//			public void resultAvailable(Void result)
//			{
////				System.out.println("Release2");
//				if(idlelistener!=null)
//					idlelistener.outdated	= true;
//				idlelistener	= new IdleListener();
//				
//				getExecutorService().getNextIdleFuture().addResultListener(access.getFeature(IExecutionFeature.class).createResultListener(idlelistener));
//			}
//			
//			public void exceptionOccurred(Exception exception)
//			{
//				resultAvailable(null);
//			}
//		});
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
//				System.out.println(this+" waiting for blockers");
				waitForBlockers().addResultListener(new IResultListener<Void>()
				{
					public void resultAvailable(Void result)
					{
//						System.out.println(access+" advancing clock");
						if(getClockService().advanceEvent())
						{
//							System.out.println(access+" advanced clock");

							if(idlelistener==null)
								idlelistener	= new IdleListener();
							getExecutorService().getNextIdleFuture().addResultListener(access.getFeature(IExecutionFeature.class).createResultListener(idlelistener));
								
						}
						else
						{
//							System.out.println(access+" Clock not advanced");
		
							// Simulation stopped due to no more entries
							// -> listen on clock until new entries available.
							if(MODE_NORMAL.equals(mode))
							{
								getClockService().addChangeListener(new IChangeListener()
								{
									public void changeOccurred(ChangeEvent event)
									{
//										System.out.println(access+" Clock changed after not advanced");
										if(IClock.EVENT_TYPE_TIMER_ADDED.equals(event.getType()))
										{
											getClockService().removeChangeListener(this);
											access.getExternalAccess().scheduleStep(new IComponentStep<Void>()
											{
												public IFuture<Void> execute(IInternalAccess ia)
												{
													// Resume execution if still executing.
													if(MODE_NORMAL.equals(mode) && executing)
													{
//														System.out.println(access+" Schedule advancing clock");
														scheduleAdvanceClock();
													}
													return IFuture.DONE;
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
					
					public void exceptionOccurred(Exception exception)
					{
						resultAvailable(null);
					}
				});
			}
		}
		// else do nothing for continuous clock as it executes itself.
//		else
//		{
//			System.out.println("Not advancing clock");
//		}
	}
	
	/**
	 *  Waits for blockers
	 * @return
	 */
	protected IFuture<Void> waitForBlockers()
	{
		IFuture<Void> ret = null;
		if (advanceblockers.size() > 0)
		{
			Future<Void> futret = new Future<>();
			ret = futret;
			FutureBarrier<Object> bar = new FutureBarrier<>();
			for (IFuture<?> blocker : advanceblockers)
			{
				@SuppressWarnings("unchecked")
				IFuture<Object>	oblocker	= (IFuture<Object>)blocker;
				bar.addFuture(oblocker);
			}
			advanceblockers.clear();
			bar.waitForIgnoreFailures(null).addResultListener(access.getFeature(IExecutionFeature.class).createResultListener(new IResultListener<Void>()
			{
				public void resultAvailable(Void result)
				{
					waitForBlockers().addResultListener(new DelegationResultListener<>(futret));
				}
				public void exceptionOccurred(Exception exception)
				{
				}
			}));
		}
		else
		{
			ret = IFuture.DONE;
		}
		return ret;
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
//			System.out.println(SimulationService.this+" Executor idle");
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
	public IFuture<Void> setProperties(Properties props)
	{
		final boolean	exe	= props.getBooleanProperty("executing");
		return access.getExternalAccess().scheduleStep(new ImmediateComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
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
				
				return IFuture.DONE;
			}
		});
	}
	
	/**
	 *  Write current state into properties.
	 */
	public IFuture<Properties> getProperties()
	{
		Properties	props	= new Properties();
		// Only save as executing when in normal mode.
		props.addProperty(new Property("executing", ""+(executing && MODE_NORMAL.equals(mode))));
		return new Future<Properties>(props);
	}
}
