package jadex.bdi.examples.garbagecollector2;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceAction;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Grid2D;
import jadex.adapter.base.envsupport.math.IVector2;

import java.util.Collection;
import java.util.Map;

/**
 *  Action for picking up waste.
 */
public class PickupWasteAction implements ISpaceAction
{
	/**
	 * Performs the action.
	 * @param parameters parameters for the action
	 * @param space the environment space
	 * @return action return value
	 */
	public Object perform(Map parameters, IEnvironmentSpace space)
	{	
		boolean ret = false;
		
		System.out.println("pickup waste action: "+parameters);
		
		Grid2D grid = (Grid2D)space;
		
		Object id = parameters.get(ISpaceObject.ACTOR_ID);
		ISpaceObject so = grid.getSpaceObject(id);
		
		if(so.getProperty("garbage")!=null)
			System.out.println("pickup failed: "+so);
		
		assert so.getProperty("garbage")==null: so;
		
		Collection wastes = grid.getSpaceObjectsByGridPosition((IVector2)so.getProperty(Grid2D.POSITION), "garbage");
		if(wastes!=null)
		{
			if(Math.random()>0.5)
			{
				Object waste = wastes.iterator().next();
				wastes.remove(waste);
				System.out.println("pickup: "+waste);
				so.setProperty("garbage", waste);
				ret = true;
				//pcs.firePropertyChange("worldObjects", garb, null);
			}
	//			System.out.println("Agent picked up: "+name+" "+getPosition(name));
			}
			else
			{
	//			System.out.println("Agent picked up failed: "+name+" "+getPosition(name));
			}
//		System.out.println("pickup] "+name);
		return ret? Boolean.TRUE: Boolean.FALSE;
	}

	/**
	 * Returns the ID of the action.
	 * @return ID of the action
	 */
	public Object getId()
	{
		// todo: remove here or from application xml?
		return "pickup";
	}
}
