package jadex.micro.testcases.nflatency;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
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
	protected IInternalAccess agent;
	
	/**
	 * 
	 */
	public IFuture<Void> methodA(long wait)
	{
//		System.out.println("methodA impl called: "+wait);
		return agent.getComponentFeature(IExecutionFeature.class).waitForDelay(wait, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				return IFuture.DONE;
			}
		});
	}
	
	/**
	 * 
	 */
	public IFuture<Void> methodB(long wait)
	{
		return methodA(wait);
	}
}