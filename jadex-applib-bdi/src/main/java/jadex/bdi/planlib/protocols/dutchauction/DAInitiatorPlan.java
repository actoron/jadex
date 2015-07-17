package jadex.bdi.planlib.protocols.dutchauction;

import jadex.bdi.planlib.protocols.AbstractInitiatorPlan;
import jadex.bdi.planlib.protocols.AuctionDescription;
import jadex.bdiv3.runtime.BDIFailureException;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.fipa.SFipa;
import jadex.commons.SUtil;
import jadex.commons.collection.SCollection;
import jadex.commons.concurrent.TimeoutException;

import java.util.List;


/**
 *  This plan implements the initiator of the "FIPA Dutch Auction Interaction
 *  Protocol Specification" (XC00032 - Experimental)
 *  
 *  A dutch auction is one where the auctioneer starts with a high start price
 *  and continually lowers it until the first bidder accepts the price.
 */
public class DAInitiatorPlan extends AbstractInitiatorPlan
{
	//-------- attributes --------
	
	/** The initial message. */
	// Hack!!! Needed for cancel-meta-protocol and for avoiding garbage collection.
	protected IMessageEvent	start;
	
	//-------- methods --------
	
	/**
	 * The plan body.
	 */
	public void body()
	{
//		System.out.println("DA initiator plan started");
		
		AuctionDescription auctiondesc = (AuctionDescription)getParameter("auction_description").getValue();
		if(auctiondesc.getRoundTimeout()<=0)
		{
			getLogger().warning(getComponentName()+"No round timeout specified");
			fail();
		}
		
		// Fetch the timeout for each round of the auction.
		long roundtimeout = auctiondesc.getRoundTimeout();
		
		// Fetch the receivers.
		List receivers = SUtil.arrayToList(getParameterSet("receivers").getValues());
		
		// Initialize negotiations.
		String convid;
		if(getParameter("conversation_id").getValue()!=null)
		{
			convid	= (String)getParameter("conversation_id").getValue();
		}
		else
		{
			convid = SUtil.createUniqueId(getComponentName());			
		}
		
		// Announce the auction by sending information about it.
		announceAuction(auctiondesc, receivers, convid);
		
		// Wait for the auction to begin.
		// Removes receivers that do not want to participate.
		waitForAuctionStart(auctiondesc, receivers);
		
		// Send calls for proposal until no more proposals are received.
		boolean running = true;
		Object winning_offer = null;
		IComponentIdentifier winner = null;
		Object cfp = getParameter("cfp").getValue();
		Object cfp_info = getParameter("cfp_info").getValue();
		List history = SCollection.createArrayList();
		history.add(cfp);
		
		// Send calls for proposal until the limit price is reached or an agent is
		// willing to pay the actual price.
		// Auction ends when winner is determined, limit price is reached or
		// no receiver is left.
		while(running && receivers.size()>0) 
		{
			//System.out.println(getAgentName()+" current offer is: "+cfp+" "+receivers);
			
			// Send CFP.
			sendCFP(cfp, convid, receivers);
			
			// Wait for proposals.
			// Removes receivers that do not offer.
			winner = waitForProposals(cfp, roundtimeout, receivers);
			//System.out.println(getAgentName()+" winner is: "+winner);
		
			// Set the winner if propsals have been received, otherwise
			// cease sending CFP-messages (so the winner of the last round will
			// be the winner of the auction).
			if(winner != null)
			{
				winning_offer = cfp;
				running = false;
			}
			else
			{
				Object[] next = decideIteration(cfp_info, history.toArray());
				//System.out.println(getAgentName()+" next cfp: "+next);
				if(next==null)
				{
					// The initiator has decided to cancel the next round for some reason.
					running = false;
				}
				else
				{
					cfp = next[0];
					cfp_info = next[1];
					history.add(cfp);
				}
			}
		}	
			
		//System.out.println("END----------END---------END");
		
		// Evaluate the auction results and determine if a winner exists.
		evaluateAuctionResults(auctiondesc, cfp_info, history.toArray(), 
			winner, winning_offer);
		
		// Announce the auction end to all (still involved) participants.
		announceAuctionEnd(receivers, convid, winning_offer, winner);
	}
	
	/**
	 *  Announce the planned auction.
	 *  @param auctiondesc the auction description.
	 *  @param receivers The receivers.
	 *  @param convid The conversation id.
	 */
	protected void	announceAuction(Object auctiondesc, List receivers, String convid)
	{
		// Send the inform_start_auction-message to all receivers.
		start = getEventbase().createMessageEvent("da_inform_start_auction");
		start.getParameterSet(SFipa.RECEIVERS).addValues(receivers.toArray());
		start.getParameter(SFipa.CONTENT).setValue(auctiondesc);
		start.getParameter(SFipa.CONVERSATION_ID).setValue(convid);
		getLogger().info(getComponentName() + ": inform_start_auction");
		getWaitqueue().addReply(start);
		
		sendMessage(start);
	}
	
	
	/** 
	 *  Wait for the auction start time.
	 *  @param auctiondesc The auction description.
	 *  @param receivers The receivers.
	 */
	protected void waitForAuctionStart(AuctionDescription auctiondesc, List receivers)
	{
		// The initiator of the interaction protocol shall wait until interested
		// agents are ready to participate.
		// If agents indicate that they do not wish to participate they are excluded
		// from the auction.
		
		long timetowait = auctiondesc.getStarttime()==0? 0: 
			auctiondesc.getStarttime() - getTime();

		//System.out.println(getAgentName()+" waiting for: "+timetowait);
		while(timetowait > 0)
		{
			IMessageEvent removebidder;
			try
			{
				removebidder = (IMessageEvent)waitForReply(start, timetowait);
			}
			catch(TimeoutException e)
			{
				break;
			}
			
			if(removebidder.getType().equals("da_not_understood"))
			{
				receivers.remove(removebidder.getParameter(SFipa.SENDER).getValue());
				getLogger().info("Removed "+((IComponentIdentifier)removebidder.getParameter(SFipa.SENDER).getValue()).getName() + ".");
			}
			else
			{
				getLogger().warning("Could not handle message of type "+removebidder.getType() 
					+" from "+((IComponentIdentifier)removebidder.getParameter(SFipa.SENDER).getValue()).getName()+".");
			}
			
			timetowait =  auctiondesc.getStarttime() - getTime();
		}
	}
	
	/**
	 *  Send cfps to all receivers.
	 *  @param cfp The cfp.
	 *  @param convid The conversation id.
	 *  @param receivers The receivers.
	 */
	protected void sendCFP(Object cfp, String convid, List receivers)
	{
		// Send CFP.
		IMessageEvent cfpm = getEventbase().createMessageEvent("da_cfp");
		cfpm.getParameterSet(SFipa.RECEIVERS).addValues(receivers.toArray());
		cfpm.getParameter(SFipa.CONTENT).setValue(cfp);
		cfpm.getParameter(SFipa.CONVERSATION_ID).setValue(convid);
		getLogger().info(getComponentName() + ": cfp(" + cfp + ")");
		sendMessage(cfpm);
	}
	
