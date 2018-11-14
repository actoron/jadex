package jadex.commons.transformation;

import jadex.commons.transformation.annotations.Include;
import jadex.commons.transformation.annotations.IncludeFields;

@IncludeFields(includePrivate = true)
public class I
{

	private String string2;

	private static final String finalStaticString = "finalStaticString";

	public I() {
		super();
	}

	public I(String string2)
	{
		this.string2 = string2;
	}

	public int hashCode()
	{
		return ((string2 == null)? 0 : string2.hashCode()) + super.hashCode();
	}

	public boolean equals(Object obj)
	{
		boolean ret = false;
		
		if(obj instanceof I)
		{
			I other = (I)obj;
			ret = string2.equals(other.string2) && finalStaticString.equals(other.finalStaticString);
		}
		
		return ret;
	}
	
}