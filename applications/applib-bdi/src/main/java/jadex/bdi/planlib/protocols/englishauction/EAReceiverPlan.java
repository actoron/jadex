package jadex.bdi.planlib.protocols.englishauction;

import java.util.List;

import jadex.bdi.planlib.protocols.AbstractReceiverPlan;
import jadex.bdi.planlib.protocols.AuctionDescription;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.impl.GoalFailureException;
import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.fipa.SFipa;
import jadex.commons.TimeoutException;
import jadex.commons.collection.SCollection;

/**
 *  This plan implements the receiver of the "FIPA Dutch Auction Interaction
 *  Protocol Specification" (XC00032 - Experimental).
 *  
 *  An English auction is one where bidders continuously can increase the current
 *  offer until no one is willing to increase any more.
 */
public class EAReceiverPlan extends AbstractReceiverPlan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		super.body();	// Hack???
		
		// Fetch the auction information.
		IMessageEvent me = (IMessageEvent)getParameter("message").getValue();
		
		// HACK!!! Problem, there is no reply message sent in this protocol. 
		// So that agent has no open active conversations.
		IMessageEvent	dummy	= createMessageEvent("ea_propose");	// Hack??? Need some conversation message to wait for
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
		
		// Have to use larger round timeout, because initiator will wait for timeout too before sending new cfp
		long buftimeout = auctiondesc.getRoundTimeout() * 2;
		long firsttimeout = auctiondesc.getStarttime()==0 || (auctiondesc.getStarttime()-getTime()<=0)
			? -1 : auctiondesc.getStarttime()-getTime()+buftimeout;
	
		List offers = SCollection.createArrayList();
		boolean running = true;
		Object winning_offer = null; // my winning offer
		Object auction_wo = null; // the winning offer of the auction
		int missing_cnt = 0;
		
		//System.out.println(getAgentName()+" timeout: "+timeout);
			
		while(participate && running)
		{
			try
			{
				//System.out.println(getAgentName()+" waiting for: "+(firsttimeout==-1? buftimeout: firsttimeout));
				getLogger().info(getComponentName()+" waiting for: "+(firsttimeout==-1? buftimeout: firsttimeout));
				IMessageEvent msg = (IMessageEvent)waitForReply(dummy, firsttimeout==-1? buftimeout: firsttimeout);
				getLogger().info(getComponentName()+" received msg: "+msg.getType());
				missing_cnt = 0; // Reset missing_cnt as auction continues
				firsttimeout=-1;
				//System.out.println(getAgentName()+" received msg: "+msg.getType());
				
				
				if(msg.getType().equals("ea_cfp"))
				{
					// If has sitting out last round and a new round has been
					// issued reset winning_offer and sitting_out and continue
					// the auction
						
					if(winning_offer!=null)
					{
						//System.out.println(getAgentName()+" sitting out: "+msg.getContent());
						getLogger().info(getComponentName()+" sitting out: "+msg.getParameter(SFipa.CONTENT).getValue());
						winning_offer = null;
					}
					else
					{
						handleCFP(msg, auctiondesc, auctioninfo, offers);
					}
				}
				else if(msg.getType().equals("ea_accept_proposal"))
				{
					winning_offer = msg.getParameter(SFipa.CONTENT).getValue();
					getLogger().info(getComponentName()+" is currently winner for "+winning_offer);
					//System.out.println(getAgentName()+" is currently winner for "+winning_offer);
				}
				else if(msg.getType().equals("ea_reject_proposal"))
				{
					winning_offer = null;
				}
				else if(msg.getType().equals("ea_inform_end_auction"))
				{
					// res contains [boolean won, Object winning_offer] 
					Object[] res = (Object[])msg.getParameter(SFipa.CONTENT).getValue();
					
					if(!((Boolean)res[0]).booleanValue())
						winning_offer = null;
					
					auction_wo = res[1];
					running = false;
				}
				else
				{
					getLogger().warning((getComponentName()+" could not understand: "+msg));
				}
			}
			catch(TimeoutException e)
			{
				getLogger().info(getComponentName()+" "+e.getMessage());
				// Exit when no offers are received any more (for 3 times).
				//System.out.println(getAgentName()+" missed cfp: "+missing_cnt);
				if(++missing_cnt==3)
				{
					getLogger().info(getComponentName()+" leaving auction due to 3 missed cfps");
					//System.out.println(getAgentName()+" leaving auction due to 3 missed cfps");
					running = false;
				}
			}
		}
		
		//System.out.println(getAgentName()+" end");
		getLogger().info(getComponentName()+" auction end");
			
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
			IGoal dp = getScope().getGoalbase().createGoal("ea_decide_participation");
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
	protected Object[] handleCFP(IMessageEvent cfp, AuctionDescription auctiondesc, Object auctioninfo, List offers)
	{
		//System.out.println(getAgentName()+" handleCFP: "+cfp);
	
		Object[] ret = new Object[2];
		ret[0] = Boolean.TRUE; // participate
		ret[1] = auctioninfo;
		
		// Instantiate make_proposal-goal with the offer of the received CFP.
		IGoal mp = createGoal("ea_make_proposal");
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
		catch(TimeoutException e)
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
		
			sendMessage(getEventbase().createReply(cfp, "ea_not_understood"));
			
			// Set participate to false.
			ret[0] = Boolean.FALSE;
		}
		else if(accept!=null && accept.booleanValue())
		{
			//System.out.println(getAgentName()+" sending proposal: "+offer);
			getLogger().info(getComponentName()+" sending proposal: "+offer);
			// Send propsal.
			sendMessage(getEventbase().createReply(cfp, "ea_propose"));
		}
		
		return ret;
	}
	
}
