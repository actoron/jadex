package jadex.bdiv3.testcases.semiautomatic;

import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalDropCondition;
import jadex.bdiv3.annotation.GoalTargetCondition;
import jadex.bdiv3.model.MProcessableElement.ExcludeMode;
import jadex.rules.eca.annotations.Event;

/**
 *  Goal with target and drop condition.
 */
@Goal(excludemode=ExcludeMode.Never)
public class CountGoal
{
	/** The target value. */
	protected int target;
	
	/** The drop value. */
	protected int drop;
	
	/**
	 *  Create a new count goal.
	 */
	public CountGoal(int target, int drop)
	{
		this.target = target;
		this.drop = drop;
	}
	
	/**
	 *  Called whenever the counter belief changes.
	 */
	@GoalTargetCondition
	protected boolean target(@Event("counter") int cnt)
	{
		System.out.println("check target: "+cnt);
		return cnt==target;
	}
	
	/**
	 *  Called whenever the counter belief changes.
	 */
	@GoalDropCondition
	protected boolean drop(@Event("counter") int cnt)
	{
		return cnt==drop;
	}
}