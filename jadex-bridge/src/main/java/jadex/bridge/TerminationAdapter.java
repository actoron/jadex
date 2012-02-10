package jadex.bridge;

import jadex.commons.IFilter;
import jadex.commons.future.IFuture;


public abstract class TerminationAdapter implements IComponentListener 
{
	/**
	 *  Invoked when the component was finally terminated.
	 *  No more component related functionality (e.g. goals plans)
	 *  can be executed.
	 */
	public void componentTerminated()
	{
	}
	
	/**
	 *  Returns an event filter, indicating which events
	 *  get passed to the eventOccured() method.
	 *  @return The event filter.
	 */
	public IFilter getFilter()
	{
		return new IFilter()
		{
			public boolean filter(Object obj)
			{
				IComponentChangeEvent cce = (IComponentChangeEvent)obj;
				return IComponentChangeEvent.SOURCE_CATEGORY_COMPONENT.equals(cce.getSourceCategory()) &&
					   IComponentChangeEvent.EVENT_TYPE_DISPOSAL.equals(cce.getEventType());
			}
		};
	}
	
	/**
	 *  Invoked when a change occurs with the component.
	 *  The changes depend on the underlying component type.
	 */
	public IFuture<Void> eventOccured(IComponentChangeEvent cce)
	{
		componentTerminated();
		return IFuture.DONE;
	}
}
