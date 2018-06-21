package jadex.micro.testcases.pull;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.commons.ICommand;
import jadex.commons.future.IPullIntermediateFuture;
import jadex.commons.future.IPullSubscriptionIntermediateFuture;
import jadex.commons.future.PullIntermediateFuture;
import jadex.commons.future.PullSubscriptionIntermediateFuture;
import jadex.commons.future.TerminationCommand;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Agent that provides a service with intermediate results.
 */
@Agent
@Service
@ProvidedServices(@ProvidedService(type=IPullResultService.class, implementation=@Implementation(expression="$pojoagent")))
@Description("Agent that provides a service with intermediate results")
public class PullResultProviderAgent implements IPullResultService
{
	//-------- attributes ---------
	
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	//-------- constructors ---------

	/**
	 *  Get the results.
	 *  @param max The number of intermediate results that will be returned.
	 *  @return The results.
	 */
	public IPullIntermediateFuture<String> getResultsA(final int max)
	{
		final PullIntermediateFuture<String> ret = new PullIntermediateFuture<String>(
			new ICommand<PullIntermediateFuture<String>>()
		{
			int cnt = 0;
			public void execute(PullIntermediateFuture<String> fut)
			{
				if(cnt<max)
					fut.addIntermediateResult("step("+(cnt++)+"/"+max+")");
				
				if(cnt==max)
					fut.setFinished();
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get the results.
	 *  @param max The number of intermediate results that will be returned.
	 *  @return The results.
	 */
	public IPullSubscriptionIntermediateFuture<String> getResultsB(final int max)
	{
		final PullSubscriptionIntermediateFuture<String> ret = new PullSubscriptionIntermediateFuture<String>(
			new ICommand<PullSubscriptionIntermediateFuture<String>>()
		{
			int cnt = 0;
			public void execute(PullSubscriptionIntermediateFuture<String> fut)
			{
				if(cnt<max)
					fut.addIntermediateResultIfUndone("step("+(cnt++)+"/"+max+")");
				
				if(cnt==max)
					fut.setFinishedIfUndone();
			}
		}, new TerminationCommand()
		{
			public void terminated(Exception reason)
			{
				System.out.println("terminated");
			}
		});
		
		return ret;
	}
}
