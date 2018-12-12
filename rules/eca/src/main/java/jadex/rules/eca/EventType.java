package jadex.rules.eca;

import java.util.Arrays;
import java.util.StringTokenizer;

/**
 *  Event type that consists of concatenated strings.
 */
public class EventType 
{
	public static final String MATCHALL = "*";
	
	/** The event type elements. */
	protected String[] types;
	
	/** The full name. */
	protected String typename;
	
//	/**
//	 *  Create an event type from a string.
//	 */
//	public EventType(String[] types)
//	{
//		this.types = types;
//	}
	
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
	 *  Create an event type from a string.
	 */
	public EventType(String typename)
	{
		if(typename==null)
			throw new IllegalArgumentException("Typename must not null");
		setTypename(typename);
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
	 *  Get the typename.
	 *  return The typename.
	 */
	public String getTypename()
	{
		return typename;
	}

	/**
	 *  Set the typename. 
	 *  @param typename The typename to set.
	 */
	public void setTypename(String typename)
	{
		this.typename = typename;
		StringTokenizer stok = new StringTokenizer(typename, ".");
		this.types = new String[stok.countTokens()];
		for(int i=0; stok.hasMoreTokens(); i++)
		{
			types[i] = stok.nextToken();
		}
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
		if(typename==null)
		{
			StringBuffer buf = new StringBuffer();
			for(int i=0; i<types.length; i++)
			{
				buf.append(types[i]);
				if(i+1<types.length)
					buf.append(" . ");
			}
			typename = buf.toString();
		}
		return typename;
	}
}
