package jadex.extension.envsupport.math;

import java.math.BigDecimal;




public class Vector3Int implements IVector3
{

	/** Zero vector. */
	public static final IVector3	ZERO	= new Vector3Int(0);

	private int						x_;

	private int						y_;

	private int						z_;

	/**
	 * Creates a new Vector3Int with the value (0,0,0).
	 */
	public Vector3Int()
	{
		x_ = 0;
		y_ = 0;
		z_ = 0;
	}

	/**
	 * Creates a new Vector3 with the same value as the input vector.
	 */
	public Vector3Int(IVector3 vector)
	{
		x_ = vector.getXAsInteger();
		y_ = vector.getYAsInteger();
		z_ = vector.getZAsInteger();
	}

	/**
	 * Creates a new Vector3Int using the scalar to assign the value
	 * (scalar,scalar).
	 */
	public Vector3Int(int scalar)
	{
		x_ = scalar;
		y_ = scalar;
		z_ = scalar;
	}

	/**
	 * Creates a new Vector3Int with the given value.
	 */
	public Vector3Int(int x, int y, int z)
	{
		x_ = x;
		y_ = y;
		z_ = z;
	}

	/** Adds a scalar to each component of this vector.
	 *
	 *  @param scalar scalar value as double
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector3 add(double scalar)
	{
			x_ += (int) scalar;
			y_ += (int) scalar;
			z_ += (int) scalar;
			return this;

	}

	/** Adds a scalar to each component of this vector.
	 *
	 *  @param scalar scalar value
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector3 add(IVector1 scalar)
	{
		x_ += scalar.getAsInteger();
		y_ += scalar.getAsInteger();
		z_ += scalar.getAsInteger();
		return this;
	}

	/** Adds another vector to this vector, adding individual components.
	 *
	 *  @param vector the vector to add to this vector
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector3 add(IVector3 vector)
	{
		x_ += vector.getXAsInteger();
		y_ += vector.getYAsInteger();
		z_ += vector.getZAsInteger();
		return this;
	}
	
	/** Subtracts a scalar to each component of this vector.
	 *
	 *  @param scalar scalar value as double
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector3 subtract(IVector1 scalar)
	{
		x_ -= scalar.getAsInteger();
		y_ -= scalar.getAsInteger();
		z_ -= scalar.getAsInteger();
		return this;
	}

	/** Subtracts a scalar to each component of this vector.
	 *
	 *  @param scalar scalar value as double
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector3 subtract(IVector3 vector)
	{
		x_ -= vector.getXAsInteger();
		y_ -= vector.getYAsInteger();
		z_ -= vector.getZAsInteger();
		return this;
	}

	
	/** Assigns this vector the values of another vector.
	 * 
	 *  @param vector the other vector
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector3 assign(IVector3 vector)
	{
		x_ = vector.getXAsInteger();
		y_ = vector.getYAsInteger();
		z_ = vector.getZAsInteger();
		return this;
	}

	/** Makes a copy of the vector without using the complex clone interface.
	 *
	 *  @return copy of the vector
	 */
	public IVector3 copy()
	{
		return new Vector3Int(this);
	}

	/** Performs a division on the vector.
	 *
	 *  @param vector vector
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector3 divide(IVector3 vector)
	{
		x_ /= vector.getXAsInteger();
		y_ /= vector.getYAsInteger();
		z_ /= vector.getZAsInteger();
		return this;
	}

	/** Compares the vector to another vector.
	 *  The vectors are equal if the components are equal.
	 * 
	 * @param vector the other vector
	 * @return true if the vectors are equal
	 */
	public boolean equals(IVector3 vector)
	{
		return vector!=null && ((x_ == vector.getXAsInteger()) && (y_ == vector.getYAsInteger()) && (z_ == vector.getXAsInteger()));
	}

	/** Returns the direction  of the vector.
	 *
	 *  @return vector direction
	 */
	public IVector2 getDirection()
	{
		//TODO: how?
		throw new UnsupportedOperationException();
	}

	/** Returns the distance to another vector.
	 *
	 *  @param vector other vector 
	 *  @return distance
	 */
	public IVector1 getDistance(IVector3 vector)
	{
		double dx = x_ - vector.getXAsDouble();
		double dy = y_ - vector.getYAsDouble();
		double dz = z_ - vector.getZAsDouble();
		return new Vector1Double(Math.sqrt((dx * dx) + (dy * dy) + (dz * dz)));
	}

	/** Returns the length (magnitude) of the vector.
	 *
	 *  @return vector length
	 */
	public IVector1 getLength()
	{
		return new Vector1Double(Math.sqrt((x_ * x_) + (y_ * y_)+ (z_ * z_)));
	}

	/** Returns the x-component of the vector.
	 *
	 *  @return x-component
	 */
	public IVector1 getX()
	{
		return new Vector1Int(x_);
	}

	/** Returns the x-component of the vector as BigDecimal.
	 *
	 *  @return x-component as BigDecimal
	 */
	public BigDecimal getXAsBigDecimal()
	{
		return new BigDecimal(x_);
	}

	/** Returns the x-component of the vector as double.
	 *
	 *  @return x-component as double
	 */
	public double getXAsDouble()
	{
		return (double)x_;
	}

	/** Returns the x-component of the vector as float.
	 *
	 *  @return x-component as float
	 */
	public float getXAsFloat()
	{
		return x_;
	}

	/** Returns the x-component of the vector as integer.
	 *
	 *  @return x-component as integer
	 */
	public int getXAsInteger()
	{
		return x_;
	}

	/** Returns the x-component of the vector as long.
	 *
	 *  @return x-component as long
	 */
	public long getXAsLong()
	{
		return x_;
	}

