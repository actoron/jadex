package jadex.micro;

import jadex.bridge.MessageType;
import jadex.commons.future.IFuture;

import java.util.Map;

/**
 *  This is a base interface for a minimal kernel agent.
 *  All methods are called on agent thread (i.e. not concurrently)
 */
public interface IMicroAgent
{
	/**
	 *  Called once after agent creation.
	 *  Creation is considered done when the returned future is finished.
	 */
	public IFuture	agentCreated();
	
	/**
	 *  Called once after agent has been started.
	 */
	public void executeBody();
	
	/**
	 *  Called, whenever a message is received.
	 *  @param msg The message map.
	 *  @param mt The message type.
	 */
	public void messageArrived(Map msg, MessageType mt);

	/**
	 *  Called just before the agent is removed from the platform.
	 *  Deletion is considered done when the returned future is finished.
	 */
	public IFuture	agentKilled();
	
}

