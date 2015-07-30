package jadex.bdi.planlib.protocols.dutchauction;

import jadex.bdi.planlib.protocols.AbstractReceiverPlan;
import jadex.bdi.planlib.protocols.AuctionDescription;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.impl.GoalFailureException;
import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.fipa.SFipa;
import jadex.commons.collection.SCollection;
import jadex.commons.concurrent.TimeoutException;

import java.util.List;

/**
 *  This plan implements the receiver of the "FIPA Dutch Auction Interaction
 *  Protocol Specification" (XC00032 - Experimental).
 *  
 *  A dutch auction is one where the auctioneer starts with a high start price
 *  and continually lowers it until the first bidder accepts the price.
 */
public class DAReceiverPlan extends AbstractReceiverPlan
{
	/**
	 * The plan body.
	 */
	public void body()
	{
		super.body();	// Hack???
		
		// Fetch the auction information.
		IMessageEvent me = (IMessageEvent)getParameter("message").getValue();
		
		// HACK!!! Problem, there is no reply message sent in this protocol. 
		// So that agent has no open active conversations.
		IMessageEvent	dummy	= createMessageEvent("da_propose");	// Hack??? Need some conversation message to wait for
		dummy.getParameter(SFipa.CONVERSATION_ID).setValue(me.getParameter(SFipa.CONVERSATION_ID).getValue());
		getWaitqueue().addReply(dummy);
		
		AuctionDescription auctiondesc = (AuctionDescription)me.getParameter(SFipa.CONTENT).getValue();
		getLogger().info(getComponentName()+": Received inform_start_auction message with auction description " +
			"start time: "+auctiondesc.getStarttime()+" Round time "+auctiondesc.getRoundTimeout()
			+" topic: "+auctiondesc.getTopic());
		if(auctiondesc.getRoundTimeout()<=0)
		{
			getLogger().warning(getComponentName()+"No round timeout specified");
			fail();
		}
		
		// Offer the possibility to decide not to participate in the auction
		Object[] tmp = decideParticipation(auctiondesc, (IComponentIdentifier)me.getParameter(SFipa.SENDER).getValue());
		boolean participate = ((Boolean)tmp[0]).booleanValue();
		Object auctioninfo = tmp[1];

		long buftimeout = (long)(auctiondesc.getRoundTimeout()*1.1);
		long firsttimeout = auctiondesc.getStarttime()==0 || (auctiondesc.getStarttime()-getTime()<=0)
			? -1 : auctiondesc.getStarttime()-getTime()+buftimeout;
	
		List offers = SCollection.createArrayList();
		boolean running = true;
		Object winning_offer = null; // my winning offer
		Object auction_wo = null; // the winning offer of the auction
		int missing_cnt = 0;
		
		while(participate && running)
		{
			try
			{
				getLogger().info(getComponentName()+" waiting for: "+(firsttimeout==-1? buftimeout: firsttimeout));
				if(getRPlan().getWaitqueue()!=null)
				{
					System.out.println("seguip");
				}
				IMessageEvent msg = (IMessageEvent)waitForReply(dummy, firsttimeout==-1? buftimeout: firsttimeout);
				getLogger().info(getComponentName()+" received cfp: "+msg.getParameter(SFipa.CONTENT).getValue());
				missing_cnt = 0; // Reset missing_cnt as auction continues
				firsttimeout=-1;
				
				if(msg.getType().equals("da_cfp"))
				{
					handleCFP(msg, auctiondesc, auctioninfo, offers);
				}
				else if(msg.getType().equals("da_accept_proposal"))
				{
					winning_offer = msg.getParameter(SFipa.CONTENT).getValue();
					running = false;
				}
				else if(msg.getType().equals("da_reject_proposal"))
				{
					winning_offer = null;
				}
				else if(msg.getType().equals("da_inform_end_auction"))
				{
					Object[] res = (Object[])msg.getParameter(SFipa.CONTENT).getValue();
					
					if(!((Boolean)res[0]).booleanValue())
						winning_offer = null;
					
					auction_wo = res[1];
					running = false;
				}
				else
				{
					getLogger().warning("Could not understand: "+msg+" "+msg.getType());
				}
			}
			catch(TimeoutException e)
			{
				getLogger().info(getComponentName()+" "+e.getMessage());
				// Exit when no offers are received any more (for 3 times).
				//System.out.println(getAgentName()+" missed cfp: "+missing_cnt);
				if(++missing_cnt==3)
					running = false; 
			}
		}
		
		if(!running)
			getParameter("result").setValue(new Object[]{winning_offer, auction_wo});
		
		getWaitqueue().removeReply(dummy);
	}
	
