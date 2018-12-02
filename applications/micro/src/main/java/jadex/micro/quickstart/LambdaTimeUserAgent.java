package jadex.micro.quickstart;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.service.IService;
import jadex.bridge.service.ServiceScope;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentServiceQuery;

/**
 *  Simple agent that uses globally available time services.
 *  Non-blocking implementation using asynchronous result-listeners with lambda expressions.
 */
@Agent
public class LambdaTimeUserAgent
{
	/**
	 *  Subscribe to any newly found time service and print the results when they arrive.
	 */
	@AgentServiceQuery(scope=ServiceScope.GLOBAL)
	public void	addTimeService(final ITimeService timeservice)
	{
		// Wait for location result before continuing.
		timeservice.getLocation().addResultListener(location ->
		// For every time result do a println.
		timeservice.subscribe().addIntermediateResultListener(time ->
			System.out.println("New time received from "
				+ ((IService)timeservice).getServiceId().getProviderId().getPlatformName()
				+ " at "+location+": "+time)
			)
		);
	}	
	
	/**
	 *  Start a Jadex platform and the TimeUserAgent.
	 */
	public static void main(String[] args)
	{
		IPlatformConfiguration config = PlatformConfigurationHandler.getDefault();
		config.addComponent(LambdaTimeUserAgent.class);
		Starter.createPlatform(config).get();
	}
}
