package jadex.bdi.examples.garbagecollector_classic;

/**
 *  The position on a grid.
 */
public class Position
{
	/** The x position. */
	protected int x;

	/** The y position. */
	protected int y;

	//-------- constructors --------

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
	 *
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
	 *
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