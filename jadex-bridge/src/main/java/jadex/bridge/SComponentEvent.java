package jadex.bridge;

import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.clock.IClockService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

import java.util.Collection;

public class SComponentEvent
{
	/**
	 *  Retrieves a timestamp from the clock service which can be used in events.
	 * 	@param provider Service provider of the component.
	 *  @return Time stamp.
	 */
	public static final IFuture getTimeStamp(IServiceProvider provider)
	{
		final Future ret = new Future();
		SServiceProvider.getService(provider, IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				ret.setResult(((IClockService)result).getTime());
			}
		});
		
		return ret;
	}
	
	/**
	 *  Dispatches a component change event.
	 *  @param event The event.
	 *  @param componentlisteners Event listeners.
	 */
	public static final void dispatchComponentChangeEvent(IComponentChangeEvent event, Collection componentlisteners)
	{
		dispatchComponentChangeEvent(event, componentlisteners, null);
	}
	
	/**
	 *  Dispatches a component change event.
	 *  @param event The event.
	 *  @param componentlisteners Event listeners.
	 *  @param finished Future, called when the event has been dispatched.
	 */
	public static final void dispatchComponentChangeEvent(IComponentChangeEvent event, final Collection componentlisteners, Future finished)
	{
		if(componentlisteners!=null)
		{
			IComponentListener[] listeners = (IComponentListener[]) componentlisteners.toArray(new IComponentListener[componentlisteners.size()]);
			for(int i=0; i<listeners.length; i++)
				if (listeners[i].getFilter().filter(event))
				{
					final IComponentListener lis = listeners[i];
					lis.eventOccured(event).addResultListener(new IResultListener()
					{
						public void resultAvailable(Object result)
						{
						}
						
						public void exceptionOccurred(Exception exception)
						{
							componentlisteners.remove(lis);
						}
					});
				}
		}
		if (finished != null)
			finished.setResult(null);
	}
	
	/**
	 *  Dispatch a "component terminated" event.
	 *  @param adapter Component adapter.
	 *  @param model Component model.
	 *  @param provider Component service provider.
	 *  @param componentlisteners Listeners of the component.
	 *  @param finished Future, called when the event has been dispatched.
	 */
	public static final void dispatchTerminatedEvent(final IComponentAdapter adapter, final IModelInfo model,
			IServiceProvider provider, final Collection componentlisteners, final Future finished)
	{
		if (componentlisteners.isEmpty())
			return;
		getTimeStamp(provider).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				ComponentChangeEvent event = new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_DISPOSAL,
																	  IComponentChangeEvent.SOURCE_CATEGORY_COMPONENT,
																	  model.getName(),
																	  adapter.getComponentIdentifier().getName(),
																	  adapter.getComponentIdentifier(), null, null, (Long) result);
				SComponentEvent.dispatchComponentChangeEvent(event, componentlisteners, finished);
			}
		});
	}
	
	/**
	 *  Dispatch a "component terminating" event.
	 *  @param adapter Component adapter.
	 *  @param model Component model.
	 *  @param provider Component service provider.
	 *  @param componentlisteners Listeners of the component.
	 *  @param finished Future, called when the event has been dispatched.
	 */
	public static final void dispatchTerminatingEvent(final IComponentAdapter adapter, final IModelInfo model,
			IServiceProvider provider, final Collection componentlisteners, final Future finished)
	{
		if (componentlisteners.isEmpty())
			return;
		getTimeStamp(provider).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				ComponentChangeEvent event = new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_OCCURRENCE,
																	  IComponentChangeEvent.SOURCE_CATEGORY_COMPONENT,
																	  model.getName(),
																	  adapter.getComponentIdentifier().getName(),
																	  adapter.getComponentIdentifier(), null, "terminating", (Long) result);
				SComponentEvent.dispatchComponentChangeEvent(event, componentlisteners, finished);
			}
		});
	}
}
