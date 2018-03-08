package jadex.commons.transformation;

import jadex.commons.transformation.annotations.Include;

public class H extends G
{
	@Include
	private String string2;

	public H() {
		super(null, -1);
	}

	public H(String string2)
	{
		super("s1", 1);
		this.string2 = string2;
	}

	public int hashCode()
	{
		return ((string2 == null)? 0 : string2.hashCode()) + super.hashCode();
	}

	public boolean equals(Object obj)
	{
		boolean ret = false;
		
		if(obj instanceof H)
		{
			H other = (H)obj;
			ret = string2.equals(other.string2) && super.equals(other);
		}
		
		return ret;
	}
	
}