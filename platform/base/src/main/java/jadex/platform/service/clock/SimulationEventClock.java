package jadex.platform.service.clock;

import java.util.logging.Logger;

import jadex.bridge.service.types.clock.IClock;
import jadex.commons.ChangeEvent;


/**
 *  An event-driven simulation clock represents a discrete 
 *  clock that is based on a event-list. 
 */
public class SimulationEventClock extends AbstractClock implements ISimulationClock
{
	//-------- constructors --------
	
	/**
	 *  Create a new clock.
	 *  @param name The name.
	 *  @param starttime The start time.
	 *  @param delta The tick size.
	 */
	public SimulationEventClock(String name, long starttime, long delta)
	{
		super(name, starttime, delta);
	}
	
	/**
	 *  Create a new clock.
	 *  @param oldclock The old clock.
	 */
	public SimulationEventClock(IClock oldclock)
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
		
		Timer dorem = null;
		synchronized(this)
		{
			//System.out.println(this+" advance "+state+" numtimers="+timers.size());
			if(STATE_RUNNING.equals(state) && timers.size()>0)
			{
				advanced	= true;
				t = (Timer)timers.first();
				long tmptime = t.getNotificationTime();
				
				if(tmptime>currenttime)
					currenttime = tmptime;
					
//				System.out.println("time event notificaton: "+currenttime+", "+t.getTimedObject());
				dorem = t;
//				removeTimer(t);
//				t.getTimedObject().timeEventOccurred();
				//System.out.println("timers remaining: "+timers.size());
			}
		}
		
		if(dorem!=null)
			removeTimer(dorem);
		
		// Must not be done while holding lock to avoid deadlocks.
		if(t!=null)
		{
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
		return IClock.TYPE_EVENT_DRIVEN;
	}

//	//-------- For debugging --------
//	
//	long last	= 0;
//	
//	@Override
//	public void addTimer(ITimer timer)
//	{
//		super.addTimer(timer);
//		
//		synchronized(this)
//		{
//			if(STATE_RUNNING.equals(state) && timers.size()>0)
//			{
//				Timer	t = (Timer)timers.first();
//				long tmptime = t.getNotificationTime();
//				
//				if(tmptime!=last)
//				{
//					System.out.println("next timepoint was "+last+", is now "+tmptime+", "+timer.getTimedObject());
//					last	= tmptime;
//				}
//			}
//		}
//	}
}