package jadex.bridge;


/**
 *  Tool adapters allow to intercept
 *  points in the agent execution
 *  and alter the execution behavior.
 */
public interface IToolAdapter
{
	/**
	 *  Initialize the tool adapter.
	 *  Called once, before any other methods are invoked.
	 */
	public void	init(IKernelAgent adapter);

	/**
	 *  Called when the agent sent a message.
	 */
	public void	messageSent(IMessageAdapter msg);
	
	/**
	 *  Called when the agent receives a message.
	 *  May be called from external (i.e. non-agent) threads.
	 *  The methods return value indicates if the message is
	 *  handled by the tool ("tool message") and
	 *    should not be propagated to the agent itself.
	 *  @return True, when the message was handled by the tool and
	 *    should not be propagated to the agent itself.
	 */
	public boolean	messageReceived(IMessageAdapter msg);

	/**
	 *  Called when the agent is about to execute a step
	 *  ("agenda action").
	 *  Always called on the agent thread.
	 *  The methods return value indicates if the agent
	 *  is allowed to execute a step. If some tool
	 *  prevents the execution of steps, the agent will be
	 *  blocked until it is released by the tool.
	 *  
	 *  Messages are still received by tools and a blocking
	 *  tool should call wakeup() on the agent, once the
	 *  agent may continue to run.
	 *  
	 *  @return True, when the agent is allowed to execute
	 *    i.e. not blocked.
	 */
	public boolean	executeAction();
}