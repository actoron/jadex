package jadex.bdiv3.model;

/**
 *  Reference to an external capability.
 */
// XML only, todo remove?
public class MCapabilityReference	extends MElement
{
	//-------- attributes --------
	
	/** The referenced capability (file name). */
	protected String	file;
	
	//-------- methods --------
	
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
