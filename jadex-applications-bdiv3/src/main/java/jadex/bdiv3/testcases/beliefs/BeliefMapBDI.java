package jadex.bdiv3.testcases.beliefs;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bdiv3.runtime.impl.RPlan;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

import java.util.Map;

/**
 * 
 */
@Agent
@Results(@Result(name="testresults", clazz=Testcase.class))
public class BeliefMapBDI
{
	@Agent
	protected BDIAgent agent;
	
	@Belief
	protected Map<String, String> names;
	
	/** The test report. */
	protected TestReport[] tr = new TestReport[3];
	
	/**
	 * 
	 */
	@AgentBody
	public void body()
	{
		tr[0] = new TestReport("#1", "Test if add trigger on belief maps work.");
		tr[1] = new TestReport("#2", "Test if change trigger on belief maps work.");
		tr[2] = new TestReport("#3", "Test if rem trigger on belief maps work.");
		
		names.put("a", "a");
		names.put("a", "b");
		names.remove("a");
		
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
		for(TestReport ter: tr)
		{
			if(!ter.isFinished())
				ter.setFailed("Plan not activated");
		}
		agent.setResultValue("testresults", new Testcase(tr.length, tr));
	}
	
	// todo: plan creation condition?!
	@Plan(trigger=@Trigger(factaddeds="names"))
	protected void printAddedFact(ChangeEvent event, RPlan rplan)
	{
		System.out.println("fact added: "+event.getValue()+" "+event.getSource()+" "+rplan);
		tr[0].setSucceeded(true);
	}
	
	@Plan(trigger=@Trigger(factchangeds="names"))
	protected void printChFact(ChangeEvent event, RPlan rplan)
	{
		System.out.println("fact change: "+event.getValue()+" "+event.getSource()+" "+rplan);
		tr[1].setSucceeded(true);
	}
	
	@Plan(trigger=@Trigger(factremoveds="names"))
	protected void printRemFact(ChangeEvent event, RPlan rplan)
	{
		System.out.println("fact removed: "+event.getValue()+" "+event.getSource()+" "+rplan);
		tr[2].setSucceeded(true);
		agent.killAgent();
	}
}
