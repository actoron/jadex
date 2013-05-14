package jadex.extension.envsupport.math;

import java.math.BigDecimal;

/** Implementation of a 2-vector using integer values.
 */
public class Vector2Int implements IVector2
{
	/** Zero vector. */
	public static final IVector2 ZERO = new Vector2Int(0);
	public static final IVector2 UNIT = new Vector2Int(1);
	
	private int x_;
	private int y_;
	
	/** Creates a new Vector2Int with the value (0,0).
	 */
	public Vector2Int()
	{
		x_ = 0;
		y_ = 0;
	}

	/** Creates a new Vector2 with the same value as the input vector.
	 */
	public Vector2Int(IVector2 vector)
	{
		x_ = vector.getXAsInteger();
		y_ = vector.getYAsInteger();
	}

	/** Creates a new Vector2Int using the scalar to assign the
	 *  value (scalar,scalar).
	 */
	public Vector2Int(int scalar)
	{
		x_ = scalar;
		y_ = scalar;
	}

	/** Creates a new Vector2Int with the given value.
	 */
	public Vector2Int(int x, int y)
	{
		x_ = x;
		y_ = y;
	}
	
	/** Assigns this vector the values of another vector.
	 * 
	 *  @param vector the other vector
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector2 assign(IVector2 vector)
	{
		x_ = vector.getXAsInteger();
		y_ = vector.getYAsInteger();
		return this;
	}
	
	/** Adds a scalar to each component of this vector.
	 *
	 *  @param scalar scalar value as double
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector2 add(double scalar)
	{
		x_ += (int) scalar;
		y_ += (int) scalar;
		return this;
	}
	
	/** Adds a scalar to each component of this vector.
	 *
	 *  @param scalar scalar value
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector2 add(IVector1 scalar)
	{
		x_ += scalar.getAsInteger();
		y_ += scalar.getAsInteger();
		return this;
	}

	/** Adds another vector to this vector, adding individual components.
	 *
	 *  @param vector the vector to add to this vector
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector2 add(IVector2 vector)
	{
		x_ += vector.getXAsInteger();
		y_ += vector.getYAsInteger();
		return this;
	}
	
	/** Subtracts a scalar to each component of this vector.
	 *
	 *  @param scalar scalar value as double
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector2 subtract(double scalar)
	{
		x_ -= (int) scalar;
		y_ -= (int) scalar;
		return this;
	}
	
	/** Subtracts a scalar to each component of this vector.
	 *
	 *  @param scalar scalar value
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector2 subtract(IVector1 scalar)
	{
		x_ -= scalar.getAsInteger();
		y_ -= scalar.getAsInteger();
		return this;
	}

	/** Subtracts another vector to this vector, subtracting individual components.
	 *
	 *  @param vector the vector to subtract from this vector
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector2 subtract(IVector2 vector)
	{
		x_ -= vector.getXAsInteger();
		y_ -= vector.getYAsInteger();
		return this;
	}

	/** Applies a modulo vector. The modulus will be added first so that
	 *  values in the interval (-modulus, 0) will wrap over into the positive range.
	 *
	 *  @param modulus modulus
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector2 mod(IVector2 modulus)
	{
		int mx = modulus.getXAsInteger();
		int my = modulus.getYAsInteger();
		x_ = (x_ + mx) % mx;
		y_ = (y_ + my) % my;
		return this;
	}
	
	/** Performs a scalar multiplication (scaling) on the vector.
	 *
	 *  @param scalar the scale factor double
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector2 multiply(double scalar)
	{
		x_ *= (int) scalar;
		y_ *= (int) scalar;
		return this;
	}
	
	/** Performs a scalar multiplication (scaling) on the vector.
	 *
	 *  @param scalar the scale factor
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector2 multiply(IVector1 scalar)
	{
		x_ *= scalar.getAsInteger();
		y_ *= scalar.getAsInteger();
		return this;
	}
	
	/** Performs a multiplication on the vector.
	 *
	 *  @param vector vector
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector2 multiply(IVector2 vector)
	{
		x_ *= vector.getXAsInteger();
		y_ *= vector.getYAsInteger();
		return this;
	}
	
	/** Performs a division on the vector.
	 *
	 *  @param vector vector
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector2 divide(IVector2 vector)
	{
		x_ /= vector.getXAsInteger();
		y_ /= vector.getYAsInteger();
		return this;
	}
	
	/** Sets all vector components to zero.
	 *
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector2 zero()
	{
		x_ = 0;
		y_ = 0;
		return this;
	}
	
	/** Negates the x-component.
	 *
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector2 negateX()
	{
		x_ = -x_;
		return this;
	}
	
	
	/** Negates the y-component.
	 *
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector2 negateY()
	{
		y_ = -y_;
		return this;
	}
	
	/** Negates the vector by negating its components.
	 *
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector2 negate()
	{
		x_ = -x_;
		y_ = -y_;
		return this;
	}
	
	/** Sets the x-component to a random value in the interval [lower,upper]
	 * 
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector2 randomX(IVector1 lower, IVector1 upper)
	{
		double l = lower.getAsDouble();
		double u = upper.getAsDouble();
		double r = Math.random();
		r *= (u - l);
		r += l;
		x_ = (int) r;
		return this;
	}
	
	/** Sets the y-component to a random value in the interval [lower,upper]
	 * 
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector2 randomY(IVector1 lower, IVector1 upper)
	{
		double l = lower.getAsDouble();
		double u = upper.getAsDouble();
		double r = Math.random();
		r *= (u - l);
		r += l;
		y_ = (int) r;
		return this;
	}
	
	/** Converts the vector to a unit vector (normalization)
	 */
	public IVector2 normalize()
	{
		double length = Math.sqrt((x_ * x_) + (y_ * y_));
		if (length != 0.0)
		{
			x_ /= length;
			y_ /= length;
		}
		return this;
	}
	
	/**
	 *  Redirects the vector to a new direction,
	 *  maintaining the magnitude.
	 *  
	 *  @param angle The new direction.
	 *  @return The vector.
	 */
	public IVector2 redirect(double angle)
	{
		double length = Math.sqrt((x_ * x_) + (y_ * y_));
		x_ = (int) Math.round(Math.cos(angle) * length);
		y_ = (int) Math.round(Math.sin(angle) * length);
		
		return this;
	}
	
	/** Returns the length (magnitude) of the vector.
	 *
	 *  @return vector length
	 */
	public IVector1 getLength()
	{
		return new Vector1Double(Math.sqrt((x_ * x_) + (y_ * y_)));
	}
	
	/** Returns the squared length (magnitude) of the vector.
	 *
	 *  @return squared vector length
	 */
	public IVector1 getSquaredLength()
	{
		return new Vector1Double((x_ * x_) + (y_ * y_));
	}
		
	/** Returns the direction (theta) of the vector.
	 *
	 *  @return vector direction
	 */
	public IVector1 getDirection()
	{
		return new Vector1Double(Math.atan2(y_, x_));
	}
	
	/** Returns the direction (theta) of the vector as float.
	 *
	 *  @return vector direction as float
	 */
	public float getDirectionAsFloat()
	{
		return (float) Math.atan2(y_, x_);
	}
	
	/** Returns the direction (theta) of the vector as double.
	 *
	 *  @return vector direction as double
	 */
	public double getDirectionAsDouble()
	{
		return Math.atan2(y_, x_);
	}
	
	/** Returns the mean average of the vector components.
	 *
	 *  @return vector direction
	 */
	public IVector1 getMean()
	{
		return new Vector1Int((x_ + y_) >> 1);
	}
	
	/** Returns the distance to another vector.
	 *
	 *  @param vector other vector 
	 *  @return distance
	 */
	public IVector1 getDistance(IVector2 vector)
	{
		int dx = x_ - vector.getXAsInteger();
		int dy = y_ - vector.getYAsInteger();
		return new Vector1Double((int)Math.sqrt((dx * dx) + (dy * dy)));
	}
	
	/** Returns the x-component of the vector.
	 *
	 *  @return x-component
	 */
	public IVector1 getX()
	{
		return new Vector1Int(x_);
	}

	/** Returns the y-component of the vector.
	 *
	 *  @return y-component
	 */
	public IVector1 getY()
	{
		return new Vector1Int(y_);
	}
	
