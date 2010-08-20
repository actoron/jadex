package jadex.micro;

import jadex.bridge.IExternalAccess;
import jadex.bridge.MessageType;
import jadex.commons.ICommand;
import jadex.commons.IFuture;
import jadex.commons.IResultCommand;

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
	public IFuture sendMessage(Map me, MessageType mt);

	/**
	 *  Schedule a step of the agent.
	 *  May safely be called from external threads.
	 *  @param step	Code to be executed as a step of the agent.
	 */
	public IFuture scheduleStep(ICommand com);
	
	/**
	 *  Schedule a step of the agent.
	 *  May safely be called from external threads.
	 *  @param step	Code to be executed as a step of the agent.
	 *  @return The result of the step.
	 */
	public IFuture scheduleResultStep(IResultCommand com);
	
}
