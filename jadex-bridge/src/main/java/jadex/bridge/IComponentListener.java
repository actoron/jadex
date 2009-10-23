package jadex.bridge;


/**
 *  Interface for locally listening to element changes.
 */
public interface IComponentListener
{
	/**
	 *  Called when a new element has been added.
	 *  @param id The identifier.
	 */
	public void componentAdded(Object desc);
	
	/**
	 *  Called when a new element has been removed.
	 *  @param id The identifier.
	 */
	public void componentRemoved(Object desc);
}
