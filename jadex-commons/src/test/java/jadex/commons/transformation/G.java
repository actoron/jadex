package jadex.commons.transformation;

import jadex.commons.transformation.annotations.Include;

public class G
{
	@Include
	private final String string;
	@Include
	private int integer;

	public Integer excludeMe;

	public G()
	{
		string = null;
	}

	public G(String string, int integer)
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
		
		if(obj instanceof G)
		{
			G other = (G)obj;
			ret = string.equals(other.string) && integer==other.integer;
			if (excludeMe != null && excludeMe.equals(other.excludeMe)) { // fail if excluded attribute was copied
				ret = false;
			}
		}
		
		return ret;
	}
	
}
