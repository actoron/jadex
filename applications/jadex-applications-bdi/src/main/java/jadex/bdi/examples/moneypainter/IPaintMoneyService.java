package jadex.bdi.examples.moneypainter;

import jadex.bridge.service.annotation.ParameterInfo;
import jadex.commons.future.IFuture;

/**
 * 
 */
public interface IPaintMoneyService
{
	/**
	 *  Paint one euro.
	 */
	public IFuture<String> paintOneEuro(@ParameterInfo("name") String name);
}
