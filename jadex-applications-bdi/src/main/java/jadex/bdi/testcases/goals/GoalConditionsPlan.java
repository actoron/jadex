package jadex.bdi.testcases.goals;

import jadex.base.test.TestReport;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.IGoal.GoalLifecycleState;
import jadex.bdiv3.runtime.impl.GoalFailureException;
import jadex.bdiv3x.runtime.Plan;

/**
 *  Plan to test goal conditions.
 *  Note: the planbase content cannot be used here, because micro plansteps
 *  currently do not guarantee that all consequences will be executed (as in old tree agenda).
 *  
 *  todo: currently needs some waitFor(10) to execute the consequences before plan continues
 */
public class GoalConditionsPlan	extends Plan
{
	/**
	 *  The plan body.
	 */
	public void	body()
	{
		// Initially there should be no goal and no plan (except this one).
		TestReport	report	= new TestReport("test_setup", "No goal and plan should exist at start", true, null);
		if(getGoalbase().getGoals("test").length!=0)
		{
			report.setFailed("Goal already exists");
		}
		else if(getPlanbase().getPlans().length!=1)
		{
			report.setFailed("Wrong planbase contents");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);
		
		
		// Now triggering goal creation (still no plan due to invalid context).
		getBeliefbase().getBelief("creation").setFact(Boolean.TRUE);

		waitFor(10); // Hack! Wait for consequences being executed :-(
		
		report	= new TestReport("trigger_creation", "Triggering goal creation", true, null);
		if(getGoalbase().getGoals("test").length!=1)
		{
			report.setFailed("Goal does not exist");
		}
		else if(getPlanbase().getPlans().length!=1)
		{
			report.setFailed("Wrong planbase contents");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);
		
		
		// Now triggering goal context to start plan.		
		IGoal[] goals = getGoalbase().getGoals("test");
		IGoal goal = goals.length>0? goals[0]: null;
		getBeliefbase().getBelief("context").setFact(Boolean.TRUE);
		report	= new TestReport("trigger_context", "Triggering goal context", true, null);	
		if(goal==null)
		{
			report.setFailed("No goal for testing");
		}
		else if(goal.getLifecycleState()!=GoalLifecycleState.ACTIVE)
		{
			report.setFailed("Goal not active: "+goal.getLifecycleState());
		}
//		else if(getPlanbase().getPlans().length!=2)
//		{
//			report.setFailed("Wrong planbase contents");
//		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);

		
		// Now triggering goal drop condition (goal and plan will be removed).
		getBeliefbase().getBelief("drop").setFact(Boolean.TRUE);
		try
		{
			waitForGoal(goal); // wait till goal is dropped.
		}
		catch(GoalFailureException gfe){}
		report	= new TestReport("trigger_drop", "Triggering goal drop condition", true, null);
		if(goal.getLifecycleState()!=GoalLifecycleState.DROPPED)
		{	
			report.setFailed("Goal not dropped: "+goal.getLifecycleState());
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);
		
		
		// Now triggering goal creation again (plan will also be created).
		getBeliefbase().getBelief("drop").setFact(Boolean.FALSE);
		getBeliefbase().getBelief("creation").setFact(Boolean.FALSE);
		getBeliefbase().getBelief("creation").setFact(Boolean.TRUE);
		
		waitFor(10); // Hack! Wait for consequences being executed :-(
		
		goal = getGoalbase().getGoals("test")[0];
		report	= new TestReport("trigger_creation2", "Triggering goal creation again", true, null);
		if(getGoalbase().getGoals("test").length!=1)
		{
			report.setFailed("Goal does not exist");
		}
//		else if(getPlanbase().getPlans().length!=2)
//		{
//			report.setFailed("Wrong planbase contents");
//		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);
		
		
		// Now invalidating goal context to abort plan.
		getBeliefbase().getBelief("context").setFact(Boolean.FALSE);
//		waitFor(10); // Hack! Wait for consequences being executed :-(
		report	= new TestReport("trigger_context2", "Invalidating goal context", true, null);
		if(getGoalbase().getGoals("test").length!=1)
		{
			report.setFailed("Goal does not exist");
		}
		else if(goal.getLifecycleState()!=GoalLifecycleState.SUSPENDED)
		{
//			report.setFailed("Goal not option: "+goal.getLifecycleState());
			report.setFailed("Goal not suspended: "+goal.getLifecycleState());
		}
//		else if(getPlanbase().getPlans().length!=1)
//		{
//			report.setFailed("Wrong planbase contents");
//		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);

		
		// Now triggering goal context again to restart plan.
		getBeliefbase().getBelief("context").setFact(Boolean.TRUE);
//		waitFor(10); // Hack! Wait for consequences being executed :-(
		report	= new TestReport("trigger_context3", "Triggering goal context again", true, null);
		if(getGoalbase().getGoals("test").length!=1)
		{
			report.setFailed("Goal does not exist");
		}
		else if(goal.getLifecycleState()!=GoalLifecycleState.ACTIVE)
		{
			report.setFailed("Goal not active: "+goal.getLifecycleState());
		}
//		else if(getPlanbase().getPlans().length!=2)
//		{
//			report.setFailed("Wrong goalbase contents");
//		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);
	}
}
