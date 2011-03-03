package jadex.bdi.testcases.semiautomatic;

import jadex.bdi.runtime.Plan;
import jadex.commons.service.IServiceContainer;
import jadex.commons.service.RequiredServiceInfo;
import jadex.commons.service.SServiceProvider;

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
		IServiceContainer container = (IServiceContainer)getScope().getServiceProvider();
		container.addService(new PrintHelloService(container));
		
		IPrintHelloService service = (IPrintHelloService)SServiceProvider.getService(container, 
			IPrintHelloService.class, RequiredServiceInfo.SCOPE_LOCAL).get(this);
		service.printHello();
	}
}

