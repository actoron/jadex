package jadex.bdi.model.editable;

import jadex.bdi.model.IMAchieveGoal;



/**
 * 
 */
public interface IMEAchieveGoal extends IMAchieveGoal, IMEGoal 
{
	/**
	 *  Create the target condition.
	 *  @return The target condition.
	 */
	public IMECondition createTargetCondition(String content, String lang);
}
