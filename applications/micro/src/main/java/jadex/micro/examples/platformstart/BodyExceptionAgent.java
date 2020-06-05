package jadex.micro.examples.platformstart;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.service.annotation.OnStart;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;

/**
 *  Test what happens if an exception is thrown in body.
 */
@Agent
public class BodyExceptionAgent
{
	/**
	 *  The agent body.
	 */
	@OnStart
	public IFuture<Void> executeBody()
	{
//		System.out.println("execute ExceptionTest ...");
		throw new RuntimeException("Exception in agent body");
//		System.out.println("... finished");
	}
	
	/**
	 *  Start a platform and run the agent.
	 */
	public static void main(String[] args)
	{
		IPlatformConfiguration	config	= PlatformConfigurationHandler.getMinimalComm();
		config.addComponent(BodyExceptionAgent.class);
		Starter.createPlatform(config, args).get();
	}
}
