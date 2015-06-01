package jadex.bdiv3.model;

/**
 *  Base element for model elements.
 */
public class MElement
{
	/** The capability separator. */
	public static final String	CAPABILITY_SEPARATOR	= "/";

	/** The element name. */
	protected String name;

	/** The element description. */
	protected String description;

	/**
	 *	Bean Constructor. 
	 */
	public MElement()
	{
	}
	
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
	 *  Get the description.
	 *  @return The description.
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 *  Set the description.
	 *  @param description The description to set.
	 */
	public void setDescription(String description)
	{
		this.description = description;
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

	/**
	 *  Get the hashcode.
	 */
	public int hashCode()
	{
		return 31 + ((name == null) ? 0 : name.hashCode());
	}

	/**
	 *  Test if objects are equal.
	 */
	public boolean equals(Object obj)
	{
		boolean ret = false;
		if(obj instanceof MElement)
		{
			MElement other = (MElement)obj;
			ret = getName()!=null? getName().equals(other.getName()): super.equals(obj);
		}
		return ret;
	}
}
