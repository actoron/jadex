package jadex.bdi.examples.blackjack.player.strategies;

import jadex.bdi.examples.blackjack.Card;

/**
 *  Interface for a player strategy.
 */
public interface IStrategy
{
	/**
	 *  Get the strategy name.
	 *  @return The name.
	 */
	public String getName();

	/**
	 *  Calculate how much to bet, given the account value.
	 */
	public int makeBet(int account);

	/**
	 *  Decide if to draw another card.
	 */
	public boolean drawCard(Card[] cards, Card dealercard);
}