package jadex.rules.eca;

import java.util.StringTokenizer;

/**
 * 
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
}
