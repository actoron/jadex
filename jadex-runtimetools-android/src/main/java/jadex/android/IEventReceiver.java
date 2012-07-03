package jadex.android;

import jadex.bridge.service.types.context.IJadexAndroidEvent;

/**
 * Event receiver to receive Messages sent from Jadex Agents and Components.
 * 
 * @param <T>
 *            Type of the Event which is received through this receiver.
 */
public interface IEventReceiver<T extends IJadexAndroidEvent> {

	/**
	 * Receive an Event
	 * 
	 * @param event
	 */
	void receiveEvent(T event);

	/**
	 * Return the Type of Event that can be received by this receiver.
	 * 
	 * @return Class
	 */
	Class<T> getEventClass();
}
