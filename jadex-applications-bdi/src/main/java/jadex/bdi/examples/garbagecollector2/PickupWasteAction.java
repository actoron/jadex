package jadex.bdi.examples.garbagecollector2;

import jadex.adapter.base.envsupport.environment.ISpaceAction;
import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Grid2D;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.bridge.IAgentIdentifier;
import jadex.commons.SimplePropertyObject;

import java.util.Collection;
import java.util.Map;

/**
 *  Action for picking up waste.
 */
public class PickupWasteAction extends SimplePropertyObject implements ISpaceAction
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
				
		Grid2D grid = (Grid2D)space;
		
		IAgentIdentifier owner = (IAgentIdentifier)parameters.get(ISpaceAction.ACTOR_ID);
		ISpaceObject so = grid.getOwnedObjects(owner)[0];

//		if(so.getProperty("garbage")!=null)
//			System.out.println("pickup failed: "+so);
		
		assert so.getProperty("garbage")==null: so;
		
		Collection wastes = grid.getSpaceObjectsByGridPosition((IVector2)so.getProperty(Grid2D.POSITION), "garbage");
		ISpaceObject waste = (ISpaceObject)(wastes!=null? wastes.iterator().next(): null);
//		System.out.println("pickup waste action: "+so+" "+so.getProperty(Grid2D.POSITION)+" "+waste);
		if(wastes!=null)
		{
//			if(Math.random()>0.5)
			{
				wastes.remove(waste);
//				System.out.println("pickup: "+waste);
				so.setProperty("garbage", waste);
				
				grid.setPosition(waste.getId(), null);
				ret = true;
				//pcs.firePropertyChange("worldObjects", garb, null);
//				System.out.println("Agent picked up: "+owner+" "+so.getProperty(Space2D.POSITION));
			}
//			else
//			{
	//			System.out.println("Agent picked up failed: "+name+" "+getPosition(name));
//			}
		}
		else
		{
//			System.out.println("Agent picked up failed: "+so);
		}

//		System.out.println("pickup waste action "+parameters);

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
