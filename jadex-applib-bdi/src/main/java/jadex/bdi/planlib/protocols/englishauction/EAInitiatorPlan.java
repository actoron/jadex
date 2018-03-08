package jadex.bdi.planlib.protocols.englishauction;

import java.util.Date;
import java.util.List;

import jadex.bdi.planlib.protocols.AbstractInitiatorPlan;
import jadex.bdi.planlib.protocols.AuctionDescription;
import jadex.bdiv3.runtime.BDIFailureException;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.fipa.SFipa;
import jadex.commons.SUtil;
import jadex.commons.TimeoutException;
import jadex.commons.collection.SCollection;

/**
 *  This plan implements the initiator of the "FIPA English Auction Interaction
 *  Protocol Specification" (XC00031 - Experimental).
 *  
 *  An English auction is one where bidders continously can increase the current
 *  offer until no one is willing to increase any more.
 */
public class EAInitiatorPlan extends AbstractInitiatorPlan
{
	//-------- attributes --------
	
	/** The initial message. */
	// Hack!!! Needed for cancel-meta-protocol and for avoiding garbage collection.
	protected IMessageEvent	start;
	
	//-------- methods --------
	
	/**
	 *  The plan body.
	 */
	public void body()
	{
		super.body();	// Hack???
		
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
		
		while(running && receivers.size()>0)
		{
			//System.out.println(getAgentName()+" current offer is "+cfp+" "+receivers.size());
			//getLogger().fine(getAgentName()+" current offer is "+cfp+" "+receivers.size());
					
			// Send CFP.
			sendCFP(cfp, convid, receivers);

			// Wait for proposals.
			// Removes receivers that do not offer.
			IComponentIdentifier first = null;
			first = waitForProposals(cfp, roundtimeout, receivers);
			//System.out.println("first: "+first);
			
			// Set the winner if propsals have been received, otherwise
			// cease sending CFP-messages (so the winner of the last round will
			// be the winner of the auction).
			if(first != null)
			{
				winner = first;
				winning_offer = cfp;
				
				Object[] next = decideIteration(cfp_info, history.toArray());
				//System.out.println("next cfp: "+SUtil.arrayToString(next));
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
			else
			{
				// End when no proposals have been received.
				running = false;
			}
		}
		
		//System.out.println("END----------END---------END");
		
		// Evaluate the auction results and determine if a winner exists.
		evaluateAuctionResults(auctiondesc, cfp_info, history.toArray(), 
			winner, winning_offer);
		
		// Announce the auction end to all (still involved) participants.
		announceAuctionEnd(receivers, convid, winning_offer, winner);		
	}
	
	@Override
	public void passed()
	{
		if(start!=null)
		{
			getWaitqueue().removeReply(start);
		}
		super.passed();
	}
	
	@Override
	public void failed()
	{
		if(start!=null)
		{
			getWaitqueue().removeReply(start);
		}
		super.failed();
	}
	
	@Override
	public void aborted()
	{
		if(start!=null)
		{
			getWaitqueue().removeReply(start);
		}
		super.aborted();
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
		start = getEventbase().createMessageEvent("ea_inform_start_auction");
		start.getParameterSet(SFipa.RECEIVERS).addValues(receivers.toArray());
		start.getParameter(SFipa.CONTENT).setValue(auctiondesc);
		start.getParameter(SFipa.CONVERSATION_ID).setValue(convid);
		getLogger().info(getComponentName() + ":\tinform_start_auction");
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
			
			if(removebidder.getType().equals("ea_not_understood"))
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
		IMessageEvent cfpm = getEventbase().createMessageEvent("ea_cfp");
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
		IGoal di = createGoal("ea_decide_iteration");
		di.getParameter("cfp_info").setValue(cfp_info);
		di.getParameterSet("history").addValues(history);
		try
		{
			dispatchSubgoalAndWait(di);
			ret = new Object[2];
			ret[0] = di.getParameter("cfp").getValue();
			ret[1] = di.getParameter("cfp_info").getValue();
		}
		//catch(Throwable e)
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
		IComponentIdentifier first_proposal = null;
	
		//The first proposal is accepted, so wait until first the proposal is received.
		long roundstart = getTime();
		getLogger().info(getComponentName()+" Waiting for proposals at "+new Date(roundstart));
		try
		{
			long	elapsed	= getTime() - roundstart;
			while(elapsed < roundtimeout)
			{
				//System.out.println(getAgentName()+" waiting for accepts for: "+offer);
				getLogger().info(getComponentName()+" Waiting for "+(roundtimeout - elapsed)+" ms");
				IMessageEvent tmp = (IMessageEvent)waitForReply(start, roundtimeout - elapsed );
				
				if(tmp.getType().equals("ea_propose"))
				{
					if(first_proposal==null)
					{
						getLogger().info(getComponentName()+" got first accept for: "
							+cfp+" from: "+tmp.getParameter(SFipa.SENDER).getValue());
						// Send the accept_proposal-message to the agent with the first proposal.
						IMessageEvent accept = getEventbase().createReply(tmp, "ea_accept_proposal");
						//accept.getParameter(SFipa.CONVERSATION_ID).setValue(convid);
						accept.getParameter(SFipa.CONTENT).setValue(cfp);
						sendMessage(accept);
						first_proposal = (IComponentIdentifier)tmp.getParameter(SFipa.SENDER).getValue();
					}
					else
					{
						getLogger().info(getComponentName()+" got too late accept for: "
							+cfp+" from: "+tmp.getParameter(SFipa.SENDER).getValue());
						// Send reject_proposal-message.
						IMessageEvent reject = getEventbase().createReply(tmp, "ea_reject_proposal");
						//reject.getParameter(SFipa.CONVERSATION_ID).setValue(convid);
						sendMessage(reject);
					}
				}
				else
				{
					// Remove agent from the list of receivers on any other of
					// message. So you can use e.g. a not_understood_message to exit the auction.
					getLogger().info(getComponentName()+" removing agent "
						+tmp.getParameter(SFipa.SENDER).getValue());
					receivers.remove(tmp.getParameter(SFipa.SENDER).getValue());
				}

				elapsed	= getTime() - roundstart;
				getLogger().info(getComponentName()+" elapsed: "+elapsed+" ms");
			}
		}
		catch(TimeoutException e)
		{
			getLogger().info("Timeout received");
		}

		getLogger().info("No further bids in this round");

		return first_proposal;
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
		boolean accept = winner!=null;
		
		if(accept)
		{
			Comparable limit = (Comparable)getParameter("limit").getValue();
			
			if(limit !=null)
			{
				if(limit.compareTo(winning_offer)>0)
				{
					//System.out.println("Offer below limit, no winner: "+limit+" "+winning_offer);
					getLogger().info("Offer below limit, no winner: "+limit+" "+winning_offer);
					accept = false;
				}
			}
			else
			{
				try
				{
					IGoal da = createGoal("ea_decide_acceptance");
					da.getParameter("auction_description").setValue(auctiondesc);
					da.getParameter("cfp").setValue(winning_offer);
					da.getParameter("cfp_info").setValue(cfp_info);
					da.getParameter("winner").setValue(winner);
					da.getParameterSet("history").addValues(history);
					dispatchSubgoalAndWait(da);
					accept = ((Boolean)da.getParameter("accept").getValue()).booleanValue();
				}
				catch(BDIFailureException e)
				{
					getLogger().info("Decide acceptance goal not handled: "+winning_offer+" "+winner);
					//System.out.println("Decide acceptance goal not handled: "+winning_offer+" "+winner);
					//e.printStackTrace();
				}	
			}
		}
		
		if(accept)
		{
			//IComponentIdentifier wina = (IComponentIdentifier)winner.getParameter(SFipa.SENDER).getValue();
			getLogger().info(getComponentName() + ": auction finished (winner: "
				+winner.getName()+" - price: "+winning_offer+")");
			
			getParameter("result").setValue(new Object[]{winner, winning_offer});
		}
		else
		{ 	
			getLogger().info(getComponentName()+ ": auction finished "+
				"(no winner - initiator didn't receive any proposals)");
		}
	}
	
	/**
	 *  Announce the end of the auction to all participants that did not leave the auction.
	 *  @param receivers The receivers.
	 *  @param convid The conversation id.
	 *  @param winning_offer The winning offer.
	 *  @param winner The winner.
	 */
	protected void announceAuctionEnd(List receivers, String convid, Object winning_offer, IComponentIdentifier winner)
	{
		// Send the inform_end_auction-message.
		List losers = SCollection.createArrayList();
		losers.addAll(receivers);
		
		if(winner!=null)
		{
			IMessageEvent end = getEventbase().createMessageEvent("ea_inform_end_auction");
			end.getParameter(SFipa.CONTENT).setValue(new Object[]{Boolean.TRUE,winning_offer});
			end.getParameterSet(SFipa.RECEIVERS).addValue(winner);
			end.getParameter(SFipa.CONVERSATION_ID).setValue(convid);
			sendMessage(end);
			// Remove the winner from list of losers to inform.
			losers.remove(winner);
		}
		if(losers.size()>0)
		{
			IMessageEvent end = getEventbase().createMessageEvent("ea_inform_end_auction");
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
