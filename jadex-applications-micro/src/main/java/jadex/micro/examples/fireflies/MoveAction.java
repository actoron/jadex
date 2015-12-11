package jadex.micro.examples.fireflies;

import java.util.Map;

import jadex.commons.SimplePropertyObject;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;

/**
 *  Action for moving a bug to one of its neighbor fields.
 */
public class MoveAction extends SimplePropertyObject implements ISpaceAction
{
	//-------- constants --------
	
	/** The position parameter. */
	public static final String PARAMETER_POSITION = "position";
	
	/** The direction parameter. */
	public static final String PARAMETER_DIRECTION = "direction";
	
	/** The clock parameter. */
	public static final String PARAMETER_CLOCK = "clock";

	//-------- methods --------
	
	/**
	 *  Perform an action.
	 */
	public Object perform(Map parameters, IEnvironmentSpace space)
	{
		// Set the new position
		Object id = parameters.get(ISpaceAction.OBJECT_ID);
		IVector2 pos = (IVector2)parameters.get(PARAMETER_POSITION);
		((Space2D)space).setPosition(id, pos);
		
		// Set the new direction
		ISpaceObject avatar = space.getSpaceObject(id);
		Double dir = (Double)parameters.get(PARAMETER_DIRECTION);
		avatar.setProperty(PARAMETER_DIRECTION, dir);
		
		// Set the internal clock state
		Integer clock = (Integer)parameters.get(PARAMETER_CLOCK);
		avatar.setProperty(PARAMETER_CLOCK, clock);
		
		return null;
	}
}
