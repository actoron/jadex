package jadex.bdiv3.model;

/**
 *  Reference to an external capability.
 */
// XML only, todo remove?
public class MCapabilityReference
{
	//-------- attributes --------
	
	/** The reference name. */
	protected String	name;
	
	/** The referenced capability (file name). */
	protected String	file;
	
	//-------- methods --------
	
	/**
	 *  Get the reference name.
	 *  @return The reference name.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 *  Set the reference name.
	 *  @param name The reference name.
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 *  Get the file name.
	 *  @return The file name.
	 */
	public String getFile()
	{
		return file;
	}
	
	/**
	 *  Set the file name.
	 *  @param file	The file name.
	 */
	public void setFile(String file)
	{
		this.file = file;
	}
}