	/**
	 *  Decide about participation.
	 *  If the goal is not handled participation is true.
	 *  @param auctiondesc The auction description.
	 *  @return The participation state (Boolean) and the local auction info (Object).
	 */
	protected Object[] decideParticipation(AuctionDescription auctiondesc, IComponentIdentifier initiator)
	{
		Object[] ret = new Object[2];
		ret[0] = Boolean.TRUE; // participate
		ret[1] = null; // auction info
		
		try
		{
			IGoal dp = getScope().getGoalbase().createGoal("da_decide_participation");
			dp.getParameter("auction_description").setValue(auctiondesc);
			dp.getParameter("initiator").setValue(initiator);
			dispatchSubgoalAndWait(dp);
			ret[1] = dp.getParameter("auction_info").getValue();
			Boolean part = (Boolean)dp.getParameter("participate").getValue();
			ret[0] = part==null? Boolean.TRUE: part;
		}
		catch(GoalFailureException e)
		{
			// Participate if no explicit decision was made.
			getLogger().info("Optional goal ea_decide_request has not been handled.");
		}
		
		return ret;
	}
	
	/**
	 *  Handle a cfp message.
	 *  @param auctiondesc The auction description.
	 *  @return The participation state (Boolean) and the local auction info (Object).
	 */
	protected Object[] handleCFP(IMessageEvent cfp, AuctionDescription auctiondesc, 
			Object auctioninfo, List offers)
	{
		//System.out.println("cfp: "+cfp.getContent());
	
		Object[] ret = new Object[2];
		ret[0] = Boolean.TRUE; // participate
		ret[1] = auctioninfo;
		
		// Instantiate make_proposal-goal with the offer of the received CFP.
		IGoal mp = createGoal("da_make_proposal");
		Object offer = cfp.getParameter(SFipa.CONTENT).getValue();
		offers.add(offer);
		mp.getParameter("cfp").setValue(offer);
		mp.getParameter("auction_description").setValue(auctiondesc);
		mp.getParameter("auction_info").setValue(auctioninfo);
		mp.getParameterSet("history").addValues(offers.toArray());
		
		try
		{
			dispatchSubgoalAndWait(mp, auctiondesc.getRoundTimeout());
		}
		catch(Exception e)
		{
			getLogger().info(getComponentName() + e.getMessage());
		}
		ret[1] = mp.getParameter("auction_info").getValue();
		Boolean leave = (Boolean)mp.getParameter("leave").getValue();
		Boolean accept = (Boolean)mp.getParameter("accept").getValue();
		if(leave!=null && leave.booleanValue())
		{
			getLogger().info(getComponentName() + " informs the initiator of the auction "
				+auctiondesc.getTopic()+" that it doesn't want to participate.");
		
			sendMessage(getEventbase().createReply(cfp, "da_not_understood"));
			
			ret[0] = Boolean.FALSE;
		}
		else if(accept!=null && accept.booleanValue())
		{
			// System.out.println(getAgentName()+" sending proposal: "+offer);
			// Send propsal.
			sendMessage(getEventbase().createReply(cfp, "da_propose"));
			getLogger().info(getComponentName()+" accepted proposal: "+cfp.getParameter(SFipa.CONTENT).getValue());
		}
		else
		{
			getLogger().info(getComponentName()+" does not accept proposal and waits: "+cfp.getParameter(SFipa.CONTENT).getValue());
		}
		
		return ret;
	}
}
