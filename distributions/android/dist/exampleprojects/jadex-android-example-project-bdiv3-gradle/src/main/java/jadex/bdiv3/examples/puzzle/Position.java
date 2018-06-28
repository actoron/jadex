package jadex.bdiv3.examples.puzzle;

import java.io.Serializable;

/**
 *  A position has two coordinates.
 */
public class Position	implements	Serializable
{
	//-------- attributes --------

	/** The x position. */
	protected int x;

	/** The y position. */
	protected int y;

	//-------- constructors --------

	/**
	 *  Create a position.
	 */
	public Position()
	{
		// do not remove bean constructor.
	}

	/**
	 *  Create a position.
	 */
	public Position(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	//-------- methods --------

	/**
	 *  Get the x value.
	 */
	public int getX()
	{
		return x;
	}

	/**
	 *  Get the y value.
	 */
	public int getY()
	{
		return y;
	}

	/**
	 *  Set x.
	 *  @param x
	 */
	public void setX(int x)
	{
		this.x = x;
	}

	/**
	 *  Set y.
	 *  @param y
	 */
	public void setY(int y)
	{
		this.y = y;
	}

	/**
	 *  Test if two positions are equal.
	 *  @return True, if equal.
	 */
	public boolean equals(Object o)
	{
		boolean ret = false;
		if(o instanceof Position)
		{
			Position tmp = (Position)o;
			if(tmp.getX()==x && tmp.getY()==y)
				ret = true;
		}
		return ret;
	}

	/**
	 *  Calculate the hash code.
	 *  @return The hash code.
	 */
	public int hashCode()
	{
		return y<<16+x;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return getX()+" "+getY();
	}
}
