package jadex.bridge.service.search;

/**
 *  Service event used if the service registry is used in event mode.
 *
 *  @param <T> The service type.
 */
public class ServiceEvent<T>
{
	/** Service was added event. */
	public static final int SERVICE_ADDED = 0;
	
	/** Service was removed event. */
	public static final int SERVICE_REMOVED = 1;
	
	/** Service changed. */
	public static final int SERVICE_CHANGED = 2;
	
	/** Event type. */
	protected int type;
	
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
		this.type = eventtype;
	}

	/**
	 *  Gets the event type.
	 *
	 *  @return The event type.
	 */
	public int getType()
	{
		return type;
	}

	/**
	 *  Sets the event type.
	 *
	 *  @param eventtype The event type.
	 */
	public void setType(int type)
	{
		this.type = type;
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

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "ServiceEvent [type=" + type + ", service=" + service + "]";
	}
}
