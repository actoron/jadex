package jadex.bdi.runtime;


/**
 *  Interface for external processes that want to be 
 *  notified on certain agent events.
 */
public interface IAgentListener
{
	/**
	 *  Called when the agent is closing down
	 *  (i.e. moving to the end state).
	 *  In this state the agent should perform cleanup operations
	 *  and is still able to execute
	 *  goals/plans as well as send/receive messages.
	 *  @param ae The agent event.
	 */
	public void agentTerminating(AgentEvent ae);
	
	/**
	 *  Invoked when the agent was finally terminated.
	 *  No more agent related functionality (e.g. goals plans)
	 *  can be executed.
	 *  @param ae The agent event.
	 */
	public void agentTerminated(AgentEvent ae);
}
