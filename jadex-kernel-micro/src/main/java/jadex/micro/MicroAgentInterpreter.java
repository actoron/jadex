package jadex.micro;

import jadex.bridge.ComponentChangeEvent;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentAdapterFactory;
import jadex.bridge.IComponentChangeEvent;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentListener;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IMessageAdapter;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.IServiceContainer;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.clock.ITimer;
import jadex.commons.SReflect;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.javaparser.IValueFetcher;
import jadex.javaparser.SimpleValueFetcher;
import jadex.kernelbase.runtime.impl.AbstractInterpreter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  The micro agent interpreter is the connection between the agent platform 
 *  and a user-written micro agent. 
 */
public class MicroAgentInterpreter extends AbstractInterpreter
{
	/** Constant for step event. */
	public static final String TYPE_STEP = "step";
	
	//-------- attributes --------
	
	/** The platform adapter for the agent. */
	protected IComponentAdapter	adapter;
	
	/** The micro agent model. */
	protected IModelInfo model;
	
	/** The micro agent. */
	protected MicroAgent microagent;
	
	/** The configuration. */
	protected String config;
	
	/** The arguments. */
	protected Map arguments;
	
	/** The results. */
	protected Map results;
	
	/** The parent. */
	protected IExternalAccess parent;
	
	/** The scheduled steps of the agent. */
	protected List steps;
	
	/** The service container. */
	protected IServiceContainer container;
	
	/** The component listeners. */
	protected List componentlisteners;
	
	/** Flag indicating that no steps may be scheduled any more. */
	protected boolean nosteps;
	
	/** The external access. */
	protected IExternalAccess access;
	
	/** The list of message handlers. */
	protected List messagehandlers;

	/** The service bindings. */
	protected RequiredServiceBinding[] bindings;
	
	/** The service fetcher. */
	protected IValueFetcher fetcher;
	
	//-------- constructors --------
	
	/**
	 *  Create a new agent.
	 *  @param adapter The adapter.
	 *  @param microagent The microagent.
	 */
	public MicroAgentInterpreter(IComponentDescription desc, IComponentAdapterFactory factory, 
		final IModelInfo model, Class microclass, final Map args, final String config, 
		final IExternalAccess parent, RequiredServiceBinding[] bindings, final Future inited)
	{
		this.model = model;
		this.config = config;
		this.arguments = args!=null? args: new HashMap();
		this.parent = parent;
		this.steps	= new ArrayList();
		this.bindings = bindings;
		this.arguments = new HashMap();
		this.results = new HashMap();
		
		try
		{
			this.microagent = (MicroAgent)microclass.newInstance();
			this.microagent.init(MicroAgentInterpreter.this);
			this.adapter = factory.createComponentAdapter(desc, model, this, parent);
			addStep((new Object[]{new IComponentStep()
			{
				public Object execute(IInternalAccess ia)
				{
					init(model, config, null, arguments, results, null)
						.addResultListener(createResultListener(new DelegationResultListener(inited)));
					
					// Call user code init.
					microagent.agentCreated().addResultListener(new DelegationResultListener(inited)
					{
						public void customResultAvailable(Object result)
						{
							addStep(new Object[]{new IComponentStep()
							{
								public Object execute(IInternalAccess ia)
								{
									microagent.executeBody();
									return null;
								}
								public String toString()
								{
									return "microagent.executeBody()_#"+this.hashCode();
								}
							}, new Future()});
							
							// Init is now finished. Notify cms.
							inited.setResult(new Object[]{MicroAgentInterpreter.this, adapter});
						}
					});
					
					return null;
				}
			}, new Future()}));
		}
		catch(Exception e)
		{
			inited.setException(e);
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
			if(!steps.isEmpty())
			{
				Object[] step = removeStep();
				Future future = (Future)step[1];
				
				// Correct to execute them in try catch?!
				try
				{
					Object res = ((IComponentStep)step[0]).execute(microagent);
					if(res instanceof IFuture)
					{
						((IFuture)res).addResultListener(new DelegationResultListener(future));
					}
					else
					{
						future.setResult(res);
					}
				}
				catch(RuntimeException e)
				{
					future.setException(e);
					throw e;
				}
			}
	
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
//		System.out.println("msgrec: "+getAgentAdapter().getComponentIdentifier()+" "+message);
//		IFuture ret = scheduleStep(new ICommand()
		scheduleStep(new HandleMessageStep(message));
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
		
		try
		{
			getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{	
					exitState();
					
					ComponentChangeEvent.dispatchTerminatingEvent(adapter, getAgentModel(), getServiceProvider(), componentlisteners, null);
					
					microagent.agentKilled().addResultListener(microagent.createResultListener(new IResultListener()
					{
						public void resultAvailable(Object result)
						{
							nosteps = true;
							exitState();
							
							ComponentChangeEvent.dispatchTerminatedEvent(adapter, getAgentModel(), getServiceProvider(), componentlisteners, null);
							
							IComponentIdentifier cid = adapter.getComponentIdentifier();
							ret.setResult(cid);							
						}
						
						public void exceptionOccurred(Exception exception)
						{
							StringWriter	sw	= new StringWriter();
							exception.printStackTrace(new PrintWriter(sw));
							microagent.getLogger().severe("Exception during cleanup: "+sw);
							resultAvailable(null);
						}
					}));
				}
				
				public String toString()
				{
					return "microagent.agentKilled()_#"+this.hashCode();
				}
			});
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
		
		return ret;
	}
	
//	/**
//	 *  Kill the component.
//	 */
//	public IFuture killComponent()
//	{
//		final Future ret = new Future();
//		
//		SServiceProvider.getService(getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new DefaultResultListener()
//		{
//			public void resultAvailable(Object result)
//			{
//				((IComponentManagementService)result).destroyComponent(adapter.getComponentIdentifier())
//					.addResultListener(new DelegationResultListener(ret));
//			}
//		});
//		
//		return ret;
//	}
	
	/**
	 *  Can be called concurrently (also during executeAction()).
	 * 
	 *  Get the external access for this agent.
	 *  The specific external access interface is kernel specific
	 *  and has to be casted to its corresponding incarnation.
	 *  @param listener	External access is delivered via result listener.
	 */
	public IExternalAccess getExternalAccess()
	{
		if(access==null)
		{
			synchronized(this)
			{
				if(access==null)
				{
					access = new ExternalAccess(microagent, this);
				}
			}
		}
		return access;
	}
	
//	/**
//	 *  Get the class loader of the agent.
//	 *  The agent class loader is required to avoid incompatible class issues,
//	 *  when changing the platform class loader while agents are running. 
//	 *  This may occur e.g. when decoding messages and instantiating parameter values.
//	 *  @return	The agent class loader. 
//	 */
//	public ClassLoader getClassLoader()
//	{
//		return model.getClassLoader();
//	}
	
	/**
	 *  Get the results.
	 *  @return The results map.
	 */
	public Map getResults()
	{
		return results!=null? Collections.unmodifiableMap(results): Collections.EMPTY_MAP;
	}
	
//	/**
//	 *  Called when a component has been created as a subcomponent of this component.
//	 *  This event may be ignored, if no special reaction  to new or destroyed components is required.
//	 *  The current subcomponents can be accessed by IComponentAdapter.getSubcomponents().
//	 *  @param comp	The newly created component.
//	 */
//	public IFuture	componentCreated(IComponentDescription desc, IModelInfo model)
//	{
//		return IFuture.DONE;
//	}
//
//	/**
//	 *  Called when a subcomponent of this component has been destroyed.
//	 *  This event may be ignored, if no special reaction  to new or destroyed components is required.
//	 *  The current subcomponents can be accessed by IComponentAdapter.getSubcomponents().
//	 *  @param comp	The destroyed component.
//	 */
//	public IFuture	componentDestroyed(IComponentDescription desc)
//	{
//		return IFuture.DONE;
//	}
	
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
	 *  Schedule a step of the agent.
	 *  May safely be called from external threads.
	 *  @param step	Code to be executed as a step of the agent.
	 */
	public IFuture scheduleStep(final IComponentStep step)
	{
		final Future ret = new Future();
//		System.out.println("ss: "+getAgentAdapter().getComponentIdentifier()+" "+Thread.currentThread()+" "+step);
		try
		{
			if(isExternalThread())
			{
				adapter.invokeLater(new Runnable()
				{			
					public void run()
					{
						addStep(new Object[]{step, ret});
					}
					
					public String toString()
					{
						return "invokeLater("+step+")";
					}
				});
			}
			else
			{
				addStep(new Object[]{step, ret});
			}
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
		return ret;
	}

	/**
	 *  Add a new step.
	 */
	protected void addStep(Object[] step)
	{
		if(nosteps)
		{
			((Future)step[1]).setException(new ComponentTerminatedException(getAgentAdapter().getComponentIdentifier()));
		}
		else
		{
			steps.add(step);
			notifyListeners(new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_CREATION, TYPE_STEP, step[0].getClass().getName(), 
				step[0].toString(), microagent.getComponentIdentifier(), getStepDetails((IComponentStep)step[0])));
		}
	}
	
