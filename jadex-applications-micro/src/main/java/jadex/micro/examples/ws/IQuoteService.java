package jadex.micro.examples.ws;

import jadex.commons.future.IFuture;

/**
 *  Example web service interface in Jadex.
 *  The original synchronous web service interface
 *  is made asynchronous to fit the programming model
 *  and avoid deadlocks.
 */
public interface IQuoteService
{
	/**
	 *  Get a quote.
	 */
	public IFuture<String> getQuote(String stock);
}
