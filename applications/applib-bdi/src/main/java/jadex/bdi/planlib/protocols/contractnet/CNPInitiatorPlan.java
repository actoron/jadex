package jadex.bdi.planlib.protocols.contractnet;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.bdi.planlib.protocols.AbstractInitiatorPlan;
import jadex.bdi.planlib.protocols.NegotiationRecord;
import jadex.bdi.planlib.protocols.ParticipantProposal;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.impl.GoalFailureException;
import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.annotation.Timeout;
import jadex.commons.SUtil;
import jadex.commons.TimeoutException;

/**
 *  Handles the initiator side of a contract-net protocol.
 */
public class CNPInitiatorPlan extends AbstractInitiatorPlan
{
	//-------- attributes --------

	/** Last sent message with convid for receiving answer messages. */
	// todo: hack, must save at least one message for being able to wait for replies. 
	// Otherwise gc would cleanup all messages.
	protected IMessageEvent me;
	
	//-------- constructors --------

	/**
	 *  The body method is called on the
	 *  instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		super.body();	// Hack???
		
		// Start negotiations.
		String convid;
		if(getParameter("conversation_id").getValue()!=null)
		{
			convid	= (String)getParameter("conversation_id").getValue();
		}
		else
		{
			convid = SUtil.createUniqueId(getComponentName());			
		}
		NegotiationRecord nr = new NegotiationRecord(getParameter("cfp").getValue(), 
			getParameter("cfp_info").getValue(), (IComponentIdentifier[])getParameterSet("receivers").getValues(), getTime());
		getParameterSet("history").addValue(nr);

		// Perform negotiation rounds.
		boolean finished = false;
		Map	proposalmessages	= null;
		ParticipantProposal[]	acceptables	= null;
		while(!finished)
		{
			// Send cfp.
			sendCFP(nr, convid);

			// Collect proposals from participants.
			proposalmessages	= new HashMap();
			collectProposals(nr, proposalmessages);

			// Evaluate proposals and determine acceptables (if any).
			acceptables	= evaluateProposals(nr, proposalmessages);
			nr.setEndtime(getTime());

			// Iterated contract net may start another round.
			if(isIterated())
			{
				// Compute next round participants and next round refined cfp.
				NegotiationRecord newnr = queryNextroundInfo(nr);
				
				// End if no new regotiation record was produced
				if(newnr!=null)
				{
					// Immediately reject excluded participants.
					rejectExcludedProposals(nr, newnr.getParticipants(), proposalmessages);
					nr = newnr;
					proposalmessages	= null;
				}
				else
				{
					finished = true;
				}
			}
			else
			{
				finished = true;
			}
		}
		
		// Result of completation phase is also stored as negotiation record.
		NegotiationRecord	newnr;

		// Protocol completion depends on executall flag.
		if(((Boolean)getParameter("executeall").getValue()).booleanValue())
		{
			newnr	= acceptAllProposals(nr, acceptables, proposalmessages);
		}
		else
		{
			newnr	= acceptOneProposal(nr, acceptables, proposalmessages);
		}

		// Reject remaining proposals, which were not accepted.
		rejectExcludedProposals(nr,  newnr.getParticipants(), proposalmessages);
		proposalmessages	= null;
		nr	= newnr;
		nr.setEndtime(getTime());
		
		// Determine failure.
		determineFailure(nr, acceptables);//, convid);

		// Store results.
		for(int i=0; i<nr.getProposals().length; i++)
		{
			if(nr.getProposals()[i].getEvaluation()!=null)
			{
				getParameterSet("result").addValue(nr.getProposals()[i].getEvaluation());
			}
		}
		getParameter("cfp_info").setValue(nr.getCFPInfo());
		getLogger().info(getComponentName()+"(I)CNPPlan finished: "+convid);		
	}
	
	@Override
	public void passed()
	{
		if(me!=null)
		{
			getWaitqueue().removeReply(me);
		}
		super.passed();
	}
	
	@Override
	public void failed()
	{
		if(me!=null)
		{
			getWaitqueue().removeReply(me);
		}
		super.failed();
	}
	
	@Override
	public void aborted()
	{
		if(me!=null)
		{
			getWaitqueue().removeReply(me);
		}
		super.aborted();
	}
	
	/**
	 *  Test if it is the iterated contract-net version.
	 *  @return True, if is is the iterated version.
	 */
	protected boolean isIterated()
	{
		return ((Boolean)getParameter("iterated").getValue()).booleanValue();	
	}
	
