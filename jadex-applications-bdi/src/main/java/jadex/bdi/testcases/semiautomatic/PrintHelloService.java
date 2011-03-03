package jadex.bdi.testcases.semiautomatic;

import jadex.commons.service.BasicService;
import jadex.commons.service.IServiceProvider;

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
	public void printHello()
	{
		System.out.println("Hello");
	}
}
