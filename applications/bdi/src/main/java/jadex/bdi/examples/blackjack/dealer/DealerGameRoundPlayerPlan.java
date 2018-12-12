package jadex.bdi.examples.blackjack.dealer;

import java.util.Collections;

import jadex.bdi.examples.blackjack.Card;
import jadex.bdi.examples.blackjack.CardSet;
import jadex.bdi.examples.blackjack.Dealer;
import jadex.bdi.examples.blackjack.GameResult;
import jadex.bdi.examples.blackjack.Player;
import jadex.bdi.examples.blackjack.RequestBet;
import jadex.bdi.examples.blackjack.RequestDraw;
import jadex.bdi.examples.blackjack.RequestFinished;
import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.fipa.Done;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.annotation.Timeout;

/**
 *  Play a game round with the given player.
 */
public class DealerGameRoundPlayerPlan extends Plan
{
	//-------- attributes --------

	/** The timout for communication with the player. */
	protected long	timeout;

	/** The player object. */
	protected Player	player;

	//-------- methods --------
	
	/**
	 *  Plan body.
	 */
	public void body()
	{
		this.timeout = ((Number)getBeliefbase().getBelief("playerwaitmillis").getFact()).longValue();
		this.player = (Player)getParameter("player").getValue();
		
		// Ask player for her bet.
		getLogger().info("Asking for bet from player: "+player);
		
		RequestBet rb = new RequestBet();
		IMessageEvent msg = createMessageEvent("request_bet");
		msg.getParameter(SFipa.CONTENT).setValue(rb);
		msg.getParameterSet(SFipa.RECEIVERS).addValue(player.getAgentID());
		getWaitqueue().addReply(msg);
		IMessageEvent betmsg = sendMessageAndWait(msg, timeout);

		// When player does not provide a bet (e.g. declines to play), end plan.
		Object content = betmsg.getParameter(SFipa.CONTENT).getValue();
		getLogger().info("Received bet from player: "+player+", "+content);
		if(!(content instanceof Done))
			fail();

		// Extract bet and update player state.
		int bet	= ((RequestBet)((Done)content).getAction()).getBet();
		if(bet>player.getAccount())
		{
			startAtomic();
			player.setState(Player.STATE_UNREGISTERED);
			getBeliefbase().getBeliefSet("players").removeFact(player);
			endAtomic();
			//fail(); // Not necessary because context becomes invalid
		}
		player.makeBet(bet);
		player.setState(Player.STATE_PLAYING);

		// Give two cards to player.
		drawCard();
		drawCard();

//		MessageEventFilter fil = new MessageEventFilter(null);
//		fil.addValue(SFipa.CONVERSATION_ID, betmsg.getParameter(SFipa.CONVERSATION_ID).getValue());
//		getWaitqueue().addFilter(fil);

//		getWaitqueue().addReply(msg);
		
		// Wait for dealer to draw a card for itself.
		getLogger().info("Waiting for dealer card: "+player);
		waitForCondition("dealer_card");//, timeout*10);	// Hmmm... use timeout???

		// Inform player about dealer card and wait for answer.
		getLogger().info("Informing player about dealer card: "+player);
		Dealer	me	= (Dealer)getBeliefbase().getBelief("myself").getFact();

		IMessageEvent answer = (IMessageEvent)waitForReply(msg);//, timeout);
		getWaitqueue().removeReply(msg);
		
//		IMessageEvent answer = (IMessageEvent)waitFor(fil, timeout);
//		getWaitqueue().removeFilter(fil);

		// Give cards to player as long she wants one.
		content = answer.getParameter(SFipa.CONTENT).getValue();
		while(content instanceof RequestDraw)
		{
			drawCard();
			RequestDraw rd = (RequestDraw)content;
			rd.setCards(player.getCards());
			Done done = new Done(rd);
			IMessageEvent mdone = getEventbase().createReply(answer, "inform_action_done");
			mdone.getParameter(SFipa.CONTENT).setValue(done);
			
			answer = sendMessageAndWait(mdone, timeout);
			content = answer.getParameter(SFipa.CONTENT).getValue();
			//correctGameState();
			getLogger().info("Player wants to draw a card: "+player);
		}
		getLogger().info("Player is finished: "+player);
		player.setState(Player.STATE_FINISHED);

		// Wait for dealer to finish, too.
		getLogger().info("Waiting for dealer to finish: "+player);
		waitForCondition("dealer_finished", timeout!=Timeout.NONE?timeout*10:timeout);	// Hmmm... use timeout???

		// Check if the player won or lost and inform player about result.
		GameResult gr = new GameResult();
		int	moneywon	= player.getMoneyWon(me.getCards());
		if(moneywon > 0) // player won
		{
			gr.setWon(true);
			gr.setMoney(moneywon);
			player.setAccount(player.getAccount() + moneywon);
		}
		else
		{
			gr.setWon(false);
		}
		RequestFinished rf = (RequestFinished)answer.getParameter(SFipa.CONTENT).getValue();
		rf.setGameresult(gr);
		Done done = new Done(rf);
		IMessageEvent result = getEventbase().createReply(answer, "inform_action_done");
		result.getParameter(SFipa.CONTENT).setValue(done);

		sendMessage(result);
		getLogger().info("Player result" + player + "-" + result.getParameter(SFipa.CONTENT).getValue());

		// Wait until allowed to proceed (step-mode or delay).
		if(((Boolean)getBeliefbase().getBelief("singleStepMode").getFact()).booleanValue())
		{
			waitForInternalEvent("step");
		}
		else
		{
//			waitFor(1000*((Number)getBeliefbase().getBelief("stepdelay").getFact()).intValue());
			int restart = 1000*((Number)getBeliefbase().getBelief("restartdelay").getFact()).intValue();
			waitFor(restart);
		}
	}

	/**
	 *  Something went wrong. Remove player from beliefs.
	 */
	public void	failed()
	{
		//System.out.println("player failed :-( "+getName());
		getLogger().info("Player failure :"+player);
		player.setState(Player.STATE_UNREGISTERED);
		getBeliefbase().getBeliefSet("players").removeFact(player);
	}

	/**
	 *  Game complete, reset player.
	 */
	public void	passed()
	{
		getLogger().info("Game completed :"+player);
		player.setState(Player.STATE_IDLE);
	}
	
	/**
	 *  Aborted, reset player.
	 */
	public void	aborted()
	{
		getLogger().info("Game aborted :"+player);
		player.setState(Player.STATE_IDLE);
	}

	//-------- helper methods --------

	/**
	 *  Draw a card for the player.
	 */
	protected void	drawCard()
	{
		// Wait until it is the players turn.
//		getLogger().info("Waiting for players turn: "+player);
		
//		ICondition	turn	= getCondition("players_turn");
//		turn.setParameter("$player", player);
		
//		System.out.println("Waiting for players turn: "+player);
		waitForCondition("players_turn", timeout!=Timeout.NONE?timeout*10:timeout,	// Hmmm... use timeout???
			Collections.singletonMap("$player", (Object)player));
//		System.out.println("Its players turn: "+player);

		// Wait until allowed to draw card (step-mode or delay).
		if(((Boolean)getBeliefbase().getBelief("singleStepMode").getFact()).booleanValue())
		{
			waitForInternalEvent("step");
		}
		else
		{
			waitFor(1000*((Number)getBeliefbase().getBelief("stepdelay").getFact()).intValue());
		}
		
		// Draw card for player.
		CardSet	cardset	= (CardSet)getBeliefbase().getBelief("cardset").getFact();
		Card	card	= cardset.drawCard();
		player.addCard(card);
	}
}
