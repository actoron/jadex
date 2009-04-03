package jadex.bdi.examples.garbagecollector2;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.agentaction.IAgentAction;
import jadex.adapter.base.envsupport.environment.space2d.Grid2D;

import java.util.Map;

/**
 *  Action for burning waste.
 */
public class BurnWasteAction implements IAgentAction
{
	/**
	 * Performs the action.
	 * @param parameters parameters for the action
	 * @param space the environment space
	 * @return action return value
	 */
	public Object perform(Map parameters, IEnvironmentSpace space)
	{
		System.out.println("burn waste action: "+parameters);
		
		Grid2D grid = (Grid2D)space;
		
		Object id = parameters.get(ISpaceObject.ACTOR_ID);
		ISpaceObject so = grid.getSpaceObject(id);
		
		assert so.getProperty("garbage")!=null;
		
		ISpaceObject garb = (ISpaceObject)so.getProperty("garbage");
		so.setProperty("garbage", null);
		space.destroySpaceObject(garb.getId());
		
		return null;
	}

	/**
	 * Returns the ID of the action.
	 * @return ID of the action
	 */
	public Object getId()
	{
		// todo: remove here or from application xml?
		return "burn";
	}
}
