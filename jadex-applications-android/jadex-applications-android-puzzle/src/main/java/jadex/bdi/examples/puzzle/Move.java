package jadex.bdi.examples.puzzle;

import java.io.Serializable;

/**
 *  A move consisting of a start and an end point.
 */
public class Move	implements Serializable
{
	//-------- attributes --------

	/** The start position. */
	protected Position start;

	/** The end position. */
	protected Position end;

	//-------- constructors --------

	/**
	 *  Create a position.
	 */
	public Move(Position start, Position end)
	{
		this.start = start;
		this.end = end;
	}

	//-------- methods --------

	/**
	 *  Get the start.
	 */
	public Position getStart()
	{
		return start;
	}

	/**
	 *  Get the target.
	 */
	public Position getEnd()
	{
		return end;
	}

	/**
	 *  Test if it is a jump move.
	 */
	public boolean isJumpMove()
	{
		return Math.abs(start.getX()-end.getX())==2 || Math.abs(start.getY()-end.getY())==2;
	}

	/**
	 *  Test if two positions are equal.
	 *  @return True, if equal.
	 */
	public boolean equals(Object o)
	{
		boolean ret = false;
		if(o instanceof Move)
		{
			Move tmp = (Move)o;
			if(tmp.getStart().equals(getStart()) && tmp.getEnd().equals(getEnd()))
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
		// todo: use xor?
		return getStart().hashCode()<<16+getEnd().hashCode();
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return getStart()+" "+getEnd();
	}
}
