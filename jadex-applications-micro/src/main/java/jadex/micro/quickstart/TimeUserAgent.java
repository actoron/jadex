package jadex.micro.quickstart;

import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
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
public class TimeUserAgent
{
	//-------- attributes --------
	
	/** The time services are searched and set at agent startup. */
	@AgentService
	IIntermediateFuture<ITimeService>	timeservices;
	
	//-------- methods --------
	
	/**
	 *  This method is called during agent startup.
	 */
	@AgentCreated
	public void	start()
	{
		// Subscribe to all found time services.
		timeservices.addResultListener(new IntermediateDefaultResultListener<ITimeService>()
		{
			public void intermediateResultAvailable(final ITimeService timeservice)
			{
				ISubscriptionIntermediateFuture<Date> subscription	= timeservice.subscribe();
				subscription.addResultListener(new IntermediateDefaultResultListener<Date>()
				{
					/**
					 *  This method gets called for each received time submission.
					 */
					public void intermediateResultAvailable(Date time)
					{
						System.out.println("New time received from "+timeservice.getName()+": "+time);
					}
				});
			}
		});
	}	
}
