package jadex.bridge.service.types.monitoring;

import java.util.HashMap;
import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;

/**
 *  Default implementation for events.
 */
public class MonitoringEvent implements IMonitoringEvent
{
	//-------- attributes --------
	
	/** The source. */
	protected IComponentIdentifier source;
	
	/** The source description. */
	protected String sourcedesc;
	
	/** The source creation time. */
	protected long creationtime;
	
	
	/** The type. */
	protected String type;
	
	/** The timepoint. */
	protected long time;
	
//	/** The cause. */
//	protected Cause cause;
	
	/** The event importance. */
	protected PublishEventLevel level;
	
	/** The service call properties. */
	protected Map<String, Object> properties;
	
	//-------- constructors --------
		
	/**
	 *  Create a new monitoring event.
	 */
	public MonitoringEvent()
	{
		// bean constructor
	}
	
	/**
	 *  Create a new monitoring event.
	 */
	public MonitoringEvent(IComponentIdentifier source, long crtime, String type, long time, PublishEventLevel importance)
	{
		this(source, crtime, null, type, time, importance, null);
	}
	
//	/**
//	 *  Create a new monitoring event.
//	 */
//	public MonitoringEvent(IComponentIdentifier source, long crtime, String type, long time, PublishEventLevel importance)
////	public MonitoringEvent(IComponentIdentifier source, long crtime, String type, Cause cause, long time, PublishEventLevel importance)
//	{
//		this(source, crtime, null, type, time, importance, null);
//	}
	
	/**
	 *  Create a new monitoring event.
	 */
	public MonitoringEvent(IComponentIdentifier source, long crtime, String sourcedesc, String type, long time, PublishEventLevel importance)
//	public MonitoringEvent(IComponentIdentifier source, long crtime, String sourcedesc, String type, Cause cause, long time, PublishEventLevel importance)
	{
		this(source, crtime, sourcedesc, type, time, importance, null);
	}
	
	/**
	 *  Create a new monitoring event.
	 */
	public MonitoringEvent(IComponentIdentifier source, long crtime, String sourcdesc, String type, long time, PublishEventLevel level, Map<String, Object> props)
//	public MonitoringEvent(IComponentIdentifier source, long crtime, String sourcdesc, String type, Cause cause, long time, PublishEventLevel level, Map<String, Object> props)
	{
		this.source = source;
		this.sourcedesc = sourcdesc;
		this.creationtime = crtime;
		this.type = type;
//		this.cause = cause;
		this.time = time;
		this.level = level!=null? level: PublishEventLevel.FINE;
		this.properties = props;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the caller.
	 *  @return The caller.
	 */
	public IComponentIdentifier getSourceIdentifier()
	{
		return source;
	}

	/**
	 *  Set the source.
	 *  @param source The source to set.
	 */
	public void setSourceIdentifier(IComponentIdentifier source)
	{
		this.source = source;
	}
	
	/**
	 *  Get the source description, e.g. if it is a service.
	 *  @return The source description.
	 */
	public String getSourceDescription()
	{
		return sourcedesc;
	}
	
	/**
	 *  Set the source description.
	 */
	public void setSourceDescription(String sourcedesc)
	{
		this.sourcedesc = sourcedesc;
	}
	
	/**
	 *  Get the source creation time, i.e. the time 
	 *  when the component was created.
	 *  @return The creation time.
	 */
	public long getSourceCreationTime()
	{
		return creationtime;
	}
	
	/**
	 *  Set the creation time.
	 *  @param creation time The creation time to set.
	 */
	public void setCreationTime(long creationtime)
	{
		this.creationtime = creationtime;
	}

	/**
	 *  Get the type.
	 *  @return The type.
	 */
	public String getType()
	{
		return type;
	}

	/**
	 *  Set the type.
	 *  @param type The type to set.
	 */
	public void setType(String type)
	{
		this.type = type;
	}
	
	/**
	 *  Get the time.
	 *  @return The time.
	 */
	public long getTime()
	{
		return time;
	}

	/**
	 *  Set the time.
	 *  @param time The time to set.
	 */
	public void setTime(long time)
	{
		this.time = time;
	}

//	/**
//	 *  Get the cause.
//	 *  @return The cause.
//	 */
//	public Cause getCause()
//	{
//		return cause;
//	}
//
//	/**
//	 *  Set the cause.
//	 *  @param cause The cause to set.
//	 */
//	public void setCause(Cause cause)
//	{
//		this.cause = cause;
//	}
	
	/**
	 *  Get a property.
	 *  @param name The property name.
	 *  @return The property.
	 */
	public Object getProperty(String name)
	{
		return properties==null? null: properties.get(name);
	}

	/**
	 *  Set a property.
	 *  @param name The property name.
	 *  @param val The property value.
	 */
	public void setProperty(String name, Object val)
	{
		if(properties==null)
			properties = new HashMap<String, Object>();
		properties.put(name, val);
	}
	
	/**
	 *  Get a property.
	 *  @param name The property name.
	 *  @return The property.
	 */
	public Map<String, Object> getProperties()
	{
		return properties;
	}
	
	/**
	 *  Set the properties.
	 *  @param properties The properties to set.
	 */
	public void setProperties(Map<String, Object> properties)
	{
		this.properties = properties;
	}
	
	/**
	 *  Get the event importance level.
	 */
	public PublishEventLevel getLevel()
	{
		return level;
	}
	
	/**
	 *  Set the importance.
	 */
	public void setLevel(PublishEventLevel importance)
	{
		this.level = importance;
	}

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "MonitoringEvent(source=" + source + ", type=" + type
			+ ", time=" + time // + ", cause=" + cause 
			+ ", properties="
			+ properties + ")";
	}
}