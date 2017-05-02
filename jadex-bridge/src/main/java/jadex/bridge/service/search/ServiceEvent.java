package jadex.bridge.service.search;

import jadex.bridge.ClassInfo;

/**
 *  Service event used if the service registry is used in event mode.
 *
 *  @param <T> The service type.
 */
public class ServiceEvent<T>
{
	/** Class info of the ServiceEvent type. */
	public static final ClassInfo CLASSINFO = new ClassInfo(ServiceEvent.class);
	
	/** Service was added event. */
	public static final int SERVICE_ADDED = 0;
	
	/** Service was removed event. */
	public static final int SERVICE_REMOVED = 1;
	
	/** Event type. */
	protected int eventtype;
	
	/** The service. */
	protected T service;
	
	/** Bean constructor. */
	public ServiceEvent()
	{
	}
	
	/**
	 *  Creates the service event.
	 *  @param service The affected service.
	 *  @param eventtype The event type.
	 */
	public ServiceEvent(T service, int eventtype)
	{
		this.service = service;
	}

	/**
	 *  Gets the event type.
	 *
	 *  @return The event type.
	 */
	public int getEventType()
	{
		return eventtype;
	}

	/**
	 *  Sets the event type.
	 *
	 *  @param eventtype The event type.
	 */
	public void setEventType(int eventtype)
	{
		this.eventtype = eventtype;
	}

	/**
	 *  Gets the service.
	 *
	 *  @return The service.
	 */
	public T getService()
	{
		return service;
	}

	/**
	 *  Sets the service.
	 *
	 *  @param service The service.
	 */
	public void setService(T service)
	{
		this.service = service;
	}
}
