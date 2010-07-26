package jadex.bdi.model;

/**
 *  Interface for achieve goals.
 */
public interface IMAchieveGoal extends IMGoal
{
	/**
	 *  Get the target condition.
	 *  @return The target condition.
	 */
	public IMCondition getTargetCondition();
}
