package jadex.bridge;

import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

import java.util.Collection;

/**
 *  The component change event. 
 */
public class ComponentChangeEvent implements IComponentChangeEvent
{
	//-------- attributes --------
	
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
	
	/** Creation time of the component. */
	protected long componentcreationtime;
	
	/** Parent of the source which generated the event. */
	protected String parent;
	
	/** Reason for the event, if any (e.g. goal succeeded). */
	protected String reason;
	
	/** Event details (e.g. step details of micro agents). */
	protected Object details;
	
	//-------- constructors --------

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
		String sourcename, IComponentIdentifier cid, long componentcreationtime, Object details)
	{
		this(eventtype, sourcecategory, sourcetype, sourcename, cid, componentcreationtime, null, details, 0);
	}
	
	/**
	 *  Create a new event.
	 */
	public ComponentChangeEvent(String eventtype, String sourcecategory, String sourcetype, 
		String sourcename, IComponentIdentifier cid, long componentcreationtime, String reason, Object details, long time)
	{
		this.eventtype = eventtype;
		this.time = time;
		this.sourcename = sourcename;
		this.sourcetype = sourcetype;
		this.sourcecategory = sourcecategory;
		this.component = cid;
		this.componentcreationtime = componentcreationtime;
		this.reason = reason;
		this.details = details;
	}

	//-------- methods --------
	
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
	 *  Returns creation time of the component that generated the event.
	 *  @return Parent ID.
	 */
	public long getComponentCreationTime()
	{
		return componentcreationtime;
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
	 *  Sets the creation time of the component that generated the event.
	 *  @param creationtime The creation time.
	 */
	public void setComponentCreationTime(long creationtime)
	{
		this.componentcreationtime = creationtime;
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
	public static final IFuture<Long> getTimeStamp(IServiceProvider provider)
	{
		final Future<Long> ret = new Future<Long>();
		
		SServiceProvider.getService(provider, IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new ExceptionDelegationResultListener<IClockService, Long>(ret)
		{
			public void customResultAvailable(IClockService result)
			{
				ret.setResult(new Long(((IClockService)result).getTime()));
			}
		});
		
		return ret;
	}
	
//	/**
//	 *  Dispatches a component change event.
//	 *  @param event The event.
//	 *  @param componentlisteners Event listeners.
//	 */
//	public static final IFuture<Void> dispatchComponentChangeEvent(IComponentChangeEvent event, Collection<IComponentListener> componentlisteners)
//	{
//		return dispatchComponentChangeEvent(event, componentlisteners);
//	}
	
	/**
	 *  Dispatches a component change event.
	 *  @param event The event.
	 *  @param componentlisteners Event listeners.
	 *  @param finished Future, called when the event has been dispatched.
	 */
	public static final IFuture<Void> dispatchComponentChangeEvent(IComponentChangeEvent event, final Collection<IComponentListener> componentlisteners)
	{
		Future<Void> ret = new Future<Void>();
		
		if(componentlisteners!=null)
		{
			IComponentListener[] listeners = (IComponentListener[])componentlisteners.toArray(new IComponentListener[componentlisteners.size()]);
			final CounterResultListener<Void> clis = new CounterResultListener<Void>(listeners.length, true, new DelegationResultListener<Void>(ret));
			
			for(int i=0; i<listeners.length; i++)
			{
				if(listeners[i].getFilter().filter(event))
				{
					final IComponentListener lis = listeners[i];
					lis.eventOccured(event).addResultListener(new IResultListener<Void>()
					{
						public void resultAvailable(Void result)
						{
							clis.resultAvailable(null);
						}
						
						public void exceptionOccurred(Exception exception)
						{
							componentlisteners.remove(lis);
							clis.resultAvailable(null);
						}
					});
				}
				else
				{
					clis.resultAvailable(null);
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Dispatch a "component terminated" event.
	 *  @param adapter Component adapter.
	 *  @param creationtime Creation time of the component.
	 *  @param model Component model.
	 *  @param provider Component service provider.
	 *  @param componentlisteners Listeners of the component.
	 *  @param finished Future, called when the event has been dispatched.
	 */
	public static final IFuture<Void> dispatchTerminatedEvent(final IComponentIdentifier cid, final long creationtime, final IModelInfo model,
		final Collection<IComponentListener> componentlisteners, IClockService clock)
	{
		final Future<Void> ret = new Future<Void>();
		
		if(componentlisteners == null || componentlisteners.isEmpty())
		{
			ret.setResult(null);
		}
		else
		{
			long time = clock.getTime();
//			getTimeStamp(provider).addResultListener(new ExceptionDelegationResultListener<Long, Void>(ret)
//			{
//				public void customResultAvailable(Long result)
//				{
					ComponentChangeEvent event = new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_DISPOSAL,
						IComponentChangeEvent.SOURCE_CATEGORY_COMPONENT, model.getName(), cid.getName(),
						cid, creationtime, null, null, time);
					dispatchComponentChangeEvent(event, componentlisteners).addResultListener(new DelegationResultListener<Void>(ret));
//				}
//			});
		}
		
		return ret;
	}
	
	/**
	 *  Dispatch a "component terminating" event.
	 *  @param adapter Component adapter.
	 *  @param creationtime Creation time of the component.
	 *  @param model Component model.
	 *  @param provider Component service provider.
	 *  @param componentlisteners Listeners of the component.
	 *  @param finished Future, called when the event has been dispatched.
	 */
	public static final IFuture<Void> dispatchTerminatingEvent(final IComponentAdapter adapter, final long creationtime, final IModelInfo model,
		IServiceProvider provider, final Collection<IComponentListener> componentlisteners)
	{
		final Future<Void> ret = new Future<Void>();
		
		if(componentlisteners == null || componentlisteners.isEmpty())
		{
			ret.setResult(null);
		}
		else
		{
			getTimeStamp(provider).addResultListener(new ExceptionDelegationResultListener<Long, Void>(ret)
			{
				public void customResultAvailable(Long result)
				{
					ComponentChangeEvent event = new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_OCCURRENCE,
						 IComponentChangeEvent.SOURCE_CATEGORY_COMPONENT, model.getName(), adapter.getComponentIdentifier().getName(),
						adapter.getComponentIdentifier(), creationtime, null, "terminating", result);
					dispatchComponentChangeEvent(event, componentlisteners).addResultListener(new DelegationResultListener<Void>(ret));
				}
			});
		}
		
		return ret;
	}
}
