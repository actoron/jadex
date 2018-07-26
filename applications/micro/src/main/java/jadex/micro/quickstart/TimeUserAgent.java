package jadex.micro.quickstart;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.service.IService;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentServiceQuery;
import jadex.micro.annotation.RequiredService;

/**
 *  Simple agent that uses globally available time services.
 */
@Agent
public class TimeUserAgent
{
	/**
	 *  The time services are searched and added whenever a new one is found.
	 */
	@AgentServiceQuery(scope=RequiredService.SCOPE_GLOBAL)
	public void	addTimeService(ITimeService timeservice)
	{
		ISubscriptionIntermediateFuture<String>	subscription = timeservice.subscribe();
		while(subscription.hasNextIntermediateResult())
		{
			String time = subscription.getNextIntermediateResult();
			String platform	= ((IService)timeservice).getId().getProviderId().getPlatformName();
			System.out.println("New time received from "+platform+" at "+timeservice.getLocation()+": "+time);
		}
	}
	
	/**
	 *  Start a Jadex platform and the TimeUserAgent.
	 */
	public static void main(String[] args)
	{
		IPlatformConfiguration	config	= PlatformConfigurationHandler.getMinimalComm();
		config.addComponent(TimeUserAgent.class);
		Starter.createPlatform(config, args).get();
	}
}

