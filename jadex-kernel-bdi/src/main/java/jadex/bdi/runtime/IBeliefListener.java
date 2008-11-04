package jadex.bdi.runtime;

/**
 *  Listener for observing beliefs.
 */
public interface IBeliefListener
{
	/**
	 *  Invoked when a belief has been changed.
	 *  @param ae The agent event.
	 */ 
	public void beliefChanged(AgentEvent ae);
}
