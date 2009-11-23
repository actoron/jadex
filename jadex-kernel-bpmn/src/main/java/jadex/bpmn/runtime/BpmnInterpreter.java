package jadex.bpmn.runtime;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.runtime.handler.DefaultActivityHandler;
import jadex.bpmn.runtime.handler.EventEndErrorActivityHandler;
import jadex.bpmn.runtime.handler.EventIntermediateMessageActivityHandler;
import jadex.bpmn.runtime.handler.EventIntermediateMultipleActivityHandler;
import jadex.bpmn.runtime.handler.EventIntermediateNotificationHandler;
import jadex.bpmn.runtime.handler.EventIntermediateTimerActivityHandler;
import jadex.bpmn.runtime.handler.GatewayParallelActivityHandler;
import jadex.bpmn.runtime.handler.GatewayXORActivityHandler;
import jadex.bpmn.runtime.handler.SubProcessActivityHandler;
import jadex.bpmn.runtime.handler.TaskActivityHandler;
import jadex.bpmn.runtime.handler.basic.UserInteractionActivityHandler;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IArgument;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IMessageAdapter;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.commons.IFilter;
import jadex.commons.concurrent.IResultListener;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.IValueFetcher;
import jadex.service.clock.IClockService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *  The micro agent interpreter is the connection between the agent platform 
 *  and a user-written micro agent. 
 */
public class BpmnInterpreter implements IComponentInstance
{
	//-------- static part --------

	/** The activity execution handlers (activity type -> handler). */
	public static final Map DEFAULT_HANDLERS;

	static
	{
		Map defhandlers = new HashMap();
	
		defhandlers.put(MBpmnModel.TASK, new TaskActivityHandler());
		defhandlers.put(MBpmnModel.SUBPROCESS, new SubProcessActivityHandler());
	
		defhandlers.put(MBpmnModel.GATEWAY_PARALLEL, new GatewayParallelActivityHandler());
		defhandlers.put(MBpmnModel.GATEWAY_DATABASED_EXCLUSIVE, new GatewayXORActivityHandler());
	
		defhandlers.put(MBpmnModel.EVENT_START_EMPTY, new DefaultActivityHandler());
		defhandlers.put(MBpmnModel.EVENT_END_EMPTY, new DefaultActivityHandler());
		defhandlers.put(MBpmnModel.EVENT_END_ERROR, new EventEndErrorActivityHandler());
		defhandlers.put(MBpmnModel.EVENT_INTERMEDIATE_ERROR, new DefaultActivityHandler());
		defhandlers.put(MBpmnModel.EVENT_INTERMEDIATE_TIMER, new EventIntermediateTimerActivityHandler());
		defhandlers.put(MBpmnModel.EVENT_INTERMEDIATE_MESSAGE, new EventIntermediateMessageActivityHandler());
		defhandlers.put(MBpmnModel.EVENT_INTERMEDIATE_MULTIPLE, new EventIntermediateMultipleActivityHandler());
		defhandlers.put(MBpmnModel.EVENT_INTERMEDIATE_SIGNAL, new EventIntermediateNotificationHandler());
//		defhandlers.put(MBpmnModel.EVENT_INTERMEDIATE_RULE, new UserInteractionActivityHandler());
		defhandlers.put(MBpmnModel.EVENT_INTERMEDIATE_RULE, new EventIntermediateNotificationHandler());
		
		DEFAULT_HANDLERS = Collections.unmodifiableMap(defhandlers);
	}
	
	//-------- attributes --------
	
	/** The platform adapter for the agent. */
	protected IComponentAdapter	adapter;
	
	/** The micro agent model. */
	protected MBpmnModel model;
	
	/** The configuration. */
	protected String config;
	
	/** The arguments. */
	protected Map arguments;
	
	// todo: ensure that entries are empty when saving
	/** The entries added from external threads. */
	protected transient final List ext_entries;
	
	/** The thread executing the component (null for none). */
	// Todo: need not be transient, because agent should only be serialized when no action is running?
	protected transient Thread thread;
	
	/** The flag if external entries are forbidden. */
	protected boolean ext_forbidden;
	
	
	/** The activity handlers. */
	protected Map	handlers;

	/** The global value fetcher. */
	protected IValueFetcher	fetcher;

	/** The thread context. */
	protected ThreadContext	context;
	
	/** The execution history. */
	protected List history;
	
	/** The change listeners. */
//	protected List listeners;
	
	/** The step number. */
	protected int stepnumber;
	
	/** The context variables. */
	protected Map variables;
	
	/** The finishing flag marker. */
	protected boolean finishing;
	
	/** The messages waitqueue. */
	protected List messages;
	
	//-------- constructors --------
	
	/**
	 *  Create a new bpmn process.
	 *  @param adapter The adapter.
	 */
	public BpmnInterpreter(IComponentAdapter adapter, MBpmnModel model, Map arguments, 
		String config, Map handlers, IValueFetcher fetcher)
	{
		this.adapter = adapter;
		this.model = model;
		this.config = config;
		this.arguments = arguments;
		this.ext_entries = Collections.synchronizedList(new ArrayList());
		this.handlers = handlers!=null? handlers: DEFAULT_HANDLERS;
		this.fetcher = fetcher!=null? fetcher: new BpmnInstanceFetcher(this, fetcher);
		this.context = new ThreadContext(model);
		this.messages = new ArrayList();
		
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
		
		// Initialize context variables.
//		if(variables==null)
		variables	= new HashMap();
		variables.put("$platform", getComponentAdapter().getServiceContainer());
		variables.put("$clock", getComponentAdapter().getServiceContainer().getService(IClockService.class));
		variables.put("$args", this.arguments);
		
		Set	vars	= model.getContextVariables();
		for(Iterator it=vars.iterator(); it.hasNext(); )
		{
			String	name	= (String)it.next();
			Object	value	= null;
			IParsedExpression	exp	= model.getContextVariableExpression(name);
			if(exp!=null)
			{
				value	= exp.getValue(this.fetcher);
			}
			variables.put(name, value);
		}
				
		// Create initial thread(s). 
		List	startevents	= model.getStartActivities();
		for(int i=0; startevents!=null && i<startevents.size(); i++)
		{
			context.addThread(new ProcessThread((MActivity)startevents.get(i), context, this));
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
			this.thread = Thread.currentThread();
			
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
	
			if(!isFinished(null, null) && isReady(null, null))
				executeStep(null, null);
			
			this.thread = null;
			
			if(!finishing && isFinished(null, null))
			{
				getComponentAdapter().killComponent();
				finishing = true;
			}
			
			System.out.println("Process wants: "+this.getComponentAdapter().getComponentIdentifier().getLocalName()+" "+!isFinished(null, null)+" "+isReady(null, null));
			
			return !isFinished(null, null) && isReady(null, null);
		}
		catch(ComponentTerminatedException ate)
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
				// Iterate through process threads and dispatch message to first
				// waiting and fitting one (filter check).
				boolean processed = false;
				for(Iterator it=context.getAllThreads().iterator(); it.hasNext() && !processed; )
				{
					ProcessThread pt = (ProcessThread)it.next();
					if(pt.isWaiting())
					{
						IFilter filter = pt.getWaitFilter();
						if(filter!=null && filter.filter(message))
						{
							((DefaultActivityHandler)getActivityHandler(pt.getActivity())).notify(pt.getActivity(), BpmnInterpreter.this, pt, message);
							processed = true;
						}
					}
				}
				
				if(!processed)
				{
					messages.add(message);
//					System.out.println("Dispatched to waitqueue: "+message);
				}
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
	public void killComponent(final IResultListener listener)
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
//							if(microagent.timer!=null)
//							{
//								microagent.timer.cancel();
//								microagent.timer = null;
//							}
//							microagent.agentKilled();
							
							// todo: initiate kill process?!
							
							System.out.println("CC");
							listener.resultAvailable(adapter.getComponentIdentifier());
						}
					});
					
					ext_forbidden = true;
				}
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
	public void getExternalAccess(final IResultListener listener)
	{
		invokeLater(new Runnable()
		{
			public void run()
			{
				// todo: develop external access
				// Hack!!! Shouldn't return instance directly.
				listener.resultAvailable(BpmnInterpreter.this);
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
				throw new ComponentTerminatedException("External actions cannot be accepted " +
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
		return thread==Thread.currentThread();
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
		String name = adapter.getComponentIdentifier().getLocalName();
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
	public IComponentAdapter getComponentAdapter()
	{
		return adapter;
	}

	/**
	 *  Get the agent model.
	 *  @return The model.
	 */
	public MBpmnModel getComponentModel()
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
		throw new UnsupportedOperationException();
//		return new MicroListener(listener);
	}
	
	/**
	 *  The micro listener for redirecting listener invocations to the agent thread.
	 */
//	class MicroListener implements IResultListener
//	{
//		protected IResultListener listener;
//		
//		public MicroListener(IResultListener listener)
//		{
//			this.listener = listener;
//		}
//		
//		public void resultAvailable(final Object result)
//		{
//			invokeLater(new Runnable()
//			{
//				public void run()
//				{
//					listener.resultAvailable(result);
//				}
//			});
//		}
//		public void exceptionOccurred(final Exception exception)
//		{
//			invokeLater(new Runnable()
//			{
//				public void run()
//				{
//					listener.resultAvailable(exception);
//				}
//			});
//		}
//	}
	
	/**
	 *  Get the model of the BPMN process instance.
	 *  @return The model.
	 */
	public MBpmnModel	getModelElement()
	{
		return (MBpmnModel)context.getModelElement();
	}
	
	/**
	 *  Get the thread context.
	 *  @return The thread context.
	 */
	public ThreadContext getThreadContext()
	{
		return context;
	}
	
	/**
	 *  Check, if the process has terminated.
	 *  @param pool	The pool to be executed or null for any.
	 *  @param lane	The lane to be executed or null for any. Nested lanes may be addressed by dot-notation, e.g. 'OuterLane.InnerLane'.
	 *  @return True, when the process instance is finished with regards to the specified pool/lane. When both pool and lane are null, true is returned only when all pools/lanes are finished.
	 */
	public boolean isFinished(String pool, String lane)
	{
		return context.isFinished(pool, lane);
	}

	/**
	 *  Execute one step of the process.
	 *  @param pool	The pool to be executed or null for any.
	 *  @param lane	The lane to be executed or null for any. Nested lanes may be addressed by dot-notation, e.g. 'OuterLane.InnerLane'.
	 */
	public void executeStep(String pool, String lane)
	{
		if(isFinished(pool, lane))
			throw new UnsupportedOperationException("Cannot execute a finished process: "+this);
		
		if(!isReady(pool, lane))
			throw new UnsupportedOperationException("Cannot execute a process with only waiting threads: "+this);
		
		// Todo: execute only external entries belonging to pool/lane
		Runnable[]	exta	= null;
		synchronized(ext_entries)
		{
			if(!ext_entries.isEmpty())
			{
				exta = (Runnable[])ext_entries.toArray(new Runnable[ext_entries.size()]);
				ext_entries.clear();
			}
		}
		if(exta!=null)
		{
			for(int i=0; i<exta.length; i++)
				exta[i].run();
		}
		
		ProcessThread	thread	= context.getExecutableThread(pool, lane);
		
		// Thread may be null when external entry has not changed waiting state of any active plan. 
		if(thread!=null)
		{
			// Update parameters based on edge inscriptions and initial values.
			thread.updateParameters(this);
			
			// Find handler and execute activity.
			IActivityHandler handler = (IActivityHandler)handlers.get(thread.getActivity().getActivityType());
			if(handler==null)
				throw new UnsupportedOperationException("No handler for activity: "+thread);
			handler.execute(thread.getActivity(), this, thread);
			if(history!=null)
				history.add(new HistoryEntry(stepnumber++, thread.getId(), thread.getActivity()));
			
			// Check if thread now waits for a message and there is at least one in the message queue.
			if(thread.isWaiting() && messages.size()>0 && MBpmnModel.EVENT_INTERMEDIATE_MESSAGE.equals(thread.getActivity().getActivityType()) 
				&& (thread.getPropertyValue(EventIntermediateMessageActivityHandler.PROPERTY_MODE)==null 
					|| EventIntermediateMessageActivityHandler.MODE_RECEIVE.equals(thread.getPropertyValue(EventIntermediateMessageActivityHandler.PROPERTY_MODE))))
			{
				boolean processed = false;
				for(int i=0; i<messages.size() && !processed; i++)
				{
					Object message = messages.get(i);
					IFilter filter = thread.getWaitFilter();
					if(filter!=null && filter.filter(message))
					{
						((DefaultActivityHandler)getActivityHandler(thread.getActivity())).notify(thread.getActivity(), BpmnInterpreter.this, thread, message);
						processed = true;
						messages.remove(i);
//						System.out.println("Dispatched from waitqueue: "+messages.size()+" "+message);
					}
				}
			}
			
//			notifyListeners(new ChangeEvent(this, "step_executed"));
		}
	}

	/**
	 *  Check if the process is ready, i.e. if at least one process thread can currently execute a step.
	 *  @param pool	The pool to be executed or null for any.
	 *  @param lane	The lane to be executed or null for any. Nested lanes may be addressed by dot-notation, e.g. 'OuterLane.InnerLane'.
	 */
	public boolean	isReady(String pool, String lane)
	{
		boolean	ready;
		// Todo: consider only external entries belonging to pool/lane
		synchronized(ext_entries)
		{
			ready	= !ext_entries.isEmpty();
		}
		ready	= ready || context.getExecutableThread(pool, lane)!=null;
		return ready;
	}
	
	/**
	 *  Add an external entry to be invoked during the next executeStep.
	 *  This method may be called from external threads.
	 *  @param code	The external code. 
	 */
//	public void	invokeLater(Runnable code)
//	{
//		synchronized(extentries)
//		{
//			extentries.add(code);
//		}
//		if(adapter!=null)
//			adapter.wakeUp();
//	}
	
	/**
	 *  Get the activity handler for an activity.
	 *  @param actvity The activity.
	 *  @return The activity handler.
	 */
	public IActivityHandler getActivityHandler(MActivity activity)
	{
		return (IActivityHandler)handlers.get(activity.getActivityType());
	}

	/**
	 *  Get the global value fetcher.
	 *  @return The value fetcher (if any).
	 */
	public IValueFetcher getValueFetcher()
	{
		return this.fetcher;
	}
	
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
		// Hack!!! synchronized because of ProcessViewPanel.
		if(enabled && history==null)
			history	= Collections.synchronizedList(new ArrayList());
		else if(!enabled && history!=null)
			history	= null;
	}
	
	/**
	 *  Add a change listener.
	 *  @param listener The listener.
	 * /
	public void addChangeListener(IChangeListener listener)
	{
		if(listeners==null)
			listeners = new ArrayList();
		listeners.add(listener);
	}*/
	
	/**
	 *  Remove a change listener.
	 *  @param listener The listener.
	 * /
	public void removeChangeListener(IChangeListener listener)
	{
		if(listeners!=null)
			listeners.remove(listener);
	}*/
	
	/**
	 *  Notify the change listeners.
	 * /
	public void notifyListeners(ChangeEvent event)
	{
		if(listeners!=null)
		{
			for(int i=0; i<listeners.size(); i++)
			{
				((IChangeListener)listeners.get(i)).changeOccurred(event);
			}
		}
	}*/
	
	/**
	 *  Test if the given context variable is declared.
	 *  @param name	The variable name.
	 *  @return True, if the variable is declared.
	 */
	public boolean hasContextVariable(String name)
	{
		return variables!=null && variables.containsKey(name);
	}
	
	/**
	 *  Get the value of the given context variable.
	 *  @param name	The variable name.
	 *  @return The variable value.
	 */
	public Object getContextVariable(String name)
	{
		if(variables!=null && variables.containsKey(name))
		{
			return variables.get(name);			
		}
		else
		{
			throw new RuntimeException("Undeclared context variable: "+name+", "+this);
		}
	}
	
	/**
	 *  Set the value of the given context variable.
	 *  @param name	The variable name.
	 *  @param value	The variable value.
	 */
	public void setContextVariable(String name, Object value)
	{
		if(variables!=null && variables.containsKey(name))
		{
			variables.put(name, value);			
		}
		else
		{
			throw new RuntimeException("Undeclared context variable: "+name+", "+this);
		}
	}
}
