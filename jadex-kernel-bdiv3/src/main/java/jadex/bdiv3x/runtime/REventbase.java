package jadex.bdiv3x.runtime;

import jadex.bdiv3.actions.FindApplicableCandidatesAction;
import jadex.bdiv3.model.MInternalEvent;
import jadex.bdiv3.model.MMessageEvent;
import jadex.bdiv3.runtime.impl.RElement;
import jadex.bdiv3.runtime.impl.RProcessableElement;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMessageFeature;
import jadex.commons.future.IFuture;

/**
 *  The event base runtime element.
 */
public class REventbase extends RElement implements IEventbase
{
	//-------- attributes --------
	
	/** The scope (for local views). */
	protected String	scope;
	
	//-------- constructors --------
	
	/**
	 *  Create a new goalbase.
	 */
	public REventbase(IInternalAccess agent, String scope)
	{
		super(null, agent);
		this.scope	= scope;
	}

	//-------- IEventbase interface --------
	
	/**
	 *  Send a message after some delay.
	 *  @param me	The message event.
	 *  @return The filter to wait for an answer.
	 */
	public IFuture<Void> sendMessage(IMessageEvent<?> me)
	{
		return getAgent().getComponentFeature(IMessageFeature.class).sendMessage(null, me.getMessage());
	}

	/**
	 *  Dispatch an event.
	 *  @param event The event.
	 */
	public void dispatchInternalEvent(IInternalEvent event)
	{
		FindApplicableCandidatesAction fac = new FindApplicableCandidatesAction((RProcessableElement)event);
		getAgent().getComponentFeature(IExecutionFeature.class).scheduleStep(fac);
	}

	/**
	 *  Create a new message event.
	 *  @return The new message event.
	 */
	public IMessageEvent createMessageEvent(String type)
	{
		MMessageEvent mevent = getCapability().getMCapability().getResolvedMessageEvent(scope, type);
		return new RMessageEvent(mevent, getAgent(), null);
	}

//	/**
//	 *  Create a reply to a message event.
//	 *  @param event	The received message event.
//	 *  @param type	The reply message event type.
//	 *  @return The reply event.
//	 */
//	public IMessageEvent createReply(IMessageEvent event, String type)
//	{
//		if(event==null)
//			throw new IllegalArgumentException("Event must not null");
//		
//		MMessageEvent mevent = getCapability().getMCapability().getResolvedMessageEvent(scope, type);
//		Map<String, Object> rep = event.getMessageType().createReply((Map<String, Object>)event.getMessage());
//		return new RMessageEvent(mevent, rep, SFipa.FIPA_MESSAGE_TYPE, getAgent());
//	}
	
	/**
	 *  Create a new intenal event.
	 *  @return The new intenal event.
	 */
	public IInternalEvent createInternalEvent(String type)
	{
		MInternalEvent mevent = getCapability().getMCapability().getResolvedInternalEvent(scope, type);
		return new RInternalEvent(mevent, getAgent(), null);
	}

//	/**
//	 *  Register a conversation or reply_with to be able
//	 *  to send back answers to the source capability.
//	 *  @param msgevent The message event.
//	 *  todo: indexing for msgevents for speed.
//	 */
//	public void registerMessageEvent(IMessageEvent mevent);
//	
//	/**
//	 *  Remove a registered message event.
//	 *  @param msgevent The message event.
//	 */
//	public void deregisterMessageEvent(IMessageEvent mevent);
}
