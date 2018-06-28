package jadex.bdi.testcases.semiautomatic;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;

/**
 *  Simple print hello service.
 */
@Service
public class PrintHelloService	implements IPrintHelloService
{
	/**
	 *  Print hello.
	 */
	public IFuture<Void> printHello()
	{
		System.out.println("Hello");
		return IFuture.DONE;
	}
}
