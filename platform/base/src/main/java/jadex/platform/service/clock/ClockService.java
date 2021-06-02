package jadex.platform.service.clock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import jadex.base.Starter;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.component.IInternalRequiredServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.clock.IClock;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.clock.ITimedObject;
import jadex.bridge.service.types.clock.ITimer;
import jadex.bridge.service.types.threadpool.IThreadPoolService;
import jadex.commons.IChangeListener;
import jadex.commons.SUtil;
import jadex.commons.concurrent.IThreadPool;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  A clock service abstracts away from clock implementations.
 *  The clock service is meant to be kept constant during runtime.
 */
public class ClockService extends BasicService implements IClockService
{
	//-------- attributes --------
	
	/** The clock. */
	protected IClock clock;
	
	/** The threadpool. */
	protected IThreadPoolService threadpool;

	/** The clock listeners. */
	protected List<IChangeListener> listeners;
	
	/** The component. */
	protected IInternalAccess component;

	/** The clock type. */
	protected ClockCreationInfo cinfo;
	
	/** The realtime timer. */
	protected java.util.Timer timer;
	
	//-------- constructors --------
	
	/**
	 *  Create a new clock service.
	 */
	public ClockService(ClockCreationInfo cinfo, IInternalAccess component)
	{
		this(cinfo, component, null);
	}
	
	/**
	 *  Create a new clock service.
	 */
	public ClockService(ClockCreationInfo cinfo, IInternalAccess component, Map properties)
	{
		super(component.getId(), IClockService.class, properties);

		this.cinfo = cinfo;
		this.component = component;
		this.listeners = Collections.synchronizedList(new ArrayList<>());
	}
	
	/**
	 *  Get the current time.
	 *  @return The current time.
	 */
	public long getTime()
	{
		return clock.getTime();
	}
	
	/**
	 *  Set the current time.
	 *  @param time The current time.
	 */
	public void setTime(long time)
	{
		clock.setStarttime(time);
		clock.reset();
	}
	
	/**
	 *  Get the current tick.
	 *  @return The current tick.
	 */
	public double getTick()
	{
		return clock.getTick();
	}
	
	/**
	 *  Get the clocks start time.
	 *  @return The start time.
	 */
	public long getStarttime()
	{
		return clock.getStarttime();
	}
	
	/**
	 *  Get the clock delta.
	 *  @return The clock delta.
	 */
	public long getDelta()
	{
		return clock.getDelta();
	}
	
	/**
	 *  Set the clock delta.
	 *  param delta The new clock delta.
	 */
	public void setDelta(long delta)
	{
		clock.setDelta(delta);
	}
	
	/**
	 *  Get the clock state.
	 *  @return The clock state.
	 */
	public String getState()
	{
		return clock.getState();
	}
	
	/**
	 *  Get the clocks dilation.
	 *  @return The clocks dilation.
	 *  // Hack!!! only for continuous clock.
	 */
	public double getDilation()
	{
		return clock instanceof ContinuousClock ? ((ContinuousClock)clock).getDilation() : 1;
	}
	
	/**
	 *  Set the clocks dilation.
	 *  @param dilation The clocks dilation.
	 *  // Hack. Remove? only for continuous
	 */
	public void setDilation(double dilation)
	{
		((ContinuousClock)clock).setDilation(dilation);
	}
	
	/**
	 *  Get the clock type.
	 *  @return The clock type.
	 */
	public String getClockType()
	{
		return clock.getType();
	}
	
	/**
	 *  Create a new timer.
	 *  The unit of the timespan value depends on the clock implementation.
	 *  For system clocks, the time value should adhere to the time representation
	 *  as used by {@link System#currentTimeMillis()}.
	 *  
	 *  @param timespan The relative timespan after which the timed object should be notified.
	 *  @param to The timed object.
	 */
	public ITimer createTimer(long time, ITimedObject to)
	{
		return clock.createTimer(time, to);
	}
	
	/**
	 *  Create a new tick timer.
	 *  todo: @param tickcount The number of ticks.
	 *  @param to The timed object.
	 */
	public ITimer createTickTimer(ITimedObject to)
	{
		return clock.createTickTimer(to);
	}
	
