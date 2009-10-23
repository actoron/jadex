package jadex.bridge;


/**
 *  Interface for locally listening to element changes.
 */
public interface IElementListener
{
	/**
	 *  Called when a new element has been added.
	 *  @param id The identifier.
	 */
	public void elementAdded(Object desc);
	
	/**
	 *  Called when a new element has been removed.
	 *  @param id The identifier.
	 */
	public void elementRemoved(Object desc);
}
