package jadex.micro.testcases.autoterminate;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentServiceSearch;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *  Agent that subscribes to the service and kills itself or its platform.
 */
@Agent
@Configurations({
	@Configuration(name="self"),
	@Configuration(name="platform")})
@RequiredServices({
	@RequiredService(name="sub", type=IAutoTerminateService.class,
		binding=@Binding(scope=Binding.SCOPE_GLOBAL)),
	@RequiredService(name="cms", type=IComponentManagementService.class,
		binding=@Binding(scope=Binding.SCOPE_PLATFORM))})
public class SubscriberAgent
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
//	/** The service. */
//	@AgentService
//	protected IAutoTerminateService	sub;

	/** The cms. */
	@AgentServiceSearch
	protected IComponentManagementService	cms;
	
	//-------- methods --------
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void	body(final IInternalAccess agent)
	{
//		agent.getLogger().severe("subscribe "+agent.getComponentIdentifier()+", "+agent.getConfiguration());
		
		SServiceProvider.getService(agent, IAutoTerminateService.class, RequiredServiceInfo.SCOPE_GLOBAL)
			.addResultListener(new IResultListener<IAutoTerminateService>()
		{
			public void exceptionOccurred(Exception exception)
			{
				throw new RuntimeException(exception);
			}
			
			public void resultAvailable(IAutoTerminateService sub)
			{
				sub.subscribe().addResultListener(new IntermediateDefaultResultListener<String>()
				{
					public void intermediateResultAvailable(String result)
					{
//						agent.getLogger().severe("subscribed "+agent.getComponentIdentifier());
						
						if("platform".equals(agent.getConfiguration()))
						{
//							agent.getLogger().severe("destroy platform: "+agent.getComponentIdentifier().getRoot());
							cms.destroyComponent(agent.getComponentIdentifier().getRoot());
						}
						else
						{
//							agent.getLogger().severe("destroy comp: "+agent.getComponentIdentifier());
							agent.killComponent();
						}
					}
				});
			}
		});
		
		
	}
}
