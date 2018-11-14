package jadex.micro.testcases.intermediate;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IntermediateFuture;
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
@ProvidedServices(@ProvidedService(type=IIntermediateResultService.class, implementation=@Implementation(expression="$pojoagent")))
@Description("Agent that provides a service with intermediate results")
public class IntermediateResultProviderAgent implements IIntermediateResultService
{
	//-------- attributes ---------
	
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	//-------- constructors ---------

	/**
	 *  Get the results.
	 *  @param delay The delay that is waited between intermediate results.
	 *  @param max The number of intermediate results that will be returned.
	 *  @return The results.
	 */
	public IIntermediateFuture<String> getResults(final long delay, final int max)
	{
		final IntermediateFuture<String> ret = new IntermediateFuture<String>();

		final int[] cnt = new int[1];
//		final int max = 5;
//		final long delay = 200;
		
//		System.out.println("start: "+System.currentTimeMillis());
		agent.getFeature(IExecutionFeature.class).waitForDelay(delay, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
//				System.out.println("setting intermediate result: "+cnt[0]);//+" - "+System.currentTimeMillis());
				ret.addIntermediateResult("step("+(cnt[0]++)+"/"+max+")");
				if(cnt[0]==max)
				{
					ret.setFinished();
				}
				else
				{
					agent.getFeature(IExecutionFeature.class).waitForDelay(delay, this);
				}
				return null;
			}
		});
		
		return ret;
	}
}
