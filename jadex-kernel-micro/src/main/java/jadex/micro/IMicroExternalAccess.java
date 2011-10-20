package jadex.micro;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.future.IFuture;

import java.util.Map;

/**
 *  External access interface for micro agents.
 */
@Reference
public interface IMicroExternalAccess	extends IExternalAccess
{
	/**
	 *  Send a message.
	 *  @param me	The message.
	 *  @param mt	The message type.
	 */
	public IFuture sendMessage(Map me, MessageType mt);

//	/**
//	 *  Schedule a step of the agent.
//	 *  May safely be called from external threads.
//	 *  @param step	Code to be executed as a step of the agent.
//	 */
//	public IFuture scheduleStep(ICommand com);
	
//	/**
//	 *  Schedule a step of the agent.
//	 *  May safely be called from external threads.
//	 *  @param step	Code to be executed as a step of the agent.
//	 *  @return The result of the step.
//	 */
//	public IFuture scheduleResultStep(IResultCommand com);
	
	// todo: support with IResultCommand also?!
	/**
	 *  Wait for an specified amount of time.
	 *  @param time The time.
	 *  @param run The runnable.
	 */
	public IFuture waitFor(final long time, IComponentStep step);
	
	// todo: support with IResultCommand also?!
	/**
	 *  Wait for the next tick.
	 *  @param time The time.
	 */
	public IFuture waitForTick(IComponentStep step);
	
}
