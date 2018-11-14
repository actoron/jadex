package jadex.extension.envsupport.math;

import java.math.BigDecimal;

/** Interface for cartesian 2-vectors
 *  NOTE: All operations on the vector are destructive and the instance
 *        returned is the same as the one whose method was called.
 *        If you require a copy of the vector before performing operations
 *        on it, use the copy constructor, the copy method or the clone
 *        interface.
 */
public interface IVector2
{
	/** Assigns this vector the values of another vector.
	 * 
	 *  @param vector the other vector
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector2 assign(IVector2 vector);
	
	/** Adds a scalar to each component of this vector.
	 *
	 *  @param scalar scalar value as double
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector2 add(double scalar);
	
	/** Adds a scalar to each component of this vector.
	 *
	 *  @param scalar scalar value
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector2 add(IVector1 scalar);

	/** Adds another vector to this vector, adding individual components.
	 *
	 *  @param vector the vector to add to this vector
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector2 add(IVector2 vector);
	
	/** Subtracts a scalar to each component of this vector.
	 *
	 *  @param scalar scalar value as double
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector2 subtract(double scalar);
	
	/** Subtracts a scalar to each component of this vector.
	 *
	 *  @param scalar scalar value
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector2 subtract(IVector1 scalar);

	/** Subtracts another vector to this vector, subtracting individual components.
	 *
	 *  @param vector the vector to subtract from this vector
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector2 subtract(IVector2 vector);

	/** Applies a modulo vector. The modulus will be added first so that
	 *  values in the interval (-modulus, 0) will wrap over into the positive range.
	 *
	 *  @param modulus modulus
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector2 mod(IVector2 modulus);
	
	/** Performs a scalar multiplication (scaling) on the vector.
	 *
	 *  @param scalar the scale factor double
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector2 multiply(double scalar);
	
	/** Performs a scalar multiplication (scaling) on the vector.
	 *
	 *  @param scalar the scale factor
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector2 multiply(IVector1 scalar);
	
	/** Performs a multiplication on the vector.
	 *
	 *  @param vector vector
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector2 multiply(IVector2 vector);
	
	/** Performs a division on the vector.
	 *
	 *  @param vector vector
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector2 divide(IVector2 vector);
	
	/** Sets all vector components to zero.
	 *
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector2 zero();
	
	/** Negates the x-component.
	 *
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector2 negateX();
	
	/** Negates the y-component.
	 *
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector2 negateY();
	
	/** Negates the vector by negating its components.
	 *
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector2 negate();
	
	/** Sets the x-component to a random value in the interval [lower,upper]
	 * 
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector2 randomX(IVector1 lower, IVector1 upper);
	
	/** Sets the y-component to a random value in the interval [lower,upper]
	 * 
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector2 randomY(IVector1 lower, IVector1 upper);
	
	/** Converts the vector to a unit vector (normalization)
	 */
	public IVector2 normalize();
	
	/**
	 *  Redirects the vector to a new direction,
	 *  maintaining the magnitude.
	 *  
	 *  @param angle The new direction.
	 *  @return The vector.
	 */
	public IVector2 redirect(double angle);
	
	/** Returns the length (magnitude) of the vector.
	 *
	 *  @return vector length
	 */
	public double getInnerProductAsDouble(IVector2 vector);
	/** Returns the length (magnitude) of the vector.
	 *
	 *  @return vector length
	 */
	public IVector1 getLength();
	
	/** Returns the squared length (magnitude) of the vector.
	 *
	 *  @return squared vector length
	 */
	public IVector1 getSquaredLength();
	
	/** Returns the direction (theta) of the vector.
	 *
	 *  @return vector direction
	 */
	public IVector1 getDirection(IVector2 vector);
	
	/** Returns the direction (theta) of the vector as float.
	 *
	 *  @return vector direction as float
	 */
	public float getDirectionAsFloat(IVector2 vector);
	
	/** Returns the direction (theta) of the vector as double.
	 *
	 *  @return vector direction as double
	 */
	public double getDirectionAsDouble(IVector2 vector);
	
	
	/** Returns the direction (theta) of the vector.
	 *
	 *  @return vector direction
	 */
	public IVector1 getDirection();
	
	/** Returns the direction (theta) of the vector as float.
	 *
	 *  @return vector direction as float
	 */
	public float getDirectionAsFloat();
	
	/** Returns the direction (theta) of the vector as double.
	 *
	 *  @return vector direction as double
	 */
	public double getDirectionAsDouble();
	
	/** Returns the mean average of the vector components.
	 *
	 *  @return vector direction
	 */
	public IVector1 getMean();
	
	/** Returns the distance to another vector.
	 *
	 *  @param vector other vector 
	 *  @return distance
	 */
	public IVector1 getDistance(IVector2 vector);
	
	/** Returns the x-component of the vector.
	 *
	 *  @return x-component
	 */
	public IVector1 getX();

	/** Returns the y-component of the vector.
	 *
	 *  @return y-component
	 */
	public IVector1 getY();
	
	/** Returns the x-component of the vector as integer.
	 *
	 *  @return x-component as integer
	 */
	public int getXAsInteger();

	/** Returns the component of the vector as integer.
	 *
	 *  @return y-component as float
	 */
	public int getYAsInteger();
	
	/** Returns the x-component of the vector as long.
	 *
	 *  @return x-component as long
	 */
	public long getXAsLong();

	/** Returns the component of the vector as float.
	 *
	 *  @return y-component as float
	 */
	public long getYAsLong();
	
	/** Returns the x-component of the vector as float.
	 *
	 *  @return x-component as float
	 */
	public float getXAsFloat();

	/** Returns the component of the vector as float.
	 *
	 *  @return y-component as float
	 */
	public float getYAsFloat();

	/** Returns the x-component of the vector as double.
	 *
	 *  @return x-component as double
	 */
	public double getXAsDouble();

	/** Returns the component of the vector as double.
	 *
	 *  @return y-component as double
	 */
	public double getYAsDouble();
	
	/** Returns the x-component of the vector as BigDecimal.
	 *
	 *  @return x-component as BigDecimal
	 */
	public BigDecimal getXAsBigDecimal();

	/** Returns the component of the vector as BigDecima;.
	 *
	 *  @return y-component as BigDecimal
	 */
	public BigDecimal getYAsBigDecimal();

	/** Makes a copy of the vector without using the complex clone interface.
	 *
	 *  @return copy of the vector
	 */
	public IVector2 copy();
	
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
	public boolean equals(IVector2 vector);
}
