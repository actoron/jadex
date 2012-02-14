package sodekovs.mapscollision;

import jadex.extension.envsupport.math.IVector2;

/**
 * 
 * @author wolf.posdorfer
 * 
 */
public class DirectionUtility
{

	/**
	 * Returns the Directions to use for FindWay in descending order of
	 * likeliness
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public static MoveAction.Direction[] getDirections(IVector2 from, IVector2 to)
	{

		int x = from.getXAsInteger() - to.getXAsInteger();
		int y = from.getYAsInteger() - to.getYAsInteger();

		MoveAction.Direction[] result = new MoveAction.Direction[4];

		if (Math.abs(x) < Math.abs(y))
		{
			if (y > 0)
			{
				result[0] = MoveAction.Direction.UP;
				result[3] = MoveAction.Direction.DOWN;
			}
			else
			{
				result[0] = MoveAction.Direction.DOWN;
				result[3] = MoveAction.Direction.UP;
			}
			if (x > 0)
			{
				result[1] = MoveAction.Direction.LEFT;
				result[2] = MoveAction.Direction.RIGHT;
			}
			else
			{
				result[1] = MoveAction.Direction.RIGHT;
				result[2] = MoveAction.Direction.LEFT;
			}
		}
		else
		{
			if (x > 0)
			{
				result[0] = MoveAction.Direction.LEFT;
				result[3] = MoveAction.Direction.RIGHT;
			}
			else
			{
				result[0] = MoveAction.Direction.RIGHT;
				result[3] = MoveAction.Direction.LEFT;

			}
			if (y > 0)
			{
				result[1] = MoveAction.Direction.UP;
				result[2] = MoveAction.Direction.DOWN;
			}
			else
			{
				result[1] = MoveAction.Direction.DOWN;
				result[2] = MoveAction.Direction.UP;
			}
		}

		return result;
	}

}