	/** Returns the y-component of the vector.
	 *
	 *  @return y-component
	 */
	public IVector1 getY()
	{
		return new Vector1Int(y_);
	}

	/** Returns the y-component of the vector as BigDecimal.
	 *
	 *  @return y-component as BigDecimal
	 */
	public BigDecimal getYAsBigDecimal()
	{
		return new BigDecimal(y_);
	}

	/** Returns the y-component of the vector as double.
	 *
	 *  @return y-component as double
	 */
	public double getYAsDouble()
	{
		return (double)y_;
	}

	/** Returns the y-component of the vector as float.
	 *
	 *  @return y-component as float
	 */
	public float getYAsFloat()
	{
		return y_;
	}

	/** Returns the y-component of the vector as integer.
	 *
	 *  @return y-component as integer
	 */
	public int getYAsInteger()
	{
		return y_;
	}

	/** Returns the y-component of the vector as long.
	 *
	 *  @return y-component as long
	 */
	public long getYAsLong()
	{
		return y_;
	}

	/** Returns the z-component of the vector.
	 *
	 *  @return z-component
	 */
	public IVector1 getZ()
	{
		return new Vector1Int(z_);
	}

	/** Returns the z-component of the vector as BigDecimal.
	 *
	 *  @return z-component as BigDecimal
	 */
	public BigDecimal getZAsBigDecimal()
	{
		return new BigDecimal(z_);
	}

	/** Returns the z-component of the vector as double.
	 *
	 *  @return z-component as double
	 */
	public double getZAsDouble()
	{
		return (double)z_;
	}

	/** Returns the z-component of the vector as float.
	 *
	 *  @return z-component as float
	 */
	public float getZAsFloat()
	{
		return z_;
	}

	/** Returns the z-component of the vector as integer.
	 *
	 *  @return z-component as integer
	 */
	public int getZAsInteger()
	{
		return z_;
	}

	/** Returns the z-component of the vector as long.
	 *
	 *  @return z-component as long
	 */
	public long getZAsLong()
	{
		return z_;
	}

	/** Applies a modulo vector. The modulus will be added first so that
	 *  values in the interval (-modulus, 0) will wrap over into the positive range.
	 *
	 *  @param modulus modulus
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector3 mod(IVector3 modulus)
	{
		int mx = modulus.getXAsInteger();
		int my = modulus.getYAsInteger();
		int mz = modulus.getZAsInteger();
		x_ = (x_ + mx) % mx;
		y_ = (y_ + my) % my;
		z_ = (z_ + mz) % mz;
		return this;
	}

	/** Performs a scalar multiplication (scaling) on the vector.
	 *
	 *  @param scalar the scale factor double
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector3 multiply(double scalar)
	{
		x_ *= (int) scalar;
		y_ *= (int) scalar;
		z_ *= (int) scalar;
		return this;
	}

	/** Performs a scalar multiplication (scaling) on the vector.
	 *
	 *  @param scalar the scale factor
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector3 multiply(IVector1 scalar)
	{
		x_ *= scalar.getAsInteger();
		y_ *= scalar.getAsInteger();
		z_ *= scalar.getAsInteger();
		return this;

	}

	/** Performs a multiplication on the vector.
	 *
	 *  @param vector vector
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector3 multiply(IVector3 vector)
	{
		x_ *= vector.getXAsInteger();
		y_ *= vector.getYAsInteger();
		z_ *= vector.getZAsInteger();
		return this;
	}

	/** Negates the vector by negating its components.
	 *
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector3 negate()
	{
		x_ = -x_;
		y_ = -y_;
		z_ = -z_;
		return this;
	}

	/** Negates the x-component.
	 *
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector3 negateX()
	{
		x_ = -x_;
		return this;
	}

	/** Negates the y-component.
	 *
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector3 negateY()
	{
		y_ = -y_;
		return this;
	}

	/** Negates the z-component.
	 *
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector3 negateZ()
	{
		z_ = -z_;
		return this;
	}

	/** Converts the vector to a unit vector (normalization)
	 */
	public IVector3 normalize()
	{
		double length = Math.sqrt((x_ * x_) + (y_ * y_)+ (z_ * z_));
		if (length != 0.0)
		{
			x_ /= length;
			y_ /= length;
			z_ /= length;
		}
		return this;
	}

	/** Sets the x-component to a random value in the interval [lower,upper]
	 * 
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector3 randomX(IVector1 lower, IVector1 upper)
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
	public IVector3 randomY(IVector1 lower, IVector1 upper)
	{
		double l = lower.getAsDouble();
		double u = upper.getAsDouble();
		double r = Math.random();
		r *= (u - l);
		r += l;
		y_ = (int) r;
		return this;
	}

	/** Sets the z-component to a random value in the interval [lower,upper]
	 * 
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector3 randomZ(IVector1 lower, IVector1 upper)
	{
		double l = lower.getAsDouble();
		double u = upper.getAsDouble();
		double r = Math.random();
		r *= (u - l);
		r += l;
		z_ = (int) r;
		return this;
	}

	/** Subtracts a scalar to each component of this vector.
	 *
	 *  @param scalar scalar value as double
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector3 subtract(double scalar)
	{
		x_ -= (int) scalar;
		y_ -= (int) scalar;
		z_ -= (int) scalar;
		return this;
	}



	
	public IVector3 zero()
	{
		x_ = 0;
		y_ = 0;
		z_ = 0;
		return this;
	}


	public Object clone() throws CloneNotSupportedException
	{
		return copy();
	}
	
	public String toString()
	{
		String ret =  "Vector3Int(" + x_+ ", "+ y_ +", " + z_ + ")";
		return ret;
		
	}


}
