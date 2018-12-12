package jadex.bdi.planlib.protocols.cancelmeta;

import jadex.bdi.planlib.protocols.InteractionState;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.impl.GoalFailureException;
import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.fipa.SFipa;

/**
 *  Receiver plan for FIPA-Cancel-Meta-Protocol.
 *  Waits for cancel message to terminate a conversation
 *  by dropping the supplied interaction goal.
 */
public class CMHandlerPlan extends Plan
{
	/** The failure reason, when cancel was not successful. */
	protected Object	failure_reason;
	
	/** The cancel message (if any). */
	protected IMessageEvent	cancel_msg;
	
	/**
	 *  The plan body.
	 */
	public void body()
	{
		IGoal	interaction_goal	= (IGoal)getParameter("interaction_goal").getValue();
		IMessageEvent	inimsg	= (IMessageEvent)interaction_goal.getParameter("message").getValue();
		IMessageEvent	canceldummy	= createMessageEvent("cm_cancel");	// Hack??? Need some conversation message to wait for
		canceldummy.getParameter(SFipa.CONVERSATION_ID).setValue(inimsg.getParameter(SFipa.CONVERSATION_ID).getValue());
//		getWaitqueue().addReply(canceldummy);	// todo why needed???
		cancel_msg = waitForReply(canceldummy, "cm_cancel");
		getLogger().info("Receiver received cancel: "+getComponentName());

		try
		{
			getLogger().info("Receiver approving cancel: "+getComponentName());
			// Post approve cancel goal to inform domain layer about cancellation.
			IGoal	approve_cancel	= createGoal("cm_approve_cancel");
			approve_cancel.getParameter(SFipa.CONVERSATION_ID).setValue(cancel_msg.getParameter(SFipa.CONVERSATION_ID).getValue());
			approve_cancel.getParameter(SFipa.PROTOCOL).setValue(inimsg.getParameter(SFipa.PROTOCOL).getValue());
			approve_cancel.getParameter("initiator").setValue(cancel_msg.getParameter(SFipa.SENDER).getValue());
			dispatchSubgoalAndWait(approve_cancel);
			getLogger().info("Receiver approved cancel: "+getComponentName());
			
			// If cancel is not approved, store failure reason.
			// Cancel anyways, as interaction is already aborted on initiator side.
			// Initiator has to check cancel result and try to settle open issues as described in failure reason.
			if(!((Boolean)approve_cancel.getParameter("result").getValue()).booleanValue())
			{
				failure_reason	= approve_cancel.getParameter("failure_reason").getValue();
				if(failure_reason==null)
				{
					failure_reason	= "Participant did not approve cancel request.";
				}
			}
			
			// Set the interaction state to recognize aborted() due to cancel
			InteractionState	state	= (InteractionState)interaction_goal.getParameter("interaction_state").getValue();
			if(InteractionState.INTERACTION_RUNNING.equals(state.getInteractionState()))
			{
				state.setInteractionState(InteractionState.INTERACTION_CANCELLED);
			}
		
			getLogger().info("Receiver dropping interaction: "+getComponentName()+", "+interaction_goal);
			interaction_goal.drop();
			try
			{
				waitForGoalFinished(interaction_goal);	// Will never return as plan gets aborted.
			}
			catch(GoalFailureException e)
			{
//				e.printStackTrace();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			failure_reason	= failure_reason!=null ? failure_reason : e.toString();
			// Post result to requester.
			IMessageEvent	reply	= getEventbase().createReply(cancel_msg, "cm_failure");
			// Use user defined language/ontology (hack???, may not support string content?).
			reply.getParameter(SFipa.CONTENT).setValue(failure_reason);
			sendMessage(reply);
			getLogger().info("Receiver sent reply: "+getComponentName());
		}
	}

	/**
	 *  Called when the plan is aborted, i.e.,
	 *  when the corresponding interaction goal
	 *  is dropped.
	 */
	public void aborted()
	{
		getLogger().info("Receiver aborting: "+getComponentName());
		IGoal	interaction_goal	= (IGoal)getParameter("interaction_goal").getValue();
		InteractionState	state	= (InteractionState)interaction_goal.getParameter("interaction_state").getValue();

		// When interaction was aborted on receiver side (i.e. not due to cancel request)
		// state is still "running".
		if(InteractionState.INTERACTION_CANCELLED.equals(state.getInteractionState()))
		{
			// Post result to requester.
			IMessageEvent	reply;
			if(failure_reason==null)
			{
				reply	= getEventbase().createReply(cancel_msg, "cm_inform");
				// Todo: content?
			}
			else
			{
				reply	= getEventbase().createReply(cancel_msg, "cm_failure");
				// Use user defined language/ontology (hack???, may not support string content?).
				reply.getParameter(SFipa.CONTENT).setValue(failure_reason);
			}
			sendMessage(reply).get();
			getLogger().info("Receiver sent reply: "+getComponentName());
		}
		else if(InteractionState.INTERACTION_RUNNING.equals(state.getInteractionState()))
		{
			getLogger().info("Receiver cancelling: "+getComponentName());
			state.setInteractionState(InteractionState.INTERACTION_CANCELLED);
			
			// Inform initator side about dropped out participant using "not-understood" message.
			IMessageEvent	inimsg	= (IMessageEvent)interaction_goal.getParameter("message").getValue();
			IMessageEvent	reply	= getEventbase().createReply(inimsg, "cm_not_understood");
			sendMessage(reply).get();
			getLogger().info("Receiver cancelled: "+getComponentName());
		}
		getLogger().info("Receiver aborted: "+getComponentName());
	}	
}
