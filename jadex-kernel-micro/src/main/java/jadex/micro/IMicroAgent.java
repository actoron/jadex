package jadex.micro;

import jadex.bridge.service.types.message.MessageType;
import jadex.commons.future.IFuture;

import java.util.Map;

/**
 *  This is a base interface for a minimal kernel agent.
 *  All methods are called on agent thread (i.e. not concurrently)
 */
//todo change interface from methods to be implemented to methods that may be called
// currently interface is not useful for pojo micro agents
public interface IMicroAgent
{
	/**
	 *  Called once after agent creation.
	 *  Creation is considered done when the returned future is finished.
	 */
	public IFuture<Void> agentCreated();
	
	/**
	 *  Called once after agent has been started.
	 */
	public IFuture<Void> executeBody();
	
	/**
	 *  Called, whenever a message is received.
	 *  @param msg The message map.
	 *  @param mt The message type.
	 */
	public void messageArrived(Map<String, Object> msg, MessageType mt);

	/**
	 *  Called just before the agent is removed from the platform.
	 *  Deletion is considered done when the returned future is finished.
	 */
	public IFuture<Void>	agentKilled();
	
}

