package jadex.micro.examples.ws;

import jadex.commons.future.IFuture;

import java.math.BigDecimal;

/**
 * 
 */
public interface IQuoteService
{
	/**
	 *  Get a quote.
	 */
	public IFuture<BigDecimal> getQuote(String exchange, String stock, String time);
}
