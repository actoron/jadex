package jadex.bdiv3.testcases.misc;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.commons.Boolean3;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Test using injected values in init expressions or constructors.
 */
@Agent(keepalive=Boolean3.FALSE)
@Results(@Result(name="testresults", clazz=Testcase.class))
public class ConstructorsBDI	extends ConstructorsSuper
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess	agent;
	
	//-------- constructors --------
	
	/**
	 *  Create the agent.
	 */
	public ConstructorsBDI()
	{
		this("C");
		calls.add("D");
	}
	
	/**
	 *  Create the agent.
	 */
	public ConstructorsBDI(String arg)
	{
		super("B");
		calls.add(arg);
	}	 
	
	//-------- methods --------
	
	/**
	 *  Agent body.
	 */
	@AgentBody
	public void	body()
	{
		TestReport	tr	= new TestReport("#1", "Test if constructor calls work.");
		if("[A, B, C, D]".equals(calls.toString()))
		{			
			tr.setSucceeded(true);
		}
		else
		{
			tr.setReason("Calls do not match: [A, B, C, D], "+calls.toString());
		}
		agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
	}
}
