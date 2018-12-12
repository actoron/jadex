package jadex.bdi.examples.garbagecollector;

import java.util.Map;

import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.commons.SimplePropertyObject;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;

/**
 *  Action for burning waste.
 */
public class BurnWasteAction extends SimplePropertyObject implements ISpaceAction
{
	/**
	 * Performs the action.
	 * @param parameters parameters for the action
	 * @param space the environment space
	 * @return action return value
	 */
	public Object perform(Map<String, Object> parameters, IEnvironmentSpace space)
	{
//		System.out.println("burn waste action: "+parameters);
		
		Grid2D grid = (Grid2D)space;
		
		IComponentDescription owner = (IComponentDescription)parameters.get(ISpaceAction.ACTOR_ID);
		ISpaceObject so = grid.getAvatar(owner);
		
		assert so.getProperty("garbage")!=null;
		
		ISpaceObject garb = (ISpaceObject)so.getProperty("garbage");
		so.setProperty("garbage", null);
		space.destroySpaceObject(garb.getId());
		
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
		return "burn";
	}
}
