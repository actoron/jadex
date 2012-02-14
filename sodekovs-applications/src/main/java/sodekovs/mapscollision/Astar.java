package sodekovs.mapscollision;

import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Int;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * http://de.wikipedia.org/wiki/A*
 * 
 * @author wolf.posdorfer
 */
public class Astar
{

	/**
	 * Calculates the Route between two given coordinates
	 * 
	 * @param from
	 * @param position
	 * @return {@link ConcurrentLinkedQueue}&lt;MoveAction.Direction>
	 */
	public static Queue<MoveAction.Direction> calculateRoute(IVector2 from, IVector2 position)
	{
		Astar astar = new Astar();
		ConcurrentLinkedQueue<MoveAction.Direction> movequeue = new ConcurrentLinkedQueue<MoveAction.Direction>();

		AstarWayPoint endpoint = astar.findpath(from, position.copy());

		ArrayList<WayPoint> liste = astar.getWayList(endpoint);

		MoveAction.Direction first = MoveAction.getDirectionToPos(from, liste.get(0).getPos());
		if (first != MoveAction.Direction.NONE)
			movequeue.add(first);

		for (int i = 1; i < liste.size(); i++)
		{
			movequeue.add(MoveAction.getDirectionToPos(liste.get(i - 1).getPos(), liste.get(i)
					.getPos()));
		}

		return movequeue;
	}

	public Astar()
	{

	}

	/**
	 * Returns the End-Node with linked Parents to find way from end to start,
	 * by traversing the Parents
	 * 
	 * @param start
	 *            from here
	 * @param end
	 *            to here
	 * @return {@link AstarWayPoint} or <code>null</code>
	 */
	public AstarWayPoint shortestPath(WayPoint start, WayPoint end)
	{

		AstarWayPoint astart = AstarWayPoint.convertToAstar(start, null, null);
		AstarWayPoint aend = AstarWayPoint.convertToAstar(end, astart, null);
		astart.setEnd(aend);

		PriorityQueue<AstarWayPoint> openlist = new PriorityQueue<AstarWayPoint>();
		List<AstarWayPoint> closedlist = new ArrayList<AstarWayPoint>();

		openlist.add(astart);

		while (!openlist.isEmpty())
		{
			AstarWayPoint currentNode = openlist.remove();

			if (currentNode.equals(aend))
			{
				return currentNode;
			}

			expandNode(currentNode, openlist, closedlist);

			closedlist.add(currentNode);

		}

		return null;
	}

	private void expandNode(AstarWayPoint current, PriorityQueue<AstarWayPoint> openlist,
			List<AstarWayPoint> closedlist)
	{

		for (AstarWayPoint successor : current.getNeighbours_())
		{
			if (closedlist.contains(successor))
				continue;

			int tentative_g = current.getSourceDistance() + 1;

			if (openlist.contains(successor) && tentative_g >= successor.getSourceDistance())
				continue;

			successor.setParent(current);

			if (!openlist.contains(successor))
				openlist.add(successor);

		}
	}

	public ArrayList<WayPoint> getWayList(AstarWayPoint endpoint)
	{

		AstarWayPoint current = endpoint;
		ArrayList<WayPoint> result = new ArrayList<WayPoint>();
		result.add(endpoint);

		while (current != null && current.getParent() != null)
		{
			current = current.getParent();
			result.add(0, current);
		}

		return result;

	}

	public AstarWayPoint findpath(IVector2 from, IVector2 to)
	{
		WayPointLinker wpl = new WayPointLinker(MapService.getInstance().getMapInt(), from, to);
		AstarWayPoint endpoint = shortestPath(wpl.getStartingPoint(), wpl.getEndingPoint());
		return endpoint;
	}

	public static void main(String[] args)
	{
		Astar astar = new Astar();
		// IVector2 from = new Vector2Int(6, 77);
		// IVector2 to = new Vector2Int(23, 88);
		IVector2 from = new Vector2Int(29, 71);
		IVector2 to = new Vector2Int(50, 86);
		WayPointLinker wpl = new WayPointLinker(MapService.getInstance().getMapInt(), from, to);

		SimpleBacktracking.printarrNoNumbers(MapService.getInstance().getMapInt());

		AstarWayPoint endpoint = astar.shortestPath(wpl.getStartingPoint(), wpl.getEndingPoint());

		dostuff(astar.getWayList(endpoint));

	}

	public static void dostuff(ArrayList<WayPoint> wplist)
	{
		int[][] arr = MapService.getInstance().getMapInt();

		for (WayPoint wp : wplist)
		{

			arr[wp.getPos().getYAsInteger()][wp.getPos().getXAsInteger()] = 3;
		}

		SimpleBacktracking.printarrNoNumbers(arr);

	}

}
