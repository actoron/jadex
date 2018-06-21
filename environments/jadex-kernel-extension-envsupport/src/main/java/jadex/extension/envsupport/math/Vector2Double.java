package jadex.extension.envsupport.math;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/** Implementation of a cartesian 2-vector using double components.
 */
public class Vector2Double implements IVector2, Cloneable
{
	/** Zero vector.
	 */
	public static final IVector2 ZERO = new Vector2Double(0.0);
	
	private double x_;
	private double y_;

	/** Creates a new Vector2Double with the value (0,0).
	 */
	public Vector2Double()
	{
		x_ = 0;
		y_ = 0;
	}

	/** Creates a new Vector2 with the same value as the input vector.
	 */
	public Vector2Double(IVector2 vector)
	{
		x_ = vector.getXAsDouble();
		y_ = vector.getYAsDouble();
	}

	/** Creates a new Vector2 using the scalar to assign the
	 *  value (scalar,scalar).
	 */
	public Vector2Double(double scalar)
	{
		x_ = scalar;
		y_ = scalar;
	}

	/** Creates a new Vector2 with the given value.
	 */
	public Vector2Double(double x, double y)
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
		x_ = vector.getXAsDouble();
		y_ = vector.getYAsDouble();
		return this;
	}
	
	public IVector2 add(double scalar)
	{
		x_ += scalar;
		y_ += scalar;
		return this;
	}

	public IVector2 add(IVector1 scalar)
	{
		x_ += scalar.getAsDouble();
		y_ += scalar.getAsDouble();
		return this;
	}

	public IVector2 add(IVector2 vector)
	{
		x_ += vector.getXAsDouble();
		y_ += vector.getYAsDouble();
		return this;
	}
	
	public IVector2 subtract(double scalar)
	{
		x_ -= scalar;
		y_ -= scalar;
		return this;
	}

	public IVector2 subtract(IVector1 scalar)
	{
		x_ -= scalar.getAsDouble();
		y_ -= scalar.getAsDouble();
		return this;
	}

	public IVector2 subtract(IVector2 vector)
	{
		x_ -= vector.getXAsDouble();
		y_ -= vector.getYAsDouble();
		return this;
	}
	
	public IVector2 mod(IVector2 modulus)
	{
		double mx = modulus.getXAsDouble();
		double my = modulus.getYAsDouble();
		x_ = (x_ + mx) % mx;
		y_ = (y_ + my) % my;
		return this;
	}
	
	public IVector2 multiply(double scalar)
	{
		x_ *= scalar;
		y_ *= scalar;
		return this;
	}

	public IVector2 multiply(IVector1 scalar)
	{
		x_ *= scalar.getAsDouble();
		y_ *= scalar.getAsDouble();
		return this;
	}

	public IVector2 multiply(IVector2 vector)
	{
		x_ *= vector.getXAsDouble();
		y_ *= vector.getYAsDouble();
		return this;
	}
	
	/** Performs a division on the vector.
	 *
	 *  @param vector vector
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector2 divide(IVector2 vector)
	{
		x_ /= vector.getXAsDouble();
		y_ /= vector.getYAsDouble();
		return this;
	}
	
	public IVector2 zero()
	{
		x_ = 0.0;
		y_ = 0.0;
		return this;
	}

	public IVector2 negateX()
	{
		x_ = -x_;
		return this;
	}

	public IVector2 negateY()
	{
		y_ = -y_;
		return this;
	}

	public IVector2 negate()
	{
		x_ = -x_;
		y_ = -y_;
		return this;
	}
	
	public IVector2 randomX(IVector1 lower, IVector1 upper)
	{
		double l = lower.getAsDouble();
		double u = upper.getAsDouble();
		double r = Math.random();
		r *= (u - l);
		r += l;
		x_ = r;
		return this;
	}
	
	public IVector2 randomY(IVector1 lower, IVector1 upper)
	{
		double l = lower.getAsDouble();
		double u = upper.getAsDouble();
		double r = Math.random();
		r *= (u - l);
		r += l;
		y_ = r;
		return this;
	}
	
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
		x_ = Math.cos(angle) * length;
		y_ = Math.sin(angle) * length;
		
		return this;
	}

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
	
	public IVector1 getDirection()
	{
		return new Vector1Double(Math.atan2(y_, x_));
	}
	
	public float getDirectionAsFloat()
	{
		return (float) Math.atan2(y_, x_);
	}

	public double getDirectionAsDouble()
	{
		return Math.atan2(y_, x_);
	}
	
	public IVector1 getMean()
	{
		return new Vector1Double((x_ + y_) / 2);
	}

	public IVector1 getDistance(IVector2 vector)
	{
		double dx = x_ - vector.getXAsDouble();
		double dy = y_ - vector.getYAsDouble();
		return new Vector1Double(Math.sqrt((dx * dx) + (dy * dy)));
	}

	public IVector1 getX()
	{
		return new Vector1Double(x_);
	}

	public IVector1 getY()
	{
		return new Vector1Double(y_);
	}
	
	public void setX(IVector1 x)
	{
		this.x_	= x.getAsDouble(); 
	}

	public void setY(IVector1 y)
	{
		this.y_	= y.getAsDouble(); 
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
	
	public float getXAsFloat()
	{
		return (float) x_;
	}

	public float getYAsFloat()
	{
		return (float) y_;
	}

	public double getXAsDouble()
	{
		return x_;
	}

	public double getYAsDouble()
	{
		return y_;
	}

	public BigDecimal getXAsBigDecimal()
	{
		return new BigDecimal(x_);
	}

	public BigDecimal getYAsBigDecimal()
	{
		return new BigDecimal(y_);
	}

	public IVector2 copy()
	{
		return new Vector2Double(x_, y_);
	}

	public Object clone() throws CloneNotSupportedException
	{
		return copy();
	}

	public boolean equals(Object obj)
	{
		if (obj instanceof IVector2)
		{
			return equals((IVector2) obj);
		}

		return false;
	}

	public boolean equals(IVector2 vector)
	{
		// Perform null check, to respect equals(Object) contract
		return (Double.doubleToLongBits(x_) == Double.doubleToLongBits(vector.getXAsDouble())) &&
			   (Double.doubleToLongBits(y_) == Double.doubleToLongBits(vector.getYAsDouble()));
	}
	
	/** 
	 *  Compute the hash code.
	 *  @return The hash code.
	 */
	public int hashCode()
	{
		return (int)x_*31 + (int)y_;
	}
	
	
	public Vector3Double getVector3DoubleValueNoHight()
	{
		return new Vector3Double(x_, 0,  y_);
		
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
		buffer.append(", ");
		if ((y_ < 10000) || (y_ > 0.001))
		{
			buffer.append(format.format(y_));
		}
		else
		{
			buffer.append(Double.toString(y_));
		}
		return buffer.toString();
	}

	/**
	 *  Get a vector for two doubles.
	 *  @param a The first value.
	 *  @param b The second value.
	 *  @return The vector (null if at least one of args is null).
	 */
	public static IVector2 getVector2(Double a, Double b)
	{
		IVector2 ret = null;
		if(a!=null && b!=null)
			ret = a.doubleValue()==0 && b.doubleValue()==0? ZERO: new Vector2Double(a.doubleValue(), b.doubleValue());
		else if (a!=null || b !=null)
			ret = new Vector2Double(a == null? 0.0:a.doubleValue(), b == null? 0.0:b.doubleValue());
		return ret;
	}



	public double getInnerProductAsDouble(IVector2 vector) {
		
		double bx = vector.getXAsDouble();
		double by = vector.getYAsDouble();
		
		return (x_*bx)+(y_*by);
	}

	public IVector1 getDirection(IVector2 vector) {
		
		return null;
	}

	public double getDirectionAsDouble(IVector2 vector) {
		double qa = Math.sqrt(x_*x_ + y_*y_);
		double qb = Math.sqrt(vector.getXAsDouble()*vector.getXAsDouble() + vector.getYAsDouble()*vector.getYAsDouble());
		
		return Math.cos(getInnerProductAsDouble(vector)/(qa*qb));
	}

	public float getDirectionAsFloat(IVector2 vector) {
		// TODO Auto-generated method stub
		return 0;
	}
}

