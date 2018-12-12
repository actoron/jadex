package jadex.xml.tutorial.example14;

/**
 *  Basic software implementation class.
 */
public class Software extends Product
{
	//-------- attributes --------
	
	/** The invoice name. */
	protected String version;

	//-------- methods --------
	
	/**
	 *  Get the version.
	 *  @return The version.
	 */
	public String getVersion()
	{
		return version;
	}

	/**
	 *  Set the version.
	 *  @param version The version to set.
	 */
	public void setVersion(String version)
	{
		this.version = version;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "Software(version=" + version + ", toString()="
			+ super.toString() + ")";
	}
	
}
