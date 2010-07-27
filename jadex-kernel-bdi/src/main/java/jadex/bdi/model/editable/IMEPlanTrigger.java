package jadex.bdi.model.editable;

import jadex.bdi.model.IMPlanTrigger;

/**
 *  Editable interface for plan trigger.
 */
public interface IMEPlanTrigger	extends IMPlanTrigger, IMEElement
{
	/**
	 *  Create a goal event.
	 *  @param reference	The referenced goal.
	 */
	public IMETriggerReference	createGoal(String reference);
	
	/**
	 *  Create the trigger condition.
	 *  @param expression	The expression.
	 *  @param language	The expression language (or null for default java-like language).
	 *  @return The trigger condition.
	 */
	public IMECondition	createCondition(String expression, String language);
}
