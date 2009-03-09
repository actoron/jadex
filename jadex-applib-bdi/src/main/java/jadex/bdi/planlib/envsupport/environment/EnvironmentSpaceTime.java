package jadex.bdi.planlib.envsupport.environment;

import java.util.Iterator;

import jadex.bdi.planlib.envsupport.math.IVector1;
import jadex.bdi.planlib.envsupport.math.Vector1Long;
import jadex.bridge.IClock;

/** An environment space with a notion of time. */
public abstract class EnvironmentSpaceTime extends AbstractEnvironmentSpace
{
	/** The current time coefficient */
	protected IVector1 timeCoefficient_;
	
	/** The last time stamp */
	private long timeStamp_;
	
	/**
	 * Initializes the TimeSpace.
	 * @param startTime the start time
	 * @param timeCoefficient the time coefficient for time differences.
	 */
	protected EnvironmentSpaceTime(long startTime, IVector1 timeCoefficient)
	{
		super();
		timeStamp_ = startTime;
		timeCoefficient_ = timeCoefficient;
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
	 * @param clock the clock
	 */
	public void timeStep(IClock clock)
	{
		long currentTime = clock.getTime();
		IVector1 deltaT = timeCoefficient_.copy().multiply(new Vector1Long(currentTime - timeStamp_));
		timeStamp_ = currentTime;
		
		synchronized (spaceObjects_)
		{
			for (Iterator it = spaceObjects_.values().iterator(); it.hasNext(); )
			{
				ISpaceObject obj = (ISpaceObject) it.next();
				obj.updateObject(clock, deltaT);
			}
		}
		
		synchronized(processes_)
		{
			Object[] processes = processes_.values().toArray();
			for(int i = 0; i < processes.length; ++i)
			{
				ISpaceProcess process = (ISpaceProcess) processes[i];
				process.execute(clock, deltaT, this);
			}
		}
	}
}
