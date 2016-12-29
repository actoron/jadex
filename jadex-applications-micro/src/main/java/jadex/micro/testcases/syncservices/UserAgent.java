package jadex.micro.testcases.syncservices;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IRequiredServiceFetcher;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 * 
 */
@Agent
@RequiredServices(@RequiredService(name="syncser", type=ISynchronousExampleService.class))
@ComponentTypes(@ComponentType(name="provider", clazz=ProviderAgent.class))
@Configurations(@Configuration(name="def", components=@Component(type="provider")))
public class UserAgent
{
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/**
	 * 
	 */
	@AgentBody
	public void body()
	{
		ISynchronousExampleService ser = (ISynchronousExampleService)agent.getComponentFeature(IRequiredServicesFeature.class).getRequiredService("syncser").get();
		
		ser.doVoid();
		System.out.println("after doVoid");
		System.out.println("after getInt: "+ser.getInt());
		System.out.println("after getColl: "+ser.getCollection());

	}
}