	/**
	 *  Get protocol abbrev name.
	 *  @return The protocol abbrev name.
	 */
	protected String getShortProtocolName()
	{
		String ret = "cnp";
		if(isIterated())
			ret = "icnp";
		return ret;
	}

	/**
	 *  Send the cfp message.
	 *  @param nr The current negotation record.
	 *  @param convid The conversation id.
	 */
	protected void sendCFP(NegotiationRecord nr, String convid)
	{
		startAtomic();
		if(me!=null)
		{
			getWaitqueue().removeReply(me);
		}
		
		me = createMessageEvent(getShortProtocolName()+"_cfp");
		me.getParameterSet(SFipa.RECEIVERS).addValues(nr.getParticipants());
		me.getParameter(SFipa.CONVERSATION_ID).setValue(convid);
		me.getParameter(SFipa.CONTENT).setValue(nr.getCFP());
		if(getParameter(SFipa.LANGUAGE).getValue()!=null)
			me.getParameter(SFipa.LANGUAGE).setValue(getParameter(SFipa.LANGUAGE).getValue());
		if(getParameter(SFipa.ONTOLOGY).getValue()!=null)
			me.getParameter(SFipa.ONTOLOGY).setValue(getParameter(SFipa.ONTOLOGY).getValue());
		getWaitqueue().addReply(me);
		endAtomic();
		
		getLogger().info(getComponentName()+" (I)CNPPlan initiated: "+convid);
		sendMessage(me);
	}

	/**
	 *  Collect proposal messages.
	 *  @param nr The negotiation record.
	 *  @param proposalmessages	Map for storing the message of each current proposal (part. proposal -> message event).
	 */
	protected void collectProposals(NegotiationRecord nr, Map proposalmessages)
	{
		List rec = SUtil.arrayToList(nr.getParticipants());

		long time = getTime();
		try
		{
			while(rec.size() > 0)
			{
				// Wait for the replies.
				long wait_time;
				if(getTimeout()==Timeout.NONE)
				{
					wait_time	= Timeout.NONE;
				}
				else
				{
					wait_time	= getTimeout() + time - getTime();
					if(wait_time <= 0)
					{
						break;
					}
				}

				getLogger().info(getComponentName()+" (I)CNPPlan: waiting: "+wait_time);

				IMessageEvent reply = (IMessageEvent)waitForReply(me, wait_time);
				IComponentIdentifier sender = (IComponentIdentifier)reply.getParameter(SFipa.SENDER).getValue();
				rec.remove(sender);
				
				// Other messages than proposals will be ignored and
				// lead to exclusion in this negotiation round (as no proposal is available).
				// The determination of participants for the next round depends
				// on the handling of the query icnp_nextround_info goal. 
				if(reply.getType().equals(getShortProtocolName()+"_propose"))
				{
					getLogger().info(getComponentName()+" (I)CNPPlan received a proposal reply: "+reply.getParameter(SFipa.CONTENT).getValue());
					ParticipantProposal	proposal	= nr.getProposal(sender);
					if(proposal!=null && proposal.getProposal()==null)
					{
						proposal.setProposal(reply.getParameter(SFipa.CONTENT).getValue());
						proposalmessages.put(proposal, reply);
					}
					else
					{
						getLogger().warning(getComponentName()+" (I)CNPPlan received an unexpected proposal reply: "+reply);
					}
				}
			}
		}
		catch(TimeoutException e)
		{
			// Timeout can occur when some of the participants do not answer at all.
			// In this case protocol proceeds normally.
		}
	}

