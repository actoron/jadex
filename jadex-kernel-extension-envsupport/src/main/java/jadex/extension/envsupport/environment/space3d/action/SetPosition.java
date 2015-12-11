package jadex.extension.envsupport.environment.space3d.action;

import java.util.Map;

import jadex.commons.SimplePropertyObject;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.space3d.Space3D;
import jadex.extension.envsupport.math.IVector3;

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
		IVector3 pos = (IVector3)parameters.get(GetPosition.PARAMETER_POSITION);
		((Space3D)space).setPosition(id, pos);
		return null;
	}
}
