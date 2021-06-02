package jadex.micro.testcases;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.annotation.OnStart;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  A minimal test case agent serving as a demonstrator.
 */
@Description("A simple test showing how the test center works with micro agents.")
@Results(@Result(name="testresults", clazz=Testcase.class))
@Agent
public class SimpleTestAgent extends JunitAgentTest
{
	@Agent
	protected IInternalAccess agent;
	
	/**
	 *  Just finish the test by setting the result and killing the agent.
	 */
	//@AgentBody
	@OnStart
	public void executeBody()
	{
		TestReport	tr	= new TestReport("#1", "Simple micro test.");
		tr.setSucceeded(true);
		agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
		agent.killComponent();
	}

}
