package jadex.bdiv3.example.counter;

import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalDropCondition;
import jadex.bdiv3.annotation.GoalTargetCondition;
import jadex.bdiv3.model.MGoal;
import jadex.rules.eca.annotations.Event;

/**
 * 
 */
@Goal(excludemode=MGoal.EXCLUDE_NEVER)
public class CountGoal
{
	protected int target;
	protected int drop;
	
	/**
	 *  Create a new count goal.
	 */
	public CountGoal(int target, int drop)
	{
		this.target = target;
		this.drop = drop;
	}
	
	@GoalTargetCondition
	protected boolean target(@Event("counter") int cnt)
	{
		System.out.println("check target: "+cnt);
		return cnt==target;
	}
	
	@GoalDropCondition
	protected boolean drop(@Event("counter") int cnt)
	{
		return cnt==drop;
	}
}