package jadex.bdi.examples.blackjack;

import jadex.bridge.fipa.IComponentAction;


/**
 *  Java class for concept RequestBet of blackjack_beans ontology.
 */
public class RequestBet implements IComponentAction
{
	//-------- attributes ----------

	/** Attribute for slot bet. */
	protected int bet;

	//-------- constructors --------

	/**
	 *  Default Constructor. <br>
	 *  Create a new <code>RequestBet</code>.
	 */
	public RequestBet()
	{
	}

	//-------- accessor methods --------

	/**
	 *  Get the bet of this RequestBet.
	 * @return bet
	 */
	public int getBet()
	{
		return this.bet;
	}

	/**
	 *  Set the bet of this RequestBet.
	 * @param bet the value to be set
	 */
	public void setBet(int bet)
	{
		this.bet = bet;
	}

	//-------- additional methods --------

	/**
	 *  Get a string representation of this RequestBet.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "RequestBet(" + ")";
	}

}
