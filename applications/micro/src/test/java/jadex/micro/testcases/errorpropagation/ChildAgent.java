package jadex.micro.testcases.errorpropagation;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

/**
 *  Child agent throwing an exception in body.
 */
@Agent
public class ChildAgent
{
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	protected void body()
	{
		System.out.println("Child started");
		agent.waitForDelay(2000).get();
		agent.scheduleStep(new IComponentStep<Void>()
		{
			@Override
			public IFuture<Void> execute(IInternalAccess ia)
			{
				throw new RuntimeException();
//				return IFuture.DONE;
			}
		}).get();
	}
}
