package jadex.commons.transformation;

import jadex.commons.transformation.annotations.Exclude;
import jadex.commons.transformation.annotations.IncludeFields;

@IncludeFields(includePrivate=true)
public class F
{
	private String string;
	private int integer;

	@Exclude
	public Integer excludeMe;

	public F()
	{
	}

	public F(String string, int integer)
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

		if(obj instanceof F)
		{
			F other = (F)obj;
			ret = string.equals(other.string) && integer==other.integer;
			if (excludeMe != null && excludeMe.equals(other.excludeMe)) { // fail if excluded attribute was copied
				ret = false;
			}
		}


		return ret;
	}

}
