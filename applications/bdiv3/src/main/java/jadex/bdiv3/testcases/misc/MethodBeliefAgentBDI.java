package jadex.bdiv3.testcases.misc;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.runtime.impl.BeliefAdapter;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.commons.future.DefaultResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;
import jadex.rules.eca.ChangeInfo;

/**
 *  Test using injected values in init expressions or constructors.
 */
@Agent(type=BDIAgentFactory.TYPE)
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
	public void	body(final IInternalAccess agent)
	{
		final TestReport	tr	= new TestReport("#1", "Test if method beliefs work.");
		agent.getFeature(IBDIAgentFeature.class).addBeliefListener("value", new BeliefAdapter<String>()
		{
			public void beliefChanged(ChangeInfo<String> value)
			{
				if(!tr.isFinished())
				{
					tr.setSucceeded(true);
					agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
					agent.killComponent();
				}
			}
		});
		
		setValue("hello");
		
		agent.getFeature(IExecutionFeature.class).waitForDelay(500).addResultListener(new DefaultResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				if(!tr.isFinished())
				{
					tr.setFailed("No event occurred.");
					agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
					agent.killComponent();
				}
			}
		});
	}
}
