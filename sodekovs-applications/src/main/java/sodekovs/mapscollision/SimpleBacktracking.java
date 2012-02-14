package sodekovs.mapscollision;

import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Int;

/**
 * Tries with basic Backtracking to find ways
 * 
 * @author wolf.posdorfer
 * 
 */
public class SimpleBacktracking
{

	public static final int WALKABLE = MapConstant.WALKABLE;
	public static final int NOTWALKABLE = MapConstant.NOTWALKABLE;
	public static final int VISITED = -3;

	private MapService _mapservice = MapService.getInstance();

	public SimpleBacktracking()
	{

	}

	public int[][] combineArrays(int[][] a, int[][] b)
	{
		int[][] result = new int[a.length][a[0].length];

		for (int y = 0; y < a.length; y++)
			for (int x = 0; x < a[y].length; x++)
			{
				if (a[y][x] == b[y][x])
				{
					result[y][x] = a[y][x];
				}
				else
				{
					result[y][x] = WALKABLE;
				}
			}
		return result;
	}

	/**
	 * Finds a simple Way between two positions
	 * 
	 * @param from
	 * @param to
	 * @param arr
	 *            the int[][] on where the way should be marked
	 * @return <code>true</code> if a way was found, else <code>false</code>
	 */
	public boolean findWayFrom(IVector2 from, IVector2 to, int[][] arr,
			MoveAction.Direction[] dirs, int counter)
	{
		if (from.equals(to))
		{
			return true;
		}
		if (!_mapservice.isPassable(from))
		{
			return false;
		}
		if (alreadyVisited(from, arr))
		{
			return false;
		}

		markFieldAsSolution(from, arr, counter);
		dirs = DirectionUtility.getDirections(from, to);
		for (int i = 0; i < dirs.length; i++)
		{
			switch (dirs[i])
			{
			case UP:
				if (findWayFrom(getUp(from), to, arr, dirs, counter + 1))
					return true;
			case DOWN:
				if (findWayFrom(getDown(from), to, arr, dirs, counter + 1))
					return true;
			case LEFT:
				if (findWayFrom(getLeft(from), to, arr, dirs, counter + 1))
					return true;
			case RIGHT:
				if (findWayFrom(getRight(from), to, arr, dirs, counter + 1))
					return true;
			}
		}

		markFieldAsVisited(from, arr);

		return false;
	}

	private Vector2Int getRight(IVector2 from)
	{
		return new Vector2Int((from.getXAsInteger() + 1) % _mapservice.getWidth(),
				from.getYAsInteger());
	}

	private Vector2Int getLeft(IVector2 from)
	{
		return new Vector2Int((_mapservice.getWidth() + from.getXAsInteger() - 1)
				% _mapservice.getWidth(), from.getYAsInteger());
	}

	private Vector2Int getDown(IVector2 from)
	{
		return new Vector2Int(from.getXAsInteger(), (from.getYAsInteger() + 1)
				% _mapservice.getHeight());
	}

	private Vector2Int getUp(IVector2 from)
	{
		return new Vector2Int(from.getXAsInteger(),
				(_mapservice.getHeight() + from.getYAsInteger() - 1) % _mapservice.getHeight());
	}

	private boolean alreadyVisited(IVector2 pos, int[][] arr)
	{
		int value = arr[pos.getYAsInteger()][pos.getXAsInteger()];
		return value == VISITED || value > 0;
	}

	private void markFieldAsSolution(IVector2 pos, int[][] arr, int value)
	{
		arr[pos.getYAsInteger()][pos.getXAsInteger()] = value;
	}

	private void markFieldAsVisited(IVector2 pos, int[][] arr)
	{
		arr[pos.getYAsInteger()][pos.getXAsInteger()] = VISITED;
	}

	public static void main(String[] args)
	{

		SimpleBacktracking simpleb = new SimpleBacktracking();

		IVector2 from = new Vector2Int(29, 71);
		IVector2 to = new Vector2Int(11, 83);
		int[][] arr = MapService.getInstance().getMapInt();
		@SuppressWarnings("unused")
		boolean foundway = simpleb.findWayFrom(from, to, arr,
				DirectionUtility.getDirections(from, to), 0);
		printarrNoNumbers(arr);

	}

	public static void printarr(int[][] arr)
	{
		for (int y = 0; y < arr.length; y++)
		{
			for (int x = 0; x < arr[y].length; x++)
			{

				char value = (char) (49 + arr[y][x]);

				if (value == MapConstant.NOTWALKABLE)
					value = '\u2588';
				else if (value == MapConstant.WALKABLE)
					value = ' ';

				System.out.print(value);
			}
			System.out.println();
		}
		System.out.println("==========================================\n");

	}

	public static void printarrNoNumbers(int[][] arr)
	{
		for (int y = 0; y < arr.length; y++)
		{
			for (int x = 0; x < arr[y].length; x++)
			{
				String output;
				switch (arr[y][x])
				{
				case NOTWALKABLE:
					output = "\u2588";
					break;
				case WALKABLE:
					output = " ";
					break;
				case VISITED:
					output = "o";
					break;
				default:
					output = "x";
				}

				System.out.print(output);
			}
			System.out.println();
		}
		System.out.println("==========================================\n");
	}
}