	/**
	 *  Add a new step.
	 */
	protected Object[] removeStep()
	{
		Object[] ret = (Object[])steps.remove(0);
		notifyListeners(new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_DISPOSAL, TYPE_STEP, 
			ret[0].getClass().getName(), ret[0].toString(), microagent.getComponentIdentifier(), getStepDetails((IComponentStep)ret[0])));
//		notifyListeners(new ChangeEvent(this, "removeStep", new Integer(0)));
		return ret;
	}
	
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
	 * /
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
	}*/
	
	/**
	 *  Check if the external thread is accessing.
	 *  @return True, if access is ok.
	 */ 
	public boolean isExternalThread()
	{
		return adapter.isExternalThread();
	}
	
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
	public IModelInfo getAgentModel()
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
	 *  Get the configuration.
	 *  @return The configuration.
	 */
	public String getConfiguration()
	{
		return this.config;
	}
	
//	/**
//	 *  Get the service provider.
//	 */
//	public IServiceProvider getServiceProvider()
//	{
//		return getServiceContainer();
//	}
	
	/**
	 *  Create the service container.
	 *  @return The service container.
	 */
	public IServiceContainer getServiceContainer()
	{
		if(container==null)
		{
			container = microagent.createServiceContainer();
		}
		return container;
	}

//	/**
//	 *  Create a result listener which is executed as an agent step.
//	 *  @param The original listener to be called.
//	 *  @return The listener.
//	 */
//	public IResultListener createResultListener(IResultListener listener)
//	{
//		return new ComponentResultListener(listener, adapter);
//	}
//
//	/**
//	 *  Create a result listener which is executed as an agent step.
//	 *  @param The original listener to be called.
//	 *  @return The listener.
//	 */
//	public IIntermediateResultListener createResultListener(IIntermediateResultListener listener)
//	{
//		return new IntermediateComponentResultListener(listener, adapter);
//	}
	
	/**
	 *  Add an component listener.
	 *  @param listener The listener.
	 */
	public IFuture addComponentListener(IComponentListener listener)
	{
		if(componentlisteners==null)
			componentlisteners = new ArrayList();
		return addComponentListener(componentlisteners, listener);
	}
	
	/**
	 *  Remove a component listener.
	 *  @param listener The listener.
	 */
	public IFuture removeComponentListener(IComponentListener listener)
	{
		return removeComponentListener(componentlisteners, listener);
	}
	
	/**
	 *  Notify the component listeners.
	 */
	public void notifyListeners(IComponentChangeEvent event)
	{
		if(componentlisteners!=null)
		{
			IComponentListener[] lstnrs = (IComponentListener[])componentlisteners.toArray(new IComponentListener[componentlisteners.size()]);
			for(int i=0; i<lstnrs.length; i++)
			{
				final IComponentListener lis = lstnrs[i];
				
				if(lis.getFilter().filter(event))
				{
					lis.eventOccured(event).addResultListener(new IResultListener()
					{
						public void resultAvailable(Object result)
						{
						}
						
						public void exceptionOccurred(Exception exception)
						{
							//Print exception?
							componentlisteners.remove(lis);
						}
					});
				}
			}
		}
	}
	
	/**
	 *  Add a message handler.
	 *  @param  The handler.
	 */
	public void addMessageHandler(final IMessageHandler handler)
	{
		if(handler.getFilter()==null)
			throw new RuntimeException("Filter must not null in handler: "+handler);
			
		if(messagehandlers==null)
			messagehandlers = new ArrayList();
		if(handler.getTimeout()>0)
		{
			microagent.waitFor(handler.getTimeout(), new IComponentStep()
			{
				public Object execute(IInternalAccess ia)
				{
					handler.timeoutOccurred();
					if(handler.isRemove())
					{
						removeMessageHandler(handler);
					}
					return null;
				}
			});
		}
		messagehandlers.add(handler);
	}
	
	/**
	 *  Remove a message handler.
	 *  @param handler The handler.
	 */
	public void removeMessageHandler(IMessageHandler handler)
	{
		if(messagehandlers!=null)
			messagehandlers.remove(handler);
	}
	
	/**
	 *  Exit the running or end state.
	 *  Cleans up remaining steps and timer entries.
	 */
	protected void exitState()
	{
//		System.out.println("cleanupComponent: "+getAgentAdapter().getComponentIdentifier());
		ComponentTerminatedException ex = new ComponentTerminatedException(getAgentAdapter().getComponentIdentifier());
		while(!steps.isEmpty())
		{
			Object[] step = removeStep();
			Future future = (Future)step[1];
			future.setException(ex);
//			System.out.println("Cleaning obsolete step: "+getAgentAdapter().getComponentIdentifier()+", "+step[0]);
		}
		
		for(int i=0; i<microagent.timers.size(); i++)
		{
			ITimer timer = (ITimer)microagent.timers.get(i);
			timer.cancel();
		}
		microagent.timers.clear();
	}

	
	
