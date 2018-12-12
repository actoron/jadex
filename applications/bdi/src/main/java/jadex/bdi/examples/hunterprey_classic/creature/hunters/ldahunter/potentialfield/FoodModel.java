/*
 * FoodModel.java
 * Copyright (c) 2004 by University of Hamburg. All Rights Reserved.
 * Departament of Informatics. 
 * Distributed Systems and Information Systems.
 *
 * Created by 9walczak on Sep 24, 2004.  
 * Last revision $Revision: 2562 $ by:
 * $Author: pokahr $ on $Date: 2005-04-26 16:31:45 +0200 (Di, 26 Apr 2005) $.
 */
package jadex.bdi.examples.hunterprey_classic.creature.hunters.ldahunter.potentialfield;

/**
 * 
 */
public final class FoodModel
{
	final int w;
	final int h;
	final int[][] food;

	/**
	 * @param x
	 * @param y
	 * @param r vision range
	 */
	public void clearRange(final int x, final int y, final int r)
	{
		for(int i = x-r; i<=x+r; i++)
		{
			for(int j = y-r; j<=y+r; j++)
			{
				food[(i+w)%w][(j+h)%h] = 0;
			}
		}
	}

	/**
	 * Constructor: <code>FoodModel</code>.
	 * @param w
	 * @param h
	 */
	public FoodModel(int w, int h)
	{
		this.w = w;
		this.h = h;
		food = new int[w][h];
	}
}
