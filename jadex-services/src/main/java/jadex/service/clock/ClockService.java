package jadex.service.clock;

import jadex.commons.IChangeListener;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.concurrent.IThreadPool;
import jadex.service.IService;

import java.util.ArrayList;
import java.util.List;

/**
 *  A clock service abstracts away from clock implementations.
 *  The clock service is menat to be kept constant during runtime.
 */
public class ClockService implements IClockService, IService
{
	//-------- attributes --------
	
	/** The clock. */
	protected IClock clock;
	
	/** The platform. */
//	protected IPlatform platform;
	
	/** The clock listeners. */
	protected List listeners;
	
	//-------- constructors --------
	
	/**
	 *  Create a new clock service.
	 */
	public ClockService(IClock clock)//, IPlatform platform)
	{
		this.clock = clock;
//		this.platform = platform;
		this.listeners = new ArrayList();
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
		return ((ContinuousClock)clock).getDilation();
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
		this.listeners.remove(listener);
		clock.removeChangeListener(listener);
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
	public void start()
	{
		clock.start();
	}
	
	/**
	 *  Shutdown the service.
	 *  @param listener The listener.
	 */
	public void shutdown(IResultListener listener)
	{
		clock.dispose();
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
		IClock clock = null;
		if(IClock.TYPE_CONTINUOUS.equals(type))
			clock = new ContinuousClock(this.clock, tp);//(IThreadPool)platform.getService(ThreadPoolService.class));
		else if(IClock.TYPE_SYSTEM.equals(type))
			clock = new SystemClock(this.clock, tp);//(IThreadPool)platform.getService(ThreadPoolService.class));
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

}
