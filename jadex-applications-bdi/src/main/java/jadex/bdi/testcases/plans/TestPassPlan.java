package jadex.bdi.testcases.plans;

import jadex.base.test.TestReport;
import jadex.bdiv3.runtime.impl.GoalFailureException;
import jadex.bdiv3x.runtime.Plan;

/**
 *  Test the passed() method.
 */
public class TestPassPlan extends Plan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		// Empty plan body passes immediately.
	}

	/**
	 *  Called when the plan has passed.
	 */
	// Todo: merge with TestAbortPlan
	public void passed()
	{
		getLogger().info("Plan passing...");
		
		/*RCapability	scope	= (RCapability)((ElementWrapper)getScope()).unwrap();
		RPlan	rplan	= scope.getPlanbase().getPlan(getName());
		
		// Check for correct agenda action.
		TestReport	tr = new TestReport("#1", "Tests plan passing.");
		IAgendaAction	action	= scope.getAgent().getInterpreter().getCurrentAgendaEntry().getAction();
		if(action instanceof ExecutePlanStepAction && ((ExecutePlanStepAction)action).getPlan().equals(rplan))
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setFailed("passed() not executed during corresponding plan cleanup action.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
		
		
		// Wait and check again.
		getLogger().info("Waiting in passed...");
		waitFor(50);
		getLogger().info("Waiting finished...");
		tr = new TestReport("#2", "Tests waitFor() in plan passing.");
		action	= scope.getAgent().getInterpreter().getCurrentAgendaEntry().getAction();
		if(action instanceof ExecutePlanStepAction && ((ExecutePlanStepAction)action).getPlan().equals(rplan))
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setFailed("passed() not executed during corresponding plan cleanup action.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);*/
		
		
		// Dispatch goal and check again (should succeed without plan).
		getLogger().info("Successful subgoal in passed...");
		TestReport tr = new TestReport("#3", "Tests successful subgoal in plan passing.");
		try
		{
			dispatchSubgoalAndWait(createGoal("successgoal"));
			tr.setSucceeded(true);
			getLogger().info("Goal succeeded...");
		}
		catch(GoalFailureException gfe)
		{
			tr.setFailed("Goal passed when expected to succeed.");
			getLogger().info("Goal passed...");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
		
		
		// Dispatch goal and check again (should fail without plan).
		getLogger().info("Failed subgoal in passed...");
		tr = new TestReport("#4", "Tests failed subgoal in plan passing.");
		try
		{
			dispatchSubgoalAndWait(createGoal("failuregoal"));
			tr.setFailed("Goal succeeded when expected to fail.");
			getLogger().info("Goal succeeded...");
		}
		catch(GoalFailureException gfe)
		{
			tr.setSucceeded(true);
			getLogger().info("Goal failed...");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
		
		
		// Dispatch goal and check again (should succeed with plan).
		getLogger().info("Succesful subgoal with plan in passed...");
		tr = new TestReport("#5", "Tests successful subgoal with plan in plan passing.");
		try
		{
			dispatchSubgoalAndWait(createGoal("successgoal2"));
			tr.setSucceeded(true);
			getLogger().info("Goal succeeded...");
		}
		catch(GoalFailureException gfe)
		{
			tr.setFailed("Goal failed when expected to succeed.");
			getLogger().info("Goal failed...");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
		
		
		// Dispatch goal and check again (should fail with plan).
		getLogger().info("Failed subgoal with plan in passed...");
		tr = new TestReport("#6", "Tests failed subgoal with plan in plan passed.");
		try
		{
			dispatchSubgoalAndWait(createGoal("failuregoal2"));
			tr.setFailed("Goal succeeded when expected to fail.");
			getLogger().info("Goal succeeded...");
		}
		catch(GoalFailureException gfe)
		{
			tr.setSucceeded(true);
			getLogger().info("Goal failed...");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}
}
