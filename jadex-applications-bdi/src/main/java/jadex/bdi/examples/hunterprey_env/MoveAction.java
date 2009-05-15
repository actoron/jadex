package jadex.bdi.examples.hunterprey_env;

import jadex.adapter.base.envsupport.environment.IAgentAction;
import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Grid2D;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector2Int;
import jadex.bridge.IAgentIdentifier;
import jadex.commons.SimplePropertyObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *  Action allowing a creature to move.
 */
public class MoveAction extends SimplePropertyObject implements IAgentAction
{
	//-------- constants --------
	
	/** The move direction parameter. */
	public static final	String	PARAMETER_DIRECTION	= "direction";
	
	/** The last position property (only for hunters). */
	public static final	String	PROPERTY_LASTPOS	= "lastpos";
	
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
		IAgentIdentifier actor = (IAgentIdentifier)parameters.get(IAgentAction.ACTOR_ID);
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
		
		// Preys can not "tunnel" through hunters, i.e. from from the field
		// where the hunter is now to the field where the hunter was before.
		if(avatar.getType().equals("prey"))
		{
			Collection	hunters	= grid.getSpaceObjectsByGridPosition((IVector2)avatar.getProperty(Space2D.POSITION), "hunter");
			if(hunters!=null)
			{
				pos	= grid.getGridPosition(pos);	// Hack!!! Position only converted in setPosition().
				for(Iterator it=hunters.iterator(); it.hasNext(); )
				{
					ISpaceObject	hunter	= (ISpaceObject)it.next();
					if(pos.equals(hunter.getProperty(Space2D.POSITION)))
					{
						System.out.println("Cannot move '"+direction+"' due to hunter: "+hunter);
						throw new RuntimeException("Cannot move '"+direction+"' due to hunter: "+hunter);
					}
				}
			}
		}
		
		// Remember last position of hunter (required for detecting "tunneling").
		else if(avatar.getType().equals("hunter"))
		{
			avatar.setProperty(PROPERTY_LASTPOS, avatar.getProperty(Space2D.POSITION));
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

	/**
	 *  Get the best way to go towards a direction.
	 *  @param space	The 2D space to move in.
	 *  @param sourcepos	The source position.
	 * 	@param targetpos	The target position.
	 * 	@return The way to go (if any).
	 */
	// Todo: A*
	public static String	getDirection(Grid2D space, IVector2 sourcepos, IVector2 targetpos)
	{
		String	ret	= null;
		
		if(!sourcepos.equals(targetpos))
		{
			Map	moves	= new HashMap();
			moves.put(DIRECTION_LEFT, new Vector2Int(sourcepos.getXAsInteger()-1, sourcepos.getYAsInteger()));
			moves.put(DIRECTION_RIGHT, new Vector2Int(sourcepos.getXAsInteger()+1, sourcepos.getYAsInteger()));
			moves.put(DIRECTION_UP, new Vector2Int(sourcepos.getXAsInteger(), sourcepos.getYAsInteger()-1));
			moves.put(DIRECTION_DOWN, new Vector2Int(sourcepos.getXAsInteger(), sourcepos.getYAsInteger()+1));

			// Get min distance of positions not filled with obstacles.
			double	mindist	= space.getDistance(sourcepos, targetpos).getAsDouble();
			for(Iterator it=moves.keySet().iterator(); it.hasNext(); )
			{
				IVector2	pos	= (IVector2)moves.get(it.next());
				Collection	obstacles	= space.getSpaceObjectsByGridPosition(pos, "obstacle");
				if(obstacles!=null && !obstacles.isEmpty())
				{
					it.remove();
				}
				else
				{
					mindist	= Math.min(mindist, space.getDistance(pos, targetpos).getAsDouble());
				}
			}
			// Retain only the best move(s).
			for(Iterator it=moves.keySet().iterator(); it.hasNext(); )
			{
				IVector2	pos	= (IVector2)moves.get(it.next());
				if(space.getDistance(pos, targetpos).getAsDouble()>mindist)
				{
					it.remove();
				}
			}

//			System.out.println("Moves: "+moves);
			
			// Chose randomly one of the remaining equally good moves.
			if(moves.size()>0)
			{
				int	chosen	= (int)(Math.random()*moves.size());
				Iterator	it	= moves.keySet().iterator();
				for(int i=0; i<=chosen; i++)
				{
					ret	= (String)it.next();
				}
			}
		}
		
		return ret;
	}
}
