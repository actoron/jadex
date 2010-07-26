package jadex.bdi.model;

/**
 *  Interface for maintain goals.
 */
public interface IMMaintainGoal extends IMGoal
{
	/**
	 *  Get the maintain condition.
	 *  @return The maintain condition.
	 */
	public IMCondition getMaintainCondition();
	
	/**
	 *  Get the target condition.
	 *  @return The target condition.
	 */
	public IMCondition getTargetCondition();
}
