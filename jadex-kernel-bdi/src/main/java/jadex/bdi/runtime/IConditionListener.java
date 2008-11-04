package jadex.bdi.runtime;

/**
 * Listener for observing conditions.
 */
public interface IConditionListener
{
	/**
	 *  Invoked when a condition has triggered.
	 *  @param ae The agent event.
	 */
	public void	conditionTriggered(AgentEvent ae);
}
