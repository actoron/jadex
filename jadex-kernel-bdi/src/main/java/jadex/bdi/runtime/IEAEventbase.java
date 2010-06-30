package jadex.bdi.runtime;

import jadex.commons.IFuture;

/**
 *  Interface for an event base.
 */
public interface IEAEventbase extends IEAElement
{
	//-------- attributes --------

	/**
	 *  Send a message after some delay.
	 *  @param me	The message event.
	 *  @return The filter to wait for an answer.
	 */
	public IFuture sendMessage(IEAMessageEvent me);

	/**
	 *  Dispatch an event.
	 *  @param event The event.
	 */
	public IFuture dispatchInternalEvent(IEAInternalEvent event);

	/**
	 *  Create a new message event.
	 *  @return The new message event.
	 */
	public IFuture createMessageEvent(String type);

	/**
	 *  Create a reply to a message event.
	 *  @param event	The received message event.
	 *  @param msgeventtype	The reply message event type.
	 *  @return The reply event.
	 */
	public IFuture createReply(IEAMessageEvent event, String msgeventtype);
	
	
	/**
	 *  Create a new intenal event.
	 *  @return The new intenal event.
	 */
	public IFuture createInternalEvent(String type);

	/**
	 *  Create a legacy internal event (not explicitly defined in ADF).
	 *  @return The new internal event.
	 *  @deprecated Convenience method for easy conversion to the new Jadex version.
	 *  Will be removed in later releases.
	 * /
	public IInternalEvent createInternalEvent(String type, Object content);*/

	/**
	 *  Register a new event model.
	 *  @param mevent The event model.
	 */
//	public void registerEvent(IMEvent mevent);

	/**
	 *  Register a new event reference model.
	 *  @param meventref The event reference model.
	 */
//	public void registerEventReference(IMEventReference meventref);

	/**
	 *  Deregister an event model.
	 *  @param mevent The event model.
	 */
//	public void deregisterEvent(IMEvent mevent);

	/**
	 *  Deregister an event reference model.
	 *  @param meventref The event reference model.
	 */
//	public void deregisterEventReference(IMEventReference meventref);
	
	/**
	 *  Register a conversation or reply_with to be able
	 *  to send back answers to the source capability.
	 *  @param msgevent The message event.
	 *  todo: indexing for msgevents for speed.
	 */
	public IFuture registerMessageEvent(IEAMessageEvent mevent);
	
	/**
	 *  Remove a registered message event.
	 *  @param msgevent The message event.
	 */
	public IFuture deregisterMessageEvent(IEAMessageEvent mevent);
	
	/**
	 *  Create component identifier.
	 *  @param name The name.
	 *  @param local True for local name.
	 *  @param addresses The addresses.
	 *  @return The new component identifier.
	 */
	public IFuture createComponentIdentifier(String name);
	
	/**
	 *  Create component identifier.
	 *  @param name The name.
	 *  @param local True for local name.
	 *  @param addresses The addresses.
	 *  @return The new component identifier.
	 */
	public IFuture createComponentIdentifier(String name, boolean local);
	
	/**
	 *  Create component identifier.
	 *  @param name The name.
	 *  @param local True for local name.
	 *  @param addresses The addresses.
	 *  @return The new component identifier.
	 */
	public IFuture createComponentIdentifier(String name, boolean local, String[] addresses);
	
	//-------- listeners --------

	/**
	 *  Add a internal event listener.
	 *  @param type	The internal event type.
	 *  @param listener The internal event listener.
	 *  @param async True, if the notification should be done on a separate thread.
	 */
	public IFuture addInternalEventListener(String type, IInternalEventListener listener);
	
	/**
	 *  Remove a internal event listener.
	 *  @param type	The internal event type.
	 *  @param listener The internal event listener.
	 */
	public IFuture removeInternalEventListener(String type, IInternalEventListener listener);
	
	/**
	 *  Add a message event listener.
	 *  @param type	The message event type.
	 *  @param listener The message event listener.
	 *  @param async True, if the notification should be done on a separate thread.
	 */
	public IFuture addMessageEventListener(String type, IMessageEventListener listener);
	
	/**
	 *  Remove a message event listener.
	 *  @param type	The message event type.
	 *  @param listener The message event listener.
	 */
	public IFuture removeMessageEventListener(String type, IMessageEventListener listener);

}
