package jadex.bdiv3.testcases.beliefs;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Agent that has a getter setter belief.
 */
@Agent
@Results(@Result(name="testresults", clazz=Testcase.class))
public class GetterSetterBeliefBDI
{
	/** The agent. */
	@Agent
	protected BDIAgent agent;
	
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
		
		agent.waitFor(3000, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				agent.killAgent();
				return IFuture.DONE;
			}
		});
	}

	/**
	 *  Called when agent is killed.
	 */
	@AgentKilled
	public void	destroy(BDIAgent agent)
	{
		agent.setResultValue("testresults", new Testcase(1, new TestReport[]{tr}));
	}
	
	/**
	 * 
	 */
	@Plan(trigger=@Trigger(factchangeds="number"))
	protected void plan()
	{
		System.out.println("plan: "+getNumber());
		tr.setSucceeded(true);
		agent.killAgent();
	}
	
}
