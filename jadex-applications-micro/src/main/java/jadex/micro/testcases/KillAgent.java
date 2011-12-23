package jadex.micro.testcases;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 * 
 */
@Agent
@RequiredServices(@RequiredService(name="cms", type=IComponentManagementService.class, 
	binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)))
public class KillAgent
{
	
	@Agent
	protected MicroAgent agent;
	
	@AgentCreated
	public IFuture<Void> body()
	{
		final Future<Void> ret = new Future<Void>();
		IFuture<IComponentManagementService> fut = agent.getServiceContainer().getRequiredService("cms");
		fut.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
		{
			public void customResultAvailable(IComponentManagementService cms)
			{
				cms.createComponent(null, "jadex.micro.MicroAgent.class", new CreationInfo(agent.getComponentIdentifier()), null)
					.addResultListener(agent.createResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Void>(ret)
				{
					public void customResultAvailable(IComponentIdentifier result) 
					{
						System.out.println("Micro agent started: "+result);
					};
				}));
				
				ret.setResult(null);
				agent.killComponent();
			}
		});
		return ret;
	}
}
