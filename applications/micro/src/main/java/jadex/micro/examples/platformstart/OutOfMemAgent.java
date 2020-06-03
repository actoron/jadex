package jadex.micro.testcases;

import java.util.ArrayList;
import java.util.List;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
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
	/** Amount of memory to hog in each step. */
	static final int HOGSIZE	= 1024*1024;	// 1 MiB
	
	/** List to collect and keep the memory hogs. */
	List<byte[]>	memhog	= new ArrayList<byte[]>();
	
	/**
	 *  The agent body.
	 */
	@OnStart
	public void executeBody(IInternalAccess agent)
	{
		agent.repeatStep(1000, 1000, ia ->
		{
			memhog.add(new byte[HOGSIZE]);
			return IFuture.DONE;
		});
	}
	
	/**
	 *  Start a platform and run the agent.
	 */
	public static void main(String[] args)
	{
		IPlatformConfiguration	config	= PlatformConfigurationHandler.getMinimalComm();
		config.addComponent(OutOfMemAgent.class);
		Starter.createPlatform(config, args).get();
	}
}
