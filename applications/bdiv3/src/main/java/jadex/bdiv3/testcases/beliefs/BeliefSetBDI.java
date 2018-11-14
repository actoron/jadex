package jadex.bdiv3.testcases.beliefs;

import java.util.HashSet;
import java.util.Set;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bdiv3.runtime.impl.RPlan;
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
 * 
 */
@Agent(type=BDIAgentFactory.TYPE)
@Results(@Result(name="testresults", clazz=Testcase.class))
public class BeliefSetBDI
{
	@Agent
	protected IInternalAccess agent;
	
	@Belief
	protected Set<String> names = new HashSet<String>();
	
	/** The test report. */
	protected TestReport[] tr = new TestReport[2];
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
		tr[0] = new TestReport("#1", "Test if add trigger on belief sets work.");
		tr[1] = new TestReport("#2", "Test if rem trigger on belief sets work.");
		
		names.add("a");
		names.remove("a");
		
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
		for(TestReport ter: tr)
		{
			if(!ter.isFinished())
				ter.setFailed("Plan not activated");
		}
		agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(2, tr));
	}
	
	// todo: plan creation condition?!
	@Plan(trigger=@Trigger(factadded="names"))
	protected void printAddedFact(ChangeEvent event, RPlan rplan)
	{
		System.out.println("fact added: "+event.getValue()+" "+event.getSource()+" "+rplan);
		tr[0].setSucceeded(true);
	}
	
	@Plan(trigger=@Trigger(factremoved="names"))
	protected void printRemFact(ChangeEvent event, RPlan rplan)
	{
		System.out.println("fact removed: "+event.getValue()+" "+event.getSource()+" "+rplan);
		tr[1].setSucceeded(true);
		agent.killComponent();
	}
}
