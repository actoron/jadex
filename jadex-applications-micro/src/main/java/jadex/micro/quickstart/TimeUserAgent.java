package jadex.micro.quickstart;

import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.service.IService;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentServiceSearch;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *  Simple agent that uses globally available time services.
 */
@Agent
@RequiredServices(@RequiredService(name="timeservices", type=ITimeService.class, multiple=true,
	binding=@Binding(scope=Binding.SCOPE_GLOBAL)))
public class TimeUserAgent
{
	/**
	 *  The time services are searched and added at agent startup.
	 */
	@AgentServiceSearch//(isquery=true)
	public void	addTimeService(ITimeService timeservice)
	{
		ISubscriptionIntermediateFuture<String>	subscription	= timeservice.subscribe();
		while(subscription.hasNextIntermediateResult())
		{
			String	time	= subscription.getNextIntermediateResult();
			String	platform	= ((IService)timeservice).getServiceIdentifier().getProviderId().getPlatformName();
			System.out.println("New time received from "+platform+" at "+timeservice.getLocation()+": "+time);
		}
	}
	
	/**
	 *  Start a Jadex platform and the TimeUserAgent.
	 */
	public static void	main(String[] args)
	{
		PlatformConfiguration	config	= PlatformConfiguration.getMinimalRelayAwareness();
		config.addComponent(TimeUserAgent.class.getName()+".class");
		Starter.createPlatform(config).get();
	}
}
