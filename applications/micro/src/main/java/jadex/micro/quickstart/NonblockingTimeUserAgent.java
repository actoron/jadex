package jadex.micro.quickstart;

import java.text.DateFormat;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.service.IService;
import jadex.bridge.service.ServiceScope;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentServiceQuery;

/**
 *  Simple agent that uses globally available time services.
 *  Non-blocking implementation using asynchronous result-listeners.
 */
@Agent
public class NonblockingTimeUserAgent
{
	/**
	 *  Subscribe to any newly found time service and print the results when they arrive.
	 */
	@AgentServiceQuery(scope=ServiceScope.GLOBAL)
	public void	addTimeService(final ITimeService timeservice)
	{
		timeservice.getLocation().addResultListener(new DefaultResultListener<String>()
		{
			@Override
			public void resultAvailable(String location)
			{
				DateFormat	format	= DateFormat.getDateTimeInstance();
				ISubscriptionIntermediateFuture<String> subscription	= timeservice.subscribe(format);
				subscription.addResultListener(new IntermediateDefaultResultListener<String>()
				{
					/**
					 *  This method gets called for each received time submission.
					 */
					public void intermediateResultAvailable(String time)
					{
						String	platform	= ((IService)timeservice).getServiceId().getProviderId().getPlatformName();
						System.out.println("New time received from "+platform+" at "+location+": "+time);
					}
				});				
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
