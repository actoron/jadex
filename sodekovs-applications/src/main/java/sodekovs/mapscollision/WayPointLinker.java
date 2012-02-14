package sodekovs.mapscollision;

import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Int;

/**
 * WayPointLinker creates a graph of WayPoints given start and endpoint<br>
 * <br>
 * Usage:<br>
 * wp = new WayPointLinker(MapService.getInstance().getMapInt, new
 * Vector2Int(10,20), new Vector2Int(30,40));<br>
 * wp.getStartingPoint() <code> <- Start searching from here</code><br>
 * wp.getEndingPoint()<code> <- Stop searching here</code><br>
 * 
 * @author wolf.posdorfer
 * 
 */
public class WayPointLinker
{

	/**
	 * The WayPoint that has the same Coordinates as start
	 */
	private WayPoint _startpoint;
	/**
	 * The WayPoint that has the same Coordinates as end
	 */
	private WayPoint _endpoint;

	/**
	 * Creates a graph given an array of integers
	 * 
	 * @param array
	 * @param start
	 * @param end
	 */
	public WayPointLinker(int[][] array, IVector2 start, IVector2 end)
	{

		WayPoint[][] wayps = createWaypoints(array, start, end);
		combineWayPoints(wayps);

	}

	private WayPoint[][] createWaypoints(int[][] arr, IVector2 start, IVector2 end)
	{

		int size = arr.length;
		WayPoint[][] result = new WayPoint[size][size];
		for (int y = 0; y < size; y++)
		{
			for (int x = 0; x < size; x++)
			{
				boolean walk = arr[y][x] != MapConstant.NOTWALKABLE;
				WayPoint create = new WayPoint(new Vector2Int(x, y), walk);
				if (x == start.getXAsInteger() && y == start.getYAsInteger())
					_startpoint = create;
				else if (x == end.getXAsInteger() && y == end.getYAsInteger())
					_endpoint = create;

				result[y][x] = create;
			}
		}
		return result;
	}

	private void combineWayPoints(WayPoint[][] arr)
	{
		int yleng = arr.length;
		int xleng = arr[0].length;

		for (int y = 0; y < yleng; y++)
		{
			for (int x = 0; x < xleng; x++)
			{
				WayPoint p = arr[y][x];
				WayPoint north = arr[(yleng + y - 1) % yleng][x];
				WayPoint south = arr[(y + 1) % yleng][x];
				WayPoint west = arr[y][(xleng + x - 1) % xleng];
				WayPoint east = arr[y][(x + 1) % xleng];

				if (p.isWalkable())
				{
					if (north != null && north.isWalkable())
						p.setNorth(north);
					if (south != null && south.isWalkable())
						p.setSouth(south);
					if (west != null && west.isWalkable())
						p.setWest(west);
					if (east != null && east.isWalkable())
						p.setEast(east);
				}
				else
				{
					arr[y][x] = null;
				}
			}
		}

	}

	public WayPoint getStartingPoint()
	{
		return _startpoint;
	}

	public WayPoint getEndingPoint()
	{
		return _endpoint;
	}

	public static void printarr(WayPoint[][] arr)
	{
		for (int y = 0; y < arr.length; y++)
		{
			for (int x = 0; x < arr[y].length; x++)
			{
				System.out.print(arr[y][x] != null ? arr[y][x].toWalkable() : " ");
			}
			System.out.println();
		}
		System.out.println("==========================================\n");

	}

	public static void main(String[] args)
	{
		SimpleBacktracking sb = new SimpleBacktracking();
		IVector2 from = new Vector2Int(29, 71);
		IVector2 to = new Vector2Int(11, 83);
		int[][] arr = MapService.getInstance().getMapInt();
		sb.findWayFrom(from, to, arr, DirectionUtility.getDirections(from, to), 0);
		new WayPointLinker(arr, from, to);

	}
}