	/**
	 *  Create a new realtime timer.
	 *  
	 *  @param timespan The relative timespan after which the timed object should be notified.
	 *  @param to The timed object.
	 */
	public TimerTask createRealtimeTimer(final long time, final ITimedObject to)
	{
//		// For testing: when realtime is forcefully disabled (e.g. for tests) do not allow realtime timers.
//		if(!Starter.isRealtimeTimeout(getProviderId(), true))
//			throw new UnsupportedOperationException();
		
		if(timer==null)
		{
//			System.out.println("create realtime timer: "+this);
			timer = new java.util.Timer(true);
		}
		TimerTask tt = new TimerTask()
		{
			public void run()
			{
				try
				{
					to.timeEventOccurred(System.currentTimeMillis());
				}
				catch(Exception e)
				{
					System.err.println("Exception on timer: "+component+"\n"+SUtil.getExceptionStacktrace(e));
				}
			}
		};
//		try
//		{
			timer.schedule(tt, time);
//		}
//		catch(IllegalStateException e)
//		{
//			throw new IllegalStateException(e.getMessage())
//			{
//				@Override
//				public void printStackTrace()
//				{
//					Thread.dumpStack();
//					super.printStackTrace();
//				}
//			};
//		}
		return tt;
	}
	
	/**
	 *  Get the next timer.
	 *  @return The next timer.
	 */
	public ITimer getNextTimer()
	{
		return clock.getNextTimer();
	}
	
	/**
	 *  Get all active timers.
	 *  @return The active timers.
	 */
	public ITimer[] getTimers()
	{
		return clock.getTimers();
	}
	
	/**
	 *  Add a change listener.
	 *  @param listener The change listener.
	 */
	public void addChangeListener(IChangeListener listener)
	{
		this.listeners.add(listener);
		clock.addChangeListener(listener);
	}
	
	/**
	 *  Remove a change listener.
	 *  @param listener The change listener.
	 */
	public void removeChangeListener(IChangeListener listener)
	{
		if(!shutdowned)
		{
			this.listeners.remove(listener);
			clock.removeChangeListener(listener);
		}
	}
	
	/**
	 *  Advance one event.
	 *  @return True, if clock could be advanced.
	 */
	public boolean advanceEvent()
	{
		if(clock instanceof ISimulationClock)
			return ((ISimulationClock)clock).advanceEvent();
		else if(clock ==null)
			throw new RuntimeException("Clockservice already shutdowned (clock is null)");
		else
			throw new RuntimeException("AdvanceEvent only possible for simulation clocks: "+clock);
	}
	
	/**
	 *  Start the clock.
	 */
	public void start()
	{
		clock.start();
	}

	/**
	 *  Stop the clock.
	 */
	public synchronized void stop()
	{
		clock.stop();
	}
	
	//-------- IPlatformService interface --------
		
	/**
	 *  Start the service.
	 */
	public IFuture<Void> startService()
	{
//		System.out.println("start clock: "+this+component.getComponentFeature(IExecutionFeature.class).isComponentThread());
		
		final Future<Void> ret = new Future<Void>();

		threadpool = ((IInternalRequiredServicesFeature)component.getFeature(IRequiredServicesFeature.class)).getRawService(IThreadPoolService.class);
//		ISettingsService settings = component.getComponentFeature(IRequiredServicesFeature.class).getLocalService(new ServiceQuery<>( ISettingsService.class, ServiceScope.PLATFORM));

//		System.out.println("clock: "+ServiceCall.get);
		
//		component.getComponentFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>( IThreadPoolService.class, ServiceScope.PLATFORM, false))
//			.addResultListener(new ExceptionDelegationResultListener<IThreadPoolService, Void>(ret)
//		{
//			public void customResultAvailable(IThreadPoolService result)
//			{
//				threadpool = result;
				setClock(cinfo, threadpool);
				clock.start();
				
				ClockService.super.startService().addResultListener(new DelegationResultListener<Void>(ret)
				{
					public void customResultAvailable(Void result)
					{
//						ISettingsService settings = component.getFeature(IRequiredServicesFeature.class).getLocalService(new ServiceQuery<>(ISettingsService.class).setMultiplicity(0));
//						if(settings!=null)
//						{
//							settings.registerPropertiesProvider("clockservice", ClockService.this)
//								.addResultListener(new DelegationResultListener<Void>(ret));
//						}
//						else
//						{
							//System.out.println("Settings service not found by clock");
							ret.setResult(null);
//						}
						
//						component.getComponentFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>( ISettingsService.class, ServiceScope.PLATFORM))
//							.addResultListener(new IResultListener<ISettingsService>()
//						{
//							public void resultAvailable(ISettingsService settings)
//							{
//								settings.registerPropertiesProvider("clockservice", ClockService.this)
//									.addResultListener(new DelegationResultListener<Void>(ret));
//							}
//							
//							public void exceptionOccurred(Exception exception)
//							{
//								// No settings service: ignore.
//								ret.setResult(null);
////								ret.setResult(getId());
//							}
//						});
					}
				});
//			}
//		});
	
		return ret;
	}
	
