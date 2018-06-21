package jadex.bdi.testcases.semiautomatic;

import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;

/**
 *  This plan performs an illegal action. 
 */
public class ErrorPlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
//		IGoal	goal	= getExternalAccess().createGoal("testgoal");
//		getExternalAccess().dispatchTopLevelGoal(goal);
//		getExternalAccess().dispatchTopLevelGoal(goal);

		IGoal	goal	= createGoal("testgoal");
		dispatchTopLevelGoal(goal);
		dispatchTopLevelGoal(goal);
	}
}
