package jadex.bdiv3.tutorial.stamp;

import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalParameter;
import jadex.bridge.IComponentIdentifier;


@Goal
public class StampGoal
{
	@GoalParameter
	protected IComponentIdentifier	wp;

	@GoalParameter
	protected String				text;

	public StampGoal(IComponentIdentifier wp, String text)
	{
		this.wp = wp;
		this.text = text;
	}
}
