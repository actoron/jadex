package jadex.bridge.component.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import jadex.base.Starter;
import jadex.bridge.ComponentResultListener;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IConditionalComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IPriorityComponentStep;
import jadex.bridge.ITransferableStep;
import jadex.bridge.IntermediateComponentResultListener;
import jadex.bridge.StepAborted;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IComponentFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Timeout;
import jadex.bridge.service.component.Breakpoint;
import jadex.bridge.service.component.ComponentSuspendable;
import jadex.bridge.service.component.interceptors.CallAccess;
import jadex.bridge.service.component.interceptors.FutureFunctionality;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.search.ServiceRegistry;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.clock.ITimedObject;
import jadex.bridge.service.types.clock.ITimer;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.execution.IExecutionService;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishTarget;
import jadex.bridge.service.types.monitoring.MonitoringEvent;
import jadex.commons.DebugException;
import jadex.commons.IResultCommand;
import jadex.commons.MutableObject;
import jadex.commons.SReflect;
import jadex.commons.Tuple2;
import jadex.commons.concurrent.Executor;
import jadex.commons.concurrent.IExecutable;
import jadex.commons.concurrent.TimeoutException;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.FutureHelper;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ISuspendable;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminationCommand;
import jadex.commons.future.ThreadLocalTransferHelper;

/**
 *  This feature provides component step execution.
 */
public class ExecutionComponentFeature	extends	AbstractComponentFeature implements IExecutionFeature, IInternalExecutionFeature, IExecutable
{
	//-------- constants --------
	
	/** Debug flag. */
	// Hack!!! Non-final to be setable from Starter 
	public static boolean DEBUG = false;
	
	/** Constant for step event. */
	public static final String TYPE_STEP = "step";
	
	//-------- attributes --------
	
	/** The component steps. */
	protected TreeSet<StepInfo> steps;
	
	/** The stepcnt - used to keep insertion order of same priority elements in the queue. */
	protected int stepcnt;
	
	/** The id of the step at which the execution was stopped because of a breakpoint. */
	protected int bpstepid = -1;
	
	/** The step at which the endstate begins. */
	protected int endstepcnt = -1;
	
	/** The current timer. */
	protected List<ITimer> timers = new ArrayList<ITimer>();
	
	/** Retained listener notifications when switching threads due to blocking. */
	protected List<Tuple2<Future<?>, IResultListener<?>>>	notifications;
	
	/** Flag for testing double execution. */
	protected volatile boolean executing;
	
	/** The thread currently executing the component (null for none). */
	protected Thread componentthread;
	
	/** The blocked threads by monitor. */
	protected Map<Object, Executor>	blocked; 
	
	/** The flag for a requested step (true when a step is allowed in stepwise execution). */
	protected String stepinfo;
	
	/** The future to be informed, when the requested step is finished. */
	protected Future<Void> stepfuture;
	
	/** The parent adapter (cached for speed). */
	protected IInternalExecutionFeature parenta;

	/** The synchronous subcomponents that want to be executed (if any). */
	protected Set<IInternalExecutionFeature> subcomponents;

	/** Future for signalling that end of agenda execution has been reached. */
	protected Future<Void> endagenda;
	
	//-------- constructors --------
	
	/**
	 *  Create the feature.
	 */
	public ExecutionComponentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
		this.endagenda = new Future<Void>();
	}
	
	/**
	 *  Shutdown the feature.
	 */
	public IFuture<Void> shutdown()
	{
		Future<Void> ret = new Future<Void>();
		
		// remember cnt when endstate starts
		this.endstepcnt = stepcnt;
		
//		if(getComponent().getComponentIdentifier().equals(getComponent().getComponentIdentifier().getRoot()))
//			System.out.println("shut platform");
		
//		System.out.println("shutdown start: "+getComponent().getComponentIdentifier());
		
		endagenda.addResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
			{
				doCleanup(new StepAborted());
				
				super.customResultAvailable(result);
			}
		});
		
		// ensure that agenda is cleaned up
		wakeup();
		
		return ret;
