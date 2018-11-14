package jadex.extension.envsupport.environment.space2d.action;

import java.util.Map;

import jadex.commons.SimplePropertyObject;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;

/**
 *  Set the position action.
 */
public class SetPosition extends SimplePropertyObject implements ISpaceAction
{
	/**
	 *  Perform an action.
	 */
	public Object perform(Map parameters, IEnvironmentSpace space)
	{
		Object id = parameters.get(ISpaceAction.OBJECT_ID);
		IVector2 pos = (IVector2)parameters.get(GetPosition.PARAMETER_POSITION);
		((Space2D)space).setPosition(id, pos);
		return null;
	}
}
