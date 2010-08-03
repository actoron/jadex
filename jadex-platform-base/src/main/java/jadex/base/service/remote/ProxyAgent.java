package jadex.base.service.remote;

import jadex.bridge.Argument;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IArgument;
import jadex.bridge.IComponentIdentifier;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;
import jadex.service.CacheServiceContainer;
import jadex.service.IServiceContainer;
import jadex.service.SServiceProvider;

/**
 *  A proxy agent is a pseudo component that mirrors services of a remote platform (or component).
 */
public class ProxyAgent extends MicroAgent
{
//	/**
//	 *  Called once after agent creation.
//	 */
//	public void agentCreated()
//	{
//		startServiceProvider();
//	}
	
	/**
	 *  Execute the functional body of the agent.
	 *  Is only called once.
	 */
	public void executeBody()
	{
		startServiceProvider();
	}
	
	/**
	 *  Get the service container.
	 *  @return The service container.
	 */
	public IServiceContainer createServiceContainer()
	{
		return /*new CacheServiceContainer(*/new RemoteServiceContainer(
			(IComponentIdentifier)getArgument("platform"), getAgentAdapter())/*, 25, 1*30*1000)*/; // 30 secs cache expire
	}
	

	/**
	 *  Get the meta information about the agent.
	 */
	public static Object getMetaInfo()
	{
		return new MicroAgentMetaInfo("This agent represents a proxy for a remote component.", 
			new String[0], new IArgument[]{
			new Argument("platform", "The component id of the remote platform", "jadex.bridge.IComponentIdentifier", 
				new ComponentIdentifier("rms@remote", new String[]{"tcp-mtp://127.0.0.1:11000", "nio-mtp://127.0.0.1:11001"}))}, 
			null, null, null);
	}
}