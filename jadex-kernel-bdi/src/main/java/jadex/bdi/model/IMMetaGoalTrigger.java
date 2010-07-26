package jadex.bdi.model;


/**
 *  Trigger for meta goals.
 */
public interface IMMetaGoalTrigger	extends IMElement
{
	/**
	 *  Get the goal events.
	 */
	public IMTriggerReference[]	getGoals();
}
