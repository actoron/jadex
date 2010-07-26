package jadex.bdi.model.editable;

import jadex.bdi.model.IMCondition;
import jadex.bdi.model.IMQueryGoal;

/**
 * 
 */
public interface IMEQueryGoal extends IMEGoal, IMQueryGoal
{
	/**
	 *  Create the maintain condition.
	 *  @return The maintain condition.
	 */
	public IMCondition createMaintainCondition();
	
	/**
	 *  Create the target condition.
	 *  @return The target condition.
	 */
	public IMCondition createTargetCondition();
}
