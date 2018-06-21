package jadex.bdi.examples.blackjack.player.strategies;

import java.util.ArrayList;
import java.util.List;

import jadex.bdi.examples.blackjack.Card;
import jadex.commons.SReflect;

/**
 *  Abstract base class for strategies.
 */
public abstract class AbstractStrategy implements IStrategy
{
	//-------- constants --------

	/** Constants for strategy names. */

	public static final String CONSTANT_VERY_CAREFUL = "ConstantVeryCareful";
	public static final String CONSTANT_CAREFUL = "ConstantCareful";
	public static final String CONSTANT_RISKY = "ConstantRisky";
	public static final String CONSTANT_VERY_RISKY = "ConstantVeryRisky";
	public static final String STOCHASTIC_TABLE = "StochasticTable";
	public static final String HUMAN_PLAYER = "HumanPlayer";

	//-------- attributes --------

	/** The strategy name. */
	protected String name;

	/** The strategies. */
	protected static final List strategies;

	//-------- constructors --------

	/**
	 *  Create a new strategy.
	 */
	public AbstractStrategy(String name)
	{
		this.name = name;
	}

	//-------- methods --------

	/**
	 * Depending on the strategy, this methods calculates the bet-amount.
	 * @param account The account-status of the player.
	 * @return how much money the player should bet.
	 */
	public abstract int makeBet(int account);

	/**
	 * Depending on the strategy, this methods decides whether to draw one more card or not.
	 * @param agentcards A String-array containing all the players cards.
	 * @param dealercard This String represents the dealer's open card.
	 * @return whether the player should draw one more card or not.
	 */
	public abstract boolean drawCard(Card[] agentcards, Card dealercard);

	/**
	 *  Get the name.
	 *  @return The strategy name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Return a string representation.
	 */
	public String toString()
	{
		return SReflect.getInnerClassName(this.getClass());
	}

	/**
	 * Return the hash code.
	 */
	public int hashCode()
	{
		return getClass().hashCode();
	}

	/**
	 * Test if two strategies are equal.
	 */
	public boolean equals(Object o)
	{
		return o instanceof IStrategy && ((IStrategy)o).getName().equals(getName());
	}

	static
	{
		strategies = new ArrayList();
		strategies.add(new ConstantStrategy(CONSTANT_VERY_CAREFUL, 10, 15));
		strategies.add(new ConstantStrategy(CONSTANT_CAREFUL, 10, 16));
		strategies.add(new ConstantStrategy(CONSTANT_RISKY, 10, 17));
		strategies.add(new ConstantStrategy(CONSTANT_VERY_RISKY, 10, 18));
		strategies.add(new StochasticTable(STOCHASTIC_TABLE));
	}

	/**
	 *  Get the strategy names.
	 *  @return The strategy names.
	 */
	public static String[] getStrategyNames()
	{
		List ret = new ArrayList();
		for(int i=0; i<strategies.size(); i++)
			ret.add(((IStrategy)strategies.get(i)).getName());
		return (String[])ret.toArray(new String[ret.size()]);
	}

	/**
	 *  Get a strategy per name.
	 *  @param name The name.
	 *  @return The strategy.
	 */
	public static IStrategy getStrategy(String name)
	{
		IStrategy ret = null;

		for(int i=0; i<strategies.size() && ret==null; i++)
		{
			IStrategy tmp = (IStrategy)strategies.get(i);
			if(tmp.getName().equals(name))
				ret = tmp;
		}

		return ret;
	}

	/**
	 *  Get all strategies.
	 *  @return All strategies.
	 */
	public static IStrategy[] getStrategies()
	{
		return (IStrategy[])strategies.toArray(new IStrategy[strategies.size()]);
	}
}
