package jadex.micro;

import jadex.bridge.ComponentResultListener;
import jadex.bridge.ComponentServiceContainer;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IArgument;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.bridge.ILoadableComponentModel;
import jadex.bridge.IMessageAdapter;
import jadex.commons.ChangeEvent;
import jadex.commons.Future;
import jadex.commons.IChangeListener;
import jadex.commons.IFuture;
import jadex.commons.concurrent.CollectionResultListener;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.service.IServiceContainer;
import jadex.service.IServiceProvider;
import jadex.service.SServiceProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  The micro agent interpreter is the connection between the agent platform 
 *  and a user-written micro agent. 
 */
public class MicroAgentInterpreter implements IComponentInstance
{
	//-------- attributes --------
	
	/** The platform adapter for the agent. */
	protected IComponentAdapter	adapter;
	
	/** The micro agent model. */
	protected MicroAgentModel model;
	
	/** The micro agent. */
	protected MicroAgent microagent;
	
	/** The service provider. */
	protected IServiceContainer provider;
	
	/** The configuration. */
	protected String config;
	
	/** The arguments. */
	protected Map arguments;
	
	/** The results. */
	protected Map results;
	
	/** The parent. */
	protected IExternalAccess parent;
	
	/** The thread executing the agent (null for none). */
	// Todo: need not be transient, because agent should only be serialized when no action is running?
	protected transient Thread agentthread;
	
	/** The scheduled steps of the agent. */
	protected List steps;
	
	/** The change listeners. */
	protected List listeners;

	/** The execution history. */
	protected List history;
	
	//-------- constructors --------
	
