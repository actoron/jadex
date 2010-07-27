package jadex.bdi.model.editable;

import jadex.bdi.model.IMTrigger;

/**
 *  Editable interface for a basic trigger (e.g. wait queue).
 */
public interface IMETrigger	extends IMTrigger, IMEElement
{
	/**
	 *  Create an internal event reference.
	 *  @param reference	The name of the referenced element.
	 */
	public IMETriggerReference	createInternalEvent(String reference);
	
	/**
	 *  Create a message event reference.
	 *  @param reference	The name of the referenced element.
	 */
	public IMETriggerReference	createMessageEvent(String reference);
	
	/**
	 *  Create a goal finished event reference.
	 *  @param reference	The name of the referenced element.
	 */
	public IMETriggerReference	createGoalFinishedEvent(String reference);
	
	/**
	 *  Create a fact added trigger.
	 *  @param reference	The name of the referenced element.
	 */
	public void	createFactAdded(String reference);
	
	/**
	 *  Create a fact removed trigger.
	 *  @param reference	The name of the referenced element.
	 */
	public void	createFactRemoved(String reference);
	
	/**
	 *  Create a fact changed trigger.
	 *  @param reference	The name of the referenced element.
	 */
	public void	createFactChanged(String reference);	
}
