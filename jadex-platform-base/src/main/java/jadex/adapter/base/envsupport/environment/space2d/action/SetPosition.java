package jadex.adapter.base.envsupport.environment.space2d.action;

import java.util.Map;

import jadex.adapter.base.envsupport.environment.IAgentAction;
import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceAction;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector2;

/**
 *  Set the position action.
 */
public class SetPosition implements ISpaceAction
{
	/**
	 *  Perform an action.
	 */
	public Object perform(Map parameters, IEnvironmentSpace space)
	{
		Object id = parameters.get(IAgentAction.OBJECT_ID);
		IVector2 pos = (IVector2)parameters.get(GetPosition.POSITION_ID);
		((Space2D)space).setPosition(id, pos);
		return null;
	}
}