	/**
	 *  Create a new agent.
	 *  @param adapter The adapter.
	 *  @param microagent The microagent.
	 */
	public MicroAgentInterpreter(IComponentAdapter adapter, MicroAgentModel model, Map arguments, String config, final IExternalAccess parent)
	{
		this.adapter = adapter;
		this.model = model;
		this.config = config;
		this.arguments = arguments;
		this.parent = parent;
		// synchronized because of MicroAgentViewPanel, todo
		this.steps	= Collections.synchronizedList(new ArrayList());
		
//		this.ext_entries = Collections.synchronizedList(new ArrayList());
		
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
		
		// Init the results with default values.
		IArgument[] res = model.getResults();
		for(int i=0; i<res.length; i++)
		{
			if(res[i].getDefaultValue(config)!=null)
			{
				if(this.results==null)
					this.results = new HashMap();
			
				this.results.put(res[i].getName(), res[i].getDefaultValue(config));
			}
		}
		
		// Create the service provider.
		// Service adding and starting the provider must be done by hand.
		this.provider = new ComponentServiceContainer(adapter);
		
		// Schedule initial step.
		addStep(new Runnable()
		{
			public void run()
			{
				microagent.executeBody();
			}
			public String toString()
			{
				return "microagent.executeBody()_#"+this.hashCode();
			}
		});

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
	public boolean executeStep()
	{
		try
		{
			this.agentthread = Thread.currentThread();
			
			if(!steps.isEmpty())
			{
				Runnable step = (Runnable)removeStep();
				String steptext = ""+step;
				step.run();
				addHistoryEntry(steptext);
			}
	
			this.agentthread = null;
			return !steps.isEmpty();
		}
		catch(ComponentTerminatedException ate)
		{
			// Todo: fix microkernel bug.
			ate.printStackTrace();
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
		scheduleStep(new Runnable()
		{
			public void run()
			{
				microagent.messageArrived(Collections.unmodifiableMap(message.getParameterMap()), message.getMessageType());
			}
			
			public String toString()
			{
				return "microagent.messageArrived("+message+")_#"+this.hashCode();
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
	public IFuture cleanupComponent()
	{
		final Future ret = new Future();
		
		getAgentAdapter().invokeLater(new Runnable()
		{
			public void run()
			{	
				if(microagent.timer!=null)
				{
					microagent.timer.cancel();
					microagent.timer = null;
				}
				microagent.agentKilled();
				provider.shutdown();
				IComponentIdentifier cid = adapter.getComponentIdentifier();
				ret.setResult(cid);
			}
			
			public String toString()
			{
				return "microagent.agentKilled()_#"+this.hashCode();
			}
		});
		
		return ret;
	}
	
	/**
	 *  Kill the component.
	 */
	public IFuture killComponent()
	{
		final Future ret = new Future();
		
		SServiceProvider.getService(getServiceProvider(), IComponentManagementService.class).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				((IComponentManagementService)result).destroyComponent(adapter.getComponentIdentifier())
					.addResultListener(new DelegationResultListener(ret));
			}
		});
		
		return ret;
	}
	
	/**
	 *  Can be called concurrently (also during executeAction()).
	 * 
	 *  Get the external access for this agent.
	 *  The specific external access interface is kernel specific
	 *  and has to be casted to its corresponding incarnation.
	 *  @param listener	External access is delivered via result listener.
	 */
	public IFuture getExternalAccess()
	{
		final Future ret = new Future();
		
		getAgentAdapter().invokeLater(new Runnable()
		{
			public void run()
			{
				Object exta = microagent.getExternalAccess();
				ret.setResult(exta);
			}
			
			public String toString()
			{
				return "microagent.getExternalAccess()_#"+this.hashCode();
			}
		});
		
		return ret;
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
	
	/**
	 *  Get the results.
	 *  @return The results map.
	 */
	public Map getResults()
	{
		return results!=null? Collections.unmodifiableMap(results): Collections.EMPTY_MAP;
	}
	
	/**
	 *  Called when a component has been created as a subcomponent of this component.
	 *  This event may be ignored, if no special reaction  to new or destroyed components is required.
	 *  The current subcomponents can be accessed by IComponentAdapter.getSubcomponents().
	 *  @param comp	The newly created component.
	 */
	public void	componentCreated(IComponentDescription desc, ILoadableComponentModel model)
	{
	}
	
	/**
	 *  Called when a subcomponent of this component has been destroyed.
	 *  This event may be ignored, if no special reaction  to new or destroyed components is required.
	 *  The current subcomponents can be accessed by IComponentAdapter.getSubcomponents().
	 *  @param comp	The destroyed component.
	 */
	public void	componentDestroyed(IComponentDescription desc)
	{
	}
	
	/**
	 *  Test if the component's execution is currently at one of the
	 *  given breakpoints. If yes, the component will be suspended by
	 *  the platform.
	 *  @param breakpoints	An array of breakpoints.
	 *  @return True, when some breakpoint is triggered.
	 */
	public boolean isAtBreakpoint(String[] breakpoints)
	{
		return microagent.isAtBreakpoint(breakpoints);
	}
	
	//-------- helpers --------
	
	/**
	 *  Get the history mode.
	 */
	public boolean isHistoryEnabled()
	{
		return history!=null;
	}
	
	/**
	 *  Get the history.
	 *  @return The history.
	 * /
	public List getSteps()
	{
		return this.steps;
	}*/
	
	/**
	 *  Get the history.
	 *  @return The history.
	 */
	public List getHistory()
	{
		return this.history;
	}

	/**
	 *  Set the history mode.
	 */
	public void	setHistoryEnabled(boolean enabled)
	{
		// Hack!!! synchronized because of MicroAgentViewPanel.
		if(enabled && history==null)
			history	= Collections.synchronizedList(new ArrayList());
		else if(!enabled && history!=null)
			history	= null;
	}
	
	/**
	 *  Schedule a step of the agent.
	 *  May safely be called from external threads.
	 *  @param step	Code to be executed as a step of the agent.
	 */
	public void	scheduleStep(final Runnable step)
	{
		adapter.invokeLater(new Runnable()
		{			
			public void run()
			{
				addStep(step);
			}
		});
	}

	/**
	 *  Add a new step.
	 */
	protected void addStep(Runnable step)
	{
		steps.add(step);
		notifyListeners(new ChangeEvent(this, "addStep", step));
	}
	
	/**
	 *  Add a new step.
	 */
	protected Object removeStep()
	{
		Object ret = steps.remove(0);
		notifyListeners(new ChangeEvent(this, "removeStep", new Integer(0)));
		return ret;
	}
	
	/**
	 *  Add a new step.
	 */
	protected void addHistoryEntry(String steptext)
	{
		if(history!=null)
		{
			history.add(steptext);
			notifyListeners(new ChangeEvent(this, "addHistoryEntry", steptext));
		}
	}
	
	/**
	 *  Clear the history.
	 * /
	public void clearHistory()
	{
		if(history!=null)
			history.clear();
	}*/
	
	/**
	 *  Add an action from external thread.
	 *  The contract of this method is as follows:
	 *  The agent ensures the execution of the external action, otherwise
	 *  the method will throw a agent terminated sexception.
	 *  @param action The action.
	 * /
	public void invokeLater(Runnable action)
	{
		synchronized(ext_entries)
		{
			if(ext_forbidden)
				throw new ComponentTerminatedException("External actions cannot be accepted " +
					"due to terminated agent state: "+this);
			{
				ext_entries.add(action);
			}
		}
		adapter.wakeup();
	}*/
	
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
			getAgentAdapter().invokeLater(new Runnable()
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
				
				public String toString()
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
		return adapter.isExternalThread();
	}
	
	/**
	 *  Get the logger.
	 *  @return The logger.
	 * /
	public Logger getLogger()
	{
		return adapter.getLogger();
	}*/

	/**
	 *  Get the agent adapter.
	 *  @return The agent adapter.
	 */
	public IComponentAdapter getAgentAdapter()
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
	 *  Set a result value.
	 *  @param name The result name.
	 *  @param value The result value.
	 */
	public void setResultValue(String name, Object value)
	{	
		if(results==null)
			results = new HashMap();
		results.put(name, value);
	}
	
	/**
	 *  Get the parent component.
	 *  @return The parent (if any).
	 */
	public IExternalAccess getParent()
	{
		return parent;
	}
	
	/**
	 *  Get the children (if any).
	 *  @return The children.
	 */
	public IFuture getChildren()
	{
		final Future ret = new Future();
		
		SServiceProvider.getService(getServiceProvider(), IComponentManagementService.class)
			.addResultListener(createResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				IComponentManagementService cms = (IComponentManagementService)result;
				IComponentIdentifier[] childs = cms.getChildren(getAgentAdapter().getComponentIdentifier());
				
				IResultListener	crl	= new CollectionResultListener(childs.length, new DelegationResultListener(ret));
				for(int i=0; !ret.isDone() && i<childs.length; i++)
				{
					cms.getExternalAccess(childs[i]).addResultListener(crl);
				}
			}
		}));
		
		return ret;
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
	 *  Get the service provider.
	 */
	public IServiceProvider getServiceProvider()
	{
		return provider;
	}

	/**
	 *  Create a result listener which is executed as an agent step.
	 *  @param The original listener to be called.
	 *  @return The listener.
	 */
	public IResultListener createResultListener(IResultListener listener)
	{
		return new ComponentResultListener(listener, adapter);
	}
	
	/**
	 *  The micro listener for executing listener invocations as an agent step.
	 * /
	class MicroListener implements IResultListener
	{
		protected IResultListener listener;
		
		public MicroListener(IResultListener listener)
		{
			this.listener = listener;
		}
		
		public void resultAvailable(final Object source, final Object result)
		{
			scheduleStep(new Runnable()
			{
				public void run()
				{
					listener.resultAvailable(source, result);
				}
				
				public String toString()
				{
					return "resultAvailable("+result+")_#"+this.hashCode();
				}
			});
		}
		public void exceptionOccurred(final Object source, final Exception exception)
		{
			scheduleStep(new Runnable()
			{
				public void run()
				{
					listener.exceptionOccurred(source, exception);
				}
				
				public String toString()
				{
					return "exceptionOccurred("+exception+")_#"+this.hashCode();
				}
			});
		}
	}*/
	
	/**
	 *  Add a change listener.
	 *  @param listener The listener.
	 */
	public void addChangeListener(IChangeListener listener)
	{
		if(listeners==null)
			listeners = new ArrayList();
		listeners.add(listener);
		
		// Inform new listener of current state.
		listener.changeOccurred(new ChangeEvent(this, "initialState", new Object[]{
			steps.toArray(), history.toArray()
		}));
	}
	
	/**
	 *  Remove a change listener.
	 *  @param listener The listener.
	 */
	public void removeChangeListener(IChangeListener listener)
	{
		if(listeners!=null)
			listeners.remove(listener);
	}
	
	/**
	 *  Notify the change listeners.
	 */
	public void notifyListeners(ChangeEvent event)
	{
		if(listeners!=null)
		{
			for(int i=0; i<listeners.size(); i++)
			{
				((IChangeListener)listeners.get(i)).changeOccurred(event);
			}
		}
	}
}