	/**
	 *  Decide about the next iteration.
	 *  @param cfp_info The cfp info.
	 *  @param history The history.
	 *  @return The new cfp and cfp_info as an object array.
	 */
	protected Object[] decideIteration(Object cfp_info, Object[] history)
	{
		Object[] ret = null;
		IGoal di = createGoal("da_decide_iteration");
		di.getParameter("cfp_info").setValue(cfp_info);
		di.getParameterSet("history").addValues(history);
		try
		{
			dispatchSubgoalAndWait(di);
			ret = new Object[2];
			ret[0] = di.getParameter("cfp").getValue();
			ret[1] = di.getParameter("cfp_info").getValue();
			
			getLogger().info(getComponentName() + "calculated new cfp: "+ret[0]);
		}
		catch(BDIFailureException e)
		{
			getLogger().fine("No further iteration: "+e);
			//e.printStackTrace();
		}
		return ret;
	}
	
	
	/**
	 *  Wait for proposals of participants.
	 *  @param cfp the cfp.
	 *  @param roundtimeout The round timeout.
	 *  @param receivers The receivers.
	 *  @return The message of the winner.
	 */
	protected IComponentIdentifier waitForProposals(Object cfp, long roundtimeout, List receivers)
	{
		IComponentIdentifier winner = null;
		
		// Perform a negotiation round as long as no winner could be determined.
		long roundstart = getTime();
		while(getTime() - roundstart < roundtimeout)
		{
			IMessageEvent tmp = null;
			try
			{
				tmp = (IMessageEvent)waitForReply(start, roundtimeout);
				if(tmp.getType().equals("da_propose"))
				{
					// Accept the first winner
					if(winner==null)
					{
						// Send the accept_proposal-message to the agent with the first proposal.
						sendMessage(getEventbase().createReply(tmp, "da_accept_proposal"));
						getLogger().info(getComponentName() + " found winner: "+tmp.getParameter(SFipa.SENDER).getValue());
												
						// Set the parameter "winner" to the identifier of the
						// winning agent.
						winner = (IComponentIdentifier)tmp.getParameter(SFipa.SENDER).getValue();
					}
					// Reject all other proposals
					else
					{
						// Send reject_proposal-message.
						sendMessage(getEventbase().createReply(tmp, "da_reject_proposal"));
						getLogger().info(getComponentName() + ": rejected proposal");
					}
				}
				else
				{
					// Remove agent from the list of receivers on any
					// other of message. So you can use e.g. a
					// not_understood_message to exit the auction
					receivers.remove(tmp.getParameter(SFipa.SENDER).getValue());
				}
			}
			catch(TimeoutException e)
			{
			}
		}
		return winner;
	}
	
	/**
	 *  Evaluate the auction results and decide about participation.
	 *  @param auctiondesc The auction description.
	 *  @param cfp_info The cfp info.
	 *  @param history The historz of cfps.
	 *  @param winner the winner.
	 *  @param winning_offer The winning offer.
	 */
	protected void evaluateAuctionResults(AuctionDescription auctiondesc, Object cfp_info, 
		Object[] history, IComponentIdentifier winner, Object winning_offer)
	{
		if(winner == null)
		{
			getLogger().info(getComponentName() + ": auction finished (no winner)");
		}
		else
		{
			getLogger().info(getComponentName() + ": auction finished (winner: " 
				+winner.getName() + " - winning offer: " + winning_offer + ")");
		
			getParameter("result").setValue(new Object[]{winner, winning_offer});
		}	
	}
	
	/**
	 *  Announce the end of the auction to all participants that did not leave the auction.
	 *  @param receivers The receivers.
	 *  @param convid The conversation id.
	 *  @param winning_offer The winning offer.
	 */
	protected void announceAuctionEnd(List receivers, String convid, Object winning_offer, IComponentIdentifier winner)
	{
		// Send the inform_end_auction-message.
		List losers = SCollection.createArrayList();
		losers.addAll(receivers);
		
		if(winner!=null)
		{
			IMessageEvent end = getEventbase().createMessageEvent("da_inform_end_auction");
			end.getParameter(SFipa.CONTENT).setValue(new Object[]{Boolean.TRUE,winning_offer});
			end.getParameterSet(SFipa.RECEIVERS).addValue(winner);
			end.getParameter(SFipa.CONVERSATION_ID).setValue(convid);
			sendMessage(end);
			// Remove the winner from list of losers to inform.
			losers.remove(winner);
		}
		if(losers.size()>0)
		{
			IMessageEvent end = getEventbase().createMessageEvent("da_inform_end_auction");
			end.getParameter(SFipa.CONTENT).setValue(new Object[]{Boolean.FALSE,winning_offer});
			end.getParameterSet(SFipa.RECEIVERS).addValues(losers.toArray());
			end.getParameter(SFipa.CONVERSATION_ID).setValue(convid);
			sendMessage(end);
		}

		getWaitqueue().removeReply(start);
	}
	
	//-------- AbstractInitiatorPlan template methods --------
	
	/**
	 *  Get the initial message.
	 */
	protected IMessageEvent getInitialMessage()
	{
		return start;
	}
}

