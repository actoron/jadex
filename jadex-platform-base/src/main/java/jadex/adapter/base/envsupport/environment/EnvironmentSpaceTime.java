package jadex.adapter.base.envsupport.environment;

import jadex.adapter.base.envsupport.math.IVector1;
import jadex.adapter.base.envsupport.math.Vector1Double;
import jadex.adapter.base.envsupport.math.Vector1Long;
import jadex.bridge.IClockService;

import java.util.Iterator;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *  An environment space with a notion of time. 
 */
public abstract class EnvironmentSpaceTime extends AbstractEnvironmentSpace implements ChangeListener
{
	//-------- attributes --------
	
	/** The current time coefficient */
	protected IVector1 timecoefficient;
	
	/** The clock server */
	protected IClockService clockservice;
	
	/** The last time stamp */
	private long timestamp;
	
	//-------- constructors --------
	
	/**
	 * Initializes the SpaceTime.
	 * @param clockService the clock service
	 * @param timeCoefficient the time coefficient for time differences.
	 */
	protected EnvironmentSpaceTime(IClockService clockService, IVector1 timeCoefficient)
	{
		super();
		if(clockService!=null)
			setClockService(clockService);
		timecoefficient = timeCoefficient != null? timeCoefficient: new Vector1Double(0.001);
	}
	
	//-------- methods --------
	
	/**
	 * Returns whether this space has a concept of time.
	 * @return true if the space has a concept of time, false otherwise.
	 */
	public boolean hasTime()
	{
		return true;
	}
	
	/** 
	 * Steps the time of the space. May be non-functional in spaces that do not have
	 * a concept of time. See hasTime().
	 * @param currentTime the current time
	 */
	public void timeStep(long currentTime)
	{
		synchronized(syncobject.getMonitor())
		{
			IVector1 deltaT = timecoefficient.copy().multiply(new Vector1Long(currentTime - timestamp));
			timestamp = currentTime;
		
			// Execute enqueued actions.
			getSynchronizedObject().executeEntries();
		
			for(Iterator it = spaceobjects.values().iterator(); it.hasNext(); )
			{
				SpaceObject obj = (SpaceObject)it.next();
				obj.updateObject(currentTime, deltaT);
			}
			
			Object[] procs = processes.values().toArray();
			for(int i = 0; i < procs.length; ++i)
			{
				ISpaceProcess process = (ISpaceProcess) procs[i];
				process.execute(currentTime, deltaT, this);
			}
		}
	}
	
	/**
	 *  Called when clock changes.
	 *  @param e The clock event.
	 */
	public void stateChanged(ChangeEvent e)
	{
		timeStep(clockservice.getTime());
	}
	
	/**
	 *  Set the clock service.
	 *  @param clockService The clock service.
	 */
	public void setClockService(IClockService clockService)
	{
		this.clockservice = clockService;
		timestamp = clockservice.getTime();
		clockservice.addChangeListener(this);
	}
}
