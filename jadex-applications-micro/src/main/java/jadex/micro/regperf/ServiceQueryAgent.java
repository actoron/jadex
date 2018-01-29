package jadex.micro.regperf;

import java.util.Collection;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

/**
 *  Agent that searches for services.
 */
@Agent
public class ServiceQueryAgent
{
	/** The internal access. */
	@Agent
	protected IInternalAccess agent;
	
	/**
	 *  Perform the agents actions.
	 */
	@AgentBody
	public void executeBody()
	{
		IComponentStep<Void> step = new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				final long start = System.currentTimeMillis();
				
				SServiceProvider.getServices(agent, IExampleService.class, RequiredServiceInfo.SCOPE_NETWORK)
					.addIntermediateResultListener(new IIntermediateResultListener<IExampleService>()
				{
					int cnt = 0;
					public void exceptionOccurred(Exception exception)
					{
						exception.printStackTrace();
						cnt = 0;
					}
					
					public void resultAvailable(Collection<IExampleService> result)
					{
						cnt = result.size();
					}
					
					public void intermediateResultAvailable(IExampleService result)
					{
						cnt++;
					}
					
					public void finished()
					{
						long end = System.currentTimeMillis();
						System.out.println("Found services: "+cnt+" took ms: "+(end-start));
						cnt = 0;
					}
				});
				return null;
			}
		};
		
		agent.getComponentFeature(IExecutionFeature.class).repeatStep(0, 4000, step);
	}
}
