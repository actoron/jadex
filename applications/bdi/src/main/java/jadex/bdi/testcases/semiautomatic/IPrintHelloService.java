package jadex.bdi.testcases.semiautomatic;

import jadex.commons.future.IFuture;

/**
 *  Interface for hello service.
 */
public interface IPrintHelloService
{
	/**
	 *  Print hello.
	 */
	public IFuture<Void> printHello();
}
