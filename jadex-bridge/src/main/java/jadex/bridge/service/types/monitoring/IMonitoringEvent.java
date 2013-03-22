package jadex.bridge.service.types.monitoring;

import jadex.bridge.Cause;
import jadex.bridge.ComponentChangeEvent;

import java.util.Map;

/**
 *  Interface for monitoring events.
 */
public interface IMonitoringEvent
{
	public static String TYPE_SERVICECALL_START = ComponentChangeEvent.EVENT_TYPE_CREATION+"."+ComponentChangeEvent.SOURCE_CATEGORY_SERVICE;
	
	public static String TYPE_SERVICECALL_END = ComponentChangeEvent.EVENT_TYPE_DISPOSAL+"."+ComponentChangeEvent.SOURCE_CATEGORY_SERVICE;

	public static String TYPE_COMPONENT_CREATED = ComponentChangeEvent.EVENT_TYPE_CREATION+"."+ComponentChangeEvent.SOURCE_CATEGORY_COMPONENT; //"component_created";
	
	public static String TYPE_COMPONENT_DISPOSED = ComponentChangeEvent.EVENT_TYPE_DISPOSAL+"."+ComponentChangeEvent.SOURCE_CATEGORY_COMPONENT; //"component_created";

	
	/**
	 *  Get the source.
	 *  @return The source.
	 */
	public String getSource();
	
	/**
	 *  Get the type.
	 *  @return The type.
	 */
	public String getType();

	/**
	 *  Get the time.
	 *  @return The time.
	 */
	public long getTime();
	
	/**
	 *  Get the cause.
	 *  @return The cause.
	 */
	public Cause getCause();
	
	/**
	 *  Set the cause.
	 *  @param cause The cause to set.
	 */
	public void setCause(Cause cause);

	/**
	 *  Get a property.
	 *  @param name The property name.
	 *  @return The property.
	 */
	public Object getProperty(String name);
	
	/**
	 *  Get a property.
	 *  @param name The property name.
	 *  @return The property.
	 */
	public Map<String, Object> getProperties();
	
}
