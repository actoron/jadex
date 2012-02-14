package sodekovs.mapscollision;

import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.commons.SimplePropertyObject;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Int;

import java.util.Collection;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Action allowing a creature to move.
 * @author wolf.posdorfer
 */
public class MoveAction extends SimplePropertyObject implements ISpaceAction
{
	// -------- constants --------

	/** The move direction parameter. */
	public static final String PARAMETER_DIRECTION = "direction";

	public enum Direction
	{
		UP, DOWN, LEFT, RIGHT, NONE;
	}

	/**
	 * Space
	 */
	private static Grid2D _grid;

	/**
	 * all objects that depend on empty spaces to move
	 */
	private static String _objectsdependingonempty = "zombie|zivi|army|scout|medic|hospital|goku|poison|mindstorm|beam_horizontal|beam_vertical";

	private static final MapService _mapService = MapService.getInstance();

	// --------- IAgentAction interface --------

	/**
	 * Performs the action.
	 * 
	 * @param parameters
	 *            parameters for the action
	 * @param space
	 *            the environment space
	 * @return action return value
	 */
	public Object perform(@SuppressWarnings("rawtypes") Map parameters, IEnvironmentSpace space)
	{

		_grid = (Grid2D) space;
		IComponentDescription actor = (IComponentDescription) parameters.get(ISpaceAction.ACTOR_ID);
		ISpaceObject avatar = _grid.getAvatar(actor);

		Direction direction = (Direction) parameters.get(PARAMETER_DIRECTION);

		IVector2 pos = (IVector2) avatar.getProperty(Space2D.PROPERTY_POSITION);
		if (direction == Direction.LEFT)
			pos = new Vector2Int(pos.getXAsInteger() - 1, pos.getYAsInteger());
		else if (direction == Direction.RIGHT)
			pos = new Vector2Int(pos.getXAsInteger() + 1, pos.getYAsInteger());
		else if (direction == Direction.UP)
			pos = new Vector2Int(pos.getXAsInteger(), pos.getYAsInteger() - 1);
		else if (direction == Direction.DOWN)
			pos = new Vector2Int(pos.getXAsInteger(), pos.getYAsInteger() + 1);
		else if (direction == Direction.NONE)
			return null;
		else
			throw new RuntimeException("Unknown move direction: " + direction);

		if (avatar.getType().equals("helicopter"))
		{
			_grid.setPosition(avatar.getId(), pos);
		}
		else if (isPosEmpty(pos, space) && _mapService.isPassable(pos))
		{
			_grid.setPosition(avatar.getId(), pos);
		}

		return null;
	}

	/**
	 * Returns a Random Direction that is NOT the given Direction
	 * 
	 * @param notDirection
	 * @return
	 */
	public static Direction getRandomDirection(Direction notDirection)
	{

		if (notDirection == Direction.DOWN)
		{
			double rand = Math.random();

			return (rand < (1.0 / 3.0)) ? Direction.LEFT : (rand > (2.0 / 3.0)) ? Direction.RIGHT
					: Direction.UP;

		}
		else if (notDirection == Direction.UP)
		{
			double rand = Math.random();

			return (rand < (1.0 / 3.0)) ? Direction.LEFT : (rand > (2.0 / 3.0)) ? Direction.RIGHT
					: Direction.DOWN;
		}
		else if (notDirection == Direction.LEFT)
		{
			double rand = Math.random();

			return (rand < (1.0 / 3.0)) ? Direction.DOWN : (rand > (2.0 / 3.0)) ? Direction.RIGHT
					: Direction.UP;
		}
		else
		{
			double rand = Math.random();

			return (rand < (1.0 / 3.0)) ? Direction.LEFT : (rand > (2.0 / 3.0)) ? Direction.DOWN
					: Direction.UP;
		}

	}

