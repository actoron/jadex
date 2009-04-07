package jadex.adapter.base.envsupport.environment;

import jadex.adapter.base.envsupport.environment.agentaction.IActionExecutor;
import jadex.adapter.base.envsupport.environment.agentaction.ImmediateExecutor;
import jadex.adapter.base.envsupport.environment.view.IView;
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
	//-------- constants --------
	
	/** The execution mode immediately. */
//	public static final String EXECUTION_MODE_IMMEDIATELY = "execution_immediately";
	
	/** The execution mode roundbased. */
//	public static final String EXECUTION_MODE_ROUNDBASED = "execution_roundbased";
	
	/** The execution mode roundbased alternating. */
//	public static final String EXECUTION_MODE_ROUNDBASED_ALTERNATING = "execution_roundbased_alternating";
	
	//-------- attributes --------
	
	/** The current time coefficient */
	protected IVector1 timecoefficient;
	
	/** The clock server */
	protected IClockService clockservice;
	
	/** The last time stamp */
	protected long timestamp;
	
	/** The action executor. */
	protected IActionExecutor executor;
	
	//-------- constructors --------
	
	/**
	 * Initializes the SpaceTime.
	 * @param clockService the clock service
	 * @param timeCoefficient the time coefficient for time differences.
	 */
	protected EnvironmentSpaceTime(IClockService clockService, IVector1 timeCoefficient, IActionExecutor executor)
	{
		if(clockService!=null)
			setClockService(clockService);
		timecoefficient = timeCoefficient != null? timeCoefficient: new Vector1Double(0.001);
		this.executor = executor==null? new ImmediateExecutor(): executor;
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
	 * @param currenttime the current time
	 */
	public void timeStep(long currenttime)
	{
		synchronized(monitor)
		{
			IVector1 deltat = timecoefficient.copy().multiply(new Vector1Long(currenttime - timestamp));
			timestamp = currenttime;
		
			// Execute enqueued actions. // obsolete
			// executor.execute(currenttime, deltat, getSynchronizedObject());
				
			for(Iterator it = spaceobjects.values().iterator(); it.hasNext(); )
			{
				SpaceObject obj = (SpaceObject)it.next();
				obj.updateObject(currenttime, deltat);
			}
			
			Object[] procs = processes.values().toArray();
			for(int i = 0; i < procs.length; ++i)
			{
				ISpaceProcess process = (ISpaceProcess) procs[i];
				process.execute(currenttime, deltat, this);
			}
			
			for (Iterator it = views.values().iterator(); it.hasNext(); )
			{
				IView view = (IView) it.next();
				view.update(this);
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
	
	/**
	 *  Set the action executor.
	 *  @param executor The executor.
	 */
	public void setActionExecutor(IActionExecutor executor)
	{
		this.executor = executor;
	}
}
