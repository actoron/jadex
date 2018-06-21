package jadex.micro.testcases.servicequeries;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Agent that provides the example service. 
 */
@Agent
@Service
@ProvidedServices(@ProvidedService(type=IExampleService.class))//, scope=RequiredServiceInfo.SCOPE_APPLICATION))
public class ProviderAgent implements IExampleService
{
	/**
	 *  An example method.
	 */
	public IFuture<String> getInfo()
	{
		return new Future<String>("info");
	}
}
