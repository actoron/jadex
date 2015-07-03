package jadex.bdi.examples.blackjack.player;

import jadex.bdi.examples.blackjack.Player;
import jadex.bdi.examples.blackjack.RequestJoin;
import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.fipa.Done;
import jadex.bridge.fipa.SFipa;

/**
 *  Find a dealer and join the game.
 */
public class PlayerJoinGamePlan extends Plan
{
	//-------- methods --------
	
	/**
	 *  First the player searches a dealer, then sends a join-request to this
	 *  dealer.
	 */
	public void body()
	{
		// Search for dealer.
		IComponentIdentifier	dealer	= (IComponentIdentifier)getBeliefbase().getBelief("dealer").getFact();

		Player me = (Player)getBeliefbase().getBelief("myself").getFact();

		// create the join-message
		IMessageEvent	msg	= createMessageEvent("request_join");
		msg.getParameterSet(SFipa.RECEIVERS).addValue(dealer);
		RequestJoin rj = new RequestJoin();
		rj.setPlayer(me);
		//msg.setContent("join:" + getAgentName() + ":" + me.getStrategyName() + ":" + me.getAccount() + ":" + Player.color2Hex(me.getColor()));
		msg.getParameter(SFipa.CONTENT).setValue(rj);

		getLogger().info("sending join-message");
		
		// send the join-message and wait for a response
		IMessageEvent	reply	= sendMessageAndWait(msg, 10000);

		// evaluate content of the reply-message
		Object content = reply.getParameter(SFipa.CONTENT).getValue();
		if(content instanceof Done)
		{
			getLogger().info("request was accepted, timeout is: " + content);
			getBeliefbase().getBelief("timeout").setFact(
				Integer.valueOf(((RequestJoin)((Done)content).getAction()).getTimeout()));
			getBeliefbase().getBelief("dealer").setFact(dealer);
		}
	}

	/**
	 *  Called when something went wrong (e.g. timeout).
	 */
	public void	failed()
	{
		// Remove dealer fact.
		getBeliefbase().getBelief("dealer").setFact(null);
	}
}
