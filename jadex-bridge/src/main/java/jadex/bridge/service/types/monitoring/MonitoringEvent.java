package jadex.bridge.service.types.monitoring;

import jadex.bridge.Cause;
import jadex.bridge.service.component.interceptors.ServiceGetter;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.util.HashMap;
import java.util.Map;

/**
 *  Default implementation for events.
 */
public class MonitoringEvent implements IMonitoringEvent
{
	//-------- attributes --------
	
	/** The source. */
	protected String source;
	
	/** The type. */
	protected String type;
	
	/** The timepoint. */
	protected long time;
	
	/** The cause. */
	protected Cause cause;
	
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
	public MonitoringEvent(String source, String type, long time)
	{
		this(source, type, null, time, null);
	}
	
	/**
	 *  Create a new monitoring event.
	 */
	public MonitoringEvent(String source, String type, Cause cause, long time)
	{
		this(source, type, cause, time, null);
	}
	
	/**
	 *  Create a new monitoring event.
	 */
	public MonitoringEvent(String source, String type, Cause cause, long time, Map<String, Object> props)
	{
		this.source = source;
		this.type = type;
		this.cause = cause;
		this.time = time;
		this.properties = props;
	}
	
	//-------- methods --------
	
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
	 *  Get the caller.
	 *  @return The caller.
	 */
	public String getSource()
	{
		return source;
	}

	/**
	 *  Set the source.
	 *  @param source The source to set.
	 */
	public void setSource(String source)
	{
		this.source = source;
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

	/**
	 *  Get the cause.
	 *  @return The cause.
	 */
	public Cause getCause()
	{
		return cause;
	}

	/**
	 *  Set the cause.
	 *  @param cause The cause to set.
	 */
	public void setCause(Cause cause)
	{
		this.cause = cause;
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
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "MonitoringEvent(source=" + source + ", type=" + type
			+ ", time=" + time + ", cause=" + cause + ", properties="
			+ properties + ")";
	}
}