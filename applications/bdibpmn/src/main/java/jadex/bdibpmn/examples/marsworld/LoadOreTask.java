package jadex.bdibpmn.examples.marsworld;

import jadex.bridge.service.types.clock.IClockService;
import jadex.extension.envsupport.environment.AbstractTask;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;

/**
 *  Move an object towards a destination.
 */
public class LoadOreTask extends AbstractTask
{
	//-------- constants --------
	
	/** The destination property. */
	public static final String	PROPERTY_TYPENAME = "load";

	/** The property for the charge state. */
	public static final String PROPERTY_TARGET = "target";

	/** The property for the charge state. */
	public static final String PROPERTY_LOAD = "load";
	
	/** The time required for loading one unit of ore (in millis). */
	public static final int	TIME	= 10;
	
	//-------- attributes --------
	
	/** The target. */
//	protected ISpaceObject	target;
	
	/** The loading / unloading flag. */
//	protected boolean	load;
	
	/** The remaining time. */
	protected long	time;
	
	//-------- constructors --------
	
	/**
	 *  Create a new move task.
	 *  @param target	The target or home base.
	 *  @param load	The loading (or unloading) flag. 
	 *  @param listsner	The result listener to be informed when the destination is reached. 
	 * /
	public LoadOreTask(ISpaceObject target, boolean load, IResultListener listener)
	{
		super(listener);
		this.target	= target;
		this.load	= load;
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
		boolean load = ((Boolean)getProperty(PROPERTY_LOAD)).booleanValue();
		
		IVector2	loc	= (IVector2)obj.getProperty(Space2D.PROPERTY_POSITION);
		IVector2	tloc	= (IVector2)target.getProperty(Space2D.PROPERTY_POSITION);
		if(!loc.equals(tloc))
			throw new RuntimeException("Not at location: "+obj+", "+target);
		
		String	targetcapprop	= load ? ProduceOreTask.PROPERTY_CAPACITY : AnalyzeTargetTask.PROPERTY_ORE;
		
		int	ore	= ((Number)obj.getProperty(AnalyzeTargetTask.PROPERTY_ORE)).intValue();
		int	mycap	= ((Number)obj.getProperty(ProduceOreTask.PROPERTY_CAPACITY)).intValue();
		int	capacity	= ((Number)target.getProperty(targetcapprop)).intValue();
	
		boolean	finished;
		if(load)
		{
			long	units	= Math.min(mycap-ore, Math.min(capacity, (time + progress)/TIME));
			ore	+= units;
			capacity	-= units;
			finished	= ore==mycap || capacity==0;
		}
		else
		{
			long	units	= Math.min(ore, (time + progress)/TIME);
			ore	-= units;
			capacity	+= units;
			finished	= ore==0;
		}
		time	= (time + progress)%TIME;
		obj.setProperty(AnalyzeTargetTask.PROPERTY_ORE, Integer.valueOf(ore));
		target.setProperty(targetcapprop, Integer.valueOf(capacity));
		
		if(finished)
		{
			setFinished(space, obj, true); // Todo amount of unloaded ore?
		}
	}
}
