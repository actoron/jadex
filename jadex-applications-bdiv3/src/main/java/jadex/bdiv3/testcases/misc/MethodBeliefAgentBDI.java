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
	
	/** The value. */
	protected String	value;
	
	//-------- methods --------
	
	/**
	 *  Get the value.
	 */
	@Belief
	public String	getValue()
	{
		return value;
	}
	
	/**
	 *  Set the value.
	 */
	@Belief
	public void	setValue(String value)
	{
		this.value	= value;
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
				}
			}
		});
	}
}
