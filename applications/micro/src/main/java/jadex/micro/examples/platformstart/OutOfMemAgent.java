package jadex.micro.examples.platformstart;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.OnStart;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;

/**
 *  An agent that should eventually die from OutOfMemoryError.
 */
@Agent
public class OutOfMemAgent
{
	/** The memory hog. */
	byte[]	memhog	= new byte[1024];
	
	/**
	 *  The agent body.
	 */
	@OnStart
	public void executeBody(IInternalAccess agent)
	{
		agent.repeatStep(1000, 1000, ia ->
		{
			memhog	= new byte[memhog.length*2];
			System.out.println("Mem usage is "+memhog.length+ " byte.");
			return IFuture.DONE;
		});
	}
	
	/**
	 *  Start a platform and run the agent.
	 */
	public static void main(String[] args)
	{
		try
		{
			IPlatformConfiguration	config	= PlatformConfigurationHandler.getMinimalComm();
			IExternalAccess	platform	= Starter.createPlatform(config, args).get();
			IExternalAccess	agent	= platform.addComponent(new OutOfMemAgent()).get();
			agent.subscribeToResults().get();
		}
		catch(Throwable t)
		{
			System.exit(1);
		}
	}
}
