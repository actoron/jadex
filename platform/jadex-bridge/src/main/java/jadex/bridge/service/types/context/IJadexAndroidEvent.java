package jadex.bridge.service.types.context;

/**
 * This is an Event that can be dispatched by platform components and received
 * by Android UI Classes.
 */
public interface IJadexAndroidEvent 
{
	/**
	 * The type of the Event, which is used
	 * to separate events from each other.
	 */
	String getType();
}
