package jadex.xml;

/**
 *  The link info stores how parent - child(ren) should be composed.
 *  Bulk mode means that linking is invoked only after all children 
 *  have been collected.
 */
public class LinkingInfo
{
	//-------- constants --------

	/** Default link mode. */
	public static final boolean DEFAULT_BULKLINK_MODE = false;
	
	//-------- attributes --------

	/** The linker. */
	protected Object linker;
	
	/** The link mode (determined by the linker if present). */
	protected boolean bulklink;

	//-------- constructors --------

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

	//-------- methods --------
	
	/**
	 *  Get the linker.
	 *  @return The linker.
	 */
	public Object getLinker()
	{
		return linker;
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
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "LinkingInfo(bulklink=" + this.bulklink + ", linker="
			+ this.linker + ")";
	}
	
	

}
