package jadex.micro.testcases.blocking;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.commons.Boolean3;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Test threaded component execution.
 */
@Agent(keepalive=Boolean3.FALSE)
@Results(@Result(name="testresults", clazz=Testcase.class))
public class SimpleBlockingTestAgent  extends JunitAgentTest
{
	/**
	 *  Execute the agent
	 */
	@AgentBody
	public void	execute(final IInternalAccess agent)
	{
		agent.getFeature(IExecutionFeature.class).waitForDelay(500).get();
		
		agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1,
			new TestReport[]{new TestReport("#1", "Test blocking wait.", true, null)}));
	}
}
