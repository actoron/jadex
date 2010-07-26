package jadex.bdi.model.editable;

import jadex.bdi.model.IMMetaGoal;
import jadex.bdi.model.IMMetaGoalTrigger;

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
