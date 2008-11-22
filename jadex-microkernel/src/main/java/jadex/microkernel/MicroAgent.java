package jadex.microkernel;

import jadex.bridge.IAgentAdapter;
import jadex.bridge.IMessageAdapter;
import jadex.bridge.IPlatform;

/**
 *  Base class for application agents.
 */
public abstract class MicroAgent implements IMicroAgent
{
	//-------- attributes --------
	
	/** The agent interpreter. */
	protected MicroAgentInterpreter interpreter;
	
	//-------- constructors --------
	
	/**
	 * 
	 */
	public void init(MicroAgentInterpreter interpreter)
	{
		this.interpreter = interpreter;
	}
	
	//-------- interface methods --------
	
	/**
	 *  Main method to perform agent execution.
	 *  Whenever this method is called, the agent performs
	 *  one of its scheduled actions.
	 *  The platform can provide different execution models for agents
	 *  (e.g. thread based, or synchronous).
	 *  To avoid idle waiting, the return value can be checked.
	 *  The platform guarantees that executeAction() will not be called in parallel. 
	 *  @return True, when there are more actions waiting to be executed. 
	 */
	public abstract boolean executeAction();

	/**
	 *  Can be called concurrently (also during executeAction()).
	 *  
	 *  Inform the agent that a message has arrived.
	 *  Can be called concurrently (also during executeAction()).
	 *  @param message The message that arrived.
	 */
	public void messageArrived(IMessageAdapter message)
	{
	}

	/**
	 *  Request agent to kill itself.
	 *  The agent might perform arbitrary cleanup activities during which executeAction()
	 *  will still be called as usual.
	 */
	public void killAgent()
	{
	}
	
	/**
	 *  Get the external access for this agent.
	 *  The specific external access interface is kernel specific
	 *  and has to be casted to its corresponding incarnation.
	 */
	public Object getExternalAccess()
	{
		// todo: implement me
		
		return null;
	}

	//-------- methods --------
	
	/**
	 *  Get the agent adapter.
	 *  @return The agent adapter.
	 */
	public IAgentAdapter getAgentAdapter()
	{
		return interpreter.getAgentAdapter();
	}
	
	/**
	 *  Get the agent platform.
	 *  @return The agent platform. 
	 */
	public IPlatform getPlatform()
	{
		return interpreter.getAgentAdapter().getPlatform();
	}
}
