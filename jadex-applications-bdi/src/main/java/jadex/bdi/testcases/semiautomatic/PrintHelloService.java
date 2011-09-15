package jadex.bdi.testcases.semiautomatic;

import jadex.bridge.service.BasicService;
import jadex.bridge.service.IServiceProvider;
import jadex.commons.future.IFuture;

/**
 *  Simple print hello service.
 */
public class PrintHelloService extends BasicService implements IPrintHelloService
{
	/**
	 *  Create a new service.
	 */
	public PrintHelloService(IServiceProvider provider)
	{
		super(provider.getId(), IPrintHelloService.class, null);
	}
	
	/**
	 *  Print hello.
	 */
	public IFuture<Void> printHello()
	{
		System.out.println("Hello");
		return IFuture.DONE;
	}
}
