package jadex.bdi.model;


/**
 *  Trigger for plans.
 */
public interface IMPlanTrigger	extends IMElement
{
	/**
	 *  Get the goal events.
	 */
	public IMTriggerReference[]	getGoals();
	
	/**
	 *  Get the trigger condition.
	 */
	public IMCondition	getCondition();
}
