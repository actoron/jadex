package jadex.bdi.examples.hunterprey_env;

import jadex.adapter.base.envsupport.environment.IAgentAction;
import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Grid2D;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector2Int;

import java.util.Collection;
import java.util.Map;

/**
 *  Action allowing a creature to move.
 */
public class MoveAction implements IAgentAction
{
	//-------- constants --------
	
	/** The move direction parameter. */
	public static final	String	PARAMETER_DIRECTION	= "direction";
	
	/** The move direction left. */
	public static final String	DIRECTION_LEFT	= "left"; 
	
	/** The move direction right. */
	public static final String	DIRECTION_RIGHT	= "right"; 
	
	/** The move direction up. */
	public static final String	DIRECTION_UP	= "up"; 
	
	/** The move direction down. */
	public static final String	DIRECTION_DOWN	= "down"; 

	//--------- IAgentAction interface --------
	
	/**
	 * Performs the action.
	 * @param parameters parameters for the action
	 * @param space the environment space
	 * @return action return value
	 */
	public Object perform(Map parameters, IEnvironmentSpace space)
	{
//		System.out.println("move action: "+parameters);
		
		Grid2D grid = (Grid2D)space;
		Object actor = parameters.get(IAgentAction.ACTOR_ID);
		String direction = (String)parameters.get(PARAMETER_DIRECTION);
		ISpaceObject avatar = grid.getOwnedObjects(actor)[0];
		
		IVector2	pos	= (IVector2)avatar.getProperty(Space2D.POSITION);
		if(DIRECTION_LEFT.equals(direction))
		{
			pos	= new Vector2Int(pos.getXAsInteger()-1, pos.getYAsInteger());
		}
		else if(DIRECTION_RIGHT.equals(direction))
		{
			pos	= new Vector2Int(pos.getXAsInteger()+1, pos.getYAsInteger());
		}
		else if(DIRECTION_UP.equals(direction))
		{
			pos	= new Vector2Int(pos.getXAsInteger(), pos.getYAsInteger()-1);
		}
		else if(DIRECTION_DOWN.equals(direction))
		{
			pos	= new Vector2Int(pos.getXAsInteger(), pos.getYAsInteger()+1);
		}
		else
		{
			throw new RuntimeException("Unknown move direction: "+direction);
		}
		
		Collection	obstacles	= grid.getSpaceObjectsByGridPosition(pos, "obstacle");
		if(obstacles!=null && !obstacles.isEmpty())
		{
			throw new RuntimeException("Cannot move '"+direction+"' due to obstacles: "+obstacles);
		}
		
		grid.setPosition(avatar.getId(), pos);
		
		return null;
	}

	/**
	 * Returns the ID of the action.
	 * @return ID of the action
	 */
	public Object getId()
	{
		// todo: remove here or from application xml?
		return "move";
	}
}
