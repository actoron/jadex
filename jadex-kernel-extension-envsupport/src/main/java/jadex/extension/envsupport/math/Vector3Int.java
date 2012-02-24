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

	
	public IVector3 add(double scalar)
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	public IVector3 add(IVector1 scalar)
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	public IVector3 add(IVector3 vector)
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	public IVector3 assign(IVector3 vector)
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	public IVector3 copy()
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	public IVector3 divide(IVector3 vector)
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	public boolean equals(IVector3 vector)
	{
		// TODO Auto-generated method stub
		return false;
	}

	
	public IVector2 getDirection()
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	public IVector1 getDistance(IVector3 vector)
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	public IVector1 getLength()
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	public IVector1 getX()
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	public BigDecimal getXAsBigDecimal()
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	public double getXAsDouble()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	
	public float getXAsFloat()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	
	public int getXAsInteger()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	
	public long getXAsLong()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	
	public IVector1 getY()
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	public BigDecimal getYAsBigDecimal()
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	public double getYAsDouble()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	
	public float getYAsFloat()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	
	public int getYAsInteger()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	
	public long getYAsLong()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	
	public IVector1 getZ()
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	public BigDecimal getZAsBigDecimal()
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	public double getZAsDouble()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	
	public float getZAsFloat()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	
	public int getZAsInteger()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	
	public long getZAsLong()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	
	public IVector3 mod(IVector3 modulus)
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	public IVector3 multiply(double scalar)
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	public IVector3 multiply(IVector1 scalar)
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	public IVector3 multiply(IVector3 vector)
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	public IVector3 negate()
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	public IVector3 negateX()
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	public IVector3 negateY()
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	public IVector3 negateZ()
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	public IVector3 normalize()
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	public IVector3 randomX(IVector1 lower, IVector1 upper)
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	public IVector3 randomY(IVector1 lower, IVector1 upper)
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	public IVector3 randomZ(IVector1 lower, IVector1 upper)
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	public IVector3 subtract(double scalar)
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	public IVector3 subtract(IVector1 scalar)
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	public IVector3 subtract(IVector3 vector)
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	public IVector3 zero()
	{
		// TODO Auto-generated method stub
		return null;
	}


	public Object clone() throws CloneNotSupportedException
	{
		return copy();
	}


}
