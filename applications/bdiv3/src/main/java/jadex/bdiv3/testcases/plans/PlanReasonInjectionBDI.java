package jadex.bdiv3.testcases.plans;

import java.util.ArrayList;
import java.util.List;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.IBDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanReason;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.runtime.IPlan;
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
 *  Agent that tests if injection works for plan reason.
 */
@Agent(type=BDIAgentFactory.TYPE)
@Results(@Result(name="testresults", clazz=Testcase.class))
public abstract class PlanReasonInjectionBDI implements IBDIAgent
{
	@Belief
	protected List<Integer> items = new ArrayList<Integer>();
	
	final TestReport tr = new TestReport("#1", "Test if waiting for an specific index works.");
	
	@AgentBody
	public void body()
	{
		items.add(2);
		getFeature(IExecutionFeature.class).waitForDelay(2000, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				tr.setReason("Plan not triggered.");
				getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
				killComponent();
				return IFuture.DONE;
			}
		});
	}
	
	@Plan(trigger=@Trigger(factadded="items"))
	public class SomePlan 
	{
		@PlanAPI
		protected IPlan rplan;
		
		@PlanReason
		protected int target;
		
		@PlanBody
		public void body()
		{
			System.out.println("plan invoked " + PlanReasonInjectionBDI.this + " for reason " + target);
			tr.setSucceeded(true);
			getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
			killComponent();
		}
	}
}