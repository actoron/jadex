package jadex.xml;

/**
 * 
 */
public class LinkingInfo
{
	public static boolean DEFAULT_BULKLINK_MODE = false;
	
	/** The linker. */
	protected Object linker;
	
	/** The link mode (determined by the linker if present). */
	protected boolean bulklink;

	/**
	 *  Create a new linking info.
	 */
	public LinkingInfo(boolean bulklink)
	{
		this(null, bulklink);
	}
	
	/**
	 *  Create a new linking info.
	 */
	public LinkingInfo(Object linker)
	{
		this(linker, false);
	}
	
	/**
	 *  Create a new linking info.
	 */
	public LinkingInfo(Object linker, boolean bulklink)
	{
		this.linker = linker;
		this.bulklink = bulklink;
	}

	/**
	 *  Get the linker.
	 *  @return The linker.
	 */
	public Object getLinker()
	{
		return linker;
	}

	/**
	 *  Set the linker.
	 *  @param linker The linker to set.
	 */
	public void setLinker(Object linker)
	{
		this.linker = linker;
	}

	/**
	 *  Get the bulklink.
	 *  @return The bulklink.
	 */
	public boolean isBulkLink()
	{
		return bulklink;
	}

	/**
	 *  Set the bulklink.
	 *  @param bulklink The bulklink to set.
	 */
	public void setBulkLink(boolean bulklink)
	{
		this.bulklink = bulklink;
	}
	
	

}
