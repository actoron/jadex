package jadex.bdiv3.examples.moneypainter;

import jadex.commons.future.IFuture;

/**
 * 
 */
public interface IPaintMoneyService
{
	/**
	 *  Paint one euro.
	 */
	public IFuture<String> paintOneEuro(String name);
}
