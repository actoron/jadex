package jadex.micro.examples.hunterprey;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.commons.SimplePropertyObject;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Int;

/**
 *  Action allowing a creature to move.
 */
public class MoveAction extends SimplePropertyObject implements ISpaceAction
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

	/** Placeholder for "no move" action. */
	public static final String	DIRECTION_NONE	= "none"; 

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
		IComponentDescription actor = (IComponentDescription)parameters.get(ISpaceAction.ACTOR_ID);
		String direction = (String)parameters.get(PARAMETER_DIRECTION);
		ISpaceObject avatar = grid.getAvatar(actor);

		if(null==space.getSpaceObject(avatar.getId()))
		{
			throw new RuntimeException("No such object in space: "+avatar);
		}
		
		IVector2	pos	= (IVector2)avatar.getProperty(Space2D.PROPERTY_POSITION);
		boolean	skip	= false;
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
		else if(DIRECTION_NONE.equals(direction))
		{
			skip	= true;
		}
		else
		{
			throw new RuntimeException("Unknown move direction: "+direction);
		}
		
		if(!skip)
		{
			Collection	obstacles	= grid.getSpaceObjectsByGridPosition(pos, "obstacle");
			if(obstacles!=null && !obstacles.isEmpty())
			{
				throw new RuntimeException("Cannot move '"+direction+"' due to obstacles: "+obstacles);
			}
			
			// Preys can not "tunnel" through hunters, i.e. move from the field
			// where the hunter is now to the field where the hunter was before.
			if(avatar.getType().equals("prey"))
			{
				Collection	hunters	= grid.getSpaceObjectsByGridPosition((IVector2)avatar.getProperty(Space2D.PROPERTY_POSITION), "hunter");
				if(hunters!=null)
				{
					pos	= grid.adjustPosition(pos);	// Hack!!! Position only converted in setPosition().
					for(Iterator it=hunters.iterator(); it.hasNext(); )
					{
						ISpaceObject	hunter	= (ISpaceObject)it.next();
						if(pos.equals(hunter.getProperty(PROPERTY_LASTPOS)))
						{
//							System.out.println("Cannot move '"+direction+"' due to hunter: "+hunter);
							throw new RuntimeException("Cannot move '"+direction+"' due to hunter: "+hunter);
						}
					}
				}
			}
			
			// Remember last position of hunter (required for detecting "tunneling").
			else if(avatar.getType().equals("hunter"))
			{
				avatar.setProperty(PROPERTY_LASTPOS, avatar.getProperty(Space2D.PROPERTY_POSITION));
			}
			
			grid.setPosition(avatar.getId(), pos);
		}
		
		return null;
	}

	/**
	 *  Get the best way to go towards a direction.
	 *  @param space	The 2D space to move in.
	 *  @param sourcepos	The source position.
	 * 	@param targetpos	The target position.
	 * 	@return The way to go (if any).
	 */
	// Todo: A*
	public static String	getDirection(final Grid2D space, IVector2 sourcepos, final IVector2 targetpos)
	{
		String	ret	= evaluateMoves(space, sourcepos, new IMoveEvaluator()
		{
			public double evaluateMove(IVector2 position)
			{
				// The smaller the distance, the better the move. 
				return -space.getDistance(position, targetpos).getAsDouble();
			}
		});
		
		return ret;
	}

	/**
	 *  Move to stay away from the given objects.
	 *  @param space	The 2D space to move in.
	 *  @param sourcepos	The source position.
	 * 	@param objects	The objects to avoid.
	 * 	@return The direction to go ('none', if no move at all is better than moving in any direction).
	 */
	public static String	getAvoidanceDirection(final Grid2D space, IVector2 sourcepos, final ISpaceObject[] objects)
	{
		String	ret	= evaluateMoves(space, sourcepos, new IMoveEvaluator()
		{
			public double evaluateMove(IVector2 position)
			{
				// The bigger the minimal distance, the better the move.
				double	 mindist	= Double.POSITIVE_INFINITY;
				for(int i=0; i<objects.length; i++)
				{
					mindist	= Math.min(mindist, space.getDistance(position,
						(IVector2)objects[i].getProperty(Space2D.PROPERTY_POSITION)).getAsDouble());
				}
				return mindist;
			}
		});
		
		return ret;
	}

	/**
	 *  Get the best move.
	 *  @param space	The 2D space to move in.
	 *  @param sourcepos	The source position.
	 * 	@param eval	The move evaluator.
	 * 	@return The direction to go ('none', if no move at all is better than moving in any direction).
	 */
	public static String	evaluateMoves(Grid2D space, IVector2 sourcepos, IMoveEvaluator eval)
	{
		String	ret	= null;
		
		Map	moves	= new HashMap();
		moves.put(DIRECTION_NONE, new Vector2Int(sourcepos.getXAsInteger(), sourcepos.getYAsInteger()));
		moves.put(DIRECTION_LEFT, new Vector2Int(sourcepos.getXAsInteger()-1, sourcepos.getYAsInteger()));
		moves.put(DIRECTION_RIGHT, new Vector2Int(sourcepos.getXAsInteger()+1, sourcepos.getYAsInteger()));
		moves.put(DIRECTION_UP, new Vector2Int(sourcepos.getXAsInteger(), sourcepos.getYAsInteger()-1));
		moves.put(DIRECTION_DOWN, new Vector2Int(sourcepos.getXAsInteger(), sourcepos.getYAsInteger()+1));

		// Get max value of positions not filled with obstacles.
		double	maxval	= Double.NEGATIVE_INFINITY;
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
				maxval	= Math.max(maxval, eval.evaluateMove(pos));
			}
		}
		// Retain only the best move(s).
		for(Iterator it=moves.keySet().iterator(); it.hasNext(); )
		{
			IVector2	pos	= (IVector2)moves.get(it.next());
			if(eval.evaluateMove(pos)<maxval)
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
		
		return ret;
	}
	
	/**
	 *  Get the possible moves.
	 *  @param space	The 2D space to move in.
	 *  @param sourcepos	The source position.
	 * 	@return The directions to go (i.e. all possible directions excluding 'none' or an array with only 'none').
	 */
	public static String[]	getPossibleDirections(Grid2D space, IVector2 sourcepos)
	{
		Map	moves	= new HashMap();
		moves.put(DIRECTION_LEFT, new Vector2Int(sourcepos.getXAsInteger()-1, sourcepos.getYAsInteger()));
		moves.put(DIRECTION_RIGHT, new Vector2Int(sourcepos.getXAsInteger()+1, sourcepos.getYAsInteger()));
		moves.put(DIRECTION_UP, new Vector2Int(sourcepos.getXAsInteger(), sourcepos.getYAsInteger()-1));
		moves.put(DIRECTION_DOWN, new Vector2Int(sourcepos.getXAsInteger(), sourcepos.getYAsInteger()+1));

		for(Iterator it=moves.keySet().iterator(); it.hasNext(); )
		{
			IVector2	pos	= (IVector2)moves.get(it.next());
			Collection	obstacles	= space.getSpaceObjectsByGridPosition(pos, "obstacle");
			if(obstacles!=null && !obstacles.isEmpty())
			{
				it.remove();
			}
		}

		return moves.isEmpty() ? new String[]{DIRECTION_NONE} : (String[])moves.keySet().toArray(new String[moves.size()]);
	}
	
	//-------- helper classes --------
	
	/**
	 *  Interface for evaluating moves.
	 */
	public static interface IMoveEvaluator
	{
		/**
		 *  Evaluate the move to the given position.
		 *  @param position	The position to move to.
		 *  @return A number representing the move value (bigger=better).
		 */
		public double	evaluateMove(IVector2 position);
	}
}
