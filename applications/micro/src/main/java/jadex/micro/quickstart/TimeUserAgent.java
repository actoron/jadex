package jadex.micro.quickstart;

import java.text.DateFormat;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.service.IService;
import jadex.bridge.service.ServiceScope;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.OnService;
import jadex.micro.annotation.RequiredService;

/**
 *  Simple agent that uses globally available time services.
 */
@Agent
public class TimeUserAgent
{
	/**
	 *  Subscribe to any newly found time service and print the results when they arrive.
	 */
	@OnService(requiredservice = @RequiredService(scope = ServiceScope.GLOBAL))
	public void	addTimeService(ITimeService timeservice)
	{
		try
		{
			String	location	= timeservice.getLocation().get();
			DateFormat	format	= DateFormat.getDateTimeInstance();
			ISubscriptionIntermediateFuture<String>	subscription = timeservice.subscribe(format);
			while(subscription.hasNextIntermediateResult())
			{
				String time = subscription.getNextIntermediateResult();
				String platform	= ((IService)timeservice).getServiceId().getProviderId().getPlatformName();
				System.out.println("New time received from "+platform+" in "+location+": "+time);
			}
		}
		catch(RuntimeException exception)
		{
			System.out.println("Disconnected from "+timeservice+" due to "+exception);			
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

