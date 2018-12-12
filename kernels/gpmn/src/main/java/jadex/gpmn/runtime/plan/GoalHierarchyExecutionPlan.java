package jadex.gpmn.runtime.plan;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.commons.SUtil;

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
		
		System.out.println("Goal decomposition: "+mode+SUtil.arrayToString(subgoals));

		if("parallel".equals(mode))
		{
			IGoal[]	goals	= new IGoal[subgoals.length];
			for(int i=0; i<subgoals.length; i++)
			{
				System.out.println("Creating Goal: "+subgoals[i]);
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
				System.out.println("Creating Goal: "+subgoals[i]);
				if(subgoals[i]!=null)
				{
					IGoal	subgoal	= createGoal(subgoals[i]);
					dispatchSubgoalAndWait(subgoal);
				}
			}
		}
	}
	
	public void passed()
	{
		System.out.println("Passed: "+this+" "+getParameter("mode").getValue()+SUtil.arrayToString(getParameterSet("subgoals").getValues()));
	}
	public void failed()
	{
		System.out.println("Failed: "+this+" "+getParameter("mode").getValue()+SUtil.arrayToString(getParameterSet("subgoals").getValues())+", "+getException());
	}
	public void aborted()
	{
		System.out.println("Aborted: "+this+" "+getParameter("mode").getValue()+SUtil.arrayToString(getParameterSet("subgoals").getValues()));
	}
}
