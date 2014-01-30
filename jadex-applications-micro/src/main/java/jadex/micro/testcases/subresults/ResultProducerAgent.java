package jadex.micro.testcases.subresults;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

@Agent
@Results(@Result(name="res", clazz=int.class))
public class ResultProducerAgent
{
	@Agent
	protected MicroAgent agent;
	
	/**
	 * 
	 */
	@AgentBody
	public void body()
	{
		final long delay = 1000;
		final int[] cnt = new int[1];
		agent.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				agent.setResultValue("res", Integer.valueOf(cnt[0]));
				if(cnt[0]++<5)
				{
					agent.scheduleStep(this, delay);
				}
				else
				{
					agent.killAgent();
				}
				return IFuture.DONE;
			}
		}, delay);
	}
}
