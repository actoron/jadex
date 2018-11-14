package jadex.platform.service.nfmethodprops;

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
//	@NFProperties(@NFProperty(value=WaitingTimeProperty.class))
	public IFuture<Void> methodA(long wait)
	{
		System.out.println("a");
		return agent.getFeature(IExecutionFeature.class).waitForDelay(wait, new IComponentStep<Void>()
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
//	@NFProperties(@NFProperty(value=WaitingTimeProperty.class))
	public IFuture<Void> methodB(long wait)
	{
		System.out.println("b");
		return methodA(wait);
	}
}