//		return IFuture.DONE;
	}
	
	/**
	 *  Check if the feature potentially executed user code in body.
	 *  Allows blocking operations in user bodies by using separate steps for each feature.
	 *  Non-user-body-features are directly executed for speed.
	 *  If unsure just return true. ;-)
	 */
	public boolean	hasUserBody()
	{
		return false;
	}
	
	/**
	 *  Kill is only invoked, when shutdown of some (e.g. other) feature does not return due to timeout.
	 *  The feature should do any kind of possible cleanup, but no asynchronous operations.
	 */
	@Override
	public void kill()
	{
		doCleanup(new ThreadDeath());
	}
	
	/**
	 *  Shared cleanup code for shutdown and kill.
	 */
	protected void doCleanup(Error e)
	{
//		System.out.println("shutdown end: "+getComponent().getComponentIdentifier());
		
		// Should not wake up all blocked threads at the same time?!
		// Could theoretically catch the threaddeath and do sth what is not guarded against concurrent access
//		if(blocked!=null && blocked.size()>0)
//			System.out.println("blocked: "+blocked.size());
		while(blocked!=null && !blocked.isEmpty())
		{
			// Unblock throwing thread death as component already has been terminated.
			unblock(blocked.keySet().iterator().next(), e);
//			unblock(blocked.keySet().iterator().next(), null);
		}
//		
		if(parenta!=null)
		{
			parenta.removeSubcomponent(ExecutionComponentFeature.this);
		}
	}
	
	//-------- IComponentFeature interface --------
	
	/**
	 *  Get the user interface type of the feature.
	 */
	public Class<?>	getType()
	{
		return IExecutionFeature.class;
	}
	
	/**
	 *  Create an instance of the feature.
	 */
	public IComponentFeature createInstance(IInternalAccess access, ComponentCreationInfo info)
	{
		return new ExecutionComponentFeature(access, info);
	}
	
	//-------- IExecutionFeature interface --------
	
	// for debugging: remember stack trace of stack addition
	protected Map<IComponentStep<?>, Exception>	stepadditions;
	
	/**
	 *  Execute a component step.
	 */
	public <T>	IFuture<T> scheduleStep(IComponentStep<T> step)
	{
		return scheduleStep(IExecutionFeature.STEP_PRIORITY_NOMRAL, step);
	}
	
	/**
	 *  Execute a component step.
	 *  @param step The component step.
	 *  @param priority The step priority (0 is default).
	 */
	public <T>	IFuture<T> scheduleStep(int priority, IComponentStep<T> step)
	{
		final Future<T> ret = createStepFuture(step);
		
		synchronized(this)
		{
			// Todo: synchronize with last step!
			if(IComponentDescription.STATE_TERMINATED.equals(getComponent().getComponentDescription().getState()))
			{
				ret.setException(new ComponentTerminatedException(getComponent().getComponentIdentifier()));
			}
			else
			{
				int prio = step instanceof IPriorityComponentStep? ((IPriorityComponentStep<?>)step).getPriority(): priority;
				addStep(new StepInfo(step, ret, new ThreadLocalTransferHelper(true), prio, stepcnt++));
				
//				System.out.println("steps: "+steps);
				
				if(DEBUG)
				{
					if(stepadditions==null)
					{
						stepadditions	= new HashMap<IComponentStep<?>, Exception>();
					}
					stepadditions.put(step, new DebugException(step.toString()));
				}
			}
		}

		if(!ret.isDone())
		{
			wakeup();
		}
		
		return ret;
	}
	
	/**
	 * Repeats a ComponentStep periodically, until terminate() is called on result future or a failure occurs in a step.
	 * @param initialDelay delay before first execution in milliseconds
	 * @param delay delay between scheduled executions of the step in milliseconds
	 * @param step The component step
	 * @return The intermediate results
	 */
	@Override
	public <T> ISubscriptionIntermediateFuture<T> repeatStep(long initialDelay, long delay, IComponentStep<T> step)
	{
		return repeatStep(initialDelay, delay, step, false);
	}
	
	/**
	 * Repeats a ComponentStep periodically, until terminate() is called on result future.
	 * 
	 * Warning: In order to avoid memory leaks, the returned subscription future does NOT store
	 * values, requiring the addition of a listener within the same step the repeat
	 * step was schedule.
	 * 
	 * @param initialDelay delay before first execution in milliseconds
	 * @param delay delay between scheduled executions of the step in milliseconds
	 * @param step The component step
	 * @param ignorefailures Don't terminate repeating after a failed step.
	 * @return The intermediate results
	 */
	@Override
	public <T> ISubscriptionIntermediateFuture<T> repeatStep(long initialDelay, final long delay, final IComponentStep<T> step, final boolean ignorefailures)
	{
		final MutableObject<Boolean>	stillRepeating	= new MutableObject<Boolean>(true);
		
		final SubscriptionIntermediateFuture<T>	ret	= new SubscriptionIntermediateFuture<T>(new TerminationCommand()
		{
			@Override
			public void terminated(Exception reason)
			{
				stillRepeating.set(Boolean.FALSE);
			}
		}, false);
		
		// schedule the initial step
		waitForDelay(initialDelay, step)
			.addResultListener(new IResultListener<T>()
		{
			@Override
			public void resultAvailable(T result)
			{
				ret.addIntermediateResult(result);
				proceed();
			}
			
			@Override
			public void exceptionOccurred(Exception exception)
			{
				if(ignorefailures)
				{
					proceed();
				}
				else
				{
					ret.setException(exception);
				}
			}
			
			private void proceed()
			{
				if (Boolean.TRUE.equals(stillRepeating.get()))
				{
					// reschedule this step if we're still repeating
					waitForDelay(delay, step)
						.addResultListener(this);
				}
				else
				{
					ret.setFinished();
				}
			}
		});
		
		return ret;
	}

	/**
	 *  Wait for some time and execute a component step afterwards.
	 */
	public <T> IFuture<T> waitForDelay(final long delay, final IComponentStep<T> step)
	{
		return waitForDelay(delay, step, false);
	}
	
	/**
	 *  Wait for some time and execute a component step afterwards.
	 */
	public <T> IFuture<T> waitForDelay(final long delay, final IComponentStep<T> step, final boolean realtime)
	{
		// todo: remember and cleanup timers in case of component removal.
		
		final Future<T> ret = new Future<T>();

		if(delay>=0)
		{
			// todo: support getLocal... with proxy==false
	//		IClockService cs = SServiceProvider.getLocalService(getComponent(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM);
			SServiceProvider.getService(getComponent(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM, false)
				.addResultListener(createResultListener(new ExceptionDelegationResultListener<IClockService, T>(ret)
			{
				public void customResultAvailable(IClockService cs)
				{
					ITimedObject	to	= new ITimedObject()
					{
						public void timeEventOccurred(long currenttime)
						{
	//						System.out.println("step: "+step);
							scheduleStep(step).addResultListener(createResultListener(new DelegationResultListener<T>(ret)));
						}
						
						public String toString()
						{
							return "waitForDelay[Step]("+getComponent().getComponentIdentifier()+")";
						}
					};
					if(realtime)
					{
						cs.createRealtimeTimer(delay, to);
					}
					else
					{
						cs.createTimer(delay, to);					
					}
				}
			}));
		}
		else
		{
			getComponent().getLogger().warning("WaitFor will never complete: "+step);
		}
		
		return ret;
	}
	
	/**
	 *  Wait for some time.
	 */
	public IFuture<Void> waitForDelay(final long delay)
	{
		return waitForDelay(delay, false);
	}

	/**
	 *  Wait for some time.
	 */
	public IFuture<Void> waitForDelay(final long delay, final boolean realtime)
	{
		final Future<Void> ret = new Future<Void>();
		
		SServiceProvider.getService(getComponent(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM, false)
			.addResultListener(createResultListener(new ExceptionDelegationResultListener<IClockService, Void>(ret)
		{
			public void customResultAvailable(IClockService cs)
			{
				ITimedObject	to	=  	new ITimedObject()
				{
					public void timeEventOccurred(long currenttime)
					{
						scheduleStep(new IComponentStep<Void>()
						{
							public IFuture<Void> execute(IInternalAccess ia)
							{
								ret.setResult(null);
								return IFuture.DONE;
							}
							
							public String toString()
							{
								return "waitForDelay("+getComponent().getComponentIdentifier()+")";
							}
						}).addResultListener(new IResultListener<Void>()
						{
							public void resultAvailable(Void result)
							{
							}
							
							public void exceptionOccurred(Exception exception)
							{
								// Ignore outdated timer entries when component is already dead.
								// propblem this can occur on clock thread
//								if(!(exception instanceof ComponentTerminatedException) || !((ComponentTerminatedException)exception).getComponentIdentifier().equals(getComponent().getComponentIdentifier()))
//								{
									ret.setExceptionIfUndone(exception);									
//								}
							}
						});
					}
					
					public String toString()
					{
						return "waitForDelay("+getComponent().getComponentIdentifier()+")";
					}
				};
				
				if(realtime)
				{
					cs.createRealtimeTimer(delay, to);
				}
				else
				{
					cs.createTimer(delay, to);
				}
			}
		}));
		
		return ret;
	}
	
//	/**
//	 *  Wait for some time.
//	 */
//	public IFuture<ITimer> waitForDelayWithTimer(final long delay, final IComponentStep<?> step)
//	{
//		final Future<ITimer> ret = new Future<ITimer>();
//		
//		IClockService cs = SServiceProvider.getLocalService(getComponent(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM);
//		ITimedObject	to	=  	new ITimedObject()
//		{
//			public void timeEventOccurred(long currenttime)
//			{
//				scheduleStep(step);
//			}
//			
//			public String toString()
//			{
//				return "waitForDelay("+getComponent().getComponentIdentifier()+")";
//			}
//		};
//		
//		ITimer timer = cs.createTimer(delay, to);
//		ret.setResult(timer);
//		
//		return ret;
//	}
	
	/**
	 *  Wait for the next tick.
	 *  @param time The time.
	 */
	public IFuture<Void> waitForTick(final IComponentStep<Void> run)
	{
//		final Future<TimerWrapper> ret = new Future<TimerWrapper>();
		final Future<Void> ret = new Future<Void>();
		
		IClockService cs = SServiceProvider.getLocalService(getComponent(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM);
		final ITimer[] ts = new ITimer[1];
		ts[0] = cs.createTickTimer(new ITimedObject()
		{
			public void timeEventOccurred(final long currenttime)
			{
				try
				{
					scheduleStep(new ExecuteWaitForStep(ts[0], run));
				}
				catch(ComponentTerminatedException e)
				{
				}
			}
		});
		if(timers==null)
			timers	= new ArrayList<ITimer>();
		timers.add(ts[0]);
//		ret.setResult(new TimerWrapper(ts[0]));
		ret.setResult(null);
		
		return ret;
	}
	
	/**
	 *  Wait for the next tick.
	 *  @param time The time.
	 */
	public IFuture<Void> waitForTick()
	{
		final Future<Void> ret = new Future<Void>();
		
		IClockService cs = SServiceProvider.getLocalService(getComponent(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM);
		final ITimer[] ts = new ITimer[1];
		ts[0] = cs.createTickTimer(new ITimedObject()
		{
			public void timeEventOccurred(final long currenttime)
			{
				ret.setResult(null);
			}
		});
		if(timers==null)
			timers	= new ArrayList<ITimer>();
		timers.add(ts[0]);
		
		return ret;
	}
	
	// todo:?
//	/**
//	 *  Wait for some time and execute a component step afterwards.
//	 */
//	public IFuture waitForImmediate(long delay, IComponentStep step);
	
	/** Flag to indicate bootstrapping execution of main thread (only for platform, hack???). */
	protected volatile boolean bootstrap;
	
	/** Flag to indicate that the execution service has become available during bootstrapping (only for platform, hack???). */
	protected volatile boolean available;
	
	/**
	 *  Trigger component execution.
	 */
	public void	wakeup()
	{
		if(getComponent().getComponentDescription().isSynchronous())
		{
			// Add to parent and wake up parent.
			if(parenta==null)
			{
				IComponentManagementService cms = SServiceProvider.getLocalService(getComponent(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM, false);
				cms.getExternalAccess(getComponent().getComponentIdentifier().getParent())
					.addResultListener(new DefaultResultListener<IExternalAccess>()
				{
					public void resultAvailable(IExternalAccess exta)
					{
						exta.scheduleStep(new IComponentStep<Void>()
						{
							public IFuture<Void> execute(IInternalAccess ia)
							{
								parenta	= (IInternalExecutionFeature)ia.getComponentFeature(IExecutionFeature.class);
								parenta.addSubcomponent(ExecutionComponentFeature.this);
								parenta.wakeup();
								return IFuture.DONE;
							}
						});
					}
					
					public void exceptionOccurred(Exception exception)
					{
						exception.printStackTrace();
					}
				});
			}
			else
			{
				parenta.addSubcomponent(this);
				parenta.wakeup();
			}
		}
		else
		{
			ServiceQuery<IExecutionService> query = new ServiceQuery<IExecutionService>(IExecutionService.class, RequiredServiceInfo.SCOPE_PLATFORM, null, component.getComponentIdentifier(), null);
			IExecutionService exe = ServiceRegistry.getRegistry(component).searchServiceSync(query);
			// Hack!!! service is foudn before it is started, grrr.
			if (exe != null && ((IService)exe).isValid().get().booleanValue())	// Hack!!! service is raw
			{
				if(bootstrap)
				{
					// Execution service found during bootstrapping execution -> stop bootstrapping as soon as possible.
					available	= true;
				}
				else
				{
					exe.execute(ExecutionComponentFeature.this);
				}
			}
			else
			{
				available	= false;
				// Happens during platform bootstrapping -> execute on platform rescue thread.
				if(!bootstrap)
				{
					bootstrap	= true;
					Starter.scheduleRescueStep(getComponent().getComponentIdentifier().getRoot(), new Runnable()
					{
						public void run()
						{
							boolean	again	= true;
							while(!available && again)
							{
								again	= execute();
							}
							bootstrap	= false;
							
							if(again)
							{		
								// Bootstrapping finished -> do real kickoff
								wakeup();
							}
						}
					});
				}
			}
//			SServiceProvider.getService(component, IExecutionService.class, RequiredServiceInfo.SCOPE_PLATFORM, false)
//				.addResultListener(new IResultListener<IExecutionService>()
//			{
//				public void resultAvailable(IExecutionService exe)
//				{
//					// Hack!!! service is foudn before it is started, grrr.
//					if(((IService)exe).isValid().get().booleanValue())	// Hack!!! service is raw
//					{
//						if(bootstrap)
//						{
//							// Execution service found during bootstrapping execution -> stop bootstrapping as soon as possible.
//							available	= true;
//						}
//						else
//						{
//							exe.execute(ExecutionComponentFeature.this);
//						}
//					}
//					else
//					{
//						exceptionOccurred(null);
//					}
//				}
//				
//				public void exceptionOccurred(Exception exception)
//				{
//					available	= false;
//					// Happens during platform bootstrapping -> execute on platform rescue thread.
//					if(!bootstrap)
//					{
//						bootstrap	= true;
//						Starter.scheduleRescueStep(getComponent().getComponentIdentifier().getRoot(), new Runnable()
//						{
//							public void run()
//							{
//								boolean	again	= true;
//								while(!available && again)
//								{
//									again	= execute();
//								}
//								bootstrap	= false;
//								
//								if(again)
//								{		
//									// Bootstrapping finished -> do real kickoff
//									wakeup();
//								}
//							}
//						});
//					}
//				}
//			});
		}
	}
	
	/**
	 *  Do a step of a suspended component.
	 */
	public IFuture<Void> doStep(String stepinfo)
	{
		Future<Void> ret = new Future<Void>();
		synchronized(this)
		{
			if(!IComponentDescription.STATE_SUSPENDED.equals(getComponent().getComponentDescription().getState()))
			{
				ret.setException(new IllegalStateException("Component not suspended: "+getComponent().getComponentIdentifier()));
			}
			else if(this.stepinfo!=null || stepfuture!=null)
			{
				ret.setException(new RuntimeException("Only one step allowed at a time."));
			}
			else
			{
//				this.dostep	= true;		
				this.stepfuture = ret;
				this.stepinfo = stepinfo;
			}
		}
		
		wakeup();
		
		return ret;
	}

	/**
	 *  Test if current thread is the component thread.
	 *  @return True if the current thread is the component thread.
	 */
	public boolean isComponentThread()
	{
		return Thread.currentThread()==componentthread || 
			IComponentDescription.STATE_TERMINATED.equals(getComponent().getComponentDescription().getState())
				&& Starter.isRescueThread(getComponent().getComponentIdentifier());
	}
	
	/**
	 *  Create a result listener that is executed on the
	 *  component thread.
	 */
	public <T> IResultListener<T> createResultListener(IResultListener<T> listener)
	{
		return new ComponentResultListener<T>(listener, component);
	}
	
	/**
	 *  Create a result listener that is executed on the
	 *  component thread.
	 */
	public <T> IIntermediateResultListener<T> createResultListener(IIntermediateResultListener<T> listener)
	{
		return new IntermediateComponentResultListener<T>(listener, component);
	}
	
	//-------- IInternalExecutionFeature --------
	
	/**
	 *  Add a synchronous subcomponent that will run on its parent's thread.
	 */
	public void	addSubcomponent(IInternalExecutionFeature sub)
	{
		synchronized(this)
		{
			if(subcomponents==null)
			{
				subcomponents	= new HashSet<IInternalExecutionFeature>();
			}
			subcomponents.add(sub);
		}
	}

	/**
	 *  Remove a synchronous subcomponent.
	 */
	public void	removeSubcomponent(IInternalExecutionFeature sub)
	{
		synchronized(this)
		{
			if(subcomponents!=null)
			{
				subcomponents.remove(sub);
			}
		}
	}
	
	
	/**
	 *  Block the current thread and allow execution on other threads.
	 *  @param monitor	The monitor to wait for.
	 *  @param realtime Flag if timeout is realtime (in contrast to simulation time).
	 */
 	public void	block(final Object monitor, long timeout, boolean realtime)
	{
		if(!isComponentThread())
		{
			throw new RuntimeException("Can only block current component thread: "+componentthread+", "+Thread.currentThread());
		}
		
		if(parenta!=null)
		{
			parenta.block(monitor, timeout, realtime);
		}
		else
		{
			// Retain listener notifications for new component thread.
			assert notifications==null;
			notifications	= FutureHelper.removeStackedListeners();
			
			Executor	exe	= Executor.EXECUTOR.get();
			if(exe==null)
			{
				throw new RuntimeException("Cannot block: no executor");
			}
			
			beforeBlock();
			
			this.executing	= false;
			this.componentthread	= null;
			
			if(blocked==null)
			{
				blocked	= new HashMap<Object, Executor>();
			}
			blocked.put(monitor, exe);
			
			// Flag to check if unblocked before timeout
			final boolean[]	unblocked	= new boolean[1];
			
			if(timeout!=Timeout.NONE)
			{
				final Exception ex	= Future.DEBUG ? new DebugException("Timeout: "+timeout) : null;
				waitForDelay(timeout, realtime)
					.addResultListener(new IResultListener<Void>()
				{
					public void resultAvailable(Void result)
					{
						if(!unblocked[0])
						{
							unblock(monitor, new TimeoutException(Future.DEBUG ? "" : "Use PlatformConfiguration.setDebugFutures(true) for timeout cause.", ex));
						}
					}
					
					public void exceptionOccurred(Exception exception)
					{
					}
				});
			}
			
			
			boolean	threaddeath	= false;
			try
			{
				exe.blockThread(monitor);
				// Todo: wait for blocked threads to be resumed before terminating component
				if(IComponentDescription.STATE_TERMINATED.equals(getComponent().getComponentDescription().getState()))
				{
					throw new ThreadDeath();
				}
			}
			catch(ThreadDeath e)
			{
				threaddeath	= true;
				throw e;
			}
			finally
			{
//				if(getComponent().toString().indexOf("Leaker")!=-1)
//				{
//					System.out.println("block done "+Thread.currentThread());
//				}
				
				unblocked[0]	= true;
				
				// Todo: should also work for thread death (i.e. blocked threads terminated before component is gone).
				assert threaddeath || !IComponentDescription.STATE_TERMINATED.equals(getComponent().getComponentDescription().getState());
				
				synchronized(this)
				{
					if(executing)
					{
						System.err.println(getComponent().getComponentIdentifier()+": double execution");
						new RuntimeException("executing: "+getComponent().getComponentIdentifier()).printStackTrace();
					}
					this.executing	= true;
				}
		
				this.componentthread	= Thread.currentThread();
				
				afterBlock();
			}
		}
	}
	
	/**
	 *  Unblock the thread waiting for the given monitor
	 *  and cease execution on the current thread.
	 *  @param monitor	The monitor to notify.
	 */
	public void	unblock(Object monitor, Throwable exception)
	{
		if(!isComponentThread())
		{
			throw new RuntimeException("Can only unblock from component thread: "+componentthread+", "+Thread.currentThread());
		}
		
		if(parenta!=null)
		{
			parenta.unblock(monitor, exception);
		}
		else
		{
//			System.out.println("unblock: "+monitor);
			Executor exe = blocked.remove(monitor);
			if(blocked.isEmpty())
			{
				blocked	= null;
			}
					
			exe.switchThread(monitor, exception);
		}
	}
	
	/**
	 *  Called before blocking the component thread.
	 */
	protected void	beforeBlock()
	{
//		System.out.println("beforeBlock: "+(ComponentSuspendable)ISuspendable.SUSPENDABLE.get()+", "+((ComponentSuspendable)ISuspendable.SUSPENDABLE.get()).getFuture());
	}
	
	/**
	 *  Called after unblocking the component thread.
	 */
	protected void	afterBlock()
	{
//		System.out.println("afterBlock: "+(ComponentSuspendable)ISuspendable.SUSPENDABLE.get()+", "+((ComponentSuspendable)ISuspendable.SUSPENDABLE.get()).getFuture());
	}
	
	//-------- IExecutable interface --------
	
	/**
	 *  Execute the executable.
	 *  @return True, if the object wants to be executed again.
	 */
	public boolean execute()
	{
		synchronized(this)
		{
			if(executing)
			{
				System.err.println(getComponent().getComponentIdentifier()+": double execution"+" "+Thread.currentThread()+" "+componentthread);
				new RuntimeException("executing: "+getComponent().getComponentIdentifier()).printStackTrace();
			}
			executing	= true;
		}

		// Todo: termination and exception!?
//		// Note: wakeup() can be called from arbitrary threads (even when the
//		// component itself is currently running. I.e. it cannot be ensured easily
//		// that an execution task is enqueued and the component has terminated
//		// meanwhile.
//		if(!IComponentDescription.STATE_TERMINATED.equals(getComponent().getComponentDescription().getState()))
//		{
//			if(exception!=null)
//			{
//				this.executing	= false;
//				return false;	// Component already failed: tell executor not to call again. (can happen during failed init)
//			}
	
			ClassLoader cl = setExecutionState();
			
			// Process listener notifications from old component thread.
//			boolean notifexecuted	= false;
			if(notifications!=null)
			{
				FutureHelper.addStackedListeners(notifications);
				notifications	= null;
				
				// Todo: termination and exception!?
//				try
//				{
					FutureHelper.notifyStackedListeners();
//					notifexecuted	= true;
//				}
//				catch(Exception e)
//				{
//					fatalError(e);
//				}
//				catch(StepAborted sa)
//				{
//				}
//				catch(Throwable t)
//				{
//					fatalError(new RuntimeException(t));
//				}

			}
			
//			boolean	again	= false;
//			if(!breakpoint_triggered && !extexecuted  && !notifexecuted && (!IComponentDescription.STATE_SUSPENDED.equals(desc.getState()) || dostep))
//			{
//				try
//				{
////					if(getComponentIdentifier()!=null && getComponentIdentifier().getParent()==null)
////						System.out.println("Executing: "+getComponentIdentifier()+", "+Thread.currentThread());
//					again	= component.executeStep();
////					if(getComponentIdentifier()!=null && getComponentIdentifier().getParent()==null)
////						System.out.println("Not Executing: "+getComponentIdentifier()+", "+Thread.currentThread());
//				}
//				catch(Exception e)
//				{
//					fatalError(e);
//				}
//				catch(StepAborted sa)
//				{
//				}
//				catch(Throwable t)
//				{
//					fatalError(new RuntimeException(t));
//				}
//				if(dostep)
//				{
//					dostep	= false;
//					if(stepfuture!=null)
//					{
//						stepfuture.setResult(null);
//					}
//				}
//				
//				// Suspend when breakpoint is triggered.
//				if(!IComponentDescription.STATE_SUSPENDED.equals(desc.getState()))
//				{
//					if(component.isAtBreakpoint(desc.getBreakpoints()))
//					{
//						breakpoint_triggered	= true;
//						getCMS().addResultListener(new DefaultResultListener<IComponentManagementService>(logger)
//						{
//							public void resultAvailable(IComponentManagementService cms)
//							{
//								cms.suspendComponent(desc.getName());
//							}
//							
//							public void exceptionOccurred(Exception exception)
//							{
//								if(!(exception instanceof ComponentTerminatedException))
//								{
//									super.exceptionOccurred(exception);
//								}
//							}
//						});
//					}
//				}
//			}
			
//		boolean	cycle	= false;
//		if(IComponentDescription.STATE_ACTIVE.equals(getComponent().getComponentDescription().getState()) || dostep || stepfuture!=null)
//		{
//			try
//			{
//				dostep	= false;
//				cycle	= executeCycle();
//			}
//			catch(Exception e)
//			{
//				// Todo: fail fast vs robust components.
//				
//				StringWriter	sw	= new StringWriter();
//				e.printStackTrace(new PrintWriter(sw));
//				getComponent().getLogger().severe("Component cycle failed:\n"+sw);
//			}				
//		}	
			
		StepInfo stepi = null;
		boolean priostep = false;
		boolean	breakpoint_triggered = false;
		
		synchronized(this)
		{			
			if(steps!=null && steps.size()>0) 
			{				
//				if(getComponent().getComponentIdentifier().getName().indexOf("Hello")!=-1)
//					System.out.println("executing");
				StepInfo si = steps.first();
				priostep = si.getPriority()>=STEP_PRIORITY_IMMEDIATE;
		
				// Suspend when breakpoint is triggered.
				// Necessary because component wakeup could be called anytime even if is at breakpoint..
				if(!priostep && stepfuture==null && !IComponentDescription.STATE_SUSPENDED.equals(getComponent().getComponentDescription().getState())
					&& getComponent().getComponentDescription().getBreakpoints().length>0)
				{
					if(isAtBreakpoint(getComponent().getComponentDescription().getBreakpoints()))
					{
						breakpoint_triggered = true;
					}
				}		
		
//				if(getComponent().getComponentIdentifier().getName().indexOf("Hello")!=-1)
//					System.out.println("executing");
				if(priostep)
				{
					// remove the element
					stepi = removeStep();
				}
				else if(!breakpoint_triggered)
				{
					if((IComponentDescription.STATE_ACTIVE.equals(getComponent().getComponentDescription().getState())))
					{
						stepi = removeStep();
					}
					else if(stepfuture!=null)
					{
						boolean found = false;
						if(stepinfo!=null)
						{
							// search for right step via stepinfo
							for(StepInfo sti: steps)
							{
								if(stepinfo.equals(""+sti.getStepCount()))
								{
									stepi = sti;
									steps.remove(sti);
									publishStepEvent(sti, IMonitoringEvent.EVENT_TYPE_DISPOSAL);
									found = true;
									break;
								}
							}
							if(!found)
								getComponent().getLogger().warning("Step not found with id: "+stepinfo+"\n");
							
							stepinfo = null;
						}
						
						if(!found)
							stepi = removeStep();
					}
				}
			}
		}
		final StepInfo step = stepi;
		
		if(breakpoint_triggered)
		{
			IComponentManagementService cms = SServiceProvider.getLocalService(component, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM);
			cms.suspendComponent(getComponent().getComponentDescription().getName());
		}
		
		boolean	hasstep;
		if(step!=null)
		{
//			if(step.getPriority()<STEP_PRIORITY_IMMEDIATE && getComponent().getComponentFeature0(IMonitoringComponentFeature.class)!=null && 
//				getComponent().getComponentFeature(IMonitoringComponentFeature.class).hasEventTargets(PublishTarget.TOALL, PublishEventLevel.FINE))
//			{
//				getComponent().getComponentFeature(IMonitoringComponentFeature.class).publishEvent(new MonitoringEvent(getComponent().getComponentIdentifier(), 
//					getComponent().getComponentDescription().getCreationTime(), step.getStep().toString(), IMonitoringEvent.EVENT_TYPE_CREATION+"."
//					+IMonitoringEvent.SOURCE_CATEGORY_EXECUTION, null, System.currentTimeMillis(), PublishEventLevel.FINE), PublishTarget.TOALL);
//				// null was step.getCause()
//			}
			
			IFuture<?>	stepfut	= null;
			Throwable ex = null;
			try
			{
				boolean valid = true;
				if(step.getStep() instanceof IConditionalComponentStep<?>)
					valid = ((IConditionalComponentStep<?>)step.getStep()).isValid();
				
				// Prio steps are always ok?!
				int endstart = ((IInternalExecutionFeature)getComponent().getComponentFeature(IExecutionFeature.class)).getEndstateStart();
				boolean stateok  = priostep || endstart==-1 || step.getStepCount()>=endstart;
				
				// for test execution to ensure that not dropped steps cause problems
//				stateok = true;
				
				if(valid && stateok) 
				{
					step.getTransfer().afterSwitch();
//					if(getComponent().getComponentIdentifier().getName().indexOf("Execute")!=-1)
//						System.out.println("executed: "+step.getStep()+" "+step.getPriority()+" "+getComponent().getComponentDescription().getState());
					stepfut	= step.getStep().execute(component);
				}
				else
				{
					getComponent().getLogger().warning(!stateok? "Step omitted due to endstate:"+" "+step.getStep(): "Step invalid "+" "+step.getStep());
					ex = new StepAborted();
//					{
//						public void printStackTrace() 
//						{
//							super.printStackTrace();
//						}
//					};
				}
			}
			catch(Throwable t)
			{
				ex = t;
				
				if(!(t instanceof StepAborted))
				{
					StringWriter	sw	= new StringWriter();
					t.printStackTrace(new PrintWriter(sw));
					getComponent().getLogger().warning("Component step threw hard exception: "+step.getStep()+"\n"+sw);
				}
			}
			
			if(ex!=null)
			{
				if(ex instanceof StepAborted)
				{
					// Todo: plan for other uses of step aborted= -> step terminated exception in addition to step aborted error?
					ex	= new ComponentTerminatedException(component.getComponentIdentifier());
				}
				else if(ex instanceof ThreadDeath)
				{
					// Hard cleanup during kill.
					resetExecutionState(cl);
					throw (ThreadDeath)ex;
				}
				step.getFuture().setExceptionIfUndone(ex instanceof Exception? (Exception)ex: new RuntimeException(ex));

				// If no listener, print failed step to console for developer.
				// Hard step failure with uncatched exception is shown also when no debug.
				if(!step.getFuture().hasResultListener() &&
					(!(ex instanceof ComponentTerminatedException)
					|| !((ComponentTerminatedException)ex).getComponentIdentifier().equals(component.getComponentIdentifier())))
				{
					final Throwable fex = ex;
					// No wait for delayed listener addition for hard failures to print errors immediately.
//					waitForDelay(3000, true)
//						.addResultListener(new IResultListener<Void>()
//					{
//						public void resultAvailable(Void result)
//						{
//							if(!step.getSecondEntity().hasResultListener())
//							{
								// Todo: fail fast vs robust components.
								StringWriter	sw	= new StringWriter();
								fex.printStackTrace(new PrintWriter(sw));
								getComponent().getLogger().severe("Component step failed: "+step.getStep()+"\n"+sw);
								
								if(DEBUG && stepadditions!=null && stepadditions.containsKey(step.getStep()))
								{
									stepadditions.get(step.getStep()).printStackTrace();
								}
//							}
//						}
//						
//						public void exceptionOccurred(Exception exception)
//						{
//							// shouldn't happen:
//							exception.printStackTrace();
//						}
//					});
				}
			}
			
			if(stepfut!=null)
			{
				try
				{
					if(step.getFuture() instanceof IntermediateFuture)
					{
						stepfut.addResultListener(new IntermediateDelegationResultListener((IntermediateFuture)step.getFuture())
						{
							public void customResultAvailable(Collection result)
							{
								if(step.getPriority()<STEP_PRIORITY_IMMEDIATE && getComponent().getComponentFeature0(IMonitoringComponentFeature.class)!=null && 
									getComponent().getComponentFeature(IMonitoringComponentFeature.class).hasEventTargets(PublishTarget.TOALL, PublishEventLevel.FINE))
								{
									getComponent().getComponentFeature(IMonitoringComponentFeature.class).publishEvent(new MonitoringEvent(getComponent().getComponentIdentifier(), 
										getComponent().getComponentDescription().getCreationTime(), step.getStep().toString(), IMonitoringEvent.EVENT_TYPE_DISPOSAL+"."
										+IMonitoringEvent.SOURCE_CATEGORY_EXECUTION, null, System.currentTimeMillis(), PublishEventLevel.FINE), PublishTarget.TOALL);
									// null was step.getCause()
								}
								
								super.customResultAvailable(result);
							}
						});
					}
					else
					{
						stepfut.addResultListener(new DelegationResultListener(step.getFuture())
						{
							public void customResultAvailable(Object result)
							{
								if(step.getPriority()<STEP_PRIORITY_IMMEDIATE && getComponent().getComponentFeature0(IMonitoringComponentFeature.class)!=null && 
									getComponent().getComponentFeature(IMonitoringComponentFeature.class).hasEventTargets(PublishTarget.TOALL, PublishEventLevel.FINE))
								{
									getComponent().getComponentFeature(IMonitoringComponentFeature.class).publishEvent(new MonitoringEvent(getComponent().getComponentIdentifier(), 
										getComponent().getComponentDescription().getCreationTime(), step.getStep().toString(), IMonitoringEvent.EVENT_TYPE_DISPOSAL+"."
										+IMonitoringEvent.SOURCE_CATEGORY_EXECUTION, null, System.currentTimeMillis(), PublishEventLevel.FINE), PublishTarget.TOALL);
									// null was step.getCause()
								}
								
								super.customResultAvailable(result);
							}
						});
					}
		
					if(DEBUG && !step.getFuture().hasResultListener())
					{
						// Wait for delayed listener addition.
						waitForDelay(3000, true)
							.addResultListener(new IResultListener<Void>()
						{
							public void resultAvailable(Void result)
							{
								if(!step.getFuture().hasResultListener())
								{
									((Future<Object>)step.getFuture()).addResultListener(new IResultListener<Object>()
									{
										public void resultAvailable(Object result)
										{
											getComponent().getLogger().warning("No listener for component step: "+step.getFuture());
											
											if(DEBUG && stepadditions!=null && stepadditions.containsKey(step.getFuture()))
											{
												stepadditions.get(step.getStep()).printStackTrace();
											}
										}
										
										public void exceptionOccurred(Exception exception)
										{
											// Todo: fail fast vs robust components.
											
											SServiceProvider.getService(component, IExecutionService.class, RequiredServiceInfo.SCOPE_PLATFORM, false)
												.addResultListener(new IResultListener<IExecutionService>()
											{
												public void resultAvailable(IExecutionService exe)
												{
													// Hack!!! service is foudn before it is started, grrr.
													if(((IService)exe).isValid().get().booleanValue())	// Hack!!! service is raw
													{
														if(bootstrap)
														{
															// Execution service found during bootstrapping execution -> stop bootstrapping as soon as possible.
															available	= true;
														}
														else
														{
															exe.execute(ExecutionComponentFeature.this);
														}
													}
													else
													{
														exceptionOccurred(null);
													}
												}
												
												public void exceptionOccurred(Exception exception)
												{
													// Happens during platform bootstrapping -> execute on platform rescue thread.
													if(!bootstrap)
													{
														bootstrap	= true;
														Starter.scheduleRescueStep(getComponent().getComponentIdentifier().getRoot(), new Runnable()
														{
															public void run()
															{
																boolean	again	= true;
																while(!available && again)
																{
																	again	= execute();
																}
																bootstrap	= false;
																
																if(again)
																{					
																	// Bootstrapping finished -> do real kickoff
																	wakeup();
																}
															}
														});
													}
												}
											});
											StringWriter	sw	= new StringWriter();
											exception.printStackTrace(new PrintWriter(sw));
											getComponent().getLogger().severe("No listener for component step exception: "+step.getStep()+"\n"+sw);
											
											if(DEBUG && stepadditions!=null && stepadditions.containsKey(step.getStep()))
											{
												stepadditions.get(step.getStep()).printStackTrace();
											}
										}
									});
								}
							}
							
							public void exceptionOccurred(Exception exception)
							{
								// shouldn't happen:
								exception.printStackTrace();
							}
						});
					}
				}
				catch(Throwable e)
				{
					// Todo: fail fast vs robust components.
					if(!(e instanceof ThreadDeath))
					{
						StringWriter	sw	= new StringWriter();
						e.printStackTrace(new PrintWriter(sw));
						getComponent().getLogger().severe("Component step listener failed: "+step.getStep()+"\n"+sw);
						
						if(DEBUG && stepadditions!=null && stepadditions.containsKey(step.getStep()))
						{
							stepadditions.get(step.getStep()).printStackTrace();
						}
					}
				}
			}
			
			synchronized(this)
			{
				hasstep	= steps!=null && steps.size()>0;
			}
		}
		else
		{
			hasstep	= false;
		}
		
		boolean	cycle	= false;
		if(IComponentDescription.STATE_ACTIVE.equals(getComponent().getComponentDescription().getState()) || stepfuture!=null)
		{
			try
			{
				cycle	= executeCycle();
			}
			catch(Exception e)
			{
				// Todo: fail fast vs robust components.
				
				StringWriter	sw	= new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				getComponent().getLogger().severe("Component cycle failed:\n"+sw);
			}				
		}
		
		// In step mode, notify done step, if any.
		Future<Void>	stepfut	= null;
		synchronized(this)
		{
			if(stepfuture!=null && stepinfo==null)
			{
				stepfut	= stepfuture;
				stepfuture	= null;
			}
		}
		if(stepfut!=null)
		{
			stepfut.setResult(null);
		}

		resetExecutionState(cl);

		// Execute the subcomponents
		boolean ret = hasstep || cycle;
		IInternalExecutionFeature[] subs = null;
		synchronized(this)
		{
			if(subcomponents!=null)
			{
				subs = subcomponents.toArray(new IInternalExecutionFeature[subcomponents.size()]);
				subcomponents	= null;
			}
		}
		if(subs!=null)
		{
			for(IInternalExecutionFeature sub: subs)
			{
//				System.out.println("execute1: "+sub.getComponentIdentifier());
				this.componentthread = Thread.currentThread();
				boolean	again = ((IInternalExecutionFeature)sub).execute();
				this.componentthread = null;
				if(again)
				{
					addSubcomponent(sub);
				}
				ret	= again || ret;
			}
		}
		
		if(endstepcnt!=-1 && !ret && !endagenda.isDone())
		{
			this.componentthread	= Thread.currentThread();
			this.executing = true;
			
			endagenda.setResult(null);
			
			this.componentthread = null;
			this.executing = false;
		}
		
//		if(endstepcnt!=-1 && getComponent().getComponentIdentifier().equals(getComponent().getComponentIdentifier().getRoot()))
//			System.out.println("platform: "+steps.size());
		
		return ret;
	}

	/**
	 *  Set flags when entering thread.
	 *  @return	The previous context class loader.
	 */
	protected ClassLoader setExecutionState()
	{
		// Remember execution thread.
		this.componentthread	= Thread.currentThread();
		IComponentIdentifier.LOCAL.set(getComponent().getComponentIdentifier());
		IInternalExecutionFeature.LOCAL.set(getComponent());
		ClassLoader	cl	= Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(component.getClassLoader());
		ISuspendable.SUSPENDABLE.set(new ComponentSuspendable(getComponent()));
		return cl;
	}
	
	/**
	 *  Reset flags when exiting thread.
	 *  @param cl	The previous context class loader.
	 */
	protected void resetExecutionState(ClassLoader cl)
	{
		// Reset execution state.
		IComponentIdentifier.LOCAL.set(null);
		IInternalExecutionFeature.LOCAL.set(null);
		// Must reset service call settings when thread retreats from components
		CallAccess.resetCurrentInvocation();
		CallAccess.resetNextInvocation();
		Thread.currentThread().setContextClassLoader(cl);
		this.componentthread = null;
		executing	= false;
		ISuspendable.SUSPENDABLE.set(null);
	}

	/**
	 *  Components with autonomous behavior may override this method
	 *  to implement a recurring execution cycle.
	 *  @return true, if the execution should continue, false, if the component may become idle. 
	 */
	protected boolean	executeCycle()
	{
		return false;
	}
	
	/**
	 *  Create intermediate of direct future.
	 */
	protected <T> Future<T> createStepFuture(IComponentStep<T> step)
	{
		Future<T> ret;
		try
		{
			Method method = step.getClass().getMethod("execute", new Class[]{IInternalAccess.class});
			Class<?> clazz = method.getReturnType();
//			ret = FutureFunctionality.getDelegationFuture(clazz, new FutureFunctionality(getLogger()));
			// Must not be fetched before properties are available!
			ret = (Future<T>)FutureFunctionality.getDelegationFuture(clazz, new FutureFunctionality(new IResultCommand<Logger, Void>()
			{
				public Logger execute(Void args)
				{
					return getComponent().getLogger();
				}
			}));
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		
		return ret;
	}
	
	/**
	 *  Add a new step.
	 */
	protected void addStep(StepInfo step)
	{
		if(steps==null)
			steps	= new TreeSet<StepInfo>();
		steps.add(step);
		
		publishStepEvent(step, IMonitoringEvent.EVENT_TYPE_CREATION);
	}
	
	/**
	 *  Remove a new step.
	 */
	protected StepInfo removeStep()
	{
		assert steps!=null && !steps.isEmpty();
		StepInfo ret = steps.pollFirst();
		
		publishStepEvent(ret, IMonitoringEvent.EVENT_TYPE_DISPOSAL);
		
		return ret;
	}
	
	/**
	 *  Publish a step event.
	 */
	public void publishStepEvent(StepInfo step, String type)
	{
		if(step.getPriority()<IExecutionFeature.STEP_PRIORITY_IMMEDIATE && getComponent().getComponentFeature0(IMonitoringComponentFeature.class)!=null 
			&& getComponent().getComponentFeature(IMonitoringComponentFeature.class).hasEventTargets(PublishTarget.TOALL, PublishEventLevel.FINE))
		{
			MonitoringEvent event = new MonitoringEvent(getComponent().getComponentIdentifier(), getComponent().getComponentDescription().getCreationTime(), step.getStep().toString(), type+"."+TYPE_STEP, null, System.currentTimeMillis(), PublishEventLevel.FINE);
			// null was step.getCause()
			event.setProperty("sourcename", SReflect.getUnqualifiedClassName(step.getStep().getClass()));
			event.setProperty("details", getStepDetails(step));
			event.setProperty("id", step.getStepCount());
			getComponent().getComponentFeature(IMonitoringComponentFeature.class).publishEvent(event, PublishTarget.TOALL);
		}
	}
	
	/**
	 *  Get the details of a step.
	 */
	public Map<String, String> getStepDetails(StepInfo step)
	{
		Map<String, String>	ret = new LinkedHashMap<String, String>();
		
		ret.put("Class", step.getStep().getClass().getName());
		ret.put("Priority", ""+step.getPriority());
		ret.put("Id", ""+step.getStepCount());
		
		if(step.getStep() instanceof ITransferableStep)
		{
			Map<String, String> det = ((ITransferableStep)step.getStep()).getTransferableObject();
			if(det!=null)
				ret.putAll(det);
		}
			
		Field[] fields = step.getStep().getClass().getDeclaredFields();
		for(int i = 0; i < fields.length; i++) 
		{
			String valtext = null;
			try 
			{
				fields[i].setAccessible(true);
				Object val = fields[i].get(step.getStep());
				valtext = val == null ? "null" : val.toString();
			} 
			catch (Exception e) 
			{
				valtext = e.getMessage();
			}

			if(valtext != null) 
			{
				ret.put(fields[i].getName(), valtext);
			}
		}
		
//		StringBuffer buf = new StringBuffer();
//
//		buf.append("Class = ").append(step.getStep().getClass().getName()).append("\n");
//		buf.append("Priority = ").append(step.getPriority()).append("\n");
//		buf.append("Id = ").append(step.getStepCount()).append("\n");
//			
//		Field[] fields = step.getStep().getClass().getDeclaredFields();
//		for(int i = 0; i < fields.length; i++) 
//		{
//			String valtext = null;
//			try 
//			{
//				fields[i].setAccessible(true);
//				Object val = fields[i].get(step.getStep());
//				valtext = val == null ? "null" : val.toString();
//			} 
//			catch (Exception e) 
//			{
//				valtext = e.getMessage();
//			}
//
//			if(valtext != null) 
//			{
//				buf.append("\n");
//				buf.append(fields[i].getName()).append(" = ").append(valtext);
//			}
//		}
//
//		ret = buf.toString();
			
		return ret;
	}
	
	/**
	 *  Get the current steps.
	 *  @return The current steps.
	 */
	public synchronized List<StepInfo> getCurrentSteps()
	{
		List<StepInfo> ret = null;
		
		if(steps!=null && steps.size()>0)
		{
			ret = new ArrayList<StepInfo>(steps);
		}
		
		return ret;
	}
	
	/**
	 *  Get the current state as events.
	 */
	public List<IMonitoringEvent> getCurrentStateEvents()
	{
		List<IMonitoringEvent> ret = null;
		
		IExecutionFeature exef = getComponent().getComponentFeature0(IExecutionFeature.class);
		if(exef instanceof ExecutionComponentFeature)
		{
			List<StepInfo> steps = ((ExecutionComponentFeature)exef).getCurrentSteps();
			if(steps!=null)
			{
				ret = new ArrayList<IMonitoringEvent>();
				for(StepInfo step: steps)
				{
					if(step.getPriority()<IExecutionFeature.STEP_PRIORITY_IMMEDIATE)
					{
						MonitoringEvent event = new MonitoringEvent(getComponent().getComponentIdentifier(), getComponent().getComponentDescription().getCreationTime(), step.getStep().toString(),  IMonitoringEvent.EVENT_TYPE_CREATION+"."+TYPE_STEP, null, System.currentTimeMillis(), PublishEventLevel.FINE);
						// null was step.getCause()
						event.setProperty("sourcename", SReflect.getUnqualifiedClassName(step.getStep().getClass()));
						event.setProperty("details", getStepDetails(step));
						event.setProperty("id", step.getStepCount());
						ret.add(event);
					}
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Test if the agent's execution is currently at one of the
	 *  given breakpoints. If yes, the agent will be suspended by
	 *  the platform.
	 *  Available breakpoints can be specified in the
	 *  micro agent meta info.
	 *  @param breakpoints	An array of breakpoints.
	 *  @return True, when some breakpoint is triggered.
	 */
	public boolean isAtBreakpoint(String[] breakpoints)
	{
		boolean ret = false;
		if(steps!=null && steps.size()>0)
		{
			
			if(steps.first().getStepCount()==bpstepid)
			{
				bpstepid = -1;
			}
			else
			{
				ret = testIfBreakpoint(breakpoints);
			}
		}
		return ret;
	}
	
	/**
	 *  Kernel specific test if the step is a breakpoint.
	 */
	public boolean testIfBreakpoint(String[] breakpoints)
	{
		boolean ret = false;
		try
		{
//			System.out.println("testing: "+steps.first().getStep().getClass());
			Method m = steps.first().getStep().getClass().getMethod("execute", new Class[]{IInternalAccess.class});
			Breakpoint bp = m.getAnnotation(Breakpoint.class);
			if(bp!=null)
			{
				Set<String>	bps	= new HashSet<String>(Arrays.asList(breakpoints));
				String bpname = bp.value();
				// todo: support wildcard matching
				ret = bps.contains(bpname);
				if(ret)
					bpstepid = steps.first().getStepCount();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 *  Get the step number when endstate began.
	 *  @return The step cnt.
	 */
	public int getEndstateStart()
	{
		return endstepcnt;
	}
	
	/**
	 *  Wrap a timer and remove it from the agent when it is cancelled.
	 */
	protected class TimerWrapper implements ITimer 
	{
		//-------- attributes --------
		
		/** The wrapped timer. */
		ITimer	timer;
		
		//-------- constructors--------
		
		/**
		 *  Wrap a timer.
		 */
		public TimerWrapper(ITimer timer)
		{
			this.timer	= timer;
		}
		
		//-------- ITimer interface --------
		
		public void cancel()
		{
			assert timers!=null;
			timers.remove(timer);
			timer.cancel();
		}

		public long getNotificationTime()
		{
			return timer.getNotificationTime();
		}

		public ITimedObject getTimedObject()
		{
			return timer.getTimedObject();
		}

		public void setNotificationTime(long time)
		{
			timer.setNotificationTime(time);
		}

		public boolean equals(Object obj)
		{
			return timer.equals(obj);
		}

		public int hashCode()
		{
			return timer.hashCode();
		}

		public String toString()
		{
			return timer.toString();
		}
	}
	
	/**
	 *  Step to execute a wait for entry.
	 */
	public static class ExecuteWaitForStep implements IComponentStep<Void>
	{
		//-------- attributes --------

		/** The timer. */
		private final ITimer ts;

		/** The component step. */
		private final IComponentStep<Void> run;

		//-------- constructors--------

		/**
		 * This class is constructed with an array of {@link ITimer}s and the {@link IComponentStep}
		 * which is scheduled for execution.
		 * @param ts an array of {@link ITimer}s
		 * @param run the {@link IComponentStep} which is scheduled for execution
		 */
		public ExecuteWaitForStep(ITimer ts, IComponentStep<Void> run)
		{
			this.ts = ts;
			this.run = run;
		}

		//-------- methods --------

		/**
		 * Removes the first entry from the {@link ITimer} array from the micro agents
		 * {@link MicroAgent#timers} {@link List} and executes the {@link IComponentStep}.
		 */
		public IFuture<Void> execute(IInternalAccess ia)
		{
			assert ((ExecutionComponentFeature)ia.getComponentFeature(IExecutionFeature.class)).timers!=null;
			((ExecutionComponentFeature)ia.getComponentFeature(IExecutionFeature.class)).timers.remove(ts);
			run.execute(ia);
			return IFuture.DONE;
		}

		/**
		 * @return "microagent.waitFor_#" plus the hash code of this class
		 */
		public String toString()
		{
			return ts==null? super.toString(): ts.getTimedObject()!=null? ts.getTimedObject().toString(): ts.toString();
		}
		
		/**
		 * Returns the {@link IComponentStep} that is scheduled for execution.
		 * @return The {@link IComponentStep} that is scheduled for execution
		 */
		public IComponentStep<Void> getComponentStep()
		{
			return run;
		}
	}
	
	/**
	 *  Info struct for steps.
	 */
	public static class StepInfo implements Comparable<StepInfo>
	{
		/** The component step. */
		protected IComponentStep<?> step; 
		
		/** The result future. */
		protected Future<?> future; 
		
		/** The service call. */
		protected ThreadLocalTransferHelper transfer;
		
		/** The priority. */
		protected int priority;
		
		/** The number of the step (preserve insert order of same prio). */
		protected int stepcnt;
		
//		/** The component state (create, init, body, end). */
//		protected ComponentLifecycleState state;
		
//		/**
//		 *  Create a new StepInfo. 
//		 */
//		public StepInfo(IComponentStep<?> step, Future<?> future, ThreadLocalTransferHelper transfer, int stepcnt)
//		{
//			this(step, future, transfer, step instanceof IPriorityComponentStep? ((IPriorityComponentStep<?>)step).getPriority(): 0, stepcnt);
//		}
		
		/**
		 *  Create a new StepInfo. 
		 */
		public StepInfo(IComponentStep<?> step, Future<?> future, ThreadLocalTransferHelper transfer, 
			int priority, int stepcnt)
		{
			this.step = step;
			this.future = future;
			this.transfer = transfer;
			this.priority = priority;
			this.stepcnt = stepcnt;
		}

		/**
		 *  Get the step.
		 *  @return The step.
		 */
		public IComponentStep<?> getStep()
		{
			return step;
		}

		/**
		 *  Set the step.
		 *  @param step The step to set.
		 */
		public void setStep(IComponentStep<?> step)
		{
			this.step = step;
		}

		/**
		 *  Get the future.
		 *  @return The future.
		 */
		public Future<?> getFuture()
		{
			return future;
		}

		/**
		 *  Set the future.
		 *  @param future The future to set.
		 */
		public void setFuture(Future<?> future)
		{
			this.future = future;
		}

		/**
		 *  Get the transfer.
		 *  @return The transfer
		 */
		public ThreadLocalTransferHelper getTransfer()
		{
			return transfer;
		}

		/**
		 *  The transfer to set.
		 *  @param transfer The transfer to set
		 */
		public void setTransfer(ThreadLocalTransferHelper transfer)
		{
			this.transfer = transfer;
		}

		/**
		 *  Get the priority.
		 *  @return The priority
		 */
		public int getPriority()
		{
			return priority;
		}

		/**
		 *  The priority to set.
		 *  @param priority The priority to set
		 */
		public void setPriority(int priority)
		{
			this.priority = priority;
		}
		
		/**
		 *  Get the stepcnt.
		 *  @return The stepcnt
		 */
		public int getStepCount()
		{
			return stepcnt;
		}

		/**
		 *  The stepcnt to set.
		 *  @param stepcnt The stepcnt to set
		 */
		public void setStepCount(int stepcnt)
		{
			this.stepcnt = stepcnt;
		}
		
//		/**
//		 *  Get the state. 
//		 *  @return The state
//		 */
//		public ComponentLifecycleState getState()
//		{
//			return state;
//		}
//
//		/**
//		 *  Set the state.
//		 *  @param state The state to set
//		 */
//		public void setState(ComponentLifecycleState state)
//		{
//			this.state = state;
//		}
		
		/**
		 *  Compare two steps.
		 */
		public int compareTo(StepInfo o)
		{
			int ret = o.getPriority()-getPriority();
			if(ret==0)
				ret = getStepCount()-o.getStepCount();
			return ret;
		}

		/**
		 *  Get the string representation.
		 */
		public String toString()
		{
			return "StepInfo(priority=" + priority + ", step=" + step + ")";
		}
	}

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "ExecutionFeature("+getComponent().getComponentIdentifier()+")";
	}
}
