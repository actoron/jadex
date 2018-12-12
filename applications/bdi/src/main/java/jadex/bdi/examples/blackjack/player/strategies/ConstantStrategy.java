package jadex.bdi.examples.blackjack.player.strategies;

import jadex.bdi.examples.blackjack.Card;
import jadex.bdi.examples.blackjack.CardSet;

/**
 *  The simple constant strategy.
 */
public class ConstantStrategy extends AbstractStrategy
{
	//-------- attributes --------

	/** The bet. */
	protected int bet;

	/** The draw limit. */
	protected int drawlimit;

	//-------- constructors --------

	/**
	 *  Create a new strategy.
	 *  @param name The strategy name.
	 *  @param bet The bet value.
	 *  @param drawlimit The draw limit.
	 */
	public ConstantStrategy(String name, int bet, int drawlimit)
	{
		super(name);
		this.bet = bet;
		this.drawlimit = drawlimit;
	}

	//-------- methods --------

	/**
	 * Depending on the strategy, this methods calculates the bet-amount.
	 * @param account The account-status of the player.
	 * @return how much money the player should bet.
	 */
	public int makeBet(int account)
	{
		return bet;
	}

	/**
	 * Depending on the strategy, this methods decides whether to draw one more card or not.
	 * @param agentcards A String-array containing all the players cards.
	 * @param dealercard This String represents the dealer's open card.
	 * @return whether the player should draw one more card or not.
	 */
	public boolean drawCard(Card[] agentcards, Card dealercard)
	{
		return CardSet.calculateDeckValue(agentcards)<drawlimit;
	}
}
