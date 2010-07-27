package jadex.bdi.model.editable;

import jadex.bdi.model.IMMetaGoalTrigger;

/**
 *  Editable interface for a meta goal trigger
 */
public interface IMEMetaGoalTrigger	extends IMMetaGoalTrigger, IMETrigger
{
	/**
	 *  Add a goal.
	 *  @param reference	The referenced goal.
	 */
	public IMETriggerReference	createGoal(String reference);
}
