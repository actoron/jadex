package jadex.micro.quickstart;

import java.text.DateFormat;
import java.util.logging.Level;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.service.IService;
import jadex.bridge.service.ServiceScope;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentServiceQuery;

/**
 *  Simple agent that uses globally available time services.
 */
@Agent
public class TimeUserAgent
{
	/**
	 *  Subscribe to any newly found time service and print the results when they arrive.
	 */
	@AgentServiceQuery(scope=ServiceScope.GLOBAL)
	public void	addTimeService(ITimeService timeservice)
	{
		String	location	= timeservice.getLocation().get();
		DateFormat	format	= DateFormat.getDateTimeInstance();
		ISubscriptionIntermediateFuture<String>	subscription = timeservice.subscribe(format);
		while(subscription.hasNextIntermediateResult())
		{
			String time = subscription.getNextIntermediateResult();
			String platform	= ((IService)timeservice).getServiceId().getProviderId().getPlatformName();
			System.out.println("New time received from "+platform+" at "+location+": "+time);
		}
	}
	
	/**
	 *  Start a Jadex platform and the TimeUserAgent.
	 */
	public static void main(String[] args)
	{
		IPlatformConfiguration	config	= PlatformConfigurationHandler.getMinimalComm();
		config.setPlatformName("timeuser_*");
		config.addComponent(TimeUserAgent.class);
		config.setLoggingLevel(Level.WARNING);
		Starter.createPlatform(config, args).get();
	}
}

