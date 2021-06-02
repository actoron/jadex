package jadex.micro.testcases;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.service.annotation.OnInit;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Description;

/**
 *  Agent that produces an exception during init.
 */
@Description("Agent that produces an exception during init.")
@Agent
public class BrokenInitAgent 
{
	/**
	 *  Init the agent.
	 */
	@OnInit
	public IFuture<Void> agentCreated()
	{
		throw new RuntimeException("Exception in init.");
	}

	/**
	 *  Start a platform and run the agent.
	 */
	public static void main(String[] args)
	{
		IPlatformConfiguration	config	= PlatformConfigurationHandler.getMinimalComm();
		config.addComponent(BrokenInitAgent.class);
		Starter.createPlatform(config, args).get();
	}
}
