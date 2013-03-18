package jadex.bridge.service.types.monitoring;

import jadex.bridge.Cause;
import jadex.commons.Tuple2;

import java.util.Map;

/**
 * 
 */
public interface IMonitoringEvent
{
	public static String TYPE_SERVICECALL_START = "servicecall_start";
	
	public static String TYPE_SERVICECALL_END = "servicecall_end";

	public static String TYPE_COMPONENT_CREATED = "component_created";

	public static String TYPE_COMPONENT_KILLED = "component_killed";
	
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
