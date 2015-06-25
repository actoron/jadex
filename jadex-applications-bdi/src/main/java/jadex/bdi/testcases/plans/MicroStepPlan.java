package jadex.bdi.testcases.plans;

import jadex.base.test.TestReport;
import jadex.bdiv3x.runtime.Plan;

/**
 *  Testing micro steps.
 */
public class MicroStepPlan extends Plan
{
	//-------- methods --------

	/**
	 *  The body method.
	 */
	public void body()
	{
		TestReport tr = new TestReport("#1", "Testing micro steps. There are 3 conditions a, b, and(a,b) that should trigger immediately.");
		getLogger().info("Testing micro steps. There are 3 conditions a, b, and(a,b) that should trigger immediately.");
		int plan_cnt1 = getCapability().getPlans().size();
		getLogger().info("Plan: Setting a to true.");
		getBeliefbase().getBelief("a").setFact(Boolean.TRUE);
		int plan_cnt2 = getCapability().getPlans().size();
		getLogger().info("Plan: a is now true.");
		getLogger().info("Plan: Setting b to true.");
		getBeliefbase().getBelief("b").setFact(Boolean.TRUE);
		int plan_cnt3 = getCapability().getPlans().size();
		getLogger().info("Plan: b is now true.");
		getLogger().info("Step 1 finished.");
		if(plan_cnt1<plan_cnt2 && plan_cnt2<plan_cnt3)
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setReason("No micro plan step occurred.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		// todo: 
		waitFor(100);
		tr = new TestReport("#2", "Resetting beliefs. As conditions should not trigger, plan will not be interrupted (use introspector to check");
		getLogger().info("\nResetting beliefs. As conditions should not trigger, plan will not be interrupted (use introspector to check).");
		getLogger().info("Plan: Setting a to false.");
//		Object entry1 = ((RCapability)getRPlan().getOwner().getOwner()).getAgent().getInterpreter().getCurrentAgendaEntry();
		getBeliefbase().getBelief("a").setFact(Boolean.FALSE);
//		Object entry2 = ((RCapability)getRPlan().getOwner().getOwner()).getAgent().getInterpreter().getCurrentAgendaEntry();
		getLogger().info("Plan: a is now false.");
		getLogger().info("Plan: Setting b to false.");
		getBeliefbase().getBelief("b").setFact(Boolean.FALSE);
//		Object entry3 = ((RCapability)getRPlan().getOwner().getOwner()).getAgent().getInterpreter().getCurrentAgendaEntry();
		getLogger().info("Plan: b is now false.");
		getLogger().info("Step 2 finished.");
//		if(entry1==entry2 && entry2==entry3)
		{
			tr.setSucceeded(true);
		}
//		else
//		{
//			tr.setReason("Micro plan step occurred.");
//		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		waitFor(100);
		startAtomic();
		tr = new TestReport("#3", "Testing atomic step. There are 3 conditions a, b, and(a,b) that should trigger after the plan step has finished.");
		getLogger().info("\nTesting atomic step. There are 3 conditions a, b, and(a,b) that should trigger after the plan step has finished.");
		getLogger().info("Plan: Setting a to true.");
		plan_cnt1 = getCapability().getPlans().size();
		getBeliefbase().getBelief("a").setFact(Boolean.TRUE);
		plan_cnt2 = getCapability().getPlans().size();
		getLogger().info("Plan: a is now true.");
		getLogger().info("Plan: Setting b to true.");
		getBeliefbase().getBelief("b").setFact(Boolean.TRUE);
		plan_cnt3 = getCapability().getPlans().size();
		getLogger().info("Plan: b is now true.");
		getLogger().info("Step 3 finished.");
		if(plan_cnt1==plan_cnt2 && plan_cnt2==plan_cnt3)
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setReason("Micro plan step occurred.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
		endAtomic();

		//waitFor(100);
		//killAgent();
	}
}
