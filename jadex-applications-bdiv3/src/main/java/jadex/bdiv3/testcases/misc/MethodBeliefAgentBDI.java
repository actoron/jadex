package jadex.bdiv3.testcases.misc;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.runtime.impl.BeliefAdapter;
import jadex.commons.future.DefaultResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Test using injected values in init expressions or constructors.
 */
@Agent
@Results(@Result(name="testresults", clazz=Testcase.class))
public class MethodBeliefAgentBDI
{
	//-------- attributes --------
	
	// Not using value to make testcase more difficult
	// In case of getter/setter beliefs the setter method is enhanced.
	/** The value. */
	protected String	val; 
	
	//-------- methods --------
	
	/**
	 *  Get the value.
	 */
	@Belief
	public String	getValue()
	{
		return val;
	}
	
	/**
	 *  Set the value.
	 */
	@Belief
	public void	setValue(String value)
	{
		this.val	= value;
	}
	
	/**
	 *  Agent body.
	 */
	@AgentBody
	public void	body(final BDIAgent agent)
	{
		final TestReport	tr	= new TestReport("#1", "Test if method beliefs work.");
		agent.addBeliefListener("value", new BeliefAdapter()
		{
			public void beliefChanged(Object value)
			{
				if(!tr.isFinished())
				{
					tr.setSucceeded(true);
					agent.setResultValue("testresults", new Testcase(1, new TestReport[]{tr}));
					agent.killAgent();
				}
			}
		});
		
		setValue("hello");
		
		agent.waitForDelay(500).addResultListener(new DefaultResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				if(!tr.isFinished())
				{
					tr.setFailed("No event occurred.");
					agent.setResultValue("testresults", new Testcase(1, new TestReport[]{tr}));
					agent.killAgent();
				}
			}
		});
	}
}
