package jadex.bdi.examples.blackjack.player;

import jadex.bdi.examples.blackjack.Card;
import jadex.bdi.examples.blackjack.CardSet;
import jadex.bdi.examples.blackjack.GameResult;
import jadex.bdi.examples.blackjack.GameState;
import jadex.bdi.examples.blackjack.Player;
import jadex.bdi.examples.blackjack.RequestBet;
import jadex.bdi.examples.blackjack.RequestDraw;
import jadex.bdi.examples.blackjack.RequestFinished;
import jadex.bdi.examples.blackjack.player.HumanPlayerInterface.HumanPlayerControlPanel;
import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.fipa.Done;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.annotation.Timeout;
import jadex.commons.TimeoutException;
import jadex.commons.gui.GuiCreator;

/**
 *  This plan is executed for every game round.
 */
public class PlayerPlayGameRoundPlan extends Plan
{
	//-------- attributes --------

	/** The human player interface. */
	HumanPlayerInterface hpi = null;

	//-------- attributes --------
	
	/**
	 *  Execute a plan.
	 */
	public void body()
	{
//		getLogger().info("created: " + this);
		if(getBeliefbase().containsBelief("gui"))
		{
			Object	gui	= getBeliefbase().getBelief("gui").getFact();
			if(gui instanceof GuiCreator)
			{
				gui	= ((GuiCreator)gui).getGui();
				getBeliefbase().getBelief("gui").setFact(gui);	// Hack!!!
				// Must replace belief because it uses property change events on the humaninterface gui!
			}
			hpi = (HumanPlayerInterface)gui;
		}
		
		IMessageEvent	querybet	= (IMessageEvent)getReason();
		RequestBet rb = (RequestBet)querybet.getParameter(SFipa.CONTENT).getValue();
		Player me = (Player)getBeliefbase().getBelief("myself").getFact();
		me.setState(Player.STATE_GAME_STARTED);
		long	timeout	= ((Number)getBeliefbase().getBelief("timeout").getFact()).longValue();

		int mybet = determineBet();
		//me.setAccount(me.getAccount()-mybet);
		me.makeBet(mybet);
		getLogger().info("new account-status=" + me.getAccount() + ", myBet=" + mybet);

		// Reply to dealer.
		IMessageEvent	msg	= getEventbase().createReply(querybet, "inform_bet");
		rb.setBet(mybet);
		msg.getParameter(SFipa.CONTENT).setValue(new Done(rb));
		getLogger().info("sending bet to the dealer ... waiting for cardSet");
		sendMessage(msg);
		me.setState(Player.STATE_PLAYING);

		waitForCondition("start_playing");
		getLogger().info("started playing");
		Card dealercard = ((GameState)getBeliefbase().getBelief("gamestate").getFact()).getDealer().getCards()[0];

		// Now draw as many cards as the strategy suggests.
		while(CardSet.calculateDeckValue(me.getCards())<21 && shouldDrawCard(dealercard))
		{
			// draw one more card
			getLogger().info("player decided to draw one more card");
			RequestDraw rd = new RequestDraw();
			IMessageEvent draw_request = getEventbase().createReply(querybet, "request_draw");
			draw_request.getParameter(SFipa.CONTENT).setValue(rd);	
			// Hack!!! Use large timeout because other players might also draw cards.
			IMessageEvent ans = sendMessageAndWait(draw_request, timeout!=Timeout.NONE?timeout*10:timeout);
			Card[] cards = ((RequestDraw)((Done)ans.getParameter(SFipa.CONTENT).getValue()).getAction()).getCards();
			me.setCards(cards);
		}

		// this player finished
		getLogger().info("player decided not to draw more cards");
		me.setState(Player.STATE_FINISHED);
		RequestFinished rf = new RequestFinished();
		IMessageEvent finished	= getEventbase().createReply(querybet, "request_finished");
		finished.getParameter(SFipa.CONTENT).setValue(rf);
		// Hack!!! Use large timeout because other players might still draw cards.
//		System.out.println("Sent: "+finished.getParameter(SFipa.CONTENT).getValue());
		IMessageEvent resultmsg	= sendMessageAndWait(finished, timeout!=Timeout.NONE?timeout*10:timeout);
//		System.out.println("Rec: "+resultmsg.getParameter(SFipa.CONTENT).getValue());

		// When player has won the game, increment account.
		GameResult gr = ((RequestFinished)((Done)resultmsg.getParameter(SFipa.CONTENT).getValue()).getAction()).getGameresult();
		if(gr.isWon())
		{
			// Update the account-status
			me.setAccount(me.getAccount()+gr.getMoney());
			getLogger().info("I won " + gr.getMoney());
		}
		else
		{
			// nothing more to do if the player lost
			getLogger().info("I lost :-(");
		}

		// Reset state.
		me.setState(Player.STATE_IDLE);
	}

	/**
	 *
	 */
	public int determineBet()
	{
		Player me = (Player)getBeliefbase().getBelief("myself").getFact();
		int mybet = 10;
		if(hpi!=null)
		{
			((HumanPlayerControlPanel)hpi.getControlPanel()).enableBid();
			try
			{
				waitForFactChanged("gui", 5000);
				//System.out.println("Player bets: "+hpi.getBet());
			}
			catch(TimeoutException e)
			{
				//System.out.println("No bet made, using strategy.");
				//mybet = me.getStrategy().makeBet(me.getAccount());
			}
			mybet = hpi.getBet();
			((HumanPlayerControlPanel)hpi.getControlPanel()).disableBid();
		}
		else
		{
			// apply betting-strategy
			mybet = me.getStrategy().makeBet(me.getAccount());
		}
		return mybet;
	}

	/**
	 *
	 * @param dealercard
	 * @return True, if another card should be drawn.
	 */
	public boolean shouldDrawCard(Card dealercard)
	{
		boolean draw = false;
		Player me = (Player)getBeliefbase().getBelief("myself").getFact();
		if(hpi!=null)
		{
			((HumanPlayerControlPanel)hpi.getControlPanel()).enableDrawCard();
			try
			{
				waitForFactChanged("gui", 5000);
				//System.out.println("Player wants to: "+hpi.isDrawCard());
				draw = hpi.isDrawCard();
			}
			catch(TimeoutException e)
			{
				//System.out.println("No draw card decision made, using strategy.");
				//draw = me.getStrategy().drawCard(me.getCards(), dealercard);
			}
			((HumanPlayerControlPanel)hpi.getControlPanel()).disableDrawCard();
		}
		else
		{
			draw = me.getStrategy().drawCard(me.getCards(), dealercard);
		}
		return draw;
	}

	/**
	 *  Called when something went wrong (e.g. timeout).
	 */
	public void	failed()
	{
		getException().printStackTrace();
		
		// Remove dealer fact.
		getBeliefbase().getBelief("dealer").setFact(null);

		// Reset state.
		Player me = (Player)getBeliefbase().getBelief("myself").getFact();
		me.setState(Player.STATE_IDLE);
	}
}