	/**
	 * Returns the direction from myPos to tagetPos
	 * 
	 * @param myPos
	 * @param targetPos
	 * @return direction to the targetPos (highest component first)
	 */
	public static Direction getDirectionToPos(IVector2 myPos, IVector2 targetPos)
	{
		int width = _grid.getAreaSize().getXAsInteger();
		int height = _grid.getAreaSize().getYAsInteger();
		if (myPos.equals(targetPos))
		{
			return Direction.NONE;
		}

		IVector2 diff = myPos.copy().subtract(targetPos);
		int x = diff.getXAsInteger();
		int y = diff.getYAsInteger();
		if (Math.abs(x) < Math.abs(y))
			if (y > 0 || Math.abs(y) > height / 2)
				return Direction.UP;
			else
				return Direction.DOWN;
		else if (x > 0 || Math.abs(x) > width / 2)
			return Direction.LEFT;
		else
			return Direction.RIGHT;
	}

	/**
	 * Returns if the given position is empty
	 * 
	 * @param pos
	 * @param space
	 * @return
	 */
	public static boolean isPosEmpty(IVector2 pos, IEnvironmentSpace space)
	{

		Grid2D grid = (Grid2D) space;

		@SuppressWarnings("unchecked")
		Collection<ISpaceObject> col = grid.getSpaceObjectsByGridPosition(pos, null);

		if (col.isEmpty())
		{
			return true;
		}
		else
		{

			for (ISpaceObject iso : col)
			{
				if (iso.getType().matches(_objectsdependingonempty))
				{
					return false;
				}
			}
			return true;

		}

	}

	/**
	 * Returns a {@link Queue} of consecutive Movements towards the destined
	 * position
	 * 
	 * @param from
	 *            {@link IVector2} from where
	 * @param destination
	 *            {@link IVector2} the destination
	 * @return {@link Queue} &lt{@link Direction}&gt
	 */
	public static Queue<Direction> getMovementsToDirection(IVector2 from, IVector2 destination)
	{
		ConcurrentLinkedQueue<Direction> ze_kju = new ConcurrentLinkedQueue<Direction>();

		IVector2 current = from;

		while (current.getXAsInteger() != destination.getXAsInteger()
				&& current.getYAsInteger() != destination.getYAsInteger())
		{
			Direction dir = getDirectionToPos(current, destination);

			switch (dir)
			{
			case LEFT:
				current = new Vector2Int(current.getXAsInteger() - 1, current.getYAsInteger());
				break;
			case RIGHT:
				current = new Vector2Int(current.getXAsInteger() + 1, current.getYAsInteger());
				break;
			case DOWN:
				current = new Vector2Int(current.getXAsInteger(), current.getYAsInteger() - 1);
				break;
			case UP:
				current = new Vector2Int(current.getXAsInteger(), current.getYAsInteger() + 1);
				break;
			}
			ze_kju.add(dir);
		}

		// Add Last position
		ze_kju.add(getDirectionToPos(current, destination));

		return ze_kju;
	}

	/**
	 * Returns a Totally Random Direction
	 * 
	 * @return
	 */
	public static Direction getTotallyRandomDirection()
	{
		int x = (int) (Math.random() * 10000 % 4);

		switch (x)
		{
		case 0:
			return Direction.LEFT;
		case 1:
			return Direction.RIGHT;
		case 2:
			return Direction.UP;
		default:
			return Direction.DOWN;

		}
	}

	/**
	 * Checks if the Next Move is Possible by calling <br>
	 * MoveAction.isPosEmpty and MapService.isPassable
	 * 
	 * @param myPosition
	 * @param direction
	 * @param space
	 * @return
	 */
	public static boolean isPossibleMove(IVector2 myPosition, Direction direction, Grid2D space)
	{

		switch (direction)
		{
		case LEFT:
			myPosition = new Vector2Int(myPosition.getXAsInteger() - 1, myPosition.getYAsInteger());
			break;
		case UP:
			myPosition = new Vector2Int(myPosition.getXAsInteger(), myPosition.getYAsInteger() - 1);
			break;
		case RIGHT:
			myPosition = new Vector2Int(myPosition.getXAsInteger() + 1, myPosition.getYAsInteger());
			break;
		case DOWN:
			myPosition = new Vector2Int(myPosition.getXAsInteger(), myPosition.getYAsInteger() + 1);
			break;
		}

		return isPosEmpty(myPosition, space) && MapService.getInstance().isPassable(myPosition);

	}
}
