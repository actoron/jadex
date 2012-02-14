package sodekovs.mapscollision;

import jadex.extension.envsupport.math.IVector2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 
 * @author wolf.posdorfer
 * 
 */
public class AstarWayPoint extends WayPoint implements Comparable<AstarWayPoint>
{

	private AstarWayPoint _parent;
	private AstarWayPoint _start;
	private AstarWayPoint _end;

	public AstarWayPoint(IVector2 pos, boolean walkable)
	{
		super(pos, walkable);
	}

	public AstarWayPoint(IVector2 pos, boolean walkable, AstarWayPoint start, AstarWayPoint end)
	{
		super(pos, walkable);
		_start = start;
		_end = end;

	}

	public AstarWayPoint getParent()
	{
		return _parent;
	}

	public void setParent(AstarWayPoint parent)
	{
		_parent = parent;
	}

	public void setStart(AstarWayPoint point)
	{
		_start = point;
	}

	public void setEnd(AstarWayPoint point)
	{
		_end = point;
	}

	public List<AstarWayPoint> getNeighbours_()
	{
		return convertWayPoint(super.getNeighbours(), _start, _end);
	}

	/**
	 * Heuristic distance to the end
	 * 
	 * @param to
	 * @return
	 */
	public double getDistanceToEnd()
	{
		int tox = _end.getPos().getXAsInteger();
		int toy = _end.getPos().getYAsInteger();
		int myx = getPos().getXAsInteger();
		int myy = getPos().getYAsInteger();

		return Math.sqrt(Math.pow((tox - myx), 2) + Math.pow((toy - myy), 2));
	}

	/**
	 * Distance from here to the Source vertix
	 * 
	 * @return
	 */
	public int getSourceDistance()
	{

		if (_start == null)
		{
			return 0;
		}
		else
			return _parent.getSourceDistance() + 1;
	}

	public static AstarWayPoint convertToAstar(WayPoint p, AstarWayPoint start, AstarWayPoint end)
	{
		AstarWayPoint result = new AstarWayPoint(p.getPos(), p.isWalkable(), start, end);
		result.setEast(p.getEast());
		result.setWest(p.getWest());
		result.setNorth(p.getNorth());
		result.setSouth(p.getSouth());

		return result;
	}

	public static ArrayList<AstarWayPoint> convertWayPoint(Collection<WayPoint> points,
			AstarWayPoint start, AstarWayPoint end)
	{
		ArrayList<AstarWayPoint> list = new ArrayList<AstarWayPoint>();

		for (WayPoint p : points)
		{
			list.add(convertToAstar(p, start, end));
		}

		return list;
	}

	@Override
	protected AstarWayPoint clone()
	{
		AstarWayPoint result = new AstarWayPoint(getPos().copy(), isWalkable(), _start, _end);
		result.setEast(getEast());
		result.setWest(getWest());
		result.setNorth(getNorth());
		result.setSouth(getSouth());
		return result;
	}

	@Override
	public int compareTo(AstarWayPoint point)
	{

		double dis = point.getDistanceToEnd();
		double mydis = this.getDistanceToEnd();

		int result = 0;
		if (mydis < dis)
		{
			result = -1;
		}
		else if (mydis > dis)
		{
			result = 1;
		}
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof AstarWayPoint && ((AstarWayPoint) obj).getPos().equals(getPos()));
		// return super.equals(obj);
	}
}
