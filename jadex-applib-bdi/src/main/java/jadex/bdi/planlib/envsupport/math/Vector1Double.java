package jadex.bdi.planlib.envsupport.math;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/** Implementation of a 1-vector using a double value.
 */
public class Vector1Double implements IVector1
{
	/** Zero vector
	 */
	public static final IVector1 ZERO = new Vector1Double(0.0);
	
	/** The component
	 */
	private double x_;
	
	/** Creates a new Vector1Double
	 * 
	 *  @param value vector value
	 */
	public Vector1Double(double value)
	{
		x_ = value;
	}
	
	public IVector1 add(IVector1 vector)
	{
		x_ += vector.getAsDouble();
		return this;
	}
	
	/** Subtracts another vector to this vector, subtracting individual components.
	 *
	 *  @param vector the vector to subtract from this vector
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector1 subtract(IVector1 vector)
	{
		x_ -= vector.getAsDouble();
		return this;
	}
	
	public IVector1 multiply(IVector1 vector)
	{
		x_ *= vector.getAsDouble();
		return this;
	}
	
	/** Sets the vector component to zero.
	 *
	 *  @return a reference to the called vector (NOT a copy)
	 */
	public IVector1 zero()
	{
		x_ = 0.0;
		return this;
	}
	
	public IVector1 negate()
	{
		x_ = -x_;
		return this;
	}
	
	public IVector1 getDistance(IVector1 vector)
	{
		double distance = Math.abs(Math.abs(x_) - Math.abs(vector.getAsDouble()));
		return new Vector1Double(distance);
	}
	
	public int getAsInteger()
	{
		return (int) Math.round(x_);
	}
	
	public long getAsLong()
	{
		return Math.round(x_);
	}
	
	public float getAsFloat()
	{
		return (float) x_;
	}
	
	public double getAsDouble()
	{
		return x_;
	}
	
	public BigDecimal getAsBigDecimal()
	{
		return new BigDecimal(x_);
	}
	
	public IVector1 copy()
	{
		return new Vector1Double(x_);
	}
	
	public Object clone() throws CloneNotSupportedException
	{
		return copy();
	}
	
	public boolean equals(Object obj)
	{
		if (obj instanceof IVector1)
		{
			return equals((IVector1) obj);
		}
		return false;
	}
	
	public boolean equals(IVector1 vector)
	{
		return (x_ == vector.getAsDouble());
	}
	
	public boolean greater(IVector1 vector)
	{
		return (x_ > vector.getAsDouble());
	}
	
	public boolean less(IVector1 vector)
	{
		return (x_ < vector.getAsDouble());
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
		return buffer.toString();
	}
}
