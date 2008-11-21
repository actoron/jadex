package jadex.microkernel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jadex.bridge.AgentTerminatedException;
import jadex.bridge.IAgentAdapter;
import jadex.bridge.IJadexAgent;
import jadex.bridge.IMessageAdapter;
import jadex.commons.concurrent.IResultListener;

/**
 * 
 */
public class JadexAgentImpl implements IJadexAgent
{
	/** The platform adapter for the agent. */
	protected IAgentAdapter	adapter;
	
	/** The micro agent carrying the application agent logic. */
	protected IMicroAgent microagent;
	
	// todo: ensure that entries are empty when saving
	/** The entries added from external threads. */
	protected transient final List ext_entries;
	
	/**
	 *
	 */
	public JadexAgentImpl(IMicroAgent microagent)
	{
		this.microagent = microagent;
		this.ext_entries = Collections.synchronizedList(new ArrayList());
	}
	
	/**
	 *  Can be called on the agent thread only.
	 * 
	 *  Main method to perform agent execution.
	 *  Whenever this method is called, the agent performs
	 *  one of its scheduled actions.
	 *  The platform can provide different execution models for agents
	 *  (e.g. thread based, or synchronous).
	 *  To avoid idle waiting, the return value can be checked.
	 *  The platform guarantees that executeAction() will not be called in parallel. 
	 *  @return True, when there are more actions waiting to be executed. 
	 */
	public boolean executeAction()
	{
		return microagent.executeAction();
	}

	/**
	 *  Can be called concurrently (also during executeAction()).
	 *  
	 *  Inform the agent that a message has arrived.
	 *  Can be called concurrently (also during executeAction()).
	 *  @param message The message that arrived.
	 */
	public void messageArrived(IMessageAdapter message)
	{
		// todo call on agent thread
	}

	/**
	 *  Can be called concurrently (also during executeAction()).
	 *   
	 *  Request agent to kill itself.
	 *  The agent might perform arbitrary cleanup activities during which executeAction()
	 *  will still be called as usual.
	 *  Can be called concurrently (also during executeAction()).
	 *  @param listener	When cleanup of the agent is finished, the listener must be notified.
	 */
	public void killAgent(IResultListener listener)
	{
		// todo call on agent thread
	}
	
	/**
	 *  Can be called concurrently (also during executeAction()).
	 * 
	 *  Get the external access for this agent.
	 *  The specific external access interface is kernel specific
	 *  and has to be casted to its corresponding incarnation.
	 *  @param listener	External access is delivered via result listener.
	 */
	public void getExternalAccess(IResultListener listener)
	{
		// todo call on agent thread
	}
	
	//-------- helpers --------
	
	/**
	 *  Add an action from external thread.
	 *  The contract of this method is as follows:
	 *  The agent ensures the execution of the external action, otherwise
	 *  the method will throw a agent terminated sexception.
	 *  @param action The action.
	 */
	public void invokeLater(Runnable action)
	{
		synchronized(ext_entries)
		{
//			if(ext_forbidden)
//				throw new AgentTerminatedException("External actions cannot be accepted " +
//					"due to terminated agent state: "+ragent);
			ext_entries.add(action);
		}
//		adapter.wakeup();
	}
}
