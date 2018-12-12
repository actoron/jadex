package jadex.bdi.examples.disastermanagement;

import jadex.bridge.service.types.clock.IClockService;
import jadex.extension.envsupport.environment.AbstractTask;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;

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
		IVector2	destination	= (IVector2)getProperty("destination");
		IVector2	loc	= (IVector2)obj.getProperty(Space2D.PROPERTY_POSITION);
		double	speed	= ((Number)obj.getProperty("speed")).doubleValue();
		IVector2	direction	= destination.copy().subtract(loc).normalize();
		double	dist	= ((Space2D)space).getDistance(loc,destination).getAsDouble();
		double	maxdist	= progress*speed*0.001;
		IVector2	newloc	= dist<=maxdist ? destination
			: direction.multiply(maxdist).add(loc);
		((Space2D)space).setPosition(obj.getId(), newloc);		
		if(newloc==destination)
			setFinished(space, obj, true);
	}
}
