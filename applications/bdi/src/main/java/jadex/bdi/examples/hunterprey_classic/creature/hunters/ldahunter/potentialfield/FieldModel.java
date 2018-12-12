/*
 * FieldModel.java
 * Copyright (c) 2004 by University of Hamburg. All Rights Reserved.
 * Departament of Informatics. 
 * Distributed Systems and Information Systems.
 *
 * Created by 9walczak on Sep 24, 2004.  
 * Last revision $Revision: 2562 $ by:
 * $Author: pokahr $ on $Date: 2005-04-26 16:31:45 +0200 (Di, 26 Apr 2005) $.
 */
package jadex.bdi.examples.hunterprey_classic.creature.hunters.ldahunter.potentialfield;

import jadex.bdi.examples.hunterprey_classic.Location;

/**
 *
 */
public final class FieldModel
{
	final int w;
	final int h;

	/**
	 * <code>obstacles</code> obstacle in field model
	 */
	public final boolean[][] obstacles;

	/**
	 * <code>visits</code>
	 */
	public final int[][] visits;

	final int[][] distance;

	/**
	 * Constructor: <code>FieldModel</code>.
	 * @param w width
	 * @param h height
	 */
	public FieldModel(int w, int h)
	{
		this.w = w;
		this.h = h;
		obstacles = new boolean[w][h];
		distance = new int[w][h];
		visits = new int[w][h];
	}

	/**
	 * @param loc the location the values should be stored to (in, out)
	 * @return false if this location has distance &lt;= 0, true if location changed
	 */
	public final boolean getNearerLocation(final Location loc)
	{
		final int ix = loc.getX();
		final int iy = loc.getY();
		int ox = ix;
		int oy = iy;
		double height = distance[ix][iy];
		if(height<0.0) return false;

		if(distance[(ix+1)%w][iy]<height)
		{
			height = distance[(ix+1)%w][iy];
			ox = (ix+1)%w;
			oy = iy;
		}
		if(distance[(ix+w-1)%w][iy]<height)
		{
			height = distance[(ix+w-1)%w][iy];
			ox = (ix+w-1)%w;
			oy = iy;
		}
		if(distance[ix][(iy+1)%h]<height)
		{
			height = distance[ix][(iy+1)%h];
			ox = ix;
			oy = (iy+1)%h;
		}
		if(distance[ix][(iy+h-1)%h]<height)
		{
			ox = ix;
			oy = (iy+h-1)%h;
		}

		loc.setX(ox);
		loc.setY(oy);
		return ox!=ix || oy!=iy;
	}

	/**
	 * @param mx - from x
	 * @param my - from y
	 */
	public void calcDistance(int mx, int my)
	{
		clearDistance();
		distance[mx][my] = 0;
		goR(mx, my, 1);
		goD(mx, my, 1);
		goL(mx, my, 1);
		goU(mx, my, 1);
	}

	/**
	 * @param x
	 * @param y
	 * @param d
	 * @return own distance
	 */
	protected final int goR(int x, int y, final int d)
	{
		x = (x+1)%w;  // go right
		if(obstacles[x][y])
		{
			return distance[x][y] = Integer.MAX_VALUE-2;
		}

		int step = d+1;
		int reverse;
		if(distance[x][y]>d)
		{ // found new way to x, y
			distance[x][y] = d;
			reverse = goD(x, y, step);
			if(reverse+1<distance[x][y])
			{ // wow there is even shorter way
				distance[x][y] = reverse+1;
				step = reverse+2;
			}
			reverse = goR(x, y, step);
			if(reverse+1<distance[x][y])
			{ // wow there is even shorter way
				distance[x][y] = reverse+1;
				step = reverse+2;
			}
			reverse = goU(x, y, step);
			if(reverse+1<distance[x][y])
			{ // wow there is even shorter way
				distance[x][y] = reverse+1;
				step = reverse+2;
			}
		}

		return distance[x][y];
	}

	/**
	 * @param x
	 * @param y
	 * @param d
	 * @return own distance
	 */
	protected final int goD(int x, int y, final int d)
	{
		y = (y+1)%h;  // go down
		if(obstacles[x][y])
		{
			return distance[x][y] = Integer.MAX_VALUE-2;
		}

		int step = d+1;
		int reverse;
		if(distance[x][y]>d)
		{ // found new way to x, y
			distance[x][y] = d;
			reverse = goL(x, y, step);
			if(reverse+1<distance[x][y])
			{ // wow there is even shorter way
				distance[x][y] = reverse+1;
				step = reverse+2;
			}
			reverse = goD(x, y, step);
			if(reverse+1<distance[x][y])
			{ // wow there is even shorter way
				distance[x][y] = reverse+1;
				step = reverse+2;
			}
			reverse = goR(x, y, step);
			if(reverse+1<distance[x][y])
			{ // wow there is even shorter way
				distance[x][y] = reverse+1;
				step = reverse+2;
			}
		}

		return distance[x][y];
	}

	/**
	 * @param x
	 * @param y
	 * @param d
	 * @return own distance
	 */
	protected final int goL(int x, int y, final int d)
	{
		x = (x+w-1)%w;  // go left
		if(obstacles[x][y])
		{
			return distance[x][y] = Integer.MAX_VALUE-2;
		}

		int step = d+1;
		int reverse;
		if(distance[x][y]>d)
		{ // found new way to x, y
			distance[x][y] = d;
			reverse = goU(x, y, step);
			if(reverse+1<distance[x][y])
			{ // wow there is even shorter way
				distance[x][y] = reverse+1;
				step = reverse+2;
			}
			reverse = goL(x, y, step);
			if(reverse+1<distance[x][y])
			{ // wow there is even shorter way
				distance[x][y] = reverse+1;
				step = reverse+2;
			}
			reverse = goD(x, y, step);
			if(reverse+1<distance[x][y])
			{ // wow there is even shorter way
				distance[x][y] = reverse+1;
				step = reverse+2;
			}
		}

		return distance[x][y];
	}

	/**
	 * @param x
	 * @param y
	 * @param d
	 * @return own distance
	 */
	protected final int goU(int x, int y, final int d)
	{
		y = (y+h-1)%h;  // go up
		if(obstacles[x][y])
		{
			return distance[x][y] = Integer.MAX_VALUE-2;
		}

		int step = d+1;
		int reverse;
		if(distance[x][y]>d)
		{ // found new way to x, y
			distance[x][y] = d;
			reverse = goR(x, y, step);
			if(reverse+1<distance[x][y])
			{ // wow there is even shorter way
				distance[x][y] = reverse+1;
				step = reverse+2;
			}
			reverse = goU(x, y, step);
			if(reverse+1<distance[x][y])
			{ // wow there is even shorter way
				distance[x][y] = reverse+1;
				step = reverse+2;
			}
			reverse = goL(x, y, step);
			if(reverse+1<distance[x][y])
			{ // wow there is even shorter way
				distance[x][y] = reverse+1;
				step = reverse+2;
			}
		}

		return distance[x][y];
	}

	/**
	 * clears the first buffer
	 */
	public void clearDistance()
	{
		for(int i = w; i-->0;)
		{
			for(int j = h; j-->0;)
			{
				distance[i][j] = Integer.MAX_VALUE-2;
			}
		}
	}

	/**
	 * @param x
	 * @param y
	 * @param r
	 * @param round
	 */
	public void clearRange(int x, int y, int r, int round)
	{
		for(int i = x-r; i<=x+r; i++)
		{
			for(int j = y-r; j<=y+r; j++)
			{
				visits[(i+w)%w][(j+h)%h] = round;
			}
		}
	}
}
