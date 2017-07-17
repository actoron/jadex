package jadex.micro.quickstart;

import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.service.IService;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentServiceQuery;
import jadex.micro.annotation.Binding;

/**
 *  Simple agent that uses globally available time services.
 */
@Agent
public class TimeUser2Agent
{
	/**
	 *  The time services are searched and added at agent startup.
	 */
	@AgentServiceQuery(scope=Binding.SCOPE_GLOBAL)
	public void	addTimeService(ITimeService timeservice)
	{
		ISubscriptionIntermediateFuture<String>	subscription = timeservice.subscribe();
		while(subscription.hasNextIntermediateResult())
		{
			String time = subscription.getNextIntermediateResult();
			String platform	= ((IService)timeservice).getServiceIdentifier().getProviderId().getPlatformName();
			System.out.println("New time received from "+platform+" at "+timeservice.getLocation()+": "+time);
		}
	}
	
	/**
	 *  Start a Jadex platform and the TimeUserAgent.
	 */
	public static void main(String[] args)
	{
		PlatformConfiguration config = PlatformConfiguration.getDefault();
		config.addComponent(TimeUserAgent.class.getName()+".class");
		Starter.createPlatform(config).get();
	}
}

