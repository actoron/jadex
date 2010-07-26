package jadex.bdi.model;

/**
 *  Interface for meta goals.
 */
public interface IMMetaGoal extends IMGoal //extends IMQueryGoal ?
{
	/**
	 *  Get the trigger.
	 *  @return The trigger.
	 */
	public IMMetaGoalTrigger getTrigger();
}
