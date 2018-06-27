package jadex.bdi.examples.blackjack.dealer;

import jadex.bdi.examples.blackjack.Card;
import jadex.bdi.examples.blackjack.CardSet;
import jadex.bdi.examples.blackjack.Dealer;
import jadex.bdi.examples.blackjack.GameStatistics;
import jadex.bdi.examples.blackjack.Player;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;
import jadex.commons.SUtil;


/**
 *  Play a game round (controls the plans responsible for single players).
 */
public class DealerGameRoundMasterPlan extends Plan
{
	//-------- methods --------
	
	/**
	 *  Plan body.
	 */
	public void body()
	{
		CardSet	cardset	= new CardSet();
		getBeliefbase().getBelief("cardset").setFact(cardset);
		Dealer	me	= (Dealer)getBeliefbase().getBelief("myself").getFact();
		me.setState(Dealer.STATE_GAME_STARTED);

		// Trigger plan for each player.
		Player[]	players	= (Player[])getBeliefbase().getBeliefSet("players").getFacts();
		IGoal[]	goals	= new IGoal[players.length];
		for(int i=0; i<players.length; i++)
		{
			players[i].setState(Player.STATE_GAME_STARTED);
			goals[i] = createGoal("play_with_player");
			goals[i].getParameter("player").setValue(players[i]);
//			getWaitqueue().addSubgoal(goals[i]);
			getWaitqueue().addGoalFinished(goals[i]);
			dispatchSubgoal(goals[i]);
			getLogger().info("Playing with player: "+players[i]);
		}

		// Draw cards for dealer.
		me.setState(Dealer.STATE_PLAYING);
		// Dealer has to draw more cards until it's deckvalue is > 16
		// this is a blackjack-rule
		boolean first = true;
		while(CardSet.calculateDeckValue(me.getCards()) <= 16)
		{
			// Wait for the dealer's turn.
			getLogger().info("Now waiting for dealer's turn.");
//			System.out.println("Now waiting for dealer's turn.");
			if(first)
			{
				waitForCondition("dealers_first_turn");//, timeout*10);	// Hmmm... use timeout???
				first = false;
			}
			else
			{
				waitForCondition("dealers_second_turn");//, timeout)
			}
			getLogger().info("Dealer's turn. Players: "+SUtil.arrayToString(players));

			// Wait until allowed to draw card (step-mode or delay).
			if(((Boolean)getBeliefbase().getBelief("singleStepMode").getFact()).booleanValue())
			{
				waitForInternalEvent("step");
			}
			else
			{
//				System.out.println("Stepdelay: "+getBeliefbase().getBelief("stepdelay").getFact());
				waitFor(1000*((Number)getBeliefbase().getBelief("stepdelay").getFact()).intValue());
			}
			
			// now go ahead, draw the card and update the beliefbase
			Card dealerCard = cardset.drawCard();
			me.addCard(dealerCard);
			getLogger().info("Dealer draws a new card, it's " + dealerCard + " deck-value=" + CardSet.calculateDeckValue(me.getCards()));
		}

		// Dealer result calculation.
		getLogger().info("Dealer finished drawing cards");
		int	newaccount	= me.getAccount();
		for(int i=0; i<players.length; i++)
		{
			if(!players[i].getState().equals(Player.STATE_IDLE))
			{
				newaccount	+= players[i].getBet() - players[i].getMoneyWon(me.getCards());
			}
		}
		me.setAccount(newaccount);
		me.setState(Dealer.STATE_FINISHED);

		// Wait until allowed to proceed (step-mode or delay).
		if(((Boolean)getBeliefbase().getBelief("singleStepMode").getFact()).booleanValue())
		{
			waitForInternalEvent("step");
		}
		else
		{
			waitFor(1000*((Number)getBeliefbase().getBelief("stepdelay").getFact()).intValue());
		}

		// Wait for player subgoals to finish.
		for(int i=0; i<players.length; i++)
		{
//			waitForSubgoal(goals[i]);
			waitForGoalFinished(goals[i]);
		}

		// Store history.
		GameStatistics stats = (GameStatistics)getBeliefbase().getBelief("statistics").getFact();
		if(stats!=null)
			stats.addGameRound(me, players);

		me.setState(Dealer.STATE_IDLE);

		// Restart delay now in player plans.
//		int restart = 1000*((Number)getBeliefbase().getBelief("restartdelay").getFact()).intValue();
////		System.out.println("Starting new game in: "+restart);
//		getLogger().info("Starting new game in: "+restart);
//		waitFor(restart);
	}

	/**
	 *  Something went wrong. Reset playing state.
	 */
	public void	failed()
	{
//		System.out.println("DealerGameRoundMasterPlan failed: "+this);
		Dealer	me	= (Dealer)getBeliefbase().getBelief("myself").getFact();
		getLogger().info("Dealer failure :"+getBeliefbase().getBelief("myself").getFact());
		me.setState(Dealer.STATE_IDLE);
	}
}
