package jadex.bridge;

import java.util.Map;

import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.commons.transformation.annotations.Exclude;

/**
 * 
 */
public class BulkMonitoringEvent implements IMonitoringEvent
{
	/** The bulk events. */
	protected IMonitoringEvent[] events;
	
	/**
	 * 
	 */
	public BulkMonitoringEvent()
	{
	}
	
	/**
	 * 
	 */
	public BulkMonitoringEvent(IMonitoringEvent[] events)
	{
		this.events = events;
	}
	
	/**
	 *  Get the caller.
	 *  @return The caller.
	 */
	public IComponentIdentifier getSourceIdentifier()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 *  Get the source description, e.g. if it is a service.
	 *  @return The source description.
	 */
	public String getSourceDescription()
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 *  Get the source creation time, i.e. the time 
	 *  when the component was created.
	 *  @return The creation time.
	 */
	public long getSourceCreationTime()
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 *  Get the type.
	 *  @return The type.
	 */
	public String getType()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 *  Get the time.
	 *  @return The time.
	 */
	public long getTime()
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 *  Get the cause.
	 *  @return The cause.
	 */
	@Exclude
	public Cause getCause()
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 *  Set the cause.
	 *  @param cause The cause to set.
	 */
	@Exclude
	public void setCause(Cause cause)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 *  Get a property.
	 *  @param name The property name.
	 *  @return The property.
	 */
	public Object getProperty(String name)
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 *  Get a property.
	 *  @param name The property name.
	 *  @return The property.
	 */
	public Map<String, Object> getProperties()
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 *  Get the event importance.
	 */
	public PublishEventLevel getLevel()
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 *  Get the bulk events.
	 *  @return The bulk events.
	 */
	public IMonitoringEvent[] getBulkEvents()
	{
		return events!=null? events: new IMonitoringEvent[0];
	}

	/**
	 *  Set the events.
	 *  @param events The events to set.
	 */
	public void setBulkEvents(IMonitoringEvent[] events)
	{
		this.events = events;
	}
}
