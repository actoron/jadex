package jadex.bdi.model.editable;

import jadex.bdi.model.IMEventbase;

/**
 * 
 */
public interface IMEEventbase extends IMEventbase, IMEElement
{
	/**
	 *  Create an internal with name.
	 *  @param name	The event name.
	 */
	public IMEInternalEvent createInternalEvent(String name);

	/**
	 *  Create a message with name.
	 *  @param name	The event set name.
	 */
	public IMEMessageEvent createMessageEvent(String name);
	
	/**
	 *  Create an internal event reference.
	 *  @param name	The event name.
	 *  @param ref The name of referenced element.
	 */
	public IMEInternalEventReference createInternalEventReference(String name, String ref);

	/**
	 *  Create a message event reference.
	 *  @param name	The event set name.
	 *  @param ref The name of referenced element.
	 */
	public IMEMessageEventReference createMessageEventReference(String name, String ref);
}