	/**
	 *  Determine acceptable proposals.
	 *  @param nr The negotiation record.
	 *  @param proposalmessages The received proposal messages (required to detect null proposals).
	 *  @return The acceptable proposals.
	 */
	protected ParticipantProposal[]	evaluateProposals(NegotiationRecord nr, Map proposalmessages)
	{
		// default: no acceptables
		ParticipantProposal[]	acceptables	= new ParticipantProposal[0];

		if(nr.getProposals().length>0)
		{
			// Determine acceptables.
			IGoal sel = createGoal(getShortProtocolName()+"_evaluate_proposals");
			sel.getParameter("cfp").setValue(nr.getCFP());
			sel.getParameter("cfp_info").setValue(nr.getCFPInfo());
			sel.getParameterSet("history").addValues(getParameterSet("history").getValues());
			// Add only actual proposals (agents which didn't answer can still be acessed from history)
			for(int i=0; i<nr.getProposals().length; i++)
			{
				if(proposalmessages.get(nr.getProposals()[i])!=null)
					sel.getParameterSet("proposals").addValue(nr.getProposals()[i]);
			}
			
			// Dispatch goal, when some proposals are available
			if(sel.getParameterSet("proposals").size()>0)
			{
				try
				{
					dispatchSubgoalAndWait(sel);
					acceptables	= (ParticipantProposal[])sel.getParameterSet("acceptables").getValues();
					nr.setCFPInfo(sel.getParameter("cfp_info").getValue());
					getLogger().info(getComponentName()+" (I)CNPPlan determined acceptables: "+SUtil.arrayToString(acceptables));
				}
				catch(GoalFailureException e)
				{
					getLogger().info("(I)CNP: determination of acceptables failed: "+e);
					// e.printStackTrace();
				}
			}
			
			// When no proposals are available no winners are determined (leading to failure if no more negotiation round is performed).
			else
			{
				getLogger().info("(I)CNP: determination of acceptables failed due to no proposals.");
			}
		}
		
		return acceptables;
	}
	
	/**
	 *  Decide if a new iteration should be performed.
	 *  @param nr The negotiation record of the current round.
	 *  @return The negotiation record for the next round (or null, if no further iteration should be performed).
	 */
	protected NegotiationRecord queryNextroundInfo(NegotiationRecord nr)
	{
		NegotiationRecord ret = null;
		
		IGoal sel = createGoal(getShortProtocolName()+"_nextround_info");
		sel.getParameter("cfp").setValue(nr.getCFP());
		sel.getParameter("cfp_info").setValue(nr.getCFPInfo());
		sel.getParameterSet("participants").addValues(nr.getParticipants());
		sel.getParameterSet("proposals").addValues(nr.getProposals());
		sel.getParameterSet("history").addValues(getParameterSet("history").getValues());
		try
		{
			dispatchSubgoalAndWait(sel);
			if(((Boolean)sel.getParameter("iterate").getValue()).booleanValue())
			{
				Object	cfp	= sel.getParameter("cfp").getValue();
				Object	cfp_info	= sel.getParameter("cfp_info").getValue();
				IComponentIdentifier[]	participants	= (IComponentIdentifier[])sel.getParameterSet("participants").getValues();
				ret	= new NegotiationRecord(cfp, cfp_info, participants, getTime());
				getParameterSet("history").addValue(ret);
				getLogger().info("ICNP: perform further negotiation round");
			}
			else
			{
				getLogger().info("ICNP: perform no further negotiation round");
			}
		}
		catch(GoalFailureException e)
		{
			getLogger().info("ICNP: perform no further negotiation round");
			// If goal does not succeed no new round will be executed.
			//e.printStackTrace();
		}
		
		return ret;
	}
	
