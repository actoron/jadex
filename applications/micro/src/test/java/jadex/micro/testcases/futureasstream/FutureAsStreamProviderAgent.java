package jadex.micro.testcases.futureasstream;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.Service;
import jadex.commons.Boolean3;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.OnService;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;

/**
 *  Provide some intermediate results. 
 */
@ProvidedServices(@ProvidedService(type=IFutureAsStreamTestService.class, scope = ServiceScope.GLOBAL))
@Service(IFutureAsStreamTestService.class)
@Agent
public class FutureAsStreamProviderAgent implements IFutureAsStreamTestService
{
	@OnService(required = Boolean3.TRUE, requiredservice = @RequiredService(scope = ServiceScope.GLOBAL))
	protected IFutureAsStreamCallbackService	callback;
	
	@Agent
	protected IInternalAccess	agent;
	
	@Override
	public IIntermediateFuture<String> getSomeResults()
	{
		IntermediateFuture<String>	results	= new IntermediateFuture<String>();
		
		// Asynchronously fetch results
		IComponentStep<Void>	fetch	= new IComponentStep<Void>()
		{
			@Override
			public IFuture<Void> execute(IInternalAccess ia)
			{
				String	str	= callback.getNextResult().get();
				System.out.println("getSomeResults: "+str);
				results.addIntermediateResult(str);
				return agent.waitForDelay(1000, this);
			}
		};
		agent.waitForDelay(1000, fetch);
		
		return results;
	}
}
