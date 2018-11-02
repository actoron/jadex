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
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Agent that has a getter setter belief.
 */
@Agent(type=BDIAgentFactory.TYPE)
@Results(@Result(name="testresults", clazz=Testcase.class))
public class GetterSetterBeliefBDI
{
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** The number field. */
	protected int number;
	
	TestReport tr = new TestReport("#1", "Test if add trigger on belief lists work.");

	/**
	 *  Get the number.
	 *  @return The number.
	 */
	@Belief
	public int getNumber()
	{
		return number;
	}

	/**
	 *  Set the number.
	 *  @param number The number to set.
	 */
	@Belief
	public void setNumber(int number)
	{
		this.number = number;
	}
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
		setNumber(22);
		
		agent.getFeature(IExecutionFeature.class).waitForDelay(3000, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				agent.killComponent();
				return IFuture.DONE;
			}
		});
	}

	/**
	 *  Called when agent is killed.
	 */
	@AgentKilled
	public void	destroy(IInternalAccess agent)
	{
		agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
	}
	
	/**
	 * 
	 */
	@Plan(trigger=@Trigger(factchanged="number"))
	protected void plan()
	{
		System.out.println("plan: "+getNumber());
		tr.setSucceeded(true);
		agent.killComponent();
	}
	
}
