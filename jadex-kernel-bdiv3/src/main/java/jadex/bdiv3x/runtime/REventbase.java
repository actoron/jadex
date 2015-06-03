package jadex.bdiv3x.runtime;

import jadex.bdiv3.actions.FindApplicableCandidatesAction;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.model.MCapability;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.model.MInternalEvent;
import jadex.bdiv3.model.MMessageEvent;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.impl.RElement;
import jadex.bdiv3.runtime.impl.RGoal;
import jadex.bdiv3.runtime.impl.RPlan;
import jadex.bdiv3.runtime.impl.RProcessableElement;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.fipa.SFipa;
import jadex.commons.future.IFuture;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
public class REventbase extends RElement implements IEventbase
{
	//-------- constructors --------
	
	/**
	 *  Create a new goalbase.
	 */
	public REventbase(IInternalAccess agent)
	{
		super(null, agent);
	}

	//-------- IEventbase interface --------
	
	/**
	 *  Send a message after some delay.
	 *  @param me	The message event.
	 *  @return The filter to wait for an answer.
	 */
	public IFuture<Void> sendMessage(IMessageEvent me)
	{
		return getAgent().getComponentFeature(IMessageFeature.class).sendMessage((Map<String, Object>)me.getMessage(), me.getMessageType());
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
		MMessageEvent mevent = getCapability().getMCapability().getMessageEvent(type);
		if(mevent==null)
			throw new RuntimeException("Message event not found: "+type);
		return new RMessageEvent(mevent, new HashMap<String, Object>(), SFipa.FIPA_MESSAGE_TYPE, getAgent());
	}

//	/**
//	 *  Create a reply to a message event.
//	 *  @param event	The received message event.
//	 *  @param msgeventtype	The reply message event type.
//	 *  @return The reply event.
//	 */
//	public IMessageEvent createReply(IMessageEvent event, String msgeventtype);
	
	
	/**
	 *  Create a new intenal event.
	 *  @return The new intenal event.
	 */
	public IInternalEvent createInternalEvent(String type)
	{
		MInternalEvent mevent = getCapability().getMCapability().getInternalEvent(type);
		if(mevent==null)
			throw new RuntimeException("Internal event not found: "+type);
		return new RInternalEvent(mevent, getAgent());
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
