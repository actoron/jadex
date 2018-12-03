package jadex.micro.quickstart;

import java.text.DateFormat;

import jadex.bridge.service.annotation.Security;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;


/**
 *  Simple service to publish the local system time.
 *  As the service does not change the local system
 *  and provides no sensitive information, no security
 *  restrictions are required. 
 */
@Security(roles=Security.UNRESTRICTED)
@Service
public interface ITimeService
{
	/**
	 *  Get the location of the platform, where the time service runs.
	 */
	// TODO: support for cached methods, yes or no? -> otherwise forbid or support sync?
	public IFuture<String>	getLocation();
	
	/**
	 *  Subscribe to the time service.
	 *  Every couple of seconds, a string with the current time will be
	 *  sent to the subscriber.
	 */
	public ISubscriptionIntermediateFuture<String>	subscribe(DateFormat format);
}
