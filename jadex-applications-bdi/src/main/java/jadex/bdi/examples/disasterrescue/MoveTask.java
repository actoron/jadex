package jadex.bdi.examples.disasterrescue;

import jadex.application.space.envsupport.environment.AbstractTask;
import jadex.application.space.envsupport.environment.IEnvironmentSpace;
import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.application.space.envsupport.environment.space2d.Space2D;
import jadex.application.space.envsupport.math.IVector2;
import jadex.commons.service.clock.IClockService;

/**
 *  Move an object towards a destination.
 */
public class MoveTask extends AbstractTask
{
	//-------- constants --------
	
	/** The task name. */
	public static final String	PROPERTY_TYPENAME = "move";
	
	/** The destination property. */
	public static final String	PROPERTY_DESTINATION = "destination";

	/** The speed property of the moving object (units per second). */
	public static final String	PROPERTY_SPEED	= "speed";
		
	//-------- IObjectTask methods --------
	
	/**
	 *  Executes the task.
	 *  @param space	The environment in which the task is executing.
	 *  @param obj	The object that is executing the task.
	 *  @param progress	The time that has passed according to the environment executor.
	 */
	public void execute(IEnvironmentSpace space, ISpaceObject obj, long progress, IClockService clock)
	{
		IVector2 destination = (IVector2)getProperty(PROPERTY_DESTINATION);

		double	speed	= ((Number)obj.getProperty(PROPERTY_SPEED)).doubleValue();
		double	maxdist	= progress*speed*0.001;
		IVector2	loc	= (IVector2)obj.getProperty(Space2D.PROPERTY_POSITION);
		// Todo: how to handle border conditions!?
		IVector2	newloc	= ((Space2D)space).getDistance(loc, destination).getAsDouble()<=maxdist
			? destination : destination.copy().subtract(loc).normalize().multiply(maxdist).add(loc);

//		System.out.println("Move task: "+destination+", "+newloc+", "+destination.hashCode()+", "+newloc.hashCode());

		((Space2D)space).setPosition(obj.getId(), newloc);
		
		if(newloc==destination)
			setFinished(space, obj, true);
	}
}
