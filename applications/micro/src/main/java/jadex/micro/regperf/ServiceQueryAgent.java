package jadex.micro.regperf;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
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
				
				agent.getFeature(IRequiredServicesFeature.class).searchServices(new ServiceQuery<>(IExampleService.class, RequiredServiceInfo.SCOPE_NETWORK))
					.addIntermediateResultListener(new IIntermediateResultListener<IExampleService>()
				{
					Set<IComponentIdentifier> plats = new HashSet<IComponentIdentifier>();
					int cnt = 0;
					public void exceptionOccurred(Exception exception)
					{
						exception.printStackTrace();
						cnt = 0;
					}
					
					public void resultAvailable(Collection<IExampleService> result)
					{
						for(IExampleService res: result)
							intermediateResultAvailable(res);
						cnt = result.size();
					}
					
					public void intermediateResultAvailable(IExampleService result)
					{
						plats.add(((IService)result).getServiceId().getProviderId().getRoot());
						cnt++;
					}
					
					public void finished()
					{
						long end = System.currentTimeMillis();
						System.out.println(agent.getId()+" found services: "+cnt+" took ms: "+(end-start)+" "+plats);
						cnt = 0;
					}
				});
				return IFuture.DONE;
			}
		};
		
		agent.getFeature(IExecutionFeature.class).repeatStep(0, 4000, step);
	}
}
