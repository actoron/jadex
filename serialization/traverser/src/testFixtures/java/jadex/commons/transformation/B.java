package jadex.commons.transformation;


public class B
{
	protected String str;

	public B()
	{
	}
	
	public B(String str)
	{
		this.str = str;
	}
	
	public String getStr()
	{
		return this.str;
	}

	public void setStr(String str)
	{
		this.str = str;
	}

	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.str == null) ? 0 : this.str.hashCode());
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
		B other = (B)obj;
		if(this.str == null)
		{
			if(other.str != null)
				return false;
		}
		else if(!this.str.equals(other.str))
			return false;
		return true;
	}

	public String toString()
	{
		return "B [str=" + this.str + "]";
	}
}

