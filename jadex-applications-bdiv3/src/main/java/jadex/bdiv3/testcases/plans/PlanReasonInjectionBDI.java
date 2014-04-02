package jadex.bdiv3.testcases.plans;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
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
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

import java.util.ArrayList;
import java.util.List;

/**
 *  Agent that tests if injection works for plan reason.
 */
@Agent
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
		getAgent().scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				tr.setReason("Plan not triggered.");
				setResultValue("testresults", new Testcase(1, new TestReport[]{tr}));
				killComponent();
				return IFuture.DONE;
			}
		}, 2000);
	}
	
	@Plan(trigger=@Trigger(factaddeds="items"))
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
			setResultValue("testresults", new Testcase(1, new TestReport[]{tr}));
			killComponent();
		}
	}
}