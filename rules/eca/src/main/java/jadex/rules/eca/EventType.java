package jadex.rules.eca;

import java.util.Arrays;

/**
 *  Event type that consists of concatenated strings.
 */
public class EventType 
{
	public static final String MATCHALL = "*";
	
	/** The event type elements. */
	protected String[] types;
	
	/**
	 *  Create an event type from a string.
	 */
	public EventType(String... types)
	{
		this.types = types;
	}
	
	/**
	 *  Create an event type.
	 */
	public EventType()
	{
	}
	
	/**
	 *  Get the types.
	 *  @return The types.
	 */
	public String[] getTypes()
	{
		return types;
	}
	
	/**
	 *  Get the type.
	 *  @return The type.
	 */
	public String getType(int i)
	{
		return i<types.length? types[i]: null;
	}

	/**
	 *  Set the types. 
	 *  @param types The types to set.
	 */
	public void setTypes(String[] types)
	{
		this.types = types;
	}

	/**
	 *  Get the hashcode.
	 */
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(types);
		return result;
	}

	/**
	 *  Test if equals to another object.
	 */
	public boolean equals(Object obj)
	{
		boolean ret = false;
		if(obj instanceof EventType)
		{
			EventType other = (EventType)obj;
			ret = Arrays.equals(types, other.types);
		}
		return ret;
	}

	/** 
	 *  Get the string representation.
	 */
	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		for(int i=0; i<types.length; i++)
		{
			buf.append(types[i]);
			if(i+1<types.length)
				buf.append(" . ");
		}
		return buf.toString();
	}
}
