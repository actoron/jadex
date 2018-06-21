package jadex.micro.regperf;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;

/**
 *  Agent that starts a service <n>-times.
 */
@Agent
public class ServiceProviderAgent implements IExampleService
{
	/** The internal access. */
	@Agent
	protected IInternalAccess agent;

	@AgentArgument
	protected int count = 1;
	
	/**
	 *  Perform the agents actions.
	 */
	@AgentBody
	public void executeBody()
	{
		long start = System.currentTimeMillis();
//		System.out.println("started: "+agent.getComponentIdentifier());
		for(int i=0; i<count; i++)
			agent.getComponentFeature(IProvidedServicesFeature.class).addService(null, IExampleService.class, this, null, RequiredServiceInfo.SCOPE_NETWORK);
//		System.out.println("start finished: "+agent.getComponentIdentifier()+" "+(System.currentTimeMillis()-start));
	}
	
	/**
	 *  Say hello method.
	 *  @param name The name.
	 */
	public IFuture<String> sayHello(String name)
	{
		return new Future<String>("Hello "+name);
	}
}
