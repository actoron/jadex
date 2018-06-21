package jadex.bdi.examples.blackjack;

import javax.swing.UIManager;


/**
 *  Object representing the dealer.
 */
public class Dealer extends Player
{
	//-------- constructors --------

	/**
	 *  Create a new Dealer.
	 */
	public Dealer()
	{
		// Empty bean constructor.
		super("Dealer", 250, UIManager.getColor("Label.background"), null);
	}
}
