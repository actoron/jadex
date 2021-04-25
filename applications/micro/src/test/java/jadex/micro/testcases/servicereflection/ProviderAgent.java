package jadex.micro.testcases.servicereflection;

import jadex.bridge.service.ServiceScope;
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
@ProvidedServices(@ProvidedService(type=IExampleService.class, scope=ServiceScope.GLOBAL))
public class ProviderAgent implements IExampleService
{
//	@Agent
//	protected IInternalAccess agent;
//	
//	@AgentCreated
//	public void started()
//	{
//		System.out.println("created: "+agent.getId()+" "+agent.getProvidedService(IExampleService.class));
//	}
	
	/**
	 *  An example method.
	 */
	public IFuture<String> getInfo()
	{
		System.out.println("invoked info method");
		return new Future<String>("info");
	}
	
	/**
	 *  Another example method.
	 */
	public IFuture<Integer> add(int a, int b)
	{
		System.out.println("invoked add method: "+a+" "+b);
		return new Future<Integer>(a+b);
	}
}
