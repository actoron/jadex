package jadex.micro;

import jadex.bridge.IExternalAccess;
import jadex.bridge.MessageType;
import jadex.commons.IFuture;

import java.util.Map;

/**
 *  External access interface for micro agents.
 */
public interface IMicroExternalAccess	extends IExternalAccess
{
	/**
	 *  Send a message.
	 *  @param me	The message.
	 *  @param mt	The message type.
	 */
	public void	sendMessage(Map me, MessageType mt);

	/**
	 *  Schedule a step of the agent.
	 *  May safely be called from external threads.
	 *  @param step	Code to be executed as a step of the agent.
	 */
	public void	scheduleStep(Runnable step);
	
	// HACK!!! todo: remove me
	/**
	 *  Get the agent implementation.
	 *  Operations on the agent object
	 *  should be properly synchronized with invokeLater()!
	 */
	public IFuture getAgent();
}
