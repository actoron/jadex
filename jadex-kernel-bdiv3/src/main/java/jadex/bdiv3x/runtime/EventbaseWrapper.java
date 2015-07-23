package jadex.bdiv3x.runtime;

import jadex.bdiv3.model.MElement;
import jadex.commons.future.IFuture;

/**
 *  Prepend capability prefix to event names.
 */
public class EventbaseWrapper implements IEventbase
{
	//-------- attributes --------
	
	/** The flat event base. */
	protected IEventbase	eventbase;
	
	/** The full capability prefix. */
	protected String	prefix;
		
	//-------- constructors --------
	
	/**
	 *  Create an event base wrapper.
	 */
	public EventbaseWrapper(IEventbase eventbase, String prefix)
	{
		this.eventbase	= eventbase;
		this.prefix	= prefix;
	}
	
	//-------- element methods ---------

	/**
	 *  Get the model element.
	 *  @return The model element.
	 */
	public MElement getModelElement()
	{
		return eventbase.getModelElement();
	}
	
	//-------- IBeliefbase methods --------
	
	/**
	 *  Send a message after some delay.
	 *  @param me	The message event.
	 *  @return The filter to wait for an answer.
	 */
	public IFuture<Void> sendMessage(IMessageEvent me)
	{
		return eventbase.sendMessage(me);
	}

	/**
	 *  Dispatch an event.
	 *  @param event The event.
	 */
	public void dispatchInternalEvent(IInternalEvent event)
	{
		eventbase.dispatchInternalEvent(event);
	}

	/**
	 *  Create a new message event.
	 *  @return The new message event.
	 */
	public IMessageEvent createMessageEvent(String type)
	{
		return eventbase.createMessageEvent(prefix + type);
	}

	/**
	 *  Create a reply to a message event.
	 *  @param event	The received message event.
	 *  @param msgeventtype	The reply message event type.
	 *  @return The reply event.
	 */
	public IMessageEvent createReply(IMessageEvent event, String msgeventtype)
	{
		return eventbase.createReply(event, prefix + msgeventtype);
	}
	
	/**
	 *  Create a new intenal event.
	 *  @return The new intenal event.
	 */
	public IInternalEvent createInternalEvent(String type)
	{
		return eventbase.createInternalEvent(prefix + type);
	}
}