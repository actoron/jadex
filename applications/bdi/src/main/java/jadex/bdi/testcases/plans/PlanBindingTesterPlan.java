package jadex.bdi.testcases.plans;

import jadex.base.test.TestReport;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.impl.GoalFailureException;
import jadex.bdiv3x.runtime.Plan;

/**
 *
 */
public class PlanBindingTesterPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		TestReport tr = new TestReport("#1", "Test plan binding parameter");

		IGoal goal = createGoal("print");
		try
		{
			dispatchSubgoalAndWait(goal);
		}
		catch(GoalFailureException e)
		{
		}

		int a = ((Integer)getBeliefbase().getBelief("a").getFact()).intValue();
		int b = ((Integer)getBeliefbase().getBelief("b").getFact()).intValue();
		int c = ((Integer)getBeliefbase().getBelief("c").getFact()).intValue();

		if(a==1 && b==1 && c==1)
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setReason("Not all bindings have been used.");
		}
		getLogger().fine(a+" "+b+" "+c);
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}
}
