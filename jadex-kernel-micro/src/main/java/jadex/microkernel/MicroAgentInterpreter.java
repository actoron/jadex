package jadex.microkernel;

import jadex.bridge.AgentTerminatedException;
import jadex.bridge.IAgentAdapter;
import jadex.bridge.IArgument;
import jadex.bridge.IKernelAgent;
import jadex.bridge.IMessageAdapter;
import jadex.commons.concurrent.IResultListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *  The micro agent interpreter is the connection between the agent platform 
 *  and a user-written micro agent. 
 */
public class MicroAgentInterpreter implements IKernelAgent
{
	//-------- attributes --------
	
	/** The platform adapter for the agent. */
	protected IAgentAdapter	adapter;
	
	/** The micro agent model. */
	protected MicroAgentModel model;
	
	/** The micro agent. */
	protected MicroAgent microagent;
	
	/** The configuration. */
	protected String config;
	
	/** The arguments. */
	protected Map arguments;
	
	// todo: ensure that entries are empty when saving
	/** The entries added from external threads. */
	protected transient final List ext_entries;
	
	/** The thread executing the agent (null for none). */
	// Todo: need not be transient, because agent should only be serialized when no action is running?
	protected transient Thread agentthread;
	
	/** Flag that indicates if the agent has been started. */
	protected boolean started;
	
	/** The flag if external entries are forbidden. */
	protected boolean ext_forbidden;
	
	//-------- constructors --------
	
	/**
	 *  Create a new agent.
	 *  @param adapter The adapter.
	 *  @param microagent The microagent.
	 */
	public MicroAgentInterpreter(IAgentAdapter adapter, MicroAgentModel model, Map arguments, String config)
	{
		this.adapter = adapter;
		this.model = model;
		this.config = config;
		this.arguments = arguments;
		this.ext_entries = Collections.synchronizedList(new ArrayList());
		
		// Init the arguments with default values.
		IArgument[] args = model.getArguments();
		for(int i=0; i<args.length; i++)
		{
			if(args[i].getDefaultValue(config)!=null)
			{
				if(this.arguments==null)
					this.arguments = new HashMap();
			
				if(this.arguments.get(args[i].getName())==null)
				{
					this.arguments.put(args[i].getName(), args[i].getDefaultValue(config));
				}
			}
		}

		try
		{
			this.microagent = (MicroAgent)model.getMicroAgentClass().newInstance();
			this.microagent.init(this);
			this.microagent.agentCreated();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}		
	}
	
	//-------- IKernelAgent interface --------
	
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
		try
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
					e.printStackTrace();
					getLogger().severe("Execution of agent led to exeception: "+e);
				}
			}
	
			if(!started)
			{
				microagent.executeBody();
				started = true;
			}
			
			this.agentthread = null;
			return false;
		}
		catch(AgentTerminatedException ate)
		{
			// Todo: fix microkernel bug.
			return false; 
		}
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
				microagent.messageArrived(Collections.unmodifiableMap(message.getParameterMap()), message.getMessageType());
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
				// must synchronize to avoid other thread calling invokeLater at the same time
				synchronized(ext_entries)
				{
					invokeLater(new Runnable()
					{
						public void run()
						{
							if(microagent.timer!=null)
							{
								microagent.timer.cancel();
								microagent.timer = null;
							}
							microagent.agentKilled();
							listener.resultAvailable(adapter.getAgentIdentifier());
						}
					});
					
					ext_forbidden = true;
					adapter.wakeup();
				}
			}
		});
		adapter.wakeup();
	}
	
	/**
	 *  Can be called concurrently (also during executeAction()).
	 * 
	 *  Get the external access for this agent.
	 *  The specific external access interface is kernel specific
	 *  and has to be casted to its corresponding incarnation.
	 *  @param listener	External access is delivered via result listener.
	 */
	public void getExternalAccess(final IResultListener listener)
	{
		invokeLater(new Runnable()
		{
			public void run()
			{
				Object exta = microagent.getExternalAccess();
				listener.resultAvailable(exta);
			}
		});
	}
	
	/**
	 *  Get the class loader of the agent.
	 *  The agent class loader is required to avoid incompatible class issues,
	 *  when changing the platform class loader while agents are running. 
	 *  This may occur e.g. when decoding messages and instantiating parameter values.
	 *  @return	The agent class loader. 
	 */
	public ClassLoader getClassLoader()
	{
		return model.getClassLoader();
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
			if(ext_forbidden)
				throw new AgentTerminatedException("External actions cannot be accepted " +
					"due to terminated agent state: "+this);
			{
				ext_entries.add(action);
			}
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
	
	/**
	 *  Get the arguments.
	 *  @return The arguments.
	 */
	public Map getArguments()
	{
		return arguments;
	}
	
	/**
	 *  Get the configuration.
	 *  @return The configuration.
	 */
	public String getConfiguration()
	{
		return this.config;
	}

	/**
	 *  Create a result listener which is called on agent thread.
	 *  @param The original listener to be called.
	 *  @return The listener.
	 */
	public IResultListener createResultListener(IResultListener listener)
	{
		return new MicroListener(listener);
	}
	
	/**
	 *  The micro listener for redirecting listener invocations to the agent thread.
	 */
	class MicroListener implements IResultListener
	{
		protected IResultListener listener;
		
		public MicroListener(IResultListener listener)
		{
			this.listener = listener;
		}
		
		public void resultAvailable(final Object result)
		{
			invokeLater(new Runnable()
			{
				public void run()
				{
					listener.resultAvailable(result);
				}
			});
		}
		public void exceptionOccurred(final Exception exception)
		{
			invokeLater(new Runnable()
			{
				public void run()
				{
					listener.resultAvailable(exception);
				}
			});
		}
	}
}
