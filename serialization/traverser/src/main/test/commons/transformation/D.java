package jadex.commons.transformation;

import jadex.commons.transformation.annotations.IncludeFields;

@IncludeFields
public class D
{
	public String string;
	public int integer;
	
	public D()
	{
	}

	public D(String string, int integer)
	{
		this.string = string;
		this.integer = integer;
	}

	public int hashCode()
	{
		return ((string == null)? 0 : string.hashCode()) + integer*1000;
	}

	public boolean equals(Object obj)
	{
		boolean ret = false;
		
		if(obj instanceof D)
		{
			D other = (D)obj;
			ret = string.equals(other.string) && integer==other.integer;
		}
		
		return ret;
	}
	
}
