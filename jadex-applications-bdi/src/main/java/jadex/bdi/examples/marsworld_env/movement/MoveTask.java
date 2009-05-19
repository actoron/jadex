package jadex.bdi.examples.marsworld_env.movement;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector1;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.commons.concurrent.IResultListener;
import jadex.javaparser.IParsedExpression;

/**
 *  Move an object towards a destination.
 */
public class MoveTask extends ListenableTask
{
	//-------- constants --------
	
	/** The speed property (units per second). */
	public static final String	PROPERTY_SPEED	= "speed";
	
	//-------- attributes --------
	
	/** The destination. */
	protected IVector2	destination;
	
	//-------- constructors --------
	
	/**
	 *  Create a new move task.
	 *  @param destination	The destination. 
	 *  @param listsner	The result listener to be informed when the destination is reached. 
	 */
	public MoveTask(IVector2 destination, IResultListener listener)
	{
		super(listener);
		this.destination	= destination;
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
		double	speed	= ((Number)((IParsedExpression)obj.getProperty(PROPERTY_SPEED)).getValue(null)).doubleValue();
		double	maxdist	= progress.getAsDouble()*speed*0.001;
		IVector2	loc	= (IVector2)obj.getProperty(Space2D.POSITION);
		
		if(((Space2D)space).getDistance(loc, destination).getAsDouble()<=maxdist)
		{
			((Space2D)space).setPosition(obj.getId(), destination);
			taskFinished(obj, null);
		}
		else
		{
			// Todo: how to handle border conditions!?
			IVector2	newloc	= destination.copy().subtract(loc).normalize().multiply(maxdist).add(loc);
			((Space2D)space).setPosition(obj.getId(), newloc);
		}
	}
}
