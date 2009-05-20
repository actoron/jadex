package jadex.microkernel;

import jadex.bridge.MessageType;

import java.util.Map;

/**
 *  External access interface for micro agents.
 */
public interface IExternalAccess
{
	/**
	 *  Send a message.
	 *  @param me	The message.
	 *  @param mt	The message type.
	 */
	public void	sendMessage(Map me, MessageType mt);

	/**
	 *  Invoke some code on the agent thread.
	 *  This method queues the runnable in the agent
	 *  and immediately return (i.e. probably before
	 *  the runnable has been executed).
	 */
	public void invokeLater(Runnable runnable);

	/**
	 *  Get the agent implementation.
	 *  Operations on the agent object
	 *  should be properly synchronized with invokeLater()!
	 */
	public IMicroAgent	getAgent();
}
