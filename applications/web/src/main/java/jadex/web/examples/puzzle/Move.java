package jadex.web.examples.puzzle;

import java.io.Serializable;

/**
 *  A move consisting of a start and an end point.
 */
public class Move	implements Serializable, Cloneable
{
	//-------- attributes --------

	/** The start position. */
	protected Position start;

	/** The end position. */
	protected Position end;

	//-------- constructors --------

	/**
	 *  Create a move.
	 */
	public Move()
	{
		// Bean constructor, do not remove.
	}
	
	/**
	 *  Create a move.
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
	 *  Set the start position.
	 */
	public void setStart(Position start)
	{
		this.start	= start;
	}

	/**
	 *  Set the end position.
	 */
	public void setEnd(Position end)
	{
		this.end	= end;
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

	/**
	 *  Clone the object.
	 *  @return The clone.
	 */
	public Object clone()
	{
		try
		{
			Move clone = (Move)super.clone();
			clone.start = (Position)start.clone();
			clone.end = (Position)end.clone();
			return clone;
		}
		catch(CloneNotSupportedException e)
		{
			throw new RuntimeException("Could not clone: "+this);
		}
	}	
}
