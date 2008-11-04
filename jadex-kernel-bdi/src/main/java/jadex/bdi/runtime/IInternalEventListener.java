package jadex.bdi.runtime;

/**
 *  Listener for observing internal event.
 */
public interface IInternalEventListener
{
	/**
	 *  Invoked when an internal event occurred.
	 *  @param ae The agent event.
	 */
	public void internalEventOccurred(AgentEvent ae);
}
