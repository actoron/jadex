package jadex.bridge;


/**
 *  The adapter for a specific platform agent (e.g. a JADE agent).
 *  These are the methods a Jadex agents needs to call on its host agent.
 */
public interface IAgentAdapter
{
	/**
	 *  Called by the agent when it probably awoke from an idle state.
	 *  The platform has to make sure that the agent will be executed
	 *  again from now on.
	 *  Note, this method can be called also from external threads
	 *  (e.g. property changes). Therefore, on the calling thread
	 *  no agent related actions must be executed (use some kind
	 *  of wake-up mechanism).
	 *  Also proper synchronization has to be made sure, as this method
	 *  can be called concurrently from different threads.
	 */
	public void	wakeup()	throws AgentTerminatedException;

	/**
	 *  Cause termination of the agent.
	 *  IJadexAgent.killAgent(IResultListener) will be
	 *  called in turn.
	 */
	public void killAgent()	throws AgentTerminatedException;

	/**
	 *  Get the agent platform.
	 *  @return The agent platform.
	 */
	public IPlatform getPlatform()	throws AgentTerminatedException;

	/**
	 *  Return the native agent-identifier that allows to send
	 *  messages to this agent.
	 */
	public IAgentIdentifier getAgentIdentifier() throws AgentTerminatedException;
}

