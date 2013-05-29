package jadex.bdiv3.examples.shop;

import jadex.bdiv3.annotation.Belief;
import jadex.micro.annotation.Agent;

/**
 *  Customer capability.
 */
@Agent
public class CustomerBDI	extends CustomerCapability
{
	//-------- attributes --------

	/** The money. */
	@Belief
	protected double	money	= 100;
	
	//-------- methods --------
	
	/**
	 *  Get the money.
	 */
	public double	getMoney()
	{
		return money;
	}
	
	/**
	 *  Set the money.
	 */
	public void 	setMoney(double money)
	{
		this.money	= money;
	}
}
