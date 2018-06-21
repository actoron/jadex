package jadex.extension.envsupport.math;

import java.math.BigDecimal;

public interface IVector1
{
	/** Adds another vector to this vector, adding individual components.
	 *
	 *  @param vector the vector to add to this vector
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector1 add(IVector1 vector);
	
	/** Subtracts another vector to this vector, subtracting individual components.
	 *
	 *  @param vector the vector to subtract from this vector
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector1 subtract(IVector1 vector);
	
	/** Performs a multiplication on the vector.
	 *
	 *  @param vector vector
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector1 multiply(IVector1 vector);
	
	/** Sets the vector component to zero.
	 *
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector1 zero();
	
	/** Negates the vector by negating its components.
	 *
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector1 negate();
	
	/**
	 *  Calculate the square root.
	 *  @return The square root.
	 */
	public IVector1 sqrt();
	
	/**
	 *  Calculate the modulo.
	 *  @return The modulo value.
	 */
	
	
	/**
	 *  Calculate the cube root.
	 *  @return The cube root.
	 */
	public IVector1 cbrt();
	
	public IVector1 mod(IVector1 mod);
	
	/** Returns the distance to another vector
	 *
	 *  @param vector other vector 
	 *  @return distance
	 */
	public IVector1 getDistance(IVector1 vector);
	
	/** Returns the vector as integer.
	 *
	 *  @return vector as integer
	 */
	public int getAsInteger();
	
	/** Returns the vector as long.
	 *
	 *  @return vector as long
	 */
	public long getAsLong();
	
	/** Returns the vector as float.
	 *
	 *  @return vector as float
	 */
	public float getAsFloat();
	
	/** Returns the vector as double.
	 *
	 *  @return vector as double
	 */
	public double getAsDouble();
	
	/** Returns the vector as BigDecimal.
	 *
	 *  @return vector as BigDecimal
	 */
	public BigDecimal getAsBigDecimal();

	/** Makes a copy of the vector without using the complex clone interface.
	 *
	 *  @return copy of the vector
	 */
	public IVector1 copy();
	
	/** Generates a deep clone of the vector.
	 *
	 *  @return clone of this vector
	 */
	public Object clone() throws CloneNotSupportedException;
	
	/** Compares the vector to an object
	 * 
	 * @param obj the object
	 * @return always returns false unless the object is an IVector2,
	 *         in which case it is equivalent to equals(IVector vector)
	 */
	public boolean equals(Object obj);
	
	/** Compares the vector to another vector.
	 *  The vectors are equal if the components are equal.
	 * 
	 * @param vector the other vector
	 * @return true if the vectors are equal
	 */
	public boolean equals(IVector1 vector);
	
	/** Tests if the vector is greater than another vector.
	 * 
	 * @param vector the other vector
	 * @return true if the vector is greater than the given vector.
	 */
	public boolean greater(IVector1 vector);
	
	/** Tests if the vector is less than another vector.
	 * 
	 * @param vector the other vector
	 * @return true if the vector is less than the given vector.
	 */
	public boolean less(IVector1 vector);
	
	/**
	 *  Create a vector2 from this and another vector.
	 *  @param sec The second vector.
	 */
	public IVector2 createVector2(IVector1 sec);
}
