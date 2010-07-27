package jadex.bdi.model.editable;

import jadex.bdi.model.IMMetaGoal;

/**
 * 
 */
public interface IMEMetaGoal extends IMMetaGoal, IMEGoal 
{
	/**
	 *  Create the trigger.
	 *  @return The trigger.
	 */
	public IMEMetaGoalTrigger createTrigger();
}
