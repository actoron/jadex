package jadex.micro.examples.noplatform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimerTask;

import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.clock.IClock;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.clock.ITimedObject;
import jadex.bridge.service.types.clock.ITimer;
import jadex.commons.IChangeListener;
import jadex.commons.concurrent.IThreadPool;
import jadex.commons.future.IFuture;
import jadex.platform.service.clock.AbstractClock;
import jadex.platform.service.clock.ClockCreationInfo;
import jadex.platform.service.clock.ContinuousClock;
import jadex.platform.service.clock.IContinuousClock;
import jadex.platform.service.clock.ISimulationClock;
import jadex.platform.service.clock.SimulationEventClock;
import jadex.platform.service.clock.SimulationTickClock;
import jadex.platform.service.clock.SystemClock;

/**
 * 
 */
public class ClockService extends BaseService implements IClockService 
{
	//-------- attributes --------
	
	/** The clock. */
	protected IClock clock;
	
	/** The threadpool. */
	//protected IThreadPoolService threadpool;
	protected IThreadPool threadpool;

	/** The clock listeners. */
	protected List<IChangeListener> listeners;
	
	/** The clock type. */
	protected ClockCreationInfo cinfo;
	
	/** The realtime timer. */
	protected java.util.Timer timer;
	
	//-------- constructors --------
	
	/**
	 *  Create a new clock service.
	 * /
	public ClockService(IComponentIdentifier cid)
	{
		this(cid, null, null);
	}*/
	
	/**
	 *  Create a new clock service.
	 */
	public ClockService(IComponentIdentifier cid, ClockCreationInfo cinfo, IThreadPool threadpool)
	{
		super(cid, IClockService.class);
		this.listeners = Collections.synchronizedList(new ArrayList<>());
		this.threadpool = threadpool;
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
		return clock instanceof ContinuousClock? ((ContinuousClock)clock).getDilation() : 1;
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
//				System.out.println("create realtime timer: "+this);
			timer = new java.util.Timer(true);
		}
		TimerTask tt = new TimerTask()
		{
			public void run()
			{
				to.timeEventOccurred(System.currentTimeMillis());
			}
		};
//			try
//			{
			timer.schedule(tt, time);
//			}
//			catch(IllegalStateException e)
//			{
//				throw new IllegalStateException(e.getMessage())
//				{
//					@Override
//					public void printStackTrace()
//					{
//						Thread.dumpStack();
//						super.printStackTrace();
//					}
//				};
//			}
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
//		if(!shutdowned)
//		{
			this.listeners.remove(listener);
			clock.removeChangeListener(listener);
//		}
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
//		System.out.println("start clock: "+this+component.getComponentFeature(IExecutionFeature.class).isComponentThread());
		//threadpool = new JavaThreadPool(false);
		setClock(cinfo, threadpool);
		clock.start();
				
		return IFuture.DONE;
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
		
		return IFuture.DONE;
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
		
		clock = createClock(cinfo, tp);
		
		if(clock instanceof ISimulationClock)
			Starter.putPlatformValue(cid.getRoot(), SIMULATION_CLOCK_FLAG, Boolean.TRUE);
		else
			Starter.putPlatformValue(cid.getRoot(), SIMULATION_CLOCK_FLAG, Boolean.FALSE);
		
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
		if(cinfo==null)
		{
			ret = new SystemClock("systemclock", 1, tp);
		}
		else if(IClock.TYPE_SYSTEM.equals(cinfo.getClockType()))
		{
			ret = new SystemClock(cinfo.getName(), cinfo.getDelta(), tp);
		}
		else if(IClock.TYPE_CONTINUOUS.equals(cinfo.getClockType()))
		{
			ret = new ContinuousClock(cinfo.getName(), cinfo.getStart(), cinfo.getDilation(), tp);
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

	
	
}
