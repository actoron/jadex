package jadex.bdiv3.examples.marsworld.sentry;

import jadex.bdiv3.examples.marsworld.SVector;
import jadex.bridge.service.types.clock.IClockService;
import jadex.extension.envsupport.environment.AbstractTask;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;

/**
 *  Analyze a given target.
 */
public class AnalyzeTargetTask extends AbstractTask
{
	//-------- constants --------
	
	/** The type name property. */
	public static final String	PROPERTY_TYPENAME = "analyze";
	
	/** The property for the target. */
	public static final String PROPERTY_TARGET = "target";
	
	
	/** The state for targets (unknown, analyzing, analyzed). */
	public static final String	PROPERTY_STATE	= "state";
	
	/** The unknown state for target. */
	public static final String	STATE_UNKNOWN	= "unknown";
	
	/** The unknown state for target. */
	public static final String	STATE_ANALYZING	= "analyzing";
	
	/** The unknown state for target. */
	public static final String	STATE_ANALYZED	= "analyzed";
	
	/** The property for the ore amount. */
	public static final String	PROPERTY_ORE	= "ore";
	
	/** The time required for analyzing a target (in millis). */
	public static final int	TIME	= 1000;
	
	//-------- attributes --------
	
	/** The target. */
	protected ISpaceObject	target;
	
	/** The remaining time. */
	protected int	time;
	
	//-------- constructors --------
	
	/**
	 *  Create a new analyze task.
	 *  @param destination	The destination. 
	 *  @param listener	The result listener to be informed when the destination is reached. 
	 * /
	public AnalyzeTargetTask(ISpaceObject target, IResultListener listener)
	{
		super(listener);
		this.target	= target;
		this.time	= TIME;
	}*/
	
	//-------- AbstractTask methods --------
	
	/**
	 *  This method will be executed by the object before the task gets added to
	 *  the execution queue.
	 *  @param obj	The object that is executing the task.
	 */
	public void start(ISpaceObject obj)
	{
		this.target = (ISpaceObject)getProperty(PROPERTY_TARGET);
		this.time = TIME;

		Object	loc	= obj.getProperty(Space2D.PROPERTY_POSITION);
		Object	tloc	= target.getProperty(Space2D.PROPERTY_POSITION);
		double r = 0.05;
		if(SVector.getDistance(loc, tloc)>r)
			throw new RuntimeException("Not at location: "+obj+", "+target);
		
		if(!target.getProperty(PROPERTY_STATE).equals(STATE_UNKNOWN))
			throw new RuntimeException("Can only analyze '"+STATE_UNKNOWN+"' targets: "+target);
		
		target.setProperty(AnalyzeTargetTask.PROPERTY_STATE, AnalyzeTargetTask.STATE_ANALYZING);		
	}
	
	/**
	 *  Executes the task.
	 *  Handles exceptions. Subclasses should implement doExecute() instead.
	 *  @param space	The environment in which the task is executing.
	 *  @param obj	The object that is executing the task.
	 *  @param progress	The time that has passed according to the environment executor.
	 */
	public void execute(IEnvironmentSpace space, ISpaceObject obj, long progress, IClockService clock)
	{
		Object	loc	= obj.getProperty(Space2D.PROPERTY_POSITION);
		Object	tloc	= target.getProperty(Space2D.PROPERTY_POSITION);
		double r = 0.05;
		if(SVector.getDistance(loc, tloc)>r)
			throw new RuntimeException("Not at location: "+obj+", "+target);
		
		time	-= progress;
		
		if(time<=0)
		{
			target.setProperty(AnalyzeTargetTask.PROPERTY_STATE, AnalyzeTargetTask.STATE_ANALYZED);
			setFinished(space, obj, true);
		}
	}
}
