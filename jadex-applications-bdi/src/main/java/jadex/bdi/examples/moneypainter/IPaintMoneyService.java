package jadex.bdi.examples.moneypainter;

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
