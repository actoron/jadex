package jadex.bdi.testcases.goals;

import jadex.base.test.TestReport;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.IGoal.GoalLifecycleState;
import jadex.bdiv3.runtime.impl.GoalFailureException;
import jadex.bdiv3x.runtime.Plan;
import jadex.commons.concurrent.TimeoutException;


/**
 *  Test creating / suspending maintain goals.
 */
public class MaintainTestPlan extends Plan
{
	/**
	 *  Plan body.
	 */
	public void body()
	{
		// Create and dispatch goal.
		TestReport	report	= new TestReport("dispatch_maintain", "Dispatch a maintain goal that should start processing.");
		getLogger().info("Creating goal");
		IGoal	maintain	= createGoal("maintain");
		dispatchSubgoal(maintain);
		// Wait for goal to be in process.
		waitFor(100);
		if(((Number)getBeliefbase().getBelief("count").getFact()).intValue()>=5
			|| ((Number)getBeliefbase().getBelief("count").getFact()).intValue()<=0)
			report.setReason("Belief should be 0<count<5, but was: "+getBeliefbase().getBelief("count").getFact());
		else
			report.setSucceeded(true);
		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);
		
		
		// Suspending goal.
		report	= new TestReport("suspend_maintain", "Suspend the maintain goal by invalidating the context.");
		getLogger().info("Suspending goal: "+maintain);
		getBeliefbase().getBelief("context").setFact(Boolean.FALSE);
		// Wait for goal to be suspended.
		waitFor(100);
		if(maintain.getLifecycleState()==GoalLifecycleState.SUSPENDED)
			report.setSucceeded(true);
		else
			report.setReason("Goal should be suspended, but was: "+maintain.getLifecycleState());
		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);
		

		// Reactivate goal.
		report	= new TestReport("reactivate_maintain", "Reactivate the maintain goal by validating the context.");
		getLogger().info("Reactivating goal: "+maintain);
		getBeliefbase().getBelief("context").setFact(Boolean.TRUE);
		// Wait for goal to be reactivated.
		waitFor(100);
		if(maintain.getLifecycleState()==GoalLifecycleState.ACTIVE)
			report.setSucceeded(true);
		else
			report.setReason("Goal should be active, but was: "+maintain.getLifecycleState());
		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);
		
		
		// Wait for goal to finish
		report	= new TestReport("waitfor_maintain", "Wait for the maintain goal to finish.");
		getLogger().info("Waiting for goal to finish (500 millis).");
		try
		{
			waitForGoalFinished(maintain, 3000);
			report.setSucceeded(true);
		}
		catch(TimeoutException e)
		{
			report.setReason("Goal did not finish.");
		}
		catch(GoalFailureException e)
		{
			report.setReason("Goal failed.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);
		
	}
}
