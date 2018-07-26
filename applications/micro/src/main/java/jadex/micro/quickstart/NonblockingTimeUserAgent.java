package jadex.micro.quickstart;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.service.IService;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentServiceQuery;
import jadex.micro.annotation.RequiredService;

/**
 *  Simple agent that uses globally available time services.
 *  Non-blocking implementation using asynchronous result-listeners.
 */
@Agent
public class NonblockingTimeUserAgent
{
	/**
	 *  The time services are searched and added whenever a new one is found.
	 */
	@AgentServiceQuery(scope=RequiredService.SCOPE_GLOBAL)
	public void	addTimeService(final ITimeService timeservice)
	{
		ISubscriptionIntermediateFuture<String> subscription	= timeservice.subscribe();
		subscription.addResultListener(new IntermediateDefaultResultListener<String>()
		{
			/**
			 *  This method gets called for each received time submission.
			 */
			public void intermediateResultAvailable(String time)
			{
				String	platform	= ((IService)timeservice).getId().getProviderId().getPlatformName();
				System.out.println("New time received from "+platform+" at "+timeservice.getLocation()+": "+time);
			}
		});				
	}	
	
	/**
	 *  Start a Jadex platform and the TimeUserAgent.
	 */
	public static void main(String[] args)
	{
		IPlatformConfiguration config = PlatformConfigurationHandler.getDefault();
		config.addComponent(NonblockingTimeUserAgent.class);
		Starter.createPlatform(config).get();
	}
}
