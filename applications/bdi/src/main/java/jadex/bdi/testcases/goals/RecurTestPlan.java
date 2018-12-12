package jadex.bdi.testcases.goals;

import jadex.base.test.TestReport;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;

/**
 *  Test recur features of goals.
 */
public class RecurTestPlan extends Plan
{
	/**
	 *	Plan code. 
	 */
	public void body()
	{
		// Dispatch goals.
		IGoal	perf	= createGoal("perf");
		IGoal	achi	= createGoal("achi");
		IGoal	quer	= createGoal("quer");
		IGoal	perf2	= createGoal("perf2");
		dispatchSubgoal(perf);
		dispatchSubgoal(achi);
		dispatchSubgoal(quer);
		dispatchSubgoal(perf2);
		
		// Check state of goals when no plan is available.
		waitFor(50);
		
		TestReport	report	= new TestReport("perform_paused", "Test if perform goal continues when no plan is found.");
		if(perf.isActive())
			report.setSucceeded(true);
		else
			report.setReason("Goal not active.");
		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);
		
		report	= new TestReport("achieve_paused", "Test if achieve goal continues when no plan is found.");
		if(achi.isActive())
			report.setSucceeded(true);
		else
			report.setReason("Goal not active.");
		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);

		report	= new TestReport("query_paused", "Test if query goal continues when no plan is found.");
		if(quer.isActive())
			report.setSucceeded(true);
		else
			report.setReason("Goal not active.");
		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);

		report	= new TestReport("perform2_paused", "Test if perform goal continues when no plan is found.");
		if(perf2.isActive())
			report.setSucceeded(true);
		else
			report.setReason("Goal not active.");
		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);

		// Necessary for perform goal to finish after plan execution. Hack!?
		((MGoal)perf.getModelElement()).setRecur(false);
		((MGoal)achi.getModelElement()).setRecur(false);
		((MGoal)quer.getModelElement()).setRecur(false);
		((MGoal)perf2.getModelElement()).setRecur(false);
		
		// Check state when plans are applicable.
		getBeliefbase().getBelief("context").setFact(Boolean.TRUE);
		waitFor(250);

		report	= new TestReport("perform_succeeded", "Test if perform goal succeeds after plan is found.");
		if(perf.isSucceeded())
			report.setSucceeded(true);
		else
			report.setReason("Goal not succeeded.");
		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);

		report	= new TestReport("achieve_succeeded", "Test if achieve goal succeeds after plan is found.");
		if(achi.isSucceeded())
			report.setSucceeded(true);
		else
			report.setReason("Goal not succeeded.");
		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);

		report	= new TestReport("query_succeeded", "Test if query goal succeeds after plan is found.");
		if(quer.isSucceeded())
			report.setSucceeded(true);
		else
			report.setReason("Goal not succeeded.");
		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);
	
		report	= new TestReport("perform2_succeeded", "Test if perform goal succeeds after plan is found.");
		if(perf2.isSucceeded())
			report.setSucceeded(true);
		else
			report.setReason("Goal not succeeded.");
		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);
	}
}
