package jadex.bdi.examples.marsworld_env.carry;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector1;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.bdi.examples.marsworld_env.movement.ListenableTask;
import jadex.bdi.examples.marsworld_env.producer.ProduceOreTask;
import jadex.bdi.examples.marsworld_env.sentry.AnalyzeTargetTask;
import jadex.commons.concurrent.IResultListener;

/**
 *  Move an object towards a destination.
 */
public class LoadOreTask extends ListenableTask
{
	//-------- constants --------
	
	/** The time required for loading one unit of ore (in millis). */
	public static final int	TIME	= 10;
	
	//-------- attributes --------
	
	/** The target. */
	protected ISpaceObject	target;
	
	/** The loading / unloading flag. */
	protected boolean	load;
	
	/** The remaining time. */
	protected int	time;
	
	//-------- constructors --------
	
	/**
	 *  Create a new move task.
	 *  @param target	The target or home base.
	 *  @param load	The loading (or unloading) flag. 
	 *  @param listsner	The result listener to be informed when the destination is reached. 
	 */
	public LoadOreTask(ISpaceObject target, boolean load, IResultListener listener)
	{
		super(listener);
		this.target	= target;
		this.load	= load;
	}
	
	//-------- ListenableTask methods --------
	
	/**
	 *  Executes the task.
	 *  @param space	The environment in which the task is executing.
	 *  @param obj	The object that is executing the task.
	 *  @param progress	The time that has passed according to the environment executor.
	 */
	public void	doExecute(IEnvironmentSpace space, ISpaceObject obj, IVector1 progress)
	{
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
			int	units	= Math.min(mycap-ore, Math.min(capacity, (time + progress.getAsInteger())/TIME));
			ore	+= units;
			capacity	-= units;
			finished	= ore==mycap || capacity==0;
		}
		else
		{
			int	units	= Math.min(ore, (time + progress.getAsInteger())/TIME);
			ore	-= units;
			capacity	+= units;
			finished	= ore==0;
		}
		time	= (time + progress.getAsInteger())%TIME;
		obj.setProperty(AnalyzeTargetTask.PROPERTY_ORE, new Integer(ore));
		target.setProperty(targetcapprop, new Integer(capacity));
		
		if(finished)
		{
			taskFinished(obj, load? obj.getProperty(AnalyzeTargetTask.PROPERTY_ORE): null); // Todo amount of unloaded ore?
		}
	}
}
