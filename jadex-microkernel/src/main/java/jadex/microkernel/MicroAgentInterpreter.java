package jadex.microkernel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import jadex.bridge.IAgentAdapter;
import jadex.bridge.IJadexAgent;
import jadex.bridge.IMessageAdapter;
import jadex.commons.concurrent.IResultListener;

/**
 * 
 */
public class MicroAgentInterpreter implements IJadexAgent
{
	//-------- attributes --------
	
	/** The platform adapter for the agent. */
	protected IAgentAdapter	adapter;
	
	/** The micro agent model. */
	protected MicroAgentModel model;
	
	/** The micro agent. */
	protected IMicroAgent microagent;
	
	// todo: ensure that entries are empty when saving
	/** The entries added from external threads. */
	protected transient final List ext_entries;
	
	/** The thread executing the agent (null for none). */
	// Todo: need not be transient, because agent should only be serialized when no action is running?
	protected transient Thread agentthread;
	
	//-------- constructors --------
	
	/**
	 *  Create a new agent.
	 *  @param adapter The adapter.
	 *  @param microagent The microagent.
	 */
	public MicroAgentInterpreter(IAgentAdapter adapter, MicroAgentModel model)
	{
		this.model = model;
		this.ext_entries = Collections.synchronizedList(new ArrayList());
		
		try
		{
			this.microagent = (IMicroAgent)model.getMicroAgentClass().newInstance();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	//-------- IJadexAgent interface --------
	
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
		this.agentthread = Thread.currentThread();
		
		// Copy actions from external threads into the state.
		// Is done in before tool check such that tools can see external actions appearing immediately (e.g. in debugger).
//		boolean	extexecuted	= false;
		Runnable[]	entries	= null;
		synchronized(ext_entries)
		{
			if(!(ext_entries.isEmpty()))
			{
				entries	= (Runnable[])ext_entries.toArray(new Runnable[ext_entries.size()]);
//				for(int i=0; i<ext_entries.size(); i++)
//					state.addAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_actions, ext_entries.get(i));
				ext_entries.clear();
				
//				extexecuted	= true;
			}
		}
		for(int i=0; entries!=null && i<entries.length; i++)
		{
			try
			{
				entries[i].run();
			}
			catch(Exception e)
			{
				getLogger().severe("Execution of action led to exeception: "+e);
			}
		}

		boolean ret = microagent.executeAction();
		
		this.agentthread = null;
		
		return ret;
	}

	/**
	 *  Can be called concurrently (also during executeAction()).
	 *  
	 *  Inform the agent that a message has arrived.
	 *  Can be called concurrently (also during executeAction()).
	 *  @param message The message that arrived.
	 */
	public void messageArrived(final IMessageAdapter message)
	{
		invokeLater(new Runnable()
		{
			public void run()
			{
				microagent.messageArrived(message);
			}
		});
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
	public void killAgent(final IResultListener listener)
	{
		invokeLater(new Runnable()
		{
			public void run()
			{
				microagent.killAgent();
				listener.resultAvailable(null);
			}
		});
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
		adapter.wakeup();
	}
	
	/**
	 *  Invoke some code with agent behaviour synchronized on the agent.
	 *  @param code The code to execute.
	 *  The method will block the externally calling thread until the
	 *  action has been executed on the agent thread.
	 *  If the agent does not accept external actions (because of termination)
	 *  the method will directly fail with a runtime exception.
	 *  Note: 1.4 compliant code.
	 *  Problem: Deadlocks cannot be detected and no exception is thrown.
	 */
	public void invokeSynchronized(final Runnable code)
	{
		if(isExternalThread())
		{
//			System.err.println("Unsynchronized internal thread.");
//			Thread.dumpStack();

			final boolean[] notified = new boolean[1];
			final RuntimeException[] exception = new RuntimeException[1];
			
			// Add external will throw exception if action execution cannot be done.
//			System.err.println("invokeSynchonized("+code+"): adding");
			invokeLater(new Runnable()
			{
				public void run()
				{
					try
					{
						code.run();
					}
					catch(RuntimeException e)
					{
						exception[0]	= e;
					}
					
					synchronized(notified)
					{
						notified.notify();
						notified[0] = true;
					}
				}
				
				public String	toString()
				{
					return code.toString();
				}
			});
			
			try
			{
//				System.err.println("invokeSynchonized("+code+"): waiting");
				synchronized(notified)
				{
					if(!notified[0])
					{
						notified.wait();
					}
				}
//				System.err.println("invokeSynchonized("+code+"): returned");
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
			if(exception[0]!=null)
				throw exception[0];
		}
		else
		{
			System.err.println("Method called from internal agent thread.");
			Thread.dumpStack();
			code.run();
		}
	}
	
	/**
	 *  Check if the external thread is accessing.
	 *  @return True, if access is ok.
	 */ 
	public boolean isExternalThread()
	{
		return !isAgentThread();
	}
	
	/**
	 *  Check if the agent thread is accessing.
	 *  @return True, if access is ok.
	 */ 
	public boolean isAgentThread()
	{
		return agentthread==Thread.currentThread();
	}
	
	/**
	 *  Get the logger.
	 *  @return The logger.
	 */
	public Logger getLogger()
	{
		// get logger with unique capability name
		// todo: implement getDetailName()
		//String name = getDetailName();
		String name = adapter.getAgentIdentifier().getLocalName();
		Logger ret = LogManager.getLogManager().getLogger(name);
		
		// if logger does not already exists, create it
		if(ret==null)
		{
			// Hack!!! Might throw exception in applet / webstart.
			try
			{
				ret = Logger.getLogger(name);
//				initLogger(state, rcapa, ret);
				//System.out.println(logger.getParent().getLevel());
			}
			catch(SecurityException e)
			{
				// Hack!!! For applets / webstart use anonymous logger.
				ret	= Logger.getAnonymousLogger();
//				initLogger(state, rcapa, ret);
			}
		}
		
		return ret;
	}

	/**
	 *  Get the agent adapter.
	 *  @return The agent adapter.
	 */
	public IAgentAdapter getAgentAdapter()
	{
		return adapter;
	}

	/**
	 *  Get the agent model.
	 *  @return The model.
	 */
	public MicroAgentModel getAgentModel()
	{
		return model;
	}
	
}
