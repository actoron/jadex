package jadex.bridge;

import jadex.commons.IFilter;
import jadex.commons.IRemotable;
import jadex.commons.future.IFuture;

/**
 *  Interface for entities that want to be 
 *  notified on certain component lifecycle events.
 */
public interface IComponentListener extends IRemotable
{
	/**
	 *  Returns an event filter, indicating which events
	 *  get passed to the eventOccured() method.
	 *  @return The event filter.
	 */
	public IFilter getFilter();
	
	/**
	 *  Invoked when a change occurs with the component.
	 *  The changes depend on the underlying component type.
	 */
	public IFuture eventOccured(IComponentChangeEvent cce);
}
