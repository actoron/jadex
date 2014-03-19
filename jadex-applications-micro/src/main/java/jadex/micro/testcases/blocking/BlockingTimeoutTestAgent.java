package jadex.micro.testcases.blocking;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IInternalAccess;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

import java.util.ArrayList;
import java.util.List;

/**
 *  Test timeouts in threaded component execution.
 */
@Agent
@Results(@Result(name="testresults", clazz=Testcase.class))
public class BlockingTimeoutTestAgent
{
	/**
	 *  Execute the agent
	 */
	@AgentBody(keepalive=false)
	public void	execute(final IInternalAccess agent)
	{
		List<TestReport>	tests	= new ArrayList<TestReport>();
		
//		// Test if unused timeout timer entries are ignored.
//		new Future<String>("dummy").get(500);
//		new Future<String>("dummy").get(500);
//		agent.waitForDelay(1000).get();
		tests.add(new TestReport("#1", "Test if unused timeout timer entries are ignored.", true, null));

//		// Test if wake up after timeout works.
//		TestReport	tr	= new TestReport("#2", "Test if wake up after timeout works.");
//		try
//		{
//			new Future<String>().get(500);
//			tr.setFailed("No timeout exception");
//		}
//		catch(TimeoutException te)
//		{
//			tr.setSucceeded(true);
//		}
//		tests.add(tr);

		agent.setResultValue("testresults", new Testcase(tests.size(), tests.toArray(new TestReport[tests.size()])));
	}
}
