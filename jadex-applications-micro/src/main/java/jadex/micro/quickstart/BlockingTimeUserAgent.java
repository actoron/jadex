package jadex.micro.quickstart;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
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
	//-------- attributes --------
	
	/** The time services are searched and set at agent startup. */
	@AgentService
	IIntermediateFuture<ITimeService>	timeservices;
	
	//-------- methods --------
	
	/**
	 *  This method is called after agent startup.
	 */
	@AgentBody
	public void	body(IInternalAccess agent)
	{
		// Subscribe to all found time services.
		while(timeservices.hasNextIntermediateResult())
		{
			final ITimeService timeservice	= timeservices.getNextIntermediateResult();
			agent.getExternalAccess().scheduleStep(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
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
