package jadex.micro.testcases.autoterminate;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentService;
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
	
	/** The service. */
	@AgentService
	protected IAutoTerminateService	sub;

	/** The cms. */
	@AgentService
	protected IComponentManagementService	cms;
	
	//-------- methods --------
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void	body(IInternalAccess agent)
	{
		sub.subscribe();
		
		if("platform".equals(agent.getConfiguration()))
		{
			cms.destroyComponent(agent.getComponentIdentifier().getRoot());
		}
		else
		{
			agent.killComponent();
		}
	}
}
