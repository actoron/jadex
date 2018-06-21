package jadex.web.examples.puzzle;

import java.io.Serializable;
import java.util.StringTokenizer;

/**
 *  A position has two coordinates.
 */
public class Position	implements	Serializable, Cloneable
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
		//return getX()+" "+getY();
		return getPrintableX()+" "+getPrintableY();
	}

	/**
	 *  Get the string representation for x.
	 *  @return The string representation for y.
	 */
	public String getPrintableX()
	{
		return ""+Character.valueOf((char)('A'+x));
	}

	/**
	 *  Get the string representation for y.
	 *  @return The string representation for y.
	 */
	public String getPrintableY()
	{
		return ""+(y+1);
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

	/**
	 *  Convert a position string back to a position object.
	 *  It can covert either "x y" and also the printable notation "[A..X] [1..X]".
	 *  @param spos The position as string.
	 *  @return The position object.
	 */
	public static Position fromString(String spos)
	{
		Position ret = null;

		StringTokenizer stok = new StringTokenizer(spos);
		if(stok.countTokens()!=2)
			throw new IllegalArgumentException("Format exception: "+spos);
		String sx = stok.nextToken();
		String sy = stok.nextToken();

		int y = Integer.parseInt(sy);
		int x = -1;
		try
		{
			x = Integer.parseInt(sx);
		}
		catch(NumberFormatException e)
		{
			// Test if printable notation
			if(sx.length()==1)
			{
				x = sx.charAt(0)-'A';
				y--;
			}
			else
			{
				throw new IllegalArgumentException("Format exception: "+spos);
			}
		}
		ret = new Position(x, y);
		return ret;
	}

	/**
	 *  Main for testing.
	 *
	public static void main(String[] args)
	{
		Position p = new Position(2, 3);
		System.out.println("Position p is: "+p);
		System.out.println("Reconstructed position p is: "+fromString(""+p));
		System.out.println("Reconstructed position p is: "+fromString(""+p.getX()+" "+p.getY()));
	}*/
}
