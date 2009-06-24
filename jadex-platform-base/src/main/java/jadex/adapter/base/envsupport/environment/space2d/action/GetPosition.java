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
	public static final String PARAMETER_POSITION = "position";
	public static final String PARAMETER_VELOCITY = "velocity";
	
	/**
	 *  Perform an action.
	 */
	public Object perform(Map parameters, IEnvironmentSpace space)
	{
		Object so = parameters.get(ISpaceAction.OBJECT_ID);
		IVector2 pos = (IVector2)space.getSpaceObject(so).getProperty(PARAMETER_POSITION);
		return pos;
	}
}
