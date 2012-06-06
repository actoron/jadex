package jadex.bridge;

import jadex.commons.IFilter;
import jadex.commons.future.IFuture;

/**
 *  A component listener to be notified about component steps.
 */
public class ComponentStepAdapter	implements IComponentListener 
{
	/**
	 *  Called for each started component step.
	 */
	public void stepStarted()
	{
	}
	
	/**
	 *  Called for each finished component step.
	 */
	public void stepEnded()
	{
	}
	
	/**
	 *  Filter to match step events.
	 */
	public final IFilter getFilter()
	{
		return new IFilter()
		{
			public boolean filter(Object obj)
			{
				IComponentChangeEvent cce = (IComponentChangeEvent)obj;
				return IComponentChangeEvent.SOURCE_CATEGORY_EXECUTION.equals(cce.getSourceCategory());
			}
		};
	}
	
	/**
	 *  Invoked when a change occurs with the component.
	 *  The changes depend on the underlying component type.
	 */
	public final IFuture<Void> eventOccured(IComponentChangeEvent cce)
	{
		if(IComponentChangeEvent.EVENT_TYPE_CREATION.equals(cce.getEventType()))
		{
			stepStarted();
		}
		else if(IComponentChangeEvent.EVENT_TYPE_DISPOSAL.equals(cce.getEventType()))
		{
			stepEnded();
		}
		return IFuture.DONE;
	}
}
