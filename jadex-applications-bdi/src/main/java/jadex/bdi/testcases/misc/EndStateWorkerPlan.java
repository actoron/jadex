package jadex.bdi.testcases.misc;

import jadex.base.test.TestReport;
import jadex.bdiv3.runtime.impl.GoalFailureException;
import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.fipa.SFipa;
import jadex.commons.concurrent.TimeoutException;

import java.util.ArrayList;
import java.util.List;

/**
 *  Check correct operation of end states.
 */
public class EndStateWorkerPlan extends Plan
{
	/**
	 *  Plan body.
	 */
	public void body()
	{
		// Plan will be aborted when agent is killed.
//		waitFor(IFilter.NEVER);
		waitForEver();
	}

	/**
	 *  Method is called when the agent terminates.
	 */
	public void aborted()
	{
		List	reports	= new ArrayList();
		
		// Test abortion of agenda actions (goal and plan creation actions should have been aborted).
		waitFor(20);
		TestReport	report	= new TestReport("agenda_action", "Test if goal creation action has not been executed due to invalid precondition.");
		if(getGoalbase().getGoals("testgoal").length==0)
		{
			report.setSucceeded(true);
		}
		else
		{
			report.setFailed("Goal was created.");
		}
		reports.add(report);

		report	= new TestReport("agenda_action2", "Test if plan creation action has not been executed due to invalid precondition.");
		if(getPlanbase().getPlans("dummy_plan").length==0)
		{
			report.setSucceeded(true);
		}
		else
		{
			report.setFailed("Plan was created.");
		}
		reports.add(report);
		
		// Test deactivation of creation/trigger conditions.
		getBeliefbase().getBelief("trigger").setFact(Boolean.FALSE);
		getBeliefbase().getBelief("trigger").setFact(Boolean.TRUE);
		waitFor(20);

		report	= new TestReport("goal_condition", "Test if goal creation conditions are disabled in end state.");
		if(getGoalbase().getGoals("testgoal").length==0)
		{
			report.setSucceeded(true);
		}
		else
		{
			report.setFailed("Goal was created.");
		}
		reports.add(report);
		
		report	= new TestReport("plan_condition", "Test if plan trigger conditions are disabled in end state.");
		if(getPlanbase().getPlans("dummy_plan").length==0)
		{
			report.setSucceeded(true);
		}
		else
		{
			report.setFailed("Plan was created.");
		}
		reports.add(report);
		
		// Test if manual creation of goal and activation of plan still works.
		report	= new TestReport("manual_goal", "Test if manual creation of goal and activation of plan still works.");
		try
		{
			dispatchSubgoalAndWait(createGoal("testgoal"), 200);
			report.setSucceeded(true);
		}
		catch(GoalFailureException e)
		{
			report.setFailed("Goal execution failed.");
		}
		catch(TimeoutException e)
		{
			report.setFailed("Timeout occurred.");
		}
		reports.add(report);

		// Wait for testcases of end state elements.
		try
		{
			waitForCondition("end_tests_finished", 1000);
		}
		catch(TimeoutException e)
		{
		}

		TestReport[]	areports	= (TestReport[])getBeliefbase().getBeliefSet("reports").getFacts();
		for(int i=0; i<areports.length; i++)
		{
			if(!areports[i].isSucceeded())
			{
				areports[i].setFailed("End element was not created");
			}
			reports.add(areports[i]);
		}
		
		// Finally send reports to test agent.
		IMessageEvent	msg	= createMessageEvent("inform_reports");
		msg.getParameter(SFipa.CONTENT).setValue(reports);
		sendMessage(msg).get();
	}
}
