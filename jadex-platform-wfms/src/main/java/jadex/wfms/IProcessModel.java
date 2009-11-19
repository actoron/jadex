package jadex.wfms;

/**
 *  Interface for process models.
 */
public interface IProcessModel
{
	/**
	 *  Get the process model name.
	 *  @return The process model name.
	 */
	public String getName();

	/**
	 *  Get the package name.
	 *  @return The package name.
	 */
	public String getPackage();
	
	/**
	 *  Get the filename.
	 *  @return The filename.
	 */
	public String getFilename();
	
	/**
	 *  Get the lastmodified date.
	 *  @return The lastmodified date.
	 */
	public long getLastModified();
}
