package jadex.extension.envsupport.math;

import java.math.BigDecimal;

/** Wrapper for synchronized access of a vector2
 */
public class SynchronizedVector2Wrapper implements IVector2
{
	private IVector2 vector_;
	
	public SynchronizedVector2Wrapper(IVector2 vector)
	{
		vector_ = vector;
	}
	
	/** Assigns this vector the values of another vector.
	 * 
	 *  @param vector the other vector
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public synchronized IVector2 assign(IVector2 vector)
	{
		vector_.assign(vector);
		return this;
	}
	
	/** Adds a scalar to each component of this vector.
	 *
	 *  @param scalar scalar value as double
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public synchronized IVector2 add(double scalar)
	{
		vector_.add(scalar);
		return this;
	}
	
	/** Adds a scalar to each component of this vector.
	 *
	 *  @param scalar scalar value
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public synchronized IVector2 add(IVector1 scalar)
	{
		vector_.add(scalar);
		return this;
	}

	/** Adds another vector to this vector, adding individual components.
	 *
	 *  @param vector the vector to add to this vector
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public synchronized IVector2 add(IVector2 vector)
	{
		vector_.add(vector);
		return this;
	}
	
	/** Subtracts a scalar to each component of this vector.
	 *
	 *  @param scalar scalar value as double
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public synchronized IVector2 subtract(double scalar)
	{
		vector_.subtract(scalar);
		return this; 
	}
	
	/** Subtracts a scalar to each component of this vector.
	 *
	 *  @param scalar scalar value
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public synchronized IVector2 subtract(IVector1 scalar)
	{
		vector_.subtract(scalar);
		return this;
	}

	/** Subtracts another vector to this vector, subtracting individual components.
	 *
	 *  @param vector the vector to subtract from this vector
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public synchronized IVector2 subtract(IVector2 vector)
	{
		vector_.subtract(vector);
		return this;
	}

	/** Applies a modulo vector. The modulus will be added first so that
	 *  values in the interval (-modulus, 0) will wrap over into the positive range.
	 *
	 *  @param modulus modulus
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public synchronized IVector2 mod(IVector2 modulus)
	{
		vector_.mod(modulus);
		return this;
	}
	
	/** Performs a scalar multiplication (scaling) on the vector.
	 *
	 *  @param scalar the scale factor double
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public synchronized IVector2 multiply(double scalar)
	{
		vector_.multiply(scalar);
		return this;
	}
	
	/** Performs a scalar multiplication (scaling) on the vector.
	 *
	 *  @param scalar the scale factor
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public synchronized IVector2 multiply(IVector1 scalar)
	{
		vector_.multiply(scalar);
		return this;
	}
	
	/** Performs a multiplication on the vector.
	 *
	 *  @param vector vector
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public synchronized IVector2 multiply(IVector2 vector)
	{
		vector_.multiply(vector);
		return this;
	}
	
	/** Performs a division on the vector.
	 *
	 *  @param vector vector
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public synchronized IVector2 divide(IVector2 vector)
	{
		vector_.divide(vector);
		return this;
	}
	
	/** Sets all vector components to zero.
	 *
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public synchronized IVector2 zero()
	{
		vector_.zero();
		return this;
	}
	
	/** Negates the x-component.
	 *
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public synchronized IVector2 negateX()
	{
		vector_.negateX();
		return this;
	}
	
	/** Negates the y-component.
	 *
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public synchronized IVector2 negateY()
	{
		vector_.negateY();
		return this;
	}
	
	/** Negates the vector by negating its components.
	 *
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public synchronized IVector2 negate()
	{
		vector_.negate();
		return this;
	}
	
	/** Sets the x-component to a random value in the interval [lower,upper]
	 * 
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public synchronized IVector2 randomX(IVector1 lower, IVector1 upper)
	{
		vector_.randomX(lower, upper);
		return this;
	}
	
	/** Sets the y-component to a random value in the interval [lower,upper]
	 * 
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public synchronized IVector2 randomY(IVector1 lower, IVector1 upper)
	{
		vector_.randomY(lower, upper);
		return this;
	}
	
	/** Converts the vector to a unit vector (normalization)
	 */
	public synchronized IVector2 normalize()
	{
		vector_.normalize();
		return this;
	}
	
	/**
	 *  Redirects the vector to a new direction,
	 *  maintaining the magnitude.
	 *  
	 *  @param angle The new direction.
	 *  @return The vector.
	 */
	public synchronized IVector2 redirect(double angle)
	{
		return vector_.redirect(angle);
	}
	
	/** Returns the length (magnitude) of the vector.
	 *
	 *  @return vector length
	 */
	public synchronized IVector1 getLength()
	{
		return vector_.getLength();
	}
	
	/** Returns the squared length (magnitude) of the vector.
	 *
	 *  @return squared vector length
	 */
	public synchronized IVector1 getSquaredLength()
	{
		return vector_.getSquaredLength();
	}
	
