package jadex.web.examples.puzzle;

import java.io.Serializable;

/**
 *  A piece for playing.
 */
public class Piece	implements Serializable, Cloneable
{
	//-------- attributes --------

	/** The piece color (white or black). */
	protected boolean white;

	//-------- constructors --------

	/**
	 *  Create a new piece.
	 */
	public Piece()
	{
		// Do not remove. Bean constructor.
	}
	
	/**
	 *  Create a new board.
	 */
	public Piece(boolean white)
	{
		this.white = white;
	}

	//-------- methods --------

	/**
	 *  Test, if it is a white piece.
	 *  @return True, if it a white piece.
	 */
	public boolean isWhite()
	{
		return white;
	}

	/**
	 *  Set tu true, if it is a white piece.
	 */
	// Hack!!! required for bean.
	public void setWhite(boolean white)
	{
		this.white = white;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return isWhite()? "white": "black";
	}

	/**
	 *  Clone the object.
	 *  @return The clone.
	 */
	public Object clone()
	{
		try
		{
			return super.clone();
		}
		catch(CloneNotSupportedException e)
		{
			throw new RuntimeException("Could not clone: "+this);
		}
	}
}