	/**
	 *  Reject all proposals, which are not part of the next round.
	 *  @param nr The current negotiation record.
	 *  @param newparticipants The remaining participants of the next round, which should not be rejected.
	 */
	protected void rejectExcludedProposals(NegotiationRecord nr, IComponentIdentifier[] newparticipants, Map proposalmessages)
	{
		// Determine proposals to reject.
		// Todo: allow for an agent to participate more than once?
		Set proposals = SUtil.arrayToSet(nr.getProposals());
		Set remaining = SUtil.arrayToSet(newparticipants);
		for(Iterator it=proposals.iterator(); it.hasNext(); )
		{
			ParticipantProposal	proposal	= (ParticipantProposal)it.next();
			if(remaining.contains(proposal.getParticipant()))
				it.remove();
		}
		ParticipantProposal[] reject_proposals = (ParticipantProposal[])proposals.toArray(new ParticipantProposal[proposals.size()]);
		
		// Send reject proposal message as reply to earlier proposal of participant.
		for(int i=0; i<reject_proposals.length; i++)
		{
			// Check if proposal message is available (otherwise agent didn't answer and no reject has to be sent).
			IMessageEvent	proposal	= (IMessageEvent)proposalmessages.get(reject_proposals[i]);
			if(proposal!=null)
			{
				IMessageEvent	reject	= getEventbase().createReply(proposal, getShortProtocolName()+"_reject");
				reject.getParameter(SFipa.CONTENT).setValue(reject_proposals[i].getProposal());
				sendMessage(reject);
			}
		}
	}

	/**
	 *  Accept all proposals in parallel.
	 *  @param nr The current negotiation record.
	 *  @param acceptables The acceptable proposals.
	 *  @param proposalmessages The map containing proposal messages to reply to.
	 */
	protected NegotiationRecord acceptAllProposals(NegotiationRecord nr, ParticipantProposal[] acceptables, Map proposalmessages)
	{
		for(int i=0; i<acceptables.length; i++)
		{
			IMessageEvent	proposal	= (IMessageEvent)proposalmessages.get(acceptables[i]);
			IMessageEvent	accept	= getEventbase().createReply(proposal, getShortProtocolName()+"_accept");
			accept.getParameter(SFipa.CONTENT).setValue(acceptables[i].getProposal());
			sendMessage(accept);
		}

		long time = getTime();
		Map	results	= new HashMap();
		try
		{
			while(results.size()<acceptables.length)
			{
				// Wait for the replies.
				long wait_time;
				if(getTimeout()==Timeout.NONE)
				{
					wait_time	= Timeout.NONE;
				}
				else
				{
					wait_time	= getTimeout() + time - getTime();
					if(wait_time <= 0)
					{
						break;
					}
				}

				// Wait for messages indicating the task execution state.
				IMessageEvent reply = (IMessageEvent)waitForReply(me, wait_time);
				IComponentIdentifier sender = (IComponentIdentifier)reply.getParameter(SFipa.SENDER).getValue();

				// Todo: Also support inform-done?
				if(reply.getType().equals(getShortProtocolName()+"_inform"))
				{
					ParticipantProposal	proposal	= nr.getProposal(sender);
					if(proposal!=null && results.get(sender)==null)
					{
						results.put(sender, reply.getParameter(SFipa.CONTENT).getValue());
						getLogger().info("Task was executed: "+this+" "+getComponentName()+" "+sender);
					}
					else
					{
						getLogger().warning(getComponentName()+" (I)CNPPlan received an unexpected acceptance reply: "+reply);
					}
				}
				else
				{
					getLogger().info("One task was possibly not executed: "+this+" "+getComponentName()+" "+sender);
				}
			}
		}
		catch(TimeoutException e)
		{
			// nop
		}
		
		// Create result negotiation record containing only successfully executed proposals.
		IComponentIdentifier[]	executed	= new IComponentIdentifier[results.size()];
		int	index	= 0;
		for(int i=0; i<acceptables.length; i++)
		{
			if(results.containsKey(acceptables[i].getParticipant()))
				executed[index++]	= acceptables[i].getParticipant();
		}
		NegotiationRecord	ret	= new NegotiationRecord(nr.getCFP(), nr.getCFPInfo(), executed, getTime());
		getParameterSet("history").addValue(ret);
		for(int i=0; i<ret.getProposals().length; i++)
		{
			ret.getProposals()[i].setProposal(nr.getProposal(ret.getProposals()[i].getParticipant()).getProposal());
			ret.getProposals()[i].setEvaluation(results.get(ret.getProposals()[i].getParticipant()));
		}			
		
		return ret;
	}

