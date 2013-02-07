package jadex.rules.eca;

import java.util.Arrays;
import java.util.StringTokenizer;

/**
 *  Event type that consists of concatenated strings.
 */
public class EventType 
{
	/** The event type elements. */
	protected String[] types;
	
	/** The full name. */
	protected String typename;
	
	/**
	 *  Create an event type from a string.
	 */
	public EventType(String[] types)
	{
		this.types = types;
	}
	
	/**
	 *  Create an event type from a string.
	 */
	public EventType(String typename)
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
	 * 
	 */
	public String toString()
	{
		return "EventType(types="+Arrays.toString(types)+")";
	}
}
