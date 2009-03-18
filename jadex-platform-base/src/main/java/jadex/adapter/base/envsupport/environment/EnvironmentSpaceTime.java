package jadex.adapter.base.envsupport.environment;

import java.util.Iterator;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jadex.adapter.base.envsupport.math.IVector1;
import jadex.adapter.base.envsupport.math.Vector1Long;
import jadex.bridge.IClock;
import jadex.bridge.IClockService;

/** An environment space with a notion of time. */
public abstract class EnvironmentSpaceTime extends AbstractEnvironmentSpace
										   implements ChangeListener
{
	/** The current time coefficient */
	protected IVector1 timeCoefficient_;
	
	/** The clock server */
	protected IClockService clockService_;
	
	/** The last time stamp */
	private long timeStamp_;
	
	/**
	 * Initializes the SpaceTime.
	 * @param clockService the clock service
	 * @param timeCoefficient the time coefficient for time differences.
	 */
	protected EnvironmentSpaceTime(IClockService clockService, IVector1 timeCoefficient)
	{
		super();
		clockService_ = clockService;
		timeStamp_ = clockService_.getTime();
		timeCoefficient_ = timeCoefficient;
		clockService_.addChangeListener(this);
	}
	
	/**
	 * Returns whether this space has a concept of time.
	 * 
	 * @return true if the space has a concept of time, false otherwise.
	 */
	public boolean hasTime()
	{
		return true;
	}
	
	/** 
	 * Steps the time of the space. May be non-functional in spaces that do not have
	 * a concept of time. See hasTime().
	 * 
	 * @param currentTime the current time
	 */
	public void timeStep(long currentTime)
	{
		IVector1 deltaT = timeCoefficient_.copy().multiply(new Vector1Long(currentTime - timeStamp_));
		timeStamp_ = currentTime;
		
		synchronized (spaceObjects_)
		{
			for (Iterator it = spaceObjects_.values().iterator(); it.hasNext(); )
			{
				ISpaceObject obj = (ISpaceObject) it.next();
				obj.updateObject(currentTime, deltaT);
			}
		}
		
		synchronized(processes_)
		{
			Object[] processes = processes_.values().toArray();
			for(int i = 0; i < processes.length; ++i)
			{
				ISpaceProcess process = (ISpaceProcess) processes[i];
				process.execute(currentTime, deltaT, this);
			}
		}
	}
	
	public void stateChanged(ChangeEvent e)
	{
		timeStep(clockService_.getTime());
	}
}
