package jadex.bdi.examples.marsworld.producer;

import jadex.bdi.examples.marsworld.sentry.AnalyzeTargetTask;
import jadex.bridge.service.types.clock.IClockService;
import jadex.extension.envsupport.environment.AbstractTask;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;

/**
 *  Move an object towards a destination.
 */
public class ProduceOreTask extends AbstractTask
{
	//-------- constants --------
	
	/** The type name property. */
	public static final String	PROPERTY_TYPENAME = "produce";
	
	/** The property for the target. */
	public static final String PROPERTY_TARGET = "target";

	
	/** The property for the produced ore amount. */
	public static final String	PROPERTY_CAPACITY	= "capacity";
	
	/** The time required for producing one unit of ore (in millis). */
	public static final int	TIME	= 100;
	
	//-------- attributes --------
	
	/** The target. */
	protected ISpaceObject	target;
	
	/** The remaining time. */
	protected long	time;
	
	//-------- constructors --------
	
	/**
	 *  Create a new move task.
	 *  @param destination	The destination. 
	 *  @param listsner	The result listener to be informed when the destination is reached. 
	 * /
	public ProduceOreTask(ISpaceObject target, IResultListener listener)
	{
		super(listener);
		this.target	= target;
	}*/
	
	//-------- AbstractTask methods --------
	
	/**
	 *  Executes the task.
	 *  Handles exceptions. Subclasses should implement doExecute() instead.
	 *  @param space	The environment in which the task is executing.
	 *  @param obj	The object that is executing the task.
	 *  @param progress	The time that has passed according to the environment executor.
	 */
	public void execute(IEnvironmentSpace space, ISpaceObject obj, long progress, IClockService clock)
	{
		ISpaceObject target = (ISpaceObject)getProperty(PROPERTY_TARGET);
		
		IVector2	loc	= (IVector2)obj.getProperty(Space2D.PROPERTY_POSITION);
		IVector2	tloc	= (IVector2)target.getProperty(Space2D.PROPERTY_POSITION);
		double r = 0.05;
		if(loc.getDistance(tloc).getAsDouble()>r)
			throw new RuntimeException("Not at location: "+obj+", "+target);
		
		int	ore	= ((Number)target.getProperty(AnalyzeTargetTask.PROPERTY_ORE)).intValue();
		int	capacity	= ((Number)target.getProperty(PROPERTY_CAPACITY)).intValue();
		capacity	+= Math.min(ore, (time + progress)/TIME);
		ore	-= Math.min(ore, (time + progress)/TIME);
		time	= (time + progress)%TIME;
		target.setProperty(AnalyzeTargetTask.PROPERTY_ORE, Integer.valueOf(ore));
		target.setProperty(PROPERTY_CAPACITY, Integer.valueOf(capacity));

		if(ore!=0)
		obj.setProperty("status", "ore");
		
		if(ore==0)
		{	
			setFinished(space, obj, true);
			obj.setProperty("status", "drive");
		}
	}
}
