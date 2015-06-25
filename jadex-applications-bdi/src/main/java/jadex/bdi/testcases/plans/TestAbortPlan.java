package jadex.bdi.testcases.plans;

import jadex.base.test.TestReport;
import jadex.bdiv3.runtime.impl.GoalFailureException;
import jadex.bdiv3x.runtime.Plan;

/**
 *  This plan just waits until it is aborted.
 */
public class TestAbortPlan extends Plan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		getLogger().info("Waiting: "+this);
//		waitFor(IFilter.NEVER);
		waitForEver();
	}

	/**
	 *  Called when the plan is aborted.
	 */
	public void aborted()
	{
		getLogger().info("Plan aborting...");
		
		/*RCapability	scope	= (RCapability)((ElementWrapper)getScope()).unwrap();
		RPlan	rplan	= scope.getPlanbase().getPlan(getName());
		
		// Check for correct agenda action.
		TestReport	tr = new TestReport("#1", "Tests plan abortion.");
		IAgendaAction	action	= scope.getAgent().getInterpreter().getCurrentAgendaEntry().getAction();
		if(action instanceof ExecutePlanStepAction && ((ExecutePlanStepAction)action).getPlan().equals(rplan))
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setFailed("aborted() not executed during corresponding plan cleanup action.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
		
		
		// Wait and check again.
		getLogger().info("Waiting in abort...");
		waitFor(50);
		getLogger().info("Waiting finished...");
		tr = new TestReport("#2", "Tests waitFor() in plan abortion.");
		action	= scope.getAgent().getInterpreter().getCurrentAgendaEntry().getAction();
		if(action instanceof ExecutePlanStepAction && ((ExecutePlanStepAction)action).getPlan().equals(rplan))
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setFailed("aborted() not executed during corresponding plan cleanup action.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
		*/
		
		// Dispatch goal and check again (should succeed without plan).
		getLogger().info("Successful subgoal in abort...");
		TestReport tr = new TestReport("#3", "Tests successful subgoal in plan abortion.");
		try
		{
			dispatchSubgoalAndWait(createGoal("successgoal"));
			tr.setSucceeded(true);
			getLogger().info("Goal succeeded...");
		}
		catch(GoalFailureException gfe)
		{
			tr.setFailed("Goal failed when expected to succeed.");
			getLogger().info("Goal failed...");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
		
		
		// Dispatch goal and check again (should fail without plan).
		getLogger().info("Failed subgoal in abort...");
		tr = new TestReport("#4", "Tests failed subgoal in plan abortion.");
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
		getLogger().info("Succesful subgoal with plan in abort...");
		tr = new TestReport("#5", "Tests successful subgoal with plan in plan abortion.");
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
		getLogger().info("Failed subgoal with plan in abort...");
		tr = new TestReport("#6", "Tests failed subgoal with plan in plan abortion.");
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
