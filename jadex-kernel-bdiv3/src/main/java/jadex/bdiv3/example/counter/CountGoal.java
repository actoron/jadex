package jadex.bdiv3.example.counter;

import jadex.bdiv3.annotation.Goal;
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
	
	/**
	 *  Create a new count goal.
	 */
	public CountGoal(int target)
	{
		this.target = target;
	}
	
	@GoalTargetCondition
//	@Condition("target")
	protected boolean target(@Event("counter") int cnt)
	{
		return cnt==target;
	}
}