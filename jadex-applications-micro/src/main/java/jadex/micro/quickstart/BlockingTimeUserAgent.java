package jadex.micro.quickstart;

import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentService;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import java.util.Date;

/**
 *  Simple agent that uses globally available time services.
 */
@Agent
@RequiredServices(@RequiredService(name="timeservices", type=ITimeService.class, multiple=true,
	binding=@Binding(scope=Binding.SCOPE_GLOBAL)))
public class BlockingTimeUserAgent
{
	/**
	 *  The time services are searched and added at agent startup.
	 */
	@AgentService
	public void	addTimeService(ITimeService timeservice)
	{
		ISubscriptionIntermediateFuture<Date>	sub	= timeservice.subscribe();
		while(sub.hasNextIntermediateResult())
		{
			System.out.println("New time received from "+timeservice.getName()+": "+sub.getNextIntermediateResult());			
		}
	}
}
