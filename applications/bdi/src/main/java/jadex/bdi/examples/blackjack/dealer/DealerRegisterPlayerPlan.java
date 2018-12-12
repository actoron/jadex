package jadex.bdi.examples.blackjack.dealer;

import jadex.bdi.examples.blackjack.Player;
import jadex.bdi.examples.blackjack.RequestJoin;
import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.fipa.Done;
import jadex.bridge.fipa.SFipa;

/**
 *  Plan to handle join request of a player.
 */
public class DealerRegisterPlayerPlan extends Plan
{
	/**
	 *  Plan body.
	 */
	public void body()
	{
		IMessageEvent	request	= (IMessageEvent)getReason();

		// the player should have sent its name, account-status and its strategy-name
		// the message-content should look like this: 'join:name:strategy:account:color'
		RequestJoin rj = (RequestJoin)request.getParameter(SFipa.CONTENT).getValue();
		Player player = rj.getPlayer();
		player.setAgentID((IComponentIdentifier)request.getParameter("sender").getValue());
		getLogger().info("New player "+player);
		if(!getBeliefbase().getBeliefSet("players").containsFact(player))
		{
			getBeliefbase().getBeliefSet("players").addFact(player);
			player.setState(Player.STATE_IDLE);
		}
		else
		{
			// Reset entry.
			Player[] players = (Player[])getBeliefbase().getBeliefSet("players").getFacts();
			for(int i=0; i<players.length; i++)
			{
				if(players[i].equals(player))
					players[i].setState(Player.STATE_IDLE);
			}
//			Player	old	= (Player)getBeliefbase().getBeliefSet("players").getFact(player);
//			old.setState(Player.STATE_IDLE);
		}

		// set FIPA-performative for the message sent back to the player
		// the content of the answer is just the games standard timeout.
		rj.setTimeout(((Long)getBeliefbase().getBelief("playerwaitmillis").getFact()).intValue());
		Done done = new Done(rj);
//		sendMessage(request.createReply("inform_action_done", done));
		IMessageEvent ans = getEventbase().createReply(request, "inform_action_done");
		ans.getParameter(SFipa.CONTENT).setValue(done);
		sendMessage(ans);
	}
}
