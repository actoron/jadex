package jadex.bdiv3.model;

/**
 * 
 */
public class MElement
{
	/** The capability separator. */
	public static final String	CAPABILITY_SEPARATOR	= "/";

	/** The element name. */
	protected String name;

	/**
	 *  Create a new element.
	 */
	public MElement(String name)
	{
		this.name = name;
	}

	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 *  Get the capability name for an element.
	 *  @return The capability name.
	 */
	public String getCapabilityName()
	{
		String ret = null;
		
		if(name!=null)
		{
			int idx = name.lastIndexOf(CAPABILITY_SEPARATOR);
			if(idx!=-1)
			{
				ret = name.substring(0, idx-1);
			}
		}
		
		return ret;
	}
}
