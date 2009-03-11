/**
 * 
 */
package jadex.bdi.planlib.simsupport.environment.grid;

import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.common.math.Vector2Int;



/**
 * The GridPosition is a simple Vector2Int that extends the
 * equals and the hashCode methods from Vector2Int to ensure
 * The matching of two GridPositions.
 * 
 * @author Claas
 */
public class GridPosition extends Vector2Int
{
	// ------- constructors -------
	
	/**
	 * Creates a GridPosition from the given position
	 * @param vector
	 */
	public GridPosition(GridPosition position)
	{
		super(position);
	}

	/**
	 * @see Vector2Int#Vector2Int(int)
	 * @param scalar
	 */
	public GridPosition(int scalar)
	{
		super(scalar);
	}

	/**
	 * @see Vector2Int#Vector2Int(int, int)
	 * @param x
	 * @param y
	 */
	public GridPosition(int x, int y)
	{
		super(x, y);
	}
	
	/**
	 * @see Vector2Int#Vector2Int(IVector2)
	 * @param vector
	 */
	public GridPosition(IVector2 vector)
	{
		super(vector);
	}
	
	// ------- methods -------

	/** 
	 * Makes a copy of the GridPosition.
	 * Returns a IVector2 to implement IVector2 Interface
	 *
	 *  @return copy of the GridPosition
	 */
	public IVector2 copy()
	{
		return new GridPosition(this);
	}

	/** 
	 * Compares the GridPosition to an object
	 * 
	 * This method tests only the integer value of the given GridPostiion or
	 * IVector2!
	 * 
	 * @param obj the object
	 * @return returns true if the object is a GridPosition or an IVector2,
	 * 		and the int(x) and int(y) coordinates are the same.
	 */
	public boolean equals(Object obj)
	{
		if (obj instanceof GridPosition || obj instanceof IVector2)
		{
			IVector2 pos = (IVector2) obj;
			return ((getXAsInteger() == pos.getXAsInteger()) && (getYAsInteger() == pos.getYAsInteger()));
		}
		return false;
	}
	
	

	/**
	 * Create a String representation of this GridPosition
	 */
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		buffer.append(getXAsInteger());
		buffer.append(", ");
		buffer.append(getYAsInteger());
		buffer.append("]");
		return buffer.toString();
	}

	/**
	 *  The hashcode is the 16 bit shifted x position
	 *  plus the y position.
	 *  @return The hashcode.  
	 */
	public int hashCode()
	{
		return getXAsInteger() << 16 + getYAsInteger();
	}
	
	

}
