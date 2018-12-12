package jadex.extension.envsupport.math;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/** Implementation of a cartesian 3-vector using double components.
 */
public class Vector3Double implements IVector3, Cloneable
{
	/** Zero vector.
	 */
	public static final IVector3 ZERO = new Vector3Double(0.0);
	
	private double x_;
	private double y_;
	private double z_;

	/** Creates a new Vector2Double with the value (0,0).
	 */
	public Vector3Double()
	{
		x_ = 0;
		y_ = 0;
		z_ = 0;
	}

	/** Creates a new Vector3 with the same value as the input vector.
	 */
	public Vector3Double(IVector3 vector)
	{
		x_ = vector.getXAsDouble();
		y_ = vector.getYAsDouble();
		z_ = vector.getYAsDouble();
	}

	/** Creates a new Vector2 using the scalar to assign the
	 *  value (scalar,scalar).
	 */
	public Vector3Double(double scalar)
	{
		x_ = scalar;
		y_ = scalar;
		z_ = scalar;
	}

	/** Creates a new Vector2 with the given value.
	 */
	public Vector3Double(double x, double y, double z)
	{
		x_ = x;
		y_ = y;
		z_ = z;
	}
	
	/** Assigns this vector the values of another vector.
	 * 
	 *  @param vector the other vector
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector3 assign(IVector3 vector)
	{
		x_ = vector.getXAsDouble();
		y_ = vector.getYAsDouble();
		z_ = vector.getZAsDouble();
		return this;
	}
	
	public IVector3 add(double scalar)
	{
		x_ += scalar;
		y_ += scalar;
		z_ += scalar;
		return this;
	}

	public IVector3 add(IVector1 scalar)
	{
		x_ += scalar.getAsDouble();
		y_ += scalar.getAsDouble();
		z_ += scalar.getAsDouble();
		return this;
	}

	public IVector3 add(IVector3 vector)
	{
		x_ += vector.getXAsDouble();
		y_ += vector.getYAsDouble();
		z_ += vector.getZAsDouble();
		return this;
	}
	
	public IVector3 subtract(double scalar)
	{
		x_ -= scalar;
		y_ -= scalar;
		z_ -= scalar;
		return this;
	}

	public IVector3 subtract(IVector1 scalar)
	{
		x_ -= scalar.getAsDouble();
		y_ -= scalar.getAsDouble();
		z_ -= scalar.getAsDouble();
		return this;
	}

	public IVector3 subtract(IVector3 vector)
	{
		x_ -= vector.getXAsDouble();
		y_ -= vector.getYAsDouble();
		z_ -= vector.getZAsDouble();
		return this;
	}
	
	public IVector3 mod(IVector3 modulus)
	{
		double mx = modulus.getXAsDouble();
		double my = modulus.getYAsDouble();
		double mz = modulus.getZAsDouble();
		x_ = (x_ + mx) % mx;
		y_ = (y_ + my) % my;
		z_ = (z_ + mz) % mz;
		return this;
	}
	
	public IVector3 multiply(double scalar)
	{
		x_ *= scalar;
		y_ *= scalar;
		z_ *= scalar;
		return this;
	}

	public IVector3 multiply(IVector1 scalar)
	{
		x_ *= scalar.getAsDouble();
		y_ *= scalar.getAsDouble();
		z_ *= scalar.getAsDouble();
		return this;
	}

	public IVector3 multiply(IVector3 vector)
	{
		x_ *= vector.getXAsDouble();
		y_ *= vector.getYAsDouble();
		z_ *= vector.getZAsDouble();
		return this;
	}
	
	/** Performs a division on the vector.
	 *
	 *  @param vector vector
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector3 divide(IVector3 vector)
	{
		x_ /= vector.getXAsDouble();
		y_ /= vector.getYAsDouble();
		z_ /= vector.getZAsDouble();
		return this;
	}
	
	public IVector3 zero()
	{
		x_ = 0.0;
		y_ = 0.0;
		z_ = 0.0;
		return this;
	}

	public IVector3 negateX()
	{
		x_ = -x_;
		return this;
	}

	public IVector3 negateY()
	{
		y_ = -y_;
		return this;
	}
	
	public IVector3 negateZ()
	{
		z_ = -z_;
		return this;
	}
	

	public IVector3 negate()
	{
		x_ = -x_;
		y_ = -y_;
		z_ = -z_;
		return this;
	}
	
	public IVector3 randomX(IVector1 lower, IVector1 upper)
	{
		double l = lower.getAsDouble();
		double u = upper.getAsDouble();
		double r = Math.random();
		r *= (u - l);
		r += l;
		x_ = r;
		return this;
	}
	
	public IVector3 randomY(IVector1 lower, IVector1 upper)
	{
		double l = lower.getAsDouble();
		double u = upper.getAsDouble();
		double r = Math.random();
		r *= (u - l);
		r += l;
		y_ = r;
		return this;
	}
	
	public IVector3 randomZ(IVector1 lower, IVector1 upper)
	{
		double l = lower.getAsDouble();
		double u = upper.getAsDouble();
		double r = Math.random();
		r *= (u - l);
		r += l;
		z_ = r;
		return this;
	}
	
	public IVector3 normalize()
	{
		double length = Math.sqrt((x_ * x_) + (y_ * y_) + (z_ * z_));
		if (length != 0.0)
		{
			x_ /= length;
			y_ /= length;
			z_ /= length;
		}
		return this;
	}
	


	public IVector1 getLength()
	{
		return new Vector1Double(Math.sqrt((x_ * x_) + (y_ * y_)+ (z_ * z_)));
	}

	public IVector2 getDirection()
	{
		// todo:
		throw new UnsupportedOperationException();
		//return new Vector1Double(Math.atan2(y_, x_));
	}

	public IVector1 getDistance(IVector3 vector)
	{
		double dx = x_ - vector.getXAsDouble();
		double dy = y_ - vector.getYAsDouble();
		double dz = z_ - vector.getZAsDouble();
		return new Vector1Double(Math.sqrt((dx * dx) + (dy * dy) + (dz * dz)));
	}

	public IVector1 getX()
	{
		return new Vector1Double(x_);
	}

	public IVector1 getY()
	{
		return new Vector1Double(y_);
	}
	
	public IVector1 getZ()
	{
		return new Vector1Double(z_);
	}
	
	public void setX(IVector1 x)
	{
		this.x_	= x.getAsDouble(); 
	}

	public void setY(IVector1 y)
	{
		this.y_	= y.getAsDouble(); 
	}

	public void setZ(IVector1 z)
	{
		this.z_	= z.getAsDouble(); 
	}
	
	/** Returns the x-component of the vector as integer.
	 *
	 *  @return x-component as integer
	 */
	public int getXAsInteger()
	{
		return (int) x_;
	}

