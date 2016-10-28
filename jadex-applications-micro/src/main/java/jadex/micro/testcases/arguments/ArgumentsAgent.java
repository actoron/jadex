package jadex.micro.testcases.arguments;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 * Tests if agent arguments can be inferred from field declaration.
 */
@Arguments(@Argument(name="text", clazz=String.class, defaultvalue="\"def\""))
@Agent
@Results(@Result(name="testresults", clazz=Testcase.class))
public class ArgumentsAgent
{
	@Agent
	protected IInternalAccess agent;

	@AgentArgument
	protected String text;
	
	@AgentArgument
	protected int cnt;

	/**
	 * The agent body.
	 */
	@AgentBody
	public void body()
	{
		final TestReport tr1 = new TestReport("#1", "Test if blocking get works.");
		
		if(agent.getModel().getArgument("text")!=null && agent.getModel().getArgument("cnt")!=null)
		{
			tr1.setSucceeded(true);
		}
		else
		{
			tr1.setFailed("Wrong number of arguments");
		}
		
		System.out.println("Agent arguments are: "+text+" "+cnt);
		
		agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr1}));
		agent.killComponent();
	}
}
