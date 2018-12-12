package jadex.commons.transformation;

import java.util.Vector;

/**
 * 
 */
public class VectorModel
{
	private Vector	v1;
	private Vector	v2;

	public VectorModel()
	{
		v1 = new Vector();
		v2 = new Vector();
	}

	public Vector getV1()
	{
		return v1;
	}

	public void setV1(Vector v1)
	{
		this.v1 = v1;
	}

	public Vector getV2()
	{
		return v2;
	}

	public void setV2(Vector v2)
	{
		this.v2 = v2;
	}

	public void addToV1(Object o)
	{
		v1.add(o);
	}
	
	public void addToV2(Object o)
	{
		v2.add(o);
	}
	
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.v1 == null) ? 0 : this.v1.hashCode());
		result = prime * result + ((this.v2 == null) ? 0 : this.v2.hashCode());
		return result;
	}

	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		VectorModel other = (VectorModel)obj;
		if(this.v1 == null)
		{
			if(other.v1 != null)
				return false;
		}
		else if(!this.v1.equals(other.v1))
			return false;
		if(this.v2 == null)
		{
			if(other.v2 != null)
				return false;
		}
		else if(!this.v2.equals(other.v2))
			return false;
		return true;
	}

	public String toString()
	{
		return "v1-size: " + v1.size() + " v2-size: " + v2.size();
	}
}
