package jadex.bdi.examples.marsworld_env.producer;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.ListenableTask;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector1;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.bdi.examples.marsworld_env.sentry.AnalyzeTargetTask;
import jadex.commons.concurrent.IResultListener;

/**
 *  Move an object towards a destination.
 */
public class ProduceOreTask extends ListenableTask
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
	protected int	time;
	
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
	
	//-------- ListenableTask methods --------
	
	/**
	 *  Executes the task.
	 *  @param space	The environment in which the task is executing.
	 *  @param obj	The object that is executing the task.
	 *  @param progress	The time that has passed according to the environment executor.
	 */
	public void	doExecute(IEnvironmentSpace space, ISpaceObject obj, IVector1 progress)
	{
		ISpaceObject target = (ISpaceObject)getProperty(PROPERTY_TARGET);
		
		IVector2	loc	= (IVector2)obj.getProperty(Space2D.PROPERTY_POSITION);
		IVector2	tloc	= (IVector2)target.getProperty(Space2D.PROPERTY_POSITION);
		if(!loc.equals(tloc))
			throw new RuntimeException("Not at location: "+obj+", "+target);
		
		int	ore	= ((Number)target.getProperty(AnalyzeTargetTask.PROPERTY_ORE)).intValue();
		int	capacity	= ((Number)target.getProperty(PROPERTY_CAPACITY)).intValue();
		capacity	+= Math.min(ore, (time + progress.getAsInteger())/TIME);
		ore	-= Math.min(ore, (time + progress.getAsInteger())/TIME);
		time	= (time + progress.getAsInteger())%TIME;
		target.setProperty(AnalyzeTargetTask.PROPERTY_ORE, new Integer(ore));
		target.setProperty(PROPERTY_CAPACITY, new Integer(capacity));
		
		if(ore==0)
		{
			taskFinished(space, obj, target.getProperty(PROPERTY_CAPACITY));
		}
	}
}