	/**
	 *  Shutdown the service.
	 */
	public IFuture<Void> shutdownService()
	{
		clock.dispose();
		if(timer!=null)
		{
//			System.out.println("cancel realtime timer: "+this);
			timer.cancel();
			timer	= null;
		}
		listeners	= null;
		threadpool	= null;
		final Future<Void>	ret	= new Future<Void>();
		super.shutdownService().addResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
			{
//				ISettingsService	settings	= component.getFeature(IRequiredServicesFeature.class).getLocalService(new ServiceQuery<>(ISettingsService.class));
//				settings.deregisterPropertiesProvider("clockservice")
//					.addResultListener(new DelegationResultListener<Void>(ret)
//				{
//					public void customResultAvailable(Void result)
//					{
						ClockService.this.component	= null;
						ClockService.this.clock	= null;
						ret.setResult(null);
//					}
//				});
			}
		});
		
		return ret;
	}
	
	//--------- methods --------
	
	/**
	 *  Get the clock.
	 *  @return The clock.
	 * /
	public synchronized IClock getClock()
	{
		return clock;
	}*/
	
	/**
	 *  Change the clock.
	 *  @param type The new clock type
	 */
	public void setClock(String type, IThreadPool tp)
	{
		assert	clock!=null; // Initially created in startService...
		setClock(new ClockCreationInfo(type, null, clock.getTime(), clock.getDelta(), clock instanceof IContinuousClock ? ((IContinuousClock)clock).getDilation() : 1), tp);
	}
	
	/**
	 *  Create and set a new clock.
	 */
	public void	setClock(ClockCreationInfo cinfo, IThreadPool tp)
	{
		IClock old	= clock;
		boolean	start	= old!=null && IClock.STATE_RUNNING.equals(old.getState());
		
		clock	= createClock(cinfo, tp);
		
		if (clock instanceof ISimulationClock)
			Starter.putPlatformValue(component.getId().getRoot(), SIMULATION_CLOCK_FLAG, Boolean.TRUE);
		else
			Starter.putPlatformValue(component.getId().getRoot(), SIMULATION_CLOCK_FLAG, Boolean.FALSE);
		
		if(old!=null)
		{
			old.stop();
			((AbstractClock)clock).copyFromClock(old);
			old.dispose();
		}
		if(start)
		{
			this.clock.start();
		}
		
		for(int i=0; i<listeners.size(); i++)
		{
			this.clock.addChangeListener((IChangeListener)listeners.get(i));
		}
	}
	
	/**
	 *  Create a clock based on creation info.
	 */
	protected static IClock	createClock(ClockCreationInfo cinfo, IThreadPool tp)
	{
		IClock	ret;
		if(IClock.TYPE_CONTINUOUS.equals(cinfo.getClockType()))
		{
			ret = new ContinuousClock(cinfo.getName(), cinfo.getStart(), cinfo.getDilation(), tp);
		}
		else if(IClock.TYPE_SYSTEM.equals(cinfo.getClockType()))
		{
			ret = new SystemClock(cinfo.getName(), cinfo.getDelta(), tp);
		}
		else if(IClock.TYPE_TIME_DRIVEN.equals(cinfo.getClockType()))
		{
			ret = new SimulationTickClock(cinfo.getName(), cinfo.getStart(), cinfo.getDelta());
		}
		else if(IClock.TYPE_EVENT_DRIVEN.equals(cinfo.getClockType()))
		{
			ret = new SimulationEventClock(cinfo.getName(), cinfo.getStart(), cinfo.getDelta());
		}
		else
		{
			throw new RuntimeException("Unknown clock type: "+cinfo.getClockType());
		}
		return ret;
	}

	//-------- IPropertiesProvider interface --------
	
	/**
	 *  Update from given properties.
	 */
//	public IFuture<Void> setProperties(Properties props)
//	{
//		// Do not change clock when explicitly started in specific mode
//		if(simulation==null)
//		{
//			String	type	= props.getStringProperty("type");
//			long	delta	= props.getLongProperty("delta");
//			double	dilation	= props.getDoubleProperty("dilation");
//			
//			String	oldstate	= clock.getState();
//			setClock(type, threadpool);
//			clock.setDelta(delta);
//			if(clock instanceof ContinuousClock)
//				((ContinuousClock)clock).setDilation(dilation);
//			
//			if(IClock.STATE_RUNNING.equals(oldstate))
//			{
//				clock.start();
//			}
//		}
//		
//		return IFuture.DONE;
//	}
//	
//	/**
//	 *  Write current state into properties.
//	 */
//	public IFuture<Properties> getProperties()
//	{
//		Properties	props	= new Properties();
//		props.addProperty(new Property("type", clock.getType()));
//		props.addProperty(new Property("delta", ""+clock.getDelta()));
//		if(clock instanceof ContinuousClock)
//			props.addProperty(new Property("dilation", ""+((ContinuousClock)clock).getDilation()));
//		
//		return new Future<Properties>(props);
//	}
}
