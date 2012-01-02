package jadex.commons;

/**
 *  Required interface for models to be managed by abstract model loader.
 */
public interface ICacheableModel
{
	/**
	 *  Get the last check time of the model.
	 *  @return The last check time of the model.
	 */
	public long	getLastChecked();

	/**
	 *  Set the last check time of the model.
	 *  @param time	The last check time of the model.
	 */
	public void	setLastChecked(long time);

	/**
	 *  Get the last modification time of the model.
	 *  @return The last modification time of the model.
	 */
	public long getLastModified();
	
	/**
	 *  Get the filename.
	 *  @return The filename.
	 */
	public String getFilename();
}
