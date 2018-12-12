package jadex.commons.transformation;

public class C
{
	public static boolean INCLUDE_FIELDS = true;
	
	public String string;
	public int integer;
	
	public C()
	{
	}

	public C(String string, int integer)
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
		
		if(obj instanceof C)
		{
			C other = (C)obj;
			ret = string.equals(other.string) && integer==other.integer;
		}
		
		return ret;
	}
	
}
