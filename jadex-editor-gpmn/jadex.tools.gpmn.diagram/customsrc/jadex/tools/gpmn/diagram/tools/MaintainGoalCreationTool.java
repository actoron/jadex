package jadex.tools.gpmn.diagram.tools;

import jadex.tools.gpmn.GoalType;

public class MaintainGoalCreationTool extends AbstractGoalCreationTool
{
	@Override
	public GoalType getGoalType()
	{
		return GoalType.MAINTAIN_GOAL;
	}
}
