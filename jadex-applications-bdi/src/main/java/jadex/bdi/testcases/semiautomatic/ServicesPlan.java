package jadex.bdi.testcases.semiautomatic;

import jadex.bdi.runtime.Plan;
import jadex.bridge.service.IServiceContainer;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;

/**
 *  Plan that creates and searches services. 
 */
public class ServicesPlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		IServiceContainer container = (IServiceContainer)getServiceContainer();
		container.addService(new PrintHelloService(container), null);
		
		IPrintHelloService service = (IPrintHelloService)SServiceProvider.getService(container, 
			IPrintHelloService.class, RequiredServiceInfo.SCOPE_LOCAL).get(this);
		service.printHello();
	}
}

