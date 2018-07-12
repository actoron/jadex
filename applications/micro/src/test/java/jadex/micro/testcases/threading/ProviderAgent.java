package jadex.micro.testcases.threading;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;


/**
 * 
 */
@Agent
@ProvidedServices(@ProvidedService(type=ITestService.class, implementation=@Implementation(expression="$pojoagent")))
@Service
public class ProviderAgent implements ITestService
{
	@Agent
	protected IInternalAccess agent;
	
	/**
	 */
	public IFuture<Void> testThreading()
	{
//		System.out.println("Provider.testThreading(): "+System.currentTimeMillis());
		
		Future<Void> ret = new Future<Void>();
		
		if(agent.getId().equals(IComponentIdentifier.LOCAL.get()))
		{
			ret.setResult(null);
		}
		else
		{
			ret.setException(new RuntimeException("Not component thread: "+IComponentIdentifier.LOCAL.get()));
		}
		
		return ret;
	}
}
