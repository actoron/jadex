package jadex.micro.testcases;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  A minimal test case agent serving as a demonstrator.
 */
@Description("A simple test showing how the test center works with micro agents.")
@Results(@Result(name="testresults", clazz=Testcase.class))
public class SimpleTestAgent extends MicroAgent
{
	/**
	 *  Just finish the test by setting the result and killing the agent.
	 */
	public IFuture<Void> executeBody()
	{
		TestReport	tr	= new TestReport("#1", "Simple micro test.");
		tr.setSucceeded(true);
		setResultValue("testresults", new Testcase(1, new TestReport[]{tr}));
//		killAgent();
		return IFuture.DONE;
	}
	
//	/**
//	 *  Add the 'testresults' marking this agent as a testcase. 
//	 */
//	public static Object getMetaInfo()
//	{
//		return new MicroAgentMetaInfo("A simple test showing how the test center works with micro agents.", 
//			null, null, new IArgument[]{new Argument("testresults", null, "Testcase")});
//	}
}
