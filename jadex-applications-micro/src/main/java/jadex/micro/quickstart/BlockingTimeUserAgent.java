package jadex.micro.quickstart;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentService;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import java.util.Date;
import java.util.List;

/**
 *  Simple agent that uses globally available time services.
 */
@Agent
@RequiredServices(@RequiredService(name="timeservices", type=ITimeService.class, multiple=true,
	binding=@Binding(scope=Binding.SCOPE_GLOBAL)))
public class BlockingTimeUserAgent
{
	//-------- attributes --------
	
	/** The time services are searched and set at agent startup. */
	@AgentService
	List<ITimeService>	timeservices;
	
	//-------- methods --------
	
	/**
	 *  This method is called during agent startup.
	 */
	@AgentCreated
	public void	start(IInternalAccess agent)
	{
		// Start a parallel component step for each found time service
		for(final ITimeService timeservice: timeservices)
		{
			agent.getExternalAccess().scheduleStep(new IComponentStep<Void>()
			{
				public jadex.commons.future.IFuture<Void> execute(IInternalAccess ia)
				{
					ISubscriptionIntermediateFuture<Date> subscription	= timeservice.subscribe();
					while(subscription.hasNextIntermediateResult())
					{
						System.out.println("New time received from "+timeservice.getName()+": "+subscription.getNextIntermediateResult());
					}
					return IFuture.DONE;
				}
			});
		}
	}	
}
