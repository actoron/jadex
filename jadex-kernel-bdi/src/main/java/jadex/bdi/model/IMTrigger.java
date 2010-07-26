package jadex.bdi.model;

/**
 *  Base trigger for plans, waitqueue, etc.
 */
public interface IMTrigger	extends IMElement
{
	/**
	 *  Get the internal events.
	 */
	public IMTriggerReference[]	getInternalEvents();
	
	/**
	 *  Get the message events.
	 */
	public IMTriggerReference[]	getMessageEvents();
	
	/**
	 *  Get the goal finished events.
	 */
	public IMTriggerReference[]	getGoalFinisheds();
	
	/**
	 *  Get the fact added triggers (belief set names).
	 */
	public String[]	getFactAddeds();
	
	/**
	 *  Get the fact added triggers (belief set names).
	 */
	public String[]	getFactRemoveds();
	
	/**
	 *  Get the fact added triggers (belief set names).
	 */
	public String[]	getFactChangeds();
}
