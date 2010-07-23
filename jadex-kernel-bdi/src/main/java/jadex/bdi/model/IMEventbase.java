package jadex.bdi.model;

/**
 *  Interface for eventbase model.
 */
public interface IMEventbase
{
	/**
	 *  Get an internal event for a name.
	 *  @param name	The event name.
	 */
	public IMInternalEvent getInternalEvent(String name);

	/**
	 *  Get a message event for a name.
	 *  @param name	The event set name.
	 */
	public IMMessageEvent getMessageEvent(String name);
	
	/**
	 *  Returns all internal events.
	 *  @return All internal events.
	 */
	public IMInternalEvent[] getInternalEvents();

	/**
	 *  Return all message events.
	 *  @return All message events.
	 */
	public IMMessageEvent[] getEventSets();
	
	
}
