package jadex.bdi.model.editable;

import jadex.bdi.model.IMAchieveGoal;



/**
 * 
 */
public interface IMEAchieveGoal extends IMEGoal, IMAchieveGoal
{
	/**
	 *  Create the target condition.
	 *  @return The target condition.
	 */
	public IMECondition createTargetCondition();
}
