package jadex.bdiv3.testcases.beliefs;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Agent that has two beliefs. 
 *  num2 belief depends on num1 and a plan depends on changes of num2.
 */
@Agent(type=BDIAgentFactory.TYPE)
@Results(@Result(name="testresults", clazz=Testcase.class))
public class DynamicBeliefBDI
{
	/** The agent (injected). */
	@Agent
	protected IInternalAccess agent;
	
	/** The belief num1. */
	@Belief
	protected int num1 = 1;
	
	/** The belief num2 depending on num1. */
	@Belief(dynamic=true)
	protected int num2 = num1+1;
	
	/** The test report. */
	protected TestReport	tr	= new TestReport("#1", "Test if dynamic belief works.");
	
	/**
	 *  Plan that reacts on belief changes of num2.
	 */
	@Plan(trigger=@Trigger(factchangeds="num2"))
	protected void successPlan(int num)
	{
		System.out.println("plan activated: num2 changed to "+num);
		if (num == 3) {
			tr.setSucceeded(true);
		}
	}

	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
		num1 = 2;
		agent.getFeature(IExecutionFeature.class).waitForDelay(3000, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				if(!tr.isFinished())
					tr.setFailed("Plan was not activated due to belief change or incorrect event change.");
				agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
				agent.killComponent();
				return IFuture.DONE;
			}
		});
	}
}