	public void setX(IVector1 x)
	{
		this.x_	= x.getAsInteger(); 
	}

	public void setY(IVector1 y)
	{
		this.y_	= y.getAsInteger(); 
	}

	/** Returns the x-component of the vector as integer.
	 *
	 *  @return x-component as integer
	 */
	public int getXAsInteger()
	{
		return x_;
	}

	/** Returns the component of the vector as integer.
	 *
	 *  @return y-component as float
	 */
	public int getYAsInteger()
	{
		return y_;
	}
	
	/** Returns the x-component of the vector as long.
	 *
	 *  @return x-component as long
	 */
	public long getXAsLong()
	{
		return x_;
	}

	/** Returns the component of the vector as float.
	 *
	 *  @return y-component as float
	 */
	public long getYAsLong()
	{
		return y_;
	}
	
	/** Returns the x-component of the vector as float.
	 *
	 *  @return x-component as float
	 */
	public float getXAsFloat()
	{
		return x_;
	}

	/** Returns the component of the vector as float.
	 *
	 *  @return y-component as float
	 */
	public float getYAsFloat()
	{
		return y_;
	}

	/** Returns the x-component of the vector as double.
	 *
	 *  @return x-component as double
	 */
	public double getXAsDouble()
	{
		return x_;
	}

	/** Returns the component of the vector as double.
	 *
	 *  @return y-component as double
	 */
	public double getYAsDouble()
	{
		return y_;
	}
	
	/** Returns the x-component of the vector as BigDecimal.
	 *
	 *  @return x-component as BigDecimal
	 */
	public BigDecimal getXAsBigDecimal()
	{
		return new BigDecimal(x_);
	}

	/** Returns the component of the vector as BigDecima;.
	 *
	 *  @return y-component as BigDecimal
	 */
	public BigDecimal getYAsBigDecimal()
	{
		return new BigDecimal(y_);
	}

	/** Makes a copy of the vector without using the complex clone interface.
	 *
	 *  @return copy of the vector
	 */
	public IVector2 copy()
	{
		return new Vector2Int(this);
	}
	
	/** Generates a deep clone of the vector.
	 *
	 *  @return clone of this vector
	 */
	public Object clone() throws CloneNotSupportedException
	{
		return copy();
	}
	
	/** Compares the vector to an object
	 * 
	 * @param obj the object
	 * @return always returns false unless the object is an IVector2,
	 *         in which case it is equivalent to equals(IVector vector)
	 */
	public boolean equals(Object obj)
	{
		if (obj instanceof IVector2)
		{
			IVector2 vector = (IVector2) obj;
			return equals(vector);
		}
		return false;
	}
	
	/** Compares the vector to another vector.
	 *  The vectors are equal if the components are equal.
	 * 
	 * @param vector the other vector
	 * @return true if the vectors are equal
	 */
	public boolean equals(IVector2 vector)
	{
		// Perform null check, to respect equals(Object) contract
		return vector!=null && ((x_ == vector.getXAsInteger()) && (y_ == vector.getYAsInteger()));
	}
	
	/** 
	 *  Compute the hash code.
	 *  @return The hash code.
	 */
	public int hashCode()
	{
		return x_*31 + y_;
	}

	/**
	 * 
	 */
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append(x_);
		buffer.append(", ");
		buffer.append(y_);
		return buffer.toString();
	}

	/**
	 *  Get a vector for two doubles.
	 *  @param a The first value.
	 *  @param b The second value.
	 *  @return The vector (null if at least one of args is null).
	 */
	public static IVector2 getVector2(Integer a, Integer b)
	{
		IVector2 ret = null;
		if(a!=null && b!=null)
			ret = a.intValue()==0 && b.intValue()==0? ZERO: new Vector2Int(a.intValue(), b.intValue());
		return ret;
	}

	public double getInnerProductAsDouble(IVector2 vector) {
		// TODO Auto-generated method stub
		return 0;
	}

	public IVector1 getDirection(IVector2 vector) {
		// TODO Auto-generated method stub
		return null;
	}

	public double getDirectionAsDouble(IVector2 vector) {
		// TODO Auto-generated method stub
		return 0;
	}

	public float getDirectionAsFloat(IVector2 vector) {
		// TODO Auto-generated method stub
		return 0;
	}


}
