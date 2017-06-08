package jadex.micro.testcases;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

@Agent
public class SystemSearchAgent
{
	@Agent
	protected IInternalAccess agent;
	
	@AgentBody
	public void body()
	{
		IComponentManagementService cms = SServiceProvider.getLocalService(agent, IComponentManagementService.class);
		System.out.println("Found: "+cms);
	}
}
