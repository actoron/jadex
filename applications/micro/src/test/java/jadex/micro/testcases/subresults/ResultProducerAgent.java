package jadex.micro.testcases.subresults;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

@Agent
@Results(@Result(name="res", clazz=int.class))
public class ResultProducerAgent
{
	@Agent
	protected IInternalAccess agent;
	
	/**
	 * 
	 */
	@AgentBody
	public void body()
	{
		final long delay = 1000;
		final int[] cnt = new int[1];
		agent.getComponentFeature(IExecutionFeature.class).waitForDelay(delay, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("res", Integer.valueOf(cnt[0]));
				if(cnt[0]++<5)
				{
					agent.getComponentFeature(IExecutionFeature.class).waitForDelay(delay, this);
				}
				else
				{
					agent.killComponent();
				}
				return IFuture.DONE;
			}
		});
	}
}
