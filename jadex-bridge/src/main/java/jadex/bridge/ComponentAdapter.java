package jadex.bridge;

import jadex.commons.IFilter;
import jadex.commons.future.IFuture;

/**
 *  Adapter for the IComponentListener interface.
 *
 */
public class ComponentAdapter implements IComponentListener
{
	/**
	 *  Returns an event filter, indicating which events
	 *  get passed to the eventOccured() method.
	 *  @return The event filter.
	 */
	public IFilter getFilter()
	{
		return IFilter.ALWAYS;
	}
	
	/**
	 *  Invoked when a change occurs with the component.
	 *  The changes depend on the underlying component type.
	 */
	public IFuture eventOccured(IComponentChangeEvent cce)
	{
		return IFuture.DONE;
	}
}
