package jadex.bdi.testcases.goals;

import jadex.base.test.TestReport;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bdi.runtime.TimeoutException;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;

/**
 *  Check test cases of goal inhibition agent.
 */
public class GoalInhibitionPlan extends Plan
{

	public void body()
	{
		// Wait for the maintain goal to execute.
		// As count is incremented by 3, but decremented by 1
		// count==5 means that the maintain goal is in process.
		TestReport	report	= new TestReport("maintain_triggered", "Wait for maintain goal to be triggered.");
		try
		{
//			waitForCondition("$beliefbase.count==5", 1000);
			waitForCondition("countis5");
			report.setSucceeded(true);
		}
		catch(TimeoutException e)
		{
			report.setReason("Timeout occurred.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);

		// Check that the perform goal is inhibited (lifecyclestate==option).
		report	= new TestReport("perform_inhibited", "Check if perform goal is inhibited.");
		IGoal[]	docnts	= getGoalbase().getGoals("docnt");
		if(docnts.length==1)
		{
			if(OAVBDIRuntimeModel.GOALLIFECYCLESTATE_OPTION.equals(docnts[0].getLifecycleState()))
			{
				report.setSucceeded(true);
			}
			else
			{
				report.setReason("Wrong lifecycle state (expected "+OAVBDIRuntimeModel.GOALLIFECYCLESTATE_OPTION+"): "+docnts[0].getLifecycleState());
			}
		}
		else
		{
			report.setReason("Wrong number of perform goals: "+docnts.length);
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);

		// Wait for the perform goal to execute again.
		report	= new TestReport("perform_reactivated", "Wait for perform goal to be execute again.");
		try
		{
//			waitForCondition("$beliefbase.count==6", 1000);
			waitForCondition("countis6");
			report.setSucceeded(true);
		}
		catch(TimeoutException e)
		{
			report.setReason("Timeout occurred.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);
	}
}
