package jadex.micro.tutorial.tut15;

import jadex.bridge.service.annotation.Security;
import jadex.commons.future.ISubscriptionIntermediateFuture;

import java.util.Date;

/**
 *  Simple service to publish the local system time.
 *  As the service does not change the local system
 *  and provides no sensitive information, no security
 *  restrictions are required. 
 */
@Security(Security.UNRESTRICTED)
public interface ITimeService
{
	/**
	 *  Get the name of the platform, where the time service runs.
	 *  Name is a constant value for each service, therefore it can be cached
	 *  and no future is needed.
	 */
	public String	getName();
	
	/**
	 *  Subscribe to the time service.
	 *  Every couple of seconds, the current time will be
	 *  sent to the subscriber.
	 */
	public ISubscriptionIntermediateFuture<Date>	subscribe();
}
