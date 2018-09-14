package jadex.micro.testcases.errorpropagation;

import jadex.bridge.IInternalAccess;
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
		throw new RuntimeException()
		{
			public void printStackTrace() 
			{
				super.printStackTrace();
			}
		};
	}
}