//	/**
//	 *  Get the binding info of a service.
//	 *  @param name The required service name.
//	 *  @return The binding info of a service.
//	 */
//	protected RequiredServiceBinding getRequiredServiceBinding(String name)
//	{
//		return bindings!=null? (RequiredServiceBinding)bindings.get(name): null;
//	}
	
	/**
	 *  Get the bindings.
	 *  @return the bindings.
	 */
	public RequiredServiceBinding[] getRequiredServiceBindings()
	{
		return bindings;
	}

	/**
	 *  Step to handle a message.
	 */
	public static class HandleMessageStep implements IComponentStep
	{
		private final IMessageAdapter	message;

		public static final String XML_CLASSNAME = "msg";

		public HandleMessageStep(IMessageAdapter message)
		{
			this.message = message;
		}

		public Object execute(IInternalAccess ia)
		{
			MicroAgent	microagent	= (MicroAgent)ia;
			MicroAgentInterpreter	ip	= microagent.interpreter;
			
			boolean done = false;
			if(ip.messagehandlers!=null)
			{
				for(int i=0; i<ip.messagehandlers.size(); i++)
				{
					IMessageHandler mh = (IMessageHandler)ip.messagehandlers.get(i);
					if(mh.getFilter().filter(message))
					{
						mh.handleMessage(message.getParameterMap(), message.getMessageType());
						if(mh.isRemove())
						{
							ip.messagehandlers.remove(i);
						}
						done = true;
					}
				}
			}
			
			if(!done)
			{
				microagent.messageArrived(Collections.unmodifiableMap(message.getParameterMap()), message.getMessageType());
			}
			return null;
		}

		public String toString()
		{
			return "microagent.messageArrived()_#"+this.hashCode();
		}
	}
	
	/**
	 *  Get the details of a step.
	 */
	public String getStepDetails(IComponentStep step)
	{
		StringBuffer buf = new StringBuffer();
		
		buf.append("Class = ").append(SReflect.getClassName(step.getClass()));
		
		Field[] fields = step.getClass().getDeclaredFields();
		for(int i=0; i<fields.length; i++)
		{
			String valtext = null;
			try
			{
				fields[i].setAccessible(true);
				Object val = fields[i].get(step);
				valtext = val==null? "null": val.toString();
			}
			catch(Exception e)
			{
				valtext = e.getMessage();
			}
			
			if(valtext!=null)
			{
				buf.append("\n");
				buf.append(fields[i].getName()).append(" = ").append(valtext);
			}
		}
		
		return buf.toString();
	}
	
	//-------- internal methods --------
	
	/**
	 *  Get the component adapter.
	 *  @return The component adapter.
	 */
	public IComponentAdapter getComponentAdapter()
	{
		return adapter;
	}

	/**
	 *  Get the model.
	 */
	public IModelInfo getModel()
	{
		return model;
	}
	
	/**
	 *  Get the imports.
	 *  @return The imports.
	 */
	public String[] getAllImports()
	{
		return new String[]{microagent.getClass().getPackage().getName()+".*"};
	}
	
	/**
	 *  Get the service bindings.
	 */
	public RequiredServiceBinding[] getServiceBindings()
	{
		return bindings;
	}
	
	/**
	 *  Get the value fetcher.
	 */
	public IValueFetcher getFetcher()
	{
		if(fetcher==null)
		{
			SimpleValueFetcher sfetcher = new SimpleValueFetcher();
			sfetcher.setValue("$args", getArguments());
			sfetcher.setValue("$properties", model.getProperties());
			sfetcher.setValue("$results", getResults());
			sfetcher.setValue("$component", microagent);
			sfetcher.setValue("$provider", getServiceProvider());
			fetcher = sfetcher;
		}
		return fetcher;
	}

	/**
	 *  Get the internal access.
	 */
	public IInternalAccess getInternalAccess()
	{
		return microagent;
	}
}
