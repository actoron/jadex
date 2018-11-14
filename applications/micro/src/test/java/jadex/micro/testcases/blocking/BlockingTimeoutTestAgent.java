package jadex.micro.testcases.blocking;

import java.util.ArrayList;
import java.util.List;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.commons.Boolean3;
import jadex.commons.TimeoutException;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Test timeouts in threaded component execution.
 */
@Agent(keepalive=Boolean3.FALSE)
@Results(@Result(name="testresults", clazz=Testcase.class))
public class BlockingTimeoutTestAgent extends JunitAgentTest
{
	/**
	 *  Execute the agent
	 */
	@AgentBody
	public void	execute(IInternalAccess agent)
	{
		List<TestReport>	tests	= new ArrayList<TestReport>();
		
		runTests(agent, tests);
		runTests(agent, tests);	// perform tests twice

		agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(tests.size(), tests.toArray(new TestReport[tests.size()])));
	}

	/**
	 *  Perform the tests.
	 */
	public void runTests(final IInternalAccess agent, List<TestReport> tests)
	{
		// Test if unused timeout timer entries are ignored.
		final Future<String>	fut	= new Future<String>();
		agent.getComponentFeature(IExecutionFeature.class).waitForDelay(250, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				fut.setResult("dummy");
				return IFuture.DONE;
			}
		});
		fut.get(500);
		final Future<String>	fut2	= new Future<String>();
		agent.getComponentFeature(IExecutionFeature.class).waitForDelay(250, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				fut2.setResult("dummy");
				return IFuture.DONE;
			}
		});
		fut2.get(500);
		agent.getComponentFeature(IExecutionFeature.class).waitForDelay(1000).get();
		tests.add(new TestReport("#1", "Test if unused timeout timer entries are ignored.", true, null));

		// Test if wake up after timeout works.
		TestReport	tr	= new TestReport("#2", "Test if wake up after timeout works.");
		try
		{
			new Future<String>().get(500);
			tr.setFailed("No timeout exception");
		}
		catch(TimeoutException te)
		{
			tr.setSucceeded(true);
		}
		tests.add(tr);
		
		// Test if wake up after timeout works again.
		tr	= new TestReport("#3", "Test if wake up after timeout works again.");
		try
		{
			new Future<String>().get(500);
			tr.setFailed("No timeout exception");
		}
		catch(TimeoutException te)
		{
			tr.setSucceeded(true);
		}
		tests.add(tr);
	}
}
