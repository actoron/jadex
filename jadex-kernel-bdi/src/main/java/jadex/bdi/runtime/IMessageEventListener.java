package jadex.bdi.runtime;

/**
 * Listener for observing message.
 */
public interface IMessageEventListener
{
	/**
	 *  Invoked when a message event has been received.
	 *  @param ae The agent event.
	 */
	public void messageEventReceived(AgentEvent ae);
	
	/**
	 *  Invoked when a reply was received.
	 *  @param ae The agent event.
	 */
	//public void replyReceived(AgentEvent ae);
	
	/**
	 *  Invoked when a message event has been received.
	 *  @param ae The agent event.
	 */
	public void messageEventSent(AgentEvent ae);
}
