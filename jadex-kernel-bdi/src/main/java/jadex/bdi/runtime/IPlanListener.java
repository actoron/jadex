package jadex.bdi.runtime;

/**
 *  Listener for observing plans.
 */
public interface IPlanListener
{
	/**
	 *  Invoked when a new plan has been added.
	 *  @param ae The agent event.
	 */
	public void planAdded(AgentEvent ae);
	
	/**
	 *  Invoked when a plan has been finished.
	 *  @param ae The agent event.
	 */
	public void planFinished(AgentEvent ae);
	
	// todo: plan state changed? plan executes step?
}
