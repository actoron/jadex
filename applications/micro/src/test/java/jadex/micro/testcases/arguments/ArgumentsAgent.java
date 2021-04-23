package jadex.micro.testcases.arguments;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.annotation.OnStart;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentResult;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 * Tests if agent arguments and results can be inferred from field declaration.
 */
@Arguments(@Argument(name="text", clazz=String.class, defaultvalue="\"def\""))
@Agent
@Results(@Result(name="testresults", clazz=Testcase.class))
public class ArgumentsAgent extends JunitAgentTest
{
	@Agent
	protected IInternalAccess agent;

	@AgentArgument
	protected String text;
	
	@AgentArgument
	protected int cnt;
	
	@AgentResult
	protected String someresult;

	/**
	 * The agent body.
	 */
	//@AgentBody
	@OnStart
	public void body()
	{
		TestReport tr1 = new TestReport("#1", "Test if all arguments can be found.");
		if(agent.getModel().getArgument("text")!=null && agent.getModel().getArgument("cnt")!=null)
		{
			tr1.setSucceeded(true);
		}
		else
		{
			tr1.setFailed("Wrong number of arguments");
		}
		System.out.println("Agent arguments are: "+text+" "+cnt);
		
		TestReport tr2 = new TestReport("#2", "Test if all results can be found.");
		if(agent.getModel().getResult("testresults")!=null && agent.getModel().getResult("someresult")!=null)
		{
			tr2.setSucceeded(true);
		}
		else
		{
			tr2.setFailed("Wrong number of results");
		}
		
		agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(2, 
			new TestReport[]{tr1, tr2}));
		agent.killComponent();
	}
}
