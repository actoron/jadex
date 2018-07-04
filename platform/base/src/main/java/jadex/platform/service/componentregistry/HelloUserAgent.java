package jadex.platform.service.componentregistry;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

@Agent
public class HelloUserAgent
{
	@Agent
	protected IInternalAccess agent;
	
	@AgentBody
	public void body()
	{
		IHelloService hs = agent.getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( IHelloService.class, RequiredServiceInfo.SCOPE_PLATFORM));
		System.out.println(hs.sayHello("Lars").get());
	}
}