	/** Returns the direction (theta) of the vector.
	 *
	 *  @return vector direction
	 */
	public synchronized IVector1 getDirection()
	{
		return vector_.getDirection();
	}
	
	/** Returns the direction (theta) of the vector as float.
	 *
	 *  @return vector direction as float
	 */
	public synchronized float getDirectionAsFloat()
	{
		return vector_.getDirectionAsFloat();
	}
	
	/** Returns the direction (theta) of the vector as double.
	 *
	 *  @return vector direction as double
	 */
	public synchronized double getDirectionAsDouble()
	{
		return vector_.getDirectionAsDouble();
	}
	
	/** Returns the mean average of the vector components.
	 *
	 *  @return vector direction
	 */
	public synchronized IVector1 getMean()
	{
		return vector_.getMean();
	}
	
	/** Returns the distance to another vector.
	 *
	 *  @param vector other vector 
	 *  @return distance
	 */
	public synchronized IVector1 getDistance(IVector2 vector)
	{
		return vector_.getDistance(vector);
	}
	
	/** Returns the x-component of the vector.
	 *
	 *  @return x-component
	 */
	public synchronized IVector1 getX()
	{
		return vector_.getX();
	}

	/** Returns the y-component of the vector.
	 *
	 *  @return y-component
	 */
	public synchronized IVector1 getY()
	{
		return vector_.getY();
	}
	
	/** Returns the x-component of the vector as integer.
	 *
	 *  @return x-component as integer
	 */
	public synchronized int getXAsInteger()
	{
		return vector_.getXAsInteger();
	}

	/** Returns the component of the vector as integer.
	 *
	 *  @return y-component as float
	 */
	public synchronized int getYAsInteger()
	{
		return vector_.getYAsInteger();
	}
	
	/** Returns the x-component of the vector as long.
	 *
	 *  @return x-component as long
	 */
	public synchronized long getXAsLong()
	{
		return vector_.getXAsLong();
	}

	/** Returns the component of the vector as float.
	 *
	 *  @return y-component as float
	 */
	public synchronized long getYAsLong()
	{
		return vector_.getYAsLong();
	}
	
	/** Returns the x-component of the vector as float.
	 *
	 *  @return x-component as float
	 */
	public synchronized float getXAsFloat()
	{
		return vector_.getXAsFloat();
	}

	/** Returns the component of the vector as float.
	 *
	 *  @return y-component as float
	 */
	public synchronized float getYAsFloat()
	{
		return vector_.getYAsFloat();
	}

	/** Returns the x-component of the vector as double.
	 *
	 *  @return x-component as double
	 */
	public synchronized double getXAsDouble()
	{
		return vector_.getXAsDouble();
	}

	/** Returns the component of the vector as double.
	 *
	 *  @return y-component as double
	 */
	public synchronized double getYAsDouble()
	{
		return vector_.getYAsDouble();
	}
	
	/** Returns the x-component of the vector as BigDecimal.
	 *
	 *  @return x-component as BigDecimal
	 */
	public synchronized BigDecimal getXAsBigDecimal()
	{
		return vector_.getXAsBigDecimal();
	}

	/** Returns the component of the vector as BigDecima;.
	 *
	 *  @return y-component as BigDecimal
	 */
	public synchronized BigDecimal getYAsBigDecimal()
	{
		return vector_.getYAsBigDecimal();
	}

	/** Makes a copy of the vector without using the complex clone interface.
	 *
	 *  @return copy of the vector
	 */
	public synchronized IVector2 copy()
	{
		return new SynchronizedVector2Wrapper(vector_.copy());
	}
	
	/** Generates a deep clone of the vector.
	 *
	 *  @return clone of this vector
	 */
	public synchronized Object clone() throws CloneNotSupportedException
	{
		return copy();
	}
	
	/**
	 *  Get the hash code.
	 */
	public int hashCode()
	{
		return vector_.hashCode();
	}
	
	/** Compares the vector to an object
	 * 
	 * @param obj the object
	 * @return always returns false unless the object is an IVector2,
	 *         in which case it is equivalent to equals(IVector vector)
	 */
	public synchronized boolean equals(Object obj)
	{
		return vector_.equals(obj);
	}
	
	/** Compares the vector to another vector.
	 *  The vectors are equal if the components are equal.
	 * 
	 * @param vector the other vector
	 * @return true if the vectors are equal
	 */
	public synchronized boolean equals(IVector2 vector)
	{
		return vector_.equals(vector);
	}
	
	public synchronized String toString()
	{
		return vector_.toString();
	}

	public double getInnerProductAsDouble(IVector2 vector) {
		// TODO Auto-generated method stub
		return 0;
	}

	public IVector1 getDirection(IVector2 vector) {
		// TODO Auto-generated method stub
		return null;
	}

	public float getDirectionAsFloat(IVector2 vector) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getDirectionAsDouble(IVector2 vector) {
		// TODO Auto-generated method stub
		return 0;
	}

}
