package jadex.micro.testcases.nfmethodprop;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.nonfunctional.annotation.NFProperties;
import jadex.bridge.nonfunctional.annotation.NFProperty;
import jadex.bridge.sensor.service.WaitingTimeProperty;
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
//	@NFProperties(@NFProperty(value=WaitingTimeProperty.class))
	public IFuture<Void> methodA(long wait)
	{
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
//	@NFProperties(@NFProperty(value=WaitingTimeProperty.class))
	public IFuture<Void> methodB(long wait)
	{
		return methodA(wait);
	}
}
