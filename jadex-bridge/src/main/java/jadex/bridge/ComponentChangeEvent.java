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

/**
 *  The component change event. 
 */
public class ComponentChangeEvent implements IComponentChangeEvent
{
	/** The time of the event. */
	protected long time;

	/** Type of event (e.g. creation, disposal). */
	protected String eventtype;

	/** The category of the source (e.g. goal, step, component) */
	protected String sourcecategory;

	/** The type of the source, i.e. model element name (moveto goal) */
	protected String sourcetype;
	
	/** The name of the source, i.e. instance name (e.g. goal13). */
	protected String sourcename;
	
	/** Component which generated the event. */
	protected IComponentIdentifier component;
	
	/** Parent of the source which generated the event. */
	protected String parent;
	
	/** Reason for the event, if any (e.g. goal succeeded). */
	protected String reason;
	
	/** Event details (e.g. step details of micro agents). */
	protected Object details;
	
	/**
	 *  Create a new event.
	 */
	public ComponentChangeEvent()
	{
	}
	
	/**
	 *  Create a new event.
	 */
	public ComponentChangeEvent(String eventtype, String sourcecategory, String sourcetype, 
		String sourcename, IComponentIdentifier cid, Object details)
	{
		this(eventtype, sourcecategory, sourcetype, sourcename, cid, null, details, 0);
	}
	
	/**
	 *  Create a new event.
	 */
	public ComponentChangeEvent(String eventtype, String sourcecategory, String sourcetype, 
		String sourcename, IComponentIdentifier cid, String reason, Object details, long time)
	{
		this.eventtype = eventtype;
		this.time = time;
		this.sourcename = sourcename;
		this.sourcetype = sourcetype;
		this.sourcecategory = sourcecategory;
		this.component = cid;
		this.reason = reason;
		this.details = details;
	}

	/**
	 *  Returns the type of the event.
	 *  @return The type of the event.
	 */
	public String getEventType()
	{
		return eventtype;
	}
	
	/**
	 *  Returns the time when the event occured.
	 *  @return Time of event.
	 */
	public long getTime()
	{
		return time;
	}
	
	/**
	 *  Returns the name of the source that caused the event.
	 *  @return Name of the source.
	 */
	public String getSourceName()
	{
		return sourcename;
	}
	
	/**
	 *  Returns the type of the source that caused the event.
	 *  @return Type of the source.
	 */
	public String getSourceType()
	{
		return sourcetype;
	}
	
	/**
	 *  Returns the category of the source that caused the event.
	 *  @return Type of the source.
	 */
	public String getSourceCategory()
	{
		return sourcecategory;
	}
	
	/**
	 *  Returns the component that generated the event.
	 *  @return Component ID.
	 */
	public IComponentIdentifier getComponent()
	{
		return component;
	}
	
	/**
	 *  Returns the parent of the source that generated the event, if any.
	 *  @return Parent ID.
	 */
	public String getParent()
	{
		return parent;
	}
	
	/**
	 *  Returns a reason why the event occured.
	 *  @return Reason why the event occured, may be null.
	 */
	public String getReason()
	{
		return reason;
	}
	
	/**
	 *  Get the details.
	 *  @return The details.
	 */
	public Object getDetails()
	{
		return details;
	}
	
	//================== Setters ===================

	/**
	 *  Sets the type of the event.
	 *  @param type The type of the event.
	 */
	public void setEventType(String type)
	{
		eventtype = type;
	}
	
	/**
	 *  Sets the time when the event occured.
	 *  @param time Time of event.
	 */
	public void setTime(long time)
	{
		this.time = time;
	}
	
	/**
	 *  Sets the name of the source that caused the event.
	 *  @param name Name of the source.
	 */
	public void setSourceName(String name)
	{
		sourcename = name;
	}
	
	/**
	 *  Sets the type of the source that caused the event.
	 *  @param type Type of the source.
	 */
	public void setSourceType(String type)
	{
		sourcetype = type;
	}
	
	/**
	 *  Sets the category of the source that caused the event.
	 *  @param category Category of the source.
	 */
	public void setSourceCategory(String category)
	{
		sourcecategory = category;
	}
	
	/**
	 *  Sets the component that generated the event.
	 *  @param id Component ID.
	 */
	public void setComponent(IComponentIdentifier id)
	{
		component = id;
	}
	
	/**
	 *  Sets the parent of the source that generated the event, if any.
	 *  @param id Parent ID.
	 */
	public void setParent(String id)
	{
		parent = id;
	}
	
	/**
	 *  Sets a reason why the event occured.
	 *  @param reason Reason why the event occured, may be null.
	 */
	public void setReason(String reason)
	{
		this.reason = reason;
	}

	/**
	 *  Set the details.
	 *  @param details The details to set.
	 */
	public void setDetails(Object details)
	{
		this.details = details;
	}
	
	/**
	 *  Get the bulk events.
	 *  @return The bulk events.
	 */
	public IComponentChangeEvent[] getBulkEvents()
	{
		return new IComponentChangeEvent[0];
	}

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		StringBuilder sb = new StringBuilder(getComponent()!=null? getComponent().getName(): "unknown");
		sb.append(" ");
		if(getEventType() != null)
		{
			sb.append(getEventType());
			sb.append(" ");
		}
		if(getSourceCategory() != null)
		{
			sb.append(getSourceCategory());
			sb.append(" ");
		}
		sb.append("(");
		sb.append(getTime());
		sb.append("): ");
		if(getSourceName() != null)
		{
			sb.append("Instance: ");
			sb.append(getSourceName());
			sb.append(", ");
		}
		
		sb.append("Type: ");
		sb.append(getSourceType());
		
		if(getReason() != null)
		{
			sb.append(", Reason: ");
			sb.append(getReason());
		}
		
		return sb.toString();
	}
	
	// =========================== Kernel Helper Methods =========================== 
	
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
			{
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
		if (componentlisteners == null || componentlisteners.isEmpty())
		{
			if (finished != null && !finished.isDone())
				finished.setResult(null);
			return;
		}
		getTimeStamp(provider).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				ComponentChangeEvent event = new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_DISPOSAL,
																	  IComponentChangeEvent.SOURCE_CATEGORY_COMPONENT,
																	  model.getName(),
																	  adapter.getComponentIdentifier().getName(),
																	  adapter.getComponentIdentifier(), null, null, (Long) result);
				dispatchComponentChangeEvent(event, componentlisteners, finished);
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
		if (componentlisteners == null || componentlisteners.isEmpty())
		{
			if (finished != null && !finished.isDone())
				finished.setResult(null);
			return;
		}
		getTimeStamp(provider).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				ComponentChangeEvent event = new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_OCCURRENCE,
																	  IComponentChangeEvent.SOURCE_CATEGORY_COMPONENT,
																	  model.getName(),
																	  adapter.getComponentIdentifier().getName(),
																	  adapter.getComponentIdentifier(), null, "terminating", (Long) result);
				dispatchComponentChangeEvent(event, componentlisteners, finished);
			}
		});
	}
}
