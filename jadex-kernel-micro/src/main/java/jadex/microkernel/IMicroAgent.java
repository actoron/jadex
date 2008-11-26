package jadex.microkernel;

import java.util.Map;

import jadex.bridge.MessageType;

/**
 *  This is a base interface for a minimal Jadex agent.
 *  All methods are called on agent thread (i.e. not concurrently)
 */
public interface IMicroAgent
{
	/**
	 *  Called when the agent is born and whenever it wants to execute an action
	 *  (e.g. calls wakeup() in one of the other methods).
	 *  The platform guarantees that executeAction() will not be called in parallel. 
	 *  @return True, when there are more actions waiting to be executed. 
	 */
	public boolean executeAction();

	/**
	 *  Called, whenever a message is received.
	 *  @param msg The message map.
	 *  @param mt The message type.
	 */
	public void messageArrived(Map msg, MessageType mt);

	/**
	 *  Called just before the agent is removed from the platform.
	 */
	public void agentKilled();
}

