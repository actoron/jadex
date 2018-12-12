package jadex.bdi.examples.blackjack.player.strategies;

import jadex.bdi.examples.blackjack.Card;
import jadex.bdi.examples.blackjack.CardSet;

/**
 *  The stochastic table strategy.
 */
public class StochasticTable extends AbstractStrategy
{
	//-------- constructors --------

	/**
	 *  Create a new strategy.
	 *  @param name The name.
	 */
	public StochasticTable(String name)
	{
		super(name);
	}

	//-------- methods --------

	/**
	 * Calculate how much to bet, given the account value.
	 * @param account
	 * @return The bet.
	 */
	public int makeBet(int account)
	{
		return 10;
	}

	/**
	 * Decide if to draw another card.
	 * @param playercards
	 * @param dealercard
	 * @return True, if the player wants to draw a card.
	 */
	public boolean drawCard(Card[] playercards, Card dealercard)
	{
		boolean playerHasAce = false;
		boolean drawCard = false;

		int playerCardsValue = CardSet.calculateDeckValue(playercards);
		int dealerCardValue = CardSet.calculateDeckValue(new Card[]{dealercard});

		for(int i = 0; i<playercards.length; i++)
		{
			if(playercards[i].getType().equals(CardSet.ACE))
				playerHasAce = true;
		}

		if(playerHasAce)
		{
			if(playerCardsValue<=17)
				drawCard = true;
			else if((playerCardsValue==18) && (dealerCardValue>8))
				drawCard = true;
		}
		else
		{
			if(playerCardsValue<12)
				drawCard = true;
			else if((playerCardsValue==12) &&
					((dealerCardValue<4) || (dealerCardValue>6)))
				drawCard = true;
			else if((playerCardsValue>12) && (playerCardsValue<17) &&
					(dealerCardValue<7))
				drawCard = true;
		}

		return drawCard;
	}
}