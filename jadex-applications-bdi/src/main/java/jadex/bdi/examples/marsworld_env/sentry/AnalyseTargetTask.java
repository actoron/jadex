package jadex.bdi.examples.marsworld_env.sentry;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector1;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.bdi.examples.marsworld_env.movement.ListenableTask;
import jadex.commons.concurrent.IResultListener;

/**
 *  Move an object towards a destination.
 */
public class AnalyseTargetTask extends ListenableTask
{
	//-------- constants --------
	
	/** The flag for analyzed targets. */
	public static final String	PROPERTY_MARKED	= "marked";
	
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
	 *  Create a new move task.
	 *  @param destination	The destination. 
	 *  @param listsner	The result listener to be informed when the destination is reached. 
	 */
	public AnalyseTargetTask(ISpaceObject target, IResultListener listener)
	{
		super(listener);
		this.target	= target;
		this.time	= TIME;
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
		IVector2	loc	= (IVector2)obj.getProperty(Space2D.POSITION);
		IVector2	tloc	= (IVector2)target.getProperty(Space2D.POSITION);
		if(!loc.equals(tloc))
			throw new RuntimeException("Not at location: "+obj+", "+target);
		
		time	-= progress.getAsInteger();
		
		if(time<=0)
		{
			target.setProperty(PROPERTY_MARKED, Boolean.TRUE);		
			taskFinished(obj, target.getProperty(PROPERTY_ORE));
		}
	}
}
