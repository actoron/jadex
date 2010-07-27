package jadex.bdi.model;

/**
 *  Interface for eventbase model.
 */
public interface IMEventbase extends IMElement
{
	/**
	 *  Get an internal event for a name.
	 *  @param name	The event name.
	 */
	public IMInternalEvent getInternalEvent(String name);

	/**
	 *  Returns all internal events.
	 *  @return All internal events.
	 */
	public IMInternalEvent[] getInternalEvents();
	
	/**
	 *  Get a message event for a name.
	 *  @param name	The event set name.
	 */
	public IMMessageEvent getMessageEvent(String name);
	
	/**
	 *  Return all message events.
	 *  @return All message events.
	 */
	public IMMessageEvent[] getMessageEvents();

	/**
	 *  Get an internal event reference for a name.
	 *  @param name	The event name.
	 */
	public IMInternalEventReference getInternalEventReference(String name);

	/**
	 *  Returns all internal event references.
	 *  @return All internal event references.
	 */
	public IMInternalEventReference[] getInternalEventReferences();
	
	/**
	 *  Get a message event reference for a name.
	 *  @param name	The event set name.
	 */
	public IMMessageEventReference getMessageEventReference(String name);
	
	/**
	 *  Return all message event references.
	 *  @return All message event references.
	 */
	public IMMessageEventReference[] getMessageEventReferences();
	
}
