package jadex.bdi.examples.hunterprey_env;

import jadex.adapter.base.envsupport.environment.IAgentAction;
import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Grid2D;

import java.util.Map;

/**
 *  Action for eating food or another creature.
 */
public class EatAction implements IAgentAction
{
	/**
	 * Performs the action.
	 * @param parameters parameters for the action
	 * @param space the environment space
	 * @return action return value
	 */
	public Object perform(Map parameters, IEnvironmentSpace space)
	{
		System.out.println("move action: "+parameters);
		
//		Grid2D grid = (Grid2D)space;
//		
//		Object owner = parameters.get(IAgentAction.ACTOR_ID);
//		ISpaceObject so = grid.getOwnedObjects(owner)[0];
//		
//		assert so.getProperty("garbage")!=null;
//		
//		ISpaceObject garb = (ISpaceObject)so.getProperty("garbage");
//		so.setProperty("garbage", null);
//		space.destroySpaceObject(garb.getId());
//		
//		System.out.println("Garbage burned: "+garb);
		
		return null;
	}

	/**
	 * Returns the ID of the action.
	 * @return ID of the action
	 */
	public Object getId()
	{
		// todo: remove here or from application xml?
		return "eat";
	}
}
