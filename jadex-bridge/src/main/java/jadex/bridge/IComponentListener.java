package jadex.bridge;

import java.util.Map;


/**
 *  Interface for locally listening to element changes.
 */
public interface IComponentListener
{
	/**
	 *  Called when a new element has been added.
	 *  @param id The identifier.
	 */
	public void componentAdded(IComponentDescription desc);
	
	/**
	 *  Called when a component has changed its state.
	 *  @param id The identifier.
	 */
	public void componentChanged(IComponentDescription desc);
	
	/**
	 *  Called when a new element has been removed.
	 *  @param id The identifier.
	 */
	public void componentRemoved(IComponentDescription desc, Map results);
}
