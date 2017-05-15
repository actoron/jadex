package jadex.platform.service.clock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClock;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.clock.ITimedObject;
import jadex.bridge.service.types.clock.ITimer;
import jadex.bridge.service.types.settings.ISettingsService;
import jadex.bridge.service.types.threadpool.IThreadPoolService;
import jadex.commons.IChangeListener;
import jadex.commons.IPropertiesProvider;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.concurrent.IThreadPool;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/**
 *  A clock service abstracts away from clock implementations.
 *  The clock service is meant to be kept constant during runtime.
 */
public class ClockService extends BasicService implements IClockService, IPropertiesProvider
{
	//-------- attributes --------
	
	/** The clock. */
	protected IClock clock;
	
	/** The threadpool. */
	protected IThreadPoolService threadpool;

	/** The clock listeners. */
	protected List listeners;
	
	/** The component. */
	protected IInternalAccess component;

	/** The clock type. */
	protected ClockCreationInfo cinfo;
	
	/** Was simulation set via argument? */
	protected Boolean simulation;
	
	/** The realtime timer. */
	protected java.util.Timer timer;
	
	//-------- constructors --------
	
	/**
	 *  Create a new clock service.
	 */
	public ClockService(ClockCreationInfo cinfo, IInternalAccess component, Boolean simulation)
	{
		this(cinfo, component, null, simulation);
	}
	
	/**
	 *  Create a new clock service.
	 */
	public ClockService(ClockCreationInfo cinfo, IInternalAccess component, Map properties, Boolean simulation)
	{
		super(component.getComponentIdentifier(), IClockService.class, properties);

		this.cinfo = cinfo;
		this.component = component;
		this.simulation = simulation;
		this.listeners = Collections.synchronizedList(new ArrayList());
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
		if(timer==null)
		{
//			System.out.println("create realtime timer: "+this);
			timer = new java.util.Timer(true);
		}
		TimerTask tt = new TimerTask()
		{
			public void run()
			{
				to.timeEventOccurred(System.currentTimeMillis());
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
//		System.out.println("start clock: "+this);
		
		final Future<Void> ret = new Future<Void>();
		
		SServiceProvider.getService(component, IThreadPoolService.class, RequiredServiceInfo.SCOPE_PLATFORM, false)
			.addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				threadpool = (IThreadPoolService)result;
				clock = createClock(cinfo, threadpool);
				clock.start();
				ClockService.super.startService().addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						SServiceProvider.getService(component, ISettingsService.class, RequiredServiceInfo.SCOPE_PLATFORM)
							.addResultListener(new IResultListener()
						{
							public void resultAvailable(Object result)
							{
								ISettingsService	settings	= (ISettingsService)result;
								settings.registerPropertiesProvider("clockservice", ClockService.this)
									.addResultListener(new DelegationResultListener(ret));
							}
							
							public void exceptionOccurred(Exception exception)
							{
								// No settings service: ignore.
								ret.setResult(null);
//								ret.setResult(getServiceIdentifier());
							}
						});
					}
				});
			}
		});
	
		return ret;
	}
	
	/**
	 *  Shutdown the service.
	 *  @param listener The listener.
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
				SServiceProvider.getService(component, ISettingsService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(new IResultListener<ISettingsService>()
				{
					public void resultAvailable(ISettingsService settings)
					{
						settings.deregisterPropertiesProvider("clockservice")
							.addResultListener(new DelegationResultListener<Void>(ret)
						{
							public void customResultAvailable(Void result)
							{
								ClockService.this.component	= null;
								ClockService.this.clock	= null;
								ret.setResult(null);
							}
						});
					}
					
					public void exceptionOccurred(Exception exception)
					{
						// No settings service: ignore.
						ClockService.this.component	= null;
						ClockService.this.clock	= null;
						ret.setResult(null);
					}
				});
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
	 *  Set the clock.
	 *  @param clock The new clock.
	 */
	public void setClock(String type, IThreadPool tp)
	{
		IClock clock;
		
		if(IClock.TYPE_CONTINUOUS.equals(type))
			clock = new ContinuousClock(this.clock, tp);
		else if(IClock.TYPE_SYSTEM.equals(type))
			clock = new SystemClock(this.clock, tp);
		else if(IClock.TYPE_TIME_DRIVEN.equals(type))
			clock = new SimulationTickClock(this.clock);
		else if(IClock.TYPE_EVENT_DRIVEN.equals(type))
			clock = new SimulationEventClock(this.clock);
		else
			throw new RuntimeException("Unknown clock type: "+type);
		
		this.clock.dispose();
		
		this.clock = clock;
		for(int i=0; i<listeners.size(); i++)
		{
			this.clock.addChangeListener((IChangeListener)listeners.get(i));
		}
	}
	
	/**
	 *  Create a clock.
	 */
	public static IClock createClock(ClockCreationInfo cinfo, IThreadPool tp)
	{
		IClock ret;
		
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
	public IFuture<Void> setProperties(Properties props)
	{
		// Do not change clock when explicitly started in specific mode
		if(simulation==null)
		{
			String	type	= props.getStringProperty("type");
			long	delta	= props.getLongProperty("delta");
			double	dilation	= props.getDoubleProperty("dilation");
			
			String	oldstate	= clock.getState();
			setClock(type, threadpool);
			clock.setDelta(delta);
			if(clock instanceof ContinuousClock)
				((ContinuousClock)clock).setDilation(dilation);
			
			if(IClock.STATE_RUNNING.equals(oldstate))
			{
				clock.start();
			}
		}
		
		return IFuture.DONE;
	}
	
	/**
	 *  Write current state into properties.
	 */
	public IFuture<Properties> getProperties()
	{
		Properties	props	= new Properties();
		props.addProperty(new Property("type", clock.getType()));
		props.addProperty(new Property("delta", ""+clock.getDelta()));
		if(clock instanceof ContinuousClock)
			props.addProperty(new Property("dilation", ""+((ContinuousClock)clock).getDilation()));
		
		return new Future<Properties>(props);
	}
}