	/**
	 *  Sequentially accept proposals until the first successful execution.
	 *  @param nr The current negotiation record.
	 *  @param acceptables The acceptable proposals.
	 *  @param proposalmessages The map containing proposal messages to reply to.
	 */
	protected NegotiationRecord acceptOneProposal(NegotiationRecord nr, ParticipantProposal[] acceptables, Map proposalmessages)
	{
		IComponentIdentifier	executed	= null;
		Object	result	= null;
		for(int i=0; i<acceptables.length; i++)
		{
			// Accept proposals as long as no proposal was executed successfully
			if(executed==null)
			{
				try
				{
					// Send acceptance to one agent and wait for reply.
					IMessageEvent	proposal	= (IMessageEvent)proposalmessages.get(acceptables[i]);
					IMessageEvent	accept	= getEventbase().createReply(proposal, getShortProtocolName()+"_accept");
					accept.getParameter(SFipa.CONTENT).setValue(acceptables[i].getProposal());
					IMessageEvent	reply	= sendMessageAndWait(accept, getTimeout());
			
					IComponentIdentifier sender = (IComponentIdentifier)reply.getParameter(SFipa.SENDER).getValue();
			
					// Todo: Also support inform-done?
					if(reply.getType().equals(getShortProtocolName()+"_inform"))
					{
						if(nr.getProposal(sender)!=null)
						{
							executed	= sender;
							result	= reply.getParameter(SFipa.CONTENT).getValue();
							getLogger().info("Task was executed: "+this+" "+getComponentName()+" "+sender);
						}
						else
						{
							getLogger().warning(getComponentName()+" (I)CNPPlan received an unexpected acceptance reply: "+reply);
						}
					}
					else
					{
						getLogger().info("One task was possibly not executed: "+this+" "+getComponentName()+" "+sender);
					}
				}
				catch(TimeoutException e)
				{
					getLogger().info("One task was possibly not executed: "+this+" "+getComponentName()+" "+acceptables[i].getParticipant());
				}
			}
			
			// Reject remaining proposals.
			else
			{
				// Check if proposal message is available (otherwise agent didn't answer and no reject has to be sent).
				IMessageEvent	proposal	= (IMessageEvent)proposalmessages.get(acceptables[i]);
				if(proposal!=null)
				{
					IMessageEvent	reject	= getEventbase().createReply(proposal, getShortProtocolName()+"_reject");
					reject.getParameter(SFipa.CONTENT).setValue(acceptables[i].getProposal());
					sendMessage(reject);
				}
			}
		}
		
		// Create result negotiation record containing the successfully executed proposal (if any).
		NegotiationRecord	ret	= new NegotiationRecord(nr.getCFP(), nr.getCFPInfo(), executed!=null ? 
			new IComponentIdentifier[]{executed} : new IComponentIdentifier[0], getTime());
		getParameterSet("history").addValue(ret);
		if(executed!=null)
		{
			ret.getProposal(executed).setProposal(nr.getProposal(executed).getProposal());
			ret.getProposal(executed).setEvaluation(result);
		}
		
		return ret;
	}

	/**
	 *  Determine success or failure of the interaction.
	 *  Will make the plan fail, if not enough proposals have been executed
	 *  according to the acceptables and the "needall" flag.
	 *  @param nr The final negotiation record containing executed proposals.
	 *  @param acceptables	The acceptable proposals.
	 */
	protected void determineFailure(NegotiationRecord nr, ParticipantProposal[] acceptables)//, String convid)
	{
		// todo reintroduce extra needall flag (posttoall-and vs. posttoall-or)???
		boolean executeall = ((Boolean)getParameter("executeall").getValue()).booleanValue();

		if(nr.getProposals().length==0 || (executeall && nr.getProposals().length!=acceptables.length))
		{
			getLogger().info("(I)CNPPlan failed: ");//+convid);
			fail();
		}
	}
	
	//-------- AbstractInitiatorPlan template methods --------
	
	/**
	 *  Get the initial message.
	 *  @return The initial message of the interaction.
	 */
	protected IMessageEvent getInitialMessage()
	{
		return me;
	}
}
