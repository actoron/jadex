package jadex.adapter.base.envsupport.environment.space2d.action;

import jadex.adapter.base.envsupport.environment.ISpaceAction;
import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.commons.SimplePropertyObject;

import java.util.Map;

/**
 * Set the position action.
 */
public class GetPosition extends SimplePropertyObject implements ISpaceAction
{
	/** The constant identifier for this action. */
	public static final String SET_POSITION = "set_position";
	
	// Default Action Parameters
	public static final String POSITION_ID = "position_id";
	public static final String VELOCITY_ID = "velocity_id";
	
	/**
	 *  Perform an action.
	 */
	public Object perform(Map parameters, IEnvironmentSpace space)
	{
		Object id = parameters.get(ISpaceAction.OBJECT_ID);
		IVector2 pos = (IVector2)space.getSpaceObject(id).getProperty(POSITION_ID);
		return pos;
	}
}
