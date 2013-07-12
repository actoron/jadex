package jadex.bdiv3.examples.puzzle;

import java.io.Serializable;

/**
 *  A piece for playing.
 */
public class Piece	implements Serializable
{
	//-------- attributes --------

	/** The piece color (white or black). */
	protected boolean is_white;

	//-------- constructors --------

	/**
	 *  Create a new board.
	 */
	public Piece(boolean is_white)
	{
		this.is_white = is_white;
	}

	//-------- methods --------

	/**
	 *  Test, if it is a white piece.
	 *  @return True, if it a white piece.
	 */
	public boolean isWhite()
	{
		return is_white;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return isWhite()? "white": "black";
	}
}