	/** Returns the component of the vector as integer.
	 *
	 *  @return y-component as float
	 */
	public int getYAsInteger()
	{
		return (int) y_;
	}
	
	/** Returns the component of the vector as integer.
	 *
	 *  @return y-component as float
	 */
	public int getZAsInteger()
	{
		return (int) z_;
	}
	
	/** Returns the x-component of the vector as long.
	 *
	 *  @return x-component as long
	 */
	public long getXAsLong()
	{
		return (long) x_;
	}

	/** Returns the component of the vector as float.
	 *
	 *  @return y-component as float
	 */
	public long getYAsLong()
	{
		return (long) y_;
	}
	
	/** Returns the component of the vector as float.
	 *
	 *  @return y-component as float
	 */
	public long getZAsLong()
	{
		return (long) z_;
	}
	
	public float getXAsFloat()
	{
		return (float) x_;
	}

	public float getYAsFloat()
	{
		return (float) y_;
	}
	
	public float getZAsFloat()
	{
		return (float) z_;
	}


	public double getXAsDouble()
	{
		return x_;
	}

	public double getYAsDouble()
	{
		return y_;
	}
	
	public double getZAsDouble()
	{
		return z_;
	}

	public BigDecimal getXAsBigDecimal()
	{
		return new BigDecimal(x_);
	}

	public BigDecimal getYAsBigDecimal()
	{
		return new BigDecimal(y_);
	}
	
	public BigDecimal getZAsBigDecimal()
	{
		return new BigDecimal(z_);
	}

	public IVector3 copy()
	{
		return new Vector3Double(x_, y_, z_);
	}

	public Object clone() throws CloneNotSupportedException
	{
		return copy();
	}

	public boolean equals(Object obj)
	{
		if (obj instanceof IVector3)
		{
			return equals((IVector3) obj);
		}

		return false;
	}

	public boolean equals(IVector3 vector)
	{
		// Perform null check, to respect equals(Object) contract
		return (Double.doubleToLongBits(x_) == Double.doubleToLongBits(vector.getXAsDouble())) &&
			   (Double.doubleToLongBits(y_) == Double.doubleToLongBits(vector.getYAsDouble())) &&
			   (Double.doubleToLongBits(z_) == Double.doubleToLongBits(vector.getZAsDouble()));
	}
	
	/** 
	 *  Compute the hash code.
	 *  @return The hash code.
	 */
	public int hashCode()
	{
		return (int)x_*31 + (int)y_*15 + (int)z_;
	}
	
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		DecimalFormat format = new DecimalFormat("####.####");
		if ((x_ < 1000) || (x_ > 0.001))
		{
			buffer.append(format.format(x_));
		}
		else
		{
			buffer.append(Double.toString(x_));
		}
		buffer.append(",");
		if ((y_ < 10000) || (y_ > 0.001))
		{
			buffer.append(format.format(y_));
		}
		else
		{
			buffer.append(Double.toString(y_));
		}
		buffer.append(",");
		if ((z_ < 10000) || (z_ > 0.001))
		{
			buffer.append(format.format(z_));
		}
		else
		{
			buffer.append(Double.toString(z_));
		}
		return buffer.toString();
	}

	/**
	 *  Get a vector for three doubles.
	 *  @param x The first value.
	 *  @param y The second value.
	*  @param z The second value
	 *  @return The vector (null if at least one of args is null).
	 */
	public static IVector3 getVector3(Double x, Double y, Double z)
	{
		IVector3 ret = null;
		if(x!=null && y!=null && z!=null)
			ret = x.doubleValue()==0 && y.doubleValue()==0 && z.doubleValue()==0? ZERO: new Vector3Double(x.doubleValue(), y.doubleValue(), z.doubleValue());
		else if (x!=null || y !=null|| z !=null)
			ret = new Vector3Double(x == null? 0.0:x.doubleValue(), y == null? 0.0:y.doubleValue(),z == null? 0.0:z.doubleValue());
		return ret;
	}
}

