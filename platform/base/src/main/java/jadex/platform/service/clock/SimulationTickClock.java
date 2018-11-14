package jadex.platform.service.clock;

import java.util.logging.Logger;

import jadex.bridge.service.types.clock.IClock;
import jadex.commons.ChangeEvent;


/**
 *  A time-driven simulation clock represents a discrete 
 *  clock that is based on ticks. This means that only time
 *  points can occur that are based on ticks (not in between ticks).
 */
public class SimulationTickClock extends AbstractClock implements ISimulationClock
{
	//-------- constructors --------
	
	/**
	 *  Create a new clock.
	 *  @param name The name.
	 *  @param starttime The start time.
	 *  @param delta The time delta for time advance.
	 */
	public SimulationTickClock(String name, long starttime, long delta)
	{
		super(name, starttime, delta);
	}
	
	/**
	 *  Create a new clock.
	 *  @param oldclock The old clock.
	 */
	public SimulationTickClock(IClock oldclock)
	{
		this(null, 0, DEFAULT_DELTA);
		copyFromClock(oldclock);
	}
	
	//-------- methods --------
	
	/**
	 *  Advance one event.
	 *  @return True, if clock could be advanced.
	 */
	public boolean advanceEvent()
	{
		boolean	advanced	= false;
		
		Timer t = null;
		synchronized(this)
		{
			if(STATE_RUNNING.equals(state) && timers.size()!=0)
			{
				// Determine next timepoint.
				t = (Timer)timers.first();
				long num = (long)Math.ceil((t.getNotificationTime()-getStarttime())/(double)(getDelta()));
				long time = num*getDelta()+getStarttime();
				
				// Ensure that clock does not run backwards.
				if(time>currenttime)
					currenttime = time;
				
//				removeTimer(t);
				
				advanced = true;
			}
		}
		
		if(advanced)
		{
			removeTimer(t);
			// Execute due timers.
			try
			{
				t.getTimedObject().timeEventOccurred(currenttime);
			}
			catch(Exception e)
			{
				Logger.getLogger(name).warning("Exception in time event: "+e);
			}
		}
		
		notifyListeners(new ChangeEvent(this, EVENT_TYPE_NEXT_TIMEPOINT));
		return advanced;
	}
	
	
	/**
	 *  Get the clock type.
	 *  @return The clock type.
	 */
	public String getType()
	{
		return IClock.TYPE_TIME_DRIVEN;
	}
}
