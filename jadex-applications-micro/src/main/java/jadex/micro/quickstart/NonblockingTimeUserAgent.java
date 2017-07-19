package jadex.micro.quickstart;

import jadex.bridge.service.IService;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentServiceSearch;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *  Simple agent that uses globally available time services.
 *  Non-blocking implementation using asynchronous result-listeners.
 */
@Agent
@RequiredServices(@RequiredService(name="timeservices", type=ITimeService.class, multiple=true,
	binding=@Binding(scope=Binding.SCOPE_GLOBAL)))
public class NonblockingTimeUserAgent
{
	//-------- attributes --------
	
	/** The time services are searched and set at agent startup. */
	@AgentServiceSearch
	private IIntermediateFuture<ITimeService>	timeservices;
	
	//-------- methods --------
	
	/**
	 *  This method is called after agent startup.
	 */
	@AgentBody
	public void	body()
	{
		// Subscribe to all found time services.
		timeservices.addResultListener(new IntermediateDefaultResultListener<ITimeService>()
		{
			public void intermediateResultAvailable(final ITimeService timeservice)
			{
				ISubscriptionIntermediateFuture<String> subscription	= timeservice.subscribe();
				subscription.addResultListener(new IntermediateDefaultResultListener<String>()
				{
					/**
					 *  This method gets called for each received time submission.
					 */
					public void intermediateResultAvailable(String time)
					{
						String	platform	= ((IService)timeservice).getServiceIdentifier().getProviderId().getPlatformName();
						System.out.println("New time received from "+platform+" at "+timeservice.getLocation()+": "+time);
					}
				});				
			}
		});
	}	
}
