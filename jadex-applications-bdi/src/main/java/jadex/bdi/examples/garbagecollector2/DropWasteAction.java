package jadex.bdi.examples.garbagecollector2;

import java.util.Map;

import jadex.adapter.base.envsupport.environment.IAgentAction;
import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceAction;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Grid2D;
import jadex.adapter.base.envsupport.math.IVector2;

/**
 *  Action for dropping waste on the robots field.
 */
public class DropWasteAction implements IAgentAction
{
	/**
	 * Performs the action.
	 * @param parameters parameters for the action
	 * @param space the environment space
	 * @return action return value
	 */
	public Object perform(Map parameters, IEnvironmentSpace space)
	{
		System.out.println("drop waste action: "+parameters);
		
		Grid2D grid = (Grid2D)space;
		
		Object owner = parameters.get(ISpaceObject.OWNER);
		ISpaceObject so = grid.getOwnedObjects(owner)[0];
		IVector2 pos = (IVector2)so.getProperty(Grid2D.POSITION);
		
		assert so.getProperty("garbage")!=null;

		ISpaceObject garb = (ISpaceObject)so.getProperty("garbage");
		grid.setPosition(garb.getId(), pos);
		so.setProperty("garbage", null);
		
		System.out.println("Agent dropped garbage: "+owner+" "+pos);
		
		return null;
	}

	/**
	 * Returns the ID of the action.
	 * @return ID of the action
	 */
	public Object getId()
	{
		// todo: remove here or from application xml?
		return "drop";
	}
}
