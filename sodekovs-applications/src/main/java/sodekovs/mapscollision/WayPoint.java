package sodekovs.mapscollision;

import jadex.extension.envsupport.math.IVector2;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A WayPoint is a Vertex in a Graph with at most four neighbouring WayPoints
 * 
 * @author wolf.posdorfer
 * 
 */
public class WayPoint
{

	private WayPoint _north;
	private WayPoint _south;
	private WayPoint _west;
	private WayPoint _east;
	private IVector2 _pos;
	private boolean _walkable;

	public WayPoint(IVector2 pos, boolean walkable)
	{
		_pos = pos;
		_walkable = walkable;
	}

	public WayPoint(IVector2 pos, boolean walkable, WayPoint north, WayPoint south, WayPoint west,
			WayPoint east)
	{
		_pos = pos;
		_walkable = walkable;
		_north = north;
		_south = south;
		_west = west;
		_east = east;
	}

	/**
	 * ArrayList of Neighbours, no <code>null</code> inside
	 * 
	 * @return
	 */
	public Collection<WayPoint> getNeighbours()
	{
		ArrayList<WayPoint> liste = new ArrayList<WayPoint>();

		if (_north != null)
			liste.add(_north);

		if (_south != null)
			liste.add(_south);

		if (_west != null)
			liste.add(_west);

		if (_east != null)
			liste.add(_east);

		return liste;
	}

	public boolean hasNeighbour(WayPoint nei)
	{
		return (_north == nei || _south == nei || _west == nei || _east == nei);
	}

	public WayPoint getNorth()
	{
		return _north;
	}

	public WayPoint getSouth()
	{
		return _south;
	}

	public WayPoint getWest()
	{
		return _west;
	}

	public WayPoint getEast()
	{
		return _east;
	}

	public void setNorth(WayPoint north)
	{
		_north = north;
	}

	public void setSouth(WayPoint south)
	{
		_south = south;
	}

	public void setWest(WayPoint west)
	{
		_west = west;
	}

	public void setEast(WayPoint east)
	{
		_east = east;
	}

	public IVector2 getPos()
	{
		return _pos;
	}

	public void setPos(IVector2 pos)
	{
		_pos = pos;
	}

	public boolean isWalkable()
	{
		return _walkable;
	}

	/**
	 * Only for graphical debugging
	 * 
	 * @return
	 */
	public String toWalkable()
	{
		return _walkable ? "1" : "0";
	}

	@Override
	public boolean equals(Object obj)
	{

		if (obj instanceof WayPoint)
		{
			WayPoint comp = (WayPoint) obj;
			return toString().equals(comp.toString());
		}
		return false;
	}

	@Override
	public String toString()
	{

		return "[pos=" + _pos.getXAsInteger() + "," + _pos.getYAsInteger() + ";walkable="
				+ _walkable + ";neighbours=" + getNeighbours().size() + "]";
	}
}
