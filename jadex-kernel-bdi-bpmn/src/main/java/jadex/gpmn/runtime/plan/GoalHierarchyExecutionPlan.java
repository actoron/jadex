package jadex.gpmn.runtime.plan;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

/**
 *  Create subgoals according to the goal hierarchy specified in plan parameters.
 *	The 'subgoals' parameter set denotes the names of goals to create.
 *  The 'mode' parameter specifies the execution mode ('sequential' or 'parallel').   
 */
public class GoalHierarchyExecutionPlan extends Plan
{
	/**
	 *  Plan body.
	 */
	public void body()
	{
		String[]	subgoals	= (String[]) getParameterSet("subgoals").getValues();
		String	mode	= (String) getParameter("mode").getValue();
		
		if("parallel".equals(mode))
		{
			IGoal[]	goals	= new IGoal[subgoals.length];
			for(int i=0; i<subgoals.length; i++)
			{
				goals[i]	= createGoal(subgoals[i]);
				dispatchSubgoal(goals[i]);
			}
			for(int i=0; i<goals.length; i++)
			{
				waitForGoal(goals[i]);
			}
		}
		else
		{
			for(int i=0; i<subgoals.length; i++)
			{
				IGoal	subgoal	= createGoal(subgoals[i]);
				dispatchSubgoalAndWait(subgoal);
			}
		}
	}
}
