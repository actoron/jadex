package jadex.micro.testcases.blocking;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IInternalAccess;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Test threaded component execution.
 */
@Agent
@Results(@Result(name="testresults", clazz=Testcase.class))
public class SimpleBlockingTestAgent
{
	/**
	 *  Execute the agent
	 */
	@AgentBody(keepalive=false)
	public void	execute(final IInternalAccess agent)
	{
		agent.waitForDelay(500).get();
		
		agent.setResultValue("testresults", new Testcase(1,
			new TestReport[]{new TestReport("#1", "Test blocking wait.", true, null)}));
	}
}
