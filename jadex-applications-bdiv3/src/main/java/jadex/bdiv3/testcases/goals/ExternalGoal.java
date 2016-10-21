package jadex.bdiv3.testcases.goals;

import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalParameter;
import jadex.bdiv3.annotation.GoalTargetCondition;

/**
 *  A test goal.
 */
@Goal
public class ExternalGoal
{
	@GoalParameter
	protected int cnt;
	
	/**
	 *  Create a new goal.
	 */
	public ExternalGoal(int cnt)
	{
		this.cnt = cnt;
	}
	
	/**
	 *  Check the target condition.
	 */
	@GoalTargetCondition
	protected boolean checkTarget()
	{
		return cnt == 2;
	}
	
	/**
	 *  Decrease the cnt.
	 */
	public void decrease()
	{
		System.out.println("cnt was: "+cnt);
		cnt--;
	}
}
