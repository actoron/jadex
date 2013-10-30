package jadex.micro.testcases.nflatency;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;


/**
 * 
 */
@Agent
@Service
@ProvidedServices(@ProvidedService(type=ITestService.class))
public class ProviderAgent implements ITestService
{
	@Agent
	protected MicroAgent agent;
	
	/**
	 * 
	 */
	public IFuture<Void> methodA(long wait)
	{
//		System.out.println("methodA impl called: "+wait);
		return agent.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				return IFuture.DONE;
			}
		}, wait);
	}
	
	/**
	 * 
	 */
	public IFuture<Void> methodB(long wait)
	{
		return methodA(wait);
	}
}