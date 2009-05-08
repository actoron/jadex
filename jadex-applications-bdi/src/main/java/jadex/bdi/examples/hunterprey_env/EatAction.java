package jadex.bdi.examples.hunterprey_env;

import jadex.adapter.base.envsupport.environment.IAgentAction;
import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Grid2D;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.bridge.IAgentIdentifier;
import jadex.commons.SimplePropertyObject;

import java.util.Map;

/**
 *  Action for eating food or another creature.
 */
public class EatAction extends SimplePropertyObject implements IAgentAction
{
	//-------- constants --------
	
	/** The property for the points of a creature. */
	public static final	String	PROPERTY_POINTS	= "points";
	
	//-------- IAgentAction interface --------
	
	/**
	 * Performs the action.
	 * @param parameters parameters for the action
	 * @param space the environment space
	 * @return action return value
	 */
	public Object perform(Map parameters, IEnvironmentSpace space)
	{
//		System.out.println("eat action: "+parameters);
		
		Grid2D grid = (Grid2D)space;
		IAgentIdentifier owner = (IAgentIdentifier)parameters.get(IAgentAction.ACTOR_ID);
		ISpaceObject avatar = grid.getOwnedObjects(owner)[0];
		ISpaceObject target = (ISpaceObject)parameters.get(IAgentAction.OBJECT_ID);
		
		if(!avatar.getProperty(Space2D.POSITION).equals(target.getProperty(Space2D.POSITION)))
		{
			throw new RuntimeException("Can only eat objects at same position.");
		}
		
		Integer	points	= (Integer)avatar.getProperty(PROPERTY_POINTS);
		if(avatar.getType().equals("prey") && target.getType().equals("food"))
		{
			points	= points!=null ? new Integer(points.intValue()+1) : new Integer(1);
		}
		else if(avatar.getType().equals("hunter") && target.getType().equals("prey"))
		{
			points	= points!=null ? new Integer(points.intValue()+5) : new Integer(5);
		}
		else
		{
			throw new RuntimeException("Objects of type '"+avatar.getType()+"' cannot eat objects of type '"+target.getType()+"'.");
		}
		
		space.destroySpaceObject(target.getId());
		avatar.setProperty(PROPERTY_POINTS, points);
//		System.out.println("Object eaten: "+target);
		
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
