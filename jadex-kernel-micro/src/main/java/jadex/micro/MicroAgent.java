package jadex.micro;

import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentListener;
import jadex.bridge.IComponentStep;
import jadex.bridge.IConnection;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.IInternalService;
import jadex.bridge.service.IServiceContainer;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.clock.ITimedObject;
import jadex.bridge.service.types.clock.ITimer;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.bridge.service.types.message.IMessageService;
import jadex.bridge.service.types.message.MessageType;
import jadex.bridge.service.types.message.MessageType.ParameterSpecification;
import jadex.commons.ComposedFilter;
import jadex.commons.IFilter;
import jadex.commons.IValueFetcher;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 *  Base class for application agents.
 */
public class MicroAgent implements IMicroAgent, IInternalAccess
{
	//-------- attributes --------
	
	/** The agent interpreter. */
	protected MicroAgentInterpreter interpreter;
	
	/** The current timer. */
	protected List<ITimer> timers;
	
	//-------- constructors --------
	
	/**
	 *  Init the micro agent with the interpreter.
	 *  @param interpreter The interpreter.
	 */
	public void init(MicroAgentInterpreter interpreter)
	{
//		System.out.println("Init: "+interpreter);
		this.interpreter = interpreter;
	}
	
	//-------- interface methods --------
	
	/**
	 *  Called once after agent creation.
	 */
	public IFuture<Void> agentCreated()
	{
		return IFuture.DONE;
	}
	
	/**
	 *  Execute the functional body of the agent.
	 *  Is only called once.
	 */
	public IFuture<Void> executeBody()
	{
		return new Future<Void>();
//		return IFuture.DONE;
	}

	/**
	 *  Called, whenever a message is received.
	 *  @param msg The message.
	 *  @param mt The message type.
	 */
	public void messageArrived(Map<String, Object> msg, MessageType mt)
	{
	}

	/**
	 *  Called, whenever a stream is received.
	 *  @param con The stream.
	 */
	public void streamArrived(IConnection con)
	{
	}
	
	/**
	 *  Called just before the agent is removed from the platform.
	 *  @return The result of the component.
	 */
	public IFuture<Void> agentKilled()
	{
		return IFuture.DONE;
	}

	//-------- methods --------
	
	/**
	 *  Get a property.
	 *  @param name	The name of the property.
	 *  @return The property value or null.
	 */
	public Object	getProperty(String name)
	{
		return interpreter.getProperties()!=null ? interpreter.getProperties().get(name) : null;
	}
	
	/**
	 *  Get the service provider.
	 *  @return The service provider.
	 */
	public IServiceProvider getServiceProvider()
	{
		return interpreter.getServiceProvider();
	}
	
	/**
	 *  Get the service container.
	 *  Internal method for accessing the service container.
	 *  @return The service container.
	 */
	public IServiceContainer getServiceContainer()
	{
		return interpreter.getServiceContainer();
	}
	
	/**
	 *  Test if the agent's execution is currently at one of the
	 *  given breakpoints. If yes, the agent will be suspended by
	 *  the platform.
	 *  Available breakpoints can be specified in the
	 *  micro agent meta info.
	 *  @param breakpoints	An array of breakpoints.
	 *  @return True, when some breakpoint is triggered.
	 */
	public boolean isAtBreakpoint(String[] breakpoints)
	{
		return false;
	}
	
	/**
	 *  Get the external access for this agent.
	 */
	public IExternalAccess	getExternalAccess()
	{
		return interpreter.getExternalAccess();
//		return new ExternalAccess(this, interpreter);
	}

	/**
	 *  Get the parent component.
	 *  @return The parent (if any).
	 */
	public IExternalAccess getParentAccess()
	{
		return interpreter.getParentAccess();
	}
	
	/**
	 *  Get the agent adapter.
	 *  @return The agent adapter.
	 *  // todo: deprecated, all kernels should have getComponentAdapter?
	 */
	public IComponentAdapter getAgentAdapter()
	{
		return interpreter.getAgentAdapter();
	}
	
	/**
	 *  Get the agent adapter.
	 *  @return The agent adapter.
	 */
	public IComponentAdapter getComponentAdapter()
	{
		return interpreter.getAgentAdapter();
	}
	
	/**
	 *  Get the component description.
	 *  @return The component description.
	 */
	public IComponentDescription getComponentDescription()
	{
		return interpreter.getComponentDescription();
	}
	
	/**
	 *  Get the arguments.
	 *  @return The arguments.
	 */
	public Map<String, Object> getArguments()
	{
		return interpreter.getArguments();
	}
	
	/**
	 *  Get the component results.
	 *  @return The results.
	 */
	public Map<String, Object> getResults()
	{
		return interpreter.getResults();
	}
	
	/**
	 *  Set a result value.
	 *  @param name The result name.
	 *  @param value The result value.
	 */
	public void setResultValue(String name, Object value)
	{	
		interpreter.setResultValue(name, value);
	}
	
	/**
	 *  Get an argument.
	 *  @param name The argument name.
	 *  @return The value. 
	 */
	public Object getArgument(String name)
	{
		return interpreter.getArguments()==null? null: interpreter.getArguments().get(name);
	}
	
	/**
	 *  Get the configuration.
	 *  @return the Configuration.
	 */
	public String getConfiguration()
	{
		return interpreter.getConfiguration();
	}
	
	/**
	 *  Get a raw reference to a provided service implementation.
	 */
	public Object getRawService(String name)
	{
		return interpreter.getRawService(name);
	}
	
	/**
	 *  Get a raw reference to a provided service implementation.
	 */
	public Object getRawService(Class<?> type)
	{
		return interpreter.getRawService(type);
	}

	/**
	 *  Get a raw reference to a provided service implementation.
	 */
	public Object[] getRawServices(Class<?> type)
	{
		return interpreter.getRawServices(type);
	}
	
	/**
	 *  Create a result listener that is executed as an agent step.
	 *  @param listener The listener to be executed as an agent step.
	 */
	public <T> IResultListener<T> createResultListener(IResultListener<T> listener)
	{
		return interpreter.createResultListener(listener);
	}
	
	/**
	 *  Create a result listener that is executed as an agent step.
	 *  @param listener The listener to be executed as an agent step.
	 */
	public <T> IIntermediateResultListener<T> createResultListener(IIntermediateResultListener<T> listener)
	{
		return interpreter.createResultListener(listener);
	}
	
	/**
	 *  Get the current time.
	 *  @return The current time.
	 */
	public IFuture<Long> getTime()
	{
		final Future<Long> ret = new Future<Long>();
		
		getServiceContainer().searchService(IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new ExceptionDelegationResultListener<IClockService, Long>(ret)
		{
			public void customResultAvailable(IClockService cs)
			{
				ret.setResult(new Long(cs.getTime()));
			}
		});
		
		return ret;
	}
	
	// for debugging.
//	protected long	longtime	= 0;
	/**
	 *  Wait for an specified amount of time.
	 *  @param time The time.
	 *  @param step The runnable.
	 */
	public IFuture<TimerWrapper> waitFor(final long time, final IComponentStep<Void> step)
	{
//		longtime	= Math.max(longtime, time);
		final Future<TimerWrapper> ret = new Future<TimerWrapper>();
		
		getServiceContainer().searchService(IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new ExceptionDelegationResultListener<IClockService, TimerWrapper>(ret)
		{
			public void customResultAvailable(IClockService cs)
			{
				final ITimer[] ts = new ITimer[1];
				ts[0] = cs.createTimer(time, new ITimedObject()
				{
					public void timeEventOccurred(long currenttime)
					{
						interpreter.scheduleStep(new ExecuteWaitForStep(ts[0], step));
					}
					
					public String toString()
					{
						return getComponentIdentifier().getLocalName()+".waitFor("+time+")_#"+this.hashCode();
					}
				});
				if(timers==null)
					timers	= new ArrayList<ITimer>();
				timers.add(ts[0]);
				ret.setResult(new TimerWrapper(ts[0]));
			}
		});
		
		return ret;
	}
	
	/**
	 *  Wait for the next tick.
	 *  @param time The time.
	 */
	public IFuture<TimerWrapper> waitForTick(final IComponentStep<Void> run)
	{
		final Future<TimerWrapper> ret = new Future<TimerWrapper>();
		
		getServiceContainer().searchService(IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new ExceptionDelegationResultListener<IClockService, TimerWrapper>(ret)
		{
			public void customResultAvailable(IClockService cs)
			{
				final ITimer[] ts = new ITimer[1];
				ts[0] = cs.createTickTimer(new ITimedObject()
				{
					public void timeEventOccurred(final long currenttime)
					{
						try
						{
							interpreter.scheduleStep(new ExecuteWaitForStep(ts[0], run));
						}
						catch(ComponentTerminatedException e)
						{
						}
					}
				});
				if(timers==null)
					timers	= new ArrayList<ITimer>();
				timers.add(ts[0]);
				ret.setResult(new TimerWrapper(ts[0]));
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setException(exception);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get the logger.
	 *  @return The logger.
	 */
	public Logger getLogger()
	{
		return getAgentAdapter().getLogger();
	}	
	
	/**
	 *  Kill the agent.
	 */
	public IFuture<Map<String, Object>> killAgent()
	{
		final Future<Map<String, Object>> ret = new Future<Map<String, Object>>();
		getServiceContainer().searchService(IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Map<String, Object>>(ret)
		{
			public void customResultAvailable(IComponentManagementService cms)
			{
				cms.destroyComponent(getComponentIdentifier())
					.addResultListener(new DelegationResultListener<Map<String, Object>>(ret));
			}
		});
		return ret;
	}
		
	/**
	 *  Send a message.
	 *  @param me	The message content (name value pairs).
	 *  @param mt	The message type describing the content.
	 */
	public IFuture<Void> sendMessage(Map<String, Object> me, MessageType mt)
	{
		return sendMessage(me, mt, null, null);
	}
	
	/**
	 *  Send a message.
	 *  @param me	The message content (name value pairs).
	 *  @param mt	The message type describing the content.
	 */
	public IFuture<Void> sendMessage(final Map<String, Object> me, final MessageType mt, 
		final byte[] codecids, final Map<String, Object> nonfunc)
	{
		final Future<Void> ret = new Future<Void>();
		
		getServiceContainer().searchService(IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new ExceptionDelegationResultListener<IMessageService, Void>(ret)
		{
			public void customResultAvailable(IMessageService ms)
			{
				ms.sendMessage(me, mt, interpreter.getAgentAdapter().getComponentIdentifier(),
					interpreter.getModel().getResourceIdentifier(), null, codecids, nonfunc)
					.addResultListener(createResultListener(new DelegationResultListener<Void>(ret)));
			}
		});
		
		return ret;
	}
	
	/**
	 *  Send a message and wait for a reply.
	 *  @param me	The message content (name value pairs).
	 *  @param mt	The message type describing the content.
	 */
	// Todo: supply reply message as future return value?
	public IFuture<Void> sendMessageAndWait(final Map<String, Object> me, final MessageType mt, final IMessageHandler handler)
	{
		boolean hasconvid = false;
		ParameterSpecification[] ps = mt.getConversationIdentifiers();
		for(int i=0; i<ps.length && !hasconvid; i++)
		{
			if(me.get(ps[i].getName())!=null)
				hasconvid = true;
		}
		if(!hasconvid)
			throw new RuntimeException("Message has no conversation identifier set: "+me);
		
		addMessageHandler(new IMessageHandler()
		{
			IFilter filter = handler.getFilter()==null? 
				(IFilter)new MessageConversationFilter(me, mt):
				new ComposedFilter(new IFilter[]{new MessageConversationFilter(me, mt), handler.getFilter()});
				
			public long getTimeout()
			{
				return handler.getTimeout();
			}	
				
			public boolean isRemove()
			{
				return handler.isRemove();
			}
			
			public void handleMessage(Map<String, Object> msg, MessageType type)
			{
				handler.handleMessage(msg, type);
			}
			
			public void timeoutOccurred()
			{
				handler.timeoutOccurred();
			}
			
			public IFilter getFilter()
			{
				return filter;
			}
		});
		return sendMessage(me, mt);
	}
	
	/**
	 *  Add a message handler.
	 *  @param  The handler.
	 */
	public void addMessageHandler(IMessageHandler handler)
	{
		interpreter.addMessageHandler(handler);
	}
	
	/**
	 *  Remove a message handler.
	 *  @param handler The handler.
	 */
	public void removeMessageHandler(IMessageHandler handler)
	{
		interpreter.removeMessageHandler(handler);
	}
	
//	/**
//	 *  Create component identifier.
//	 *  @param name The name.
//	 *  @param local True for local name.
//	 *  @param addresses The addresses.
//	 *  @return The new component identifier.
//	 */
//	public IFuture<IComponentIdentifier> createComponentIdentifier(String name)
//	{
//		return createComponentIdentifier(name, true, null);
//	}
//	
//	/**
//	 *  Create component identifier.
//	 *  @param name The name.
//	 *  @param local True for local name.
//	 *  @param addresses The addresses.
//	 *  @return The new component identifier.
//	 */
//	public IFuture<IComponentIdentifier> createComponentIdentifier(String name, boolean local)
//	{
//		return createComponentIdentifier(name, local, null);
//	}
//	
//	/**
//	 *  Create component identifier.
//	 *  @param name The name.
//	 *  @param local True for local name.
//	 *  @param addresses The addresses.
//	 *  @return The new component identifier.
//	 */
//	public IFuture<IComponentIdentifier> createComponentIdentifier(final String name, final boolean local, final String[] addresses)
//	{
//		final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
//		
//		getServiceContainer().searchService(IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//			.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, IComponentIdentifier>(ret)
//		{
//			public void customResultAvailable(IComponentManagementService cms)
//			{
//				ret.setResult(cms.createComponentIdentifier(name, local, addresses));
//			}
//		});
//		
//		return ret;
//	}
	
	/**
	 *  Create a reply to this message event.
	 *  @param msgeventtype	The message event type.
	 *  @return The reply event.
	 */
	public IFuture<Map<String, Object>> createReply(Map<String, Object> msg, MessageType mt)
	{
		return new Future<Map<String, Object>>(mt.createReply(msg));
	}
	
	/**
	 *  Get the agent name.
	 *  @return The agent name.
	 */
	public String getAgentName()
	{
		return getComponentIdentifier().getLocalName();
	}
	
	/**
	 * Get the agent identifier.
	 * @return The agent identifier.
	 */
	public IComponentIdentifier	getComponentIdentifier()
	{
		return interpreter.getAgentAdapter().getComponentIdentifier();
	}
		
	/**
	 *  Schedule a step of the agent.
	 *  May safely be called from external threads.
	 *  @param step	Code to be executed as a step of the agent.
	 * /
	public void	scheduleStep(ICommand step)
	{
		interpreter.scheduleStep(step);
	}*/
	
	/**
	 *  Schedule a step of the agent.
	 *  May safely be called from external threads.
	 *  @param step	Code to be executed as a step of the agent.
	 */
	public <T> IFuture<T> scheduleStep(IComponentStep<T> step)
	{
		return interpreter.scheduleStep(step);		
	}
	
	/**
	 *  Add a service to the platform.
	 *  If under the same name and type a service was contained,
	 *  the old one is removed and shutdowned.
	 *  @param type The public service interface.
	 *  @param service The service.
	 *  @param type The proxy type (@see{BasicServiceInvocationHandler}).
	 */
	public IFuture<Void>	addService(String name, Class<?> type, Object service, String proxytype)
	{
		final Future<Void> ret = new Future<Void>();
		IFuture<IInternalService> fut = interpreter.addService(name, type, proxytype, null, service, null);
		fut.addResultListener(createResultListener(new ExceptionDelegationResultListener<IInternalService, Void>(ret)
		{
			public void customResultAvailable(IInternalService result)
			{
				ret.setResult(null);
			}
		}));
		return ret;
	}
	
	/**
	 *  Add a service to the platform. 
	 *  If under the same name and type a service was contained,
	 *  the old one is removed and shutdowned.
	 *  @param type The public service interface.
	 *  @param service The service.
	 */
	public IFuture<Void>	addService(String name, Class<?> type, Object service)
	{
		final Future<Void> ret = new Future<Void>();
		IFuture<IInternalService> fut = interpreter.addService(name, type, BasicServiceInvocationHandler.PROXYTYPE_DECOUPLED, null, service, null);
		fut.addResultListener(createResultListener(new ExceptionDelegationResultListener<IInternalService, Void>(ret)
		{
			public void customResultAvailable(IInternalService result)
			{
				ret.setResult(null);
			}
		}));
		return ret;
	}

	/**
	 *  Removes a service from the platform (shutdowns also the service).
	 *  @param service The service.
	 */
	public IFuture<Void>	removeService(IServiceIdentifier sid)
	{
		return ((IServiceContainer)interpreter.getServiceProvider()).removeService(sid);
	}
	
	/**
	 *  Wait for some time and execute a component step afterwards.
	 */
	public <T> IFuture<T> waitForDelay(final long delay, final IComponentStep<T> step)
	{
		return interpreter.waitForDelay(delay, step);
	}
	
	/**
	 *  Start the service provider.
	 * /
	public IFuture startServiceProvider()
	{
		return ((IServiceContainer)interpreter.getServiceProvider()).start();
	}*/
	
	/**
	 *  Invoke a runnable later that is guaranteed 
	 *  to be executed on agent thread.
	 * /
	// Use getExternalAccess().invokeLater() instead ->
	 * will not be executed as step of agent but as external entry.
	public void invokeLater(Runnable run)
	{
		interpreter.getAgentAdapter().invokeLater(run);
	}*/
	
	/**
	 *  Get the model of the component.
	 *  @return	The model.
	 */
	public IModelInfo getModel()
	{
		return interpreter.getModel();
	}
	
	/**
	 *  Get the children (if any).
	 *  @return The children.
	 */
	public IFuture<Collection<IExternalAccess>> getChildrenAccesses()
	{
		return interpreter.getAgentAdapter().getChildrenAccesses();
	}
	
	/**
	 *  Get the children (if any).
	 *  @return The children.
	 */
	public IFuture<IComponentIdentifier[]> getChildrenIdentifiers()
	{
		return interpreter.getAgentAdapter().getChildrenIdentifiers();
	}
	
	/**
	 *  Kill the component.
	 */
	public IFuture<Map<String, Object>> killComponent()
	{
		return killAgent();
	}
	
	/**
	 *  Add an component listener.
	 *  @param listener The listener.
	 */
	public IFuture<Void> addComponentListener(IComponentListener listener)
	{
		return interpreter.addComponentListener(listener);
	}
	
	/**
	 *  Remove a component listener.
	 *  @param listener The listener.
	 */
	public IFuture<Void> removeComponentListener(IComponentListener listener)
	{
		return interpreter.removeComponentListener(listener);
	}
	
	/**
	 *  Get a required service of a given name.
	 *  @param name The service name.
	 *  @return The service.
	 */
	public <T> IFuture<T> getRequiredService(String name)
	{
		return getServiceContainer().getRequiredService(name);
	}
	
	/**
	 *  Get a required services of a given name.
	 *  @param name The services name.
	 *  @return The service.
	 */
	public <T> IIntermediateFuture<T> getRequiredServices(String name)
	{
		return getServiceContainer().getRequiredServices(name);
	}
		
	//-------- helper classes --------
	
	/**
	 *  Wrap a timer and remove it from the agent when it is cancelled.
	 */
	protected class TimerWrapper implements ITimer
	{
		//-------- attributes --------
		
		/** The wrapped timer. */
		ITimer	timer;
		
		//-------- constructors--------
		
		/**
		 *  Wrap a timer.
		 */
		public TimerWrapper(ITimer timer)
		{
			this.timer	= timer;
		}
		
		//-------- ITimer interface --------
		
		public void cancel()
		{
			assert timers!=null;
			timers.remove(timer);
			timer.cancel();
		}

		public long getNotificationTime()
		{
			return timer.getNotificationTime();
		}

		public ITimedObject getTimedObject()
		{
			return timer.getTimedObject();
		}

		public void setNotificationTime(long time)
		{
			timer.setNotificationTime(time);
		}

		public boolean equals(Object obj)
		{
			return timer.equals(obj);
		}

		public int hashCode()
		{
			return timer.hashCode();
		}

		public String toString()
		{
			return timer.toString();
		}
	}

	/**
	 *  Step to execute a wait for entry.
	 */
	public static class ExecuteWaitForStep implements IComponentStep<Void>
	{
		//-------- attributes --------

		/** The timer. */
		private final ITimer ts;

		/** The component step. */
		private final IComponentStep<Void> run;

		//-------- constructors--------

		/**
		 * This class is constructed with an array of {@link ITimer}s and the {@link IComponentStep}
		 * which is scheduled for execution.
		 * @param ts an array of {@link ITimer}s
		 * @param run the {@link IComponentStep} which is scheduled for execution
		 */
		public ExecuteWaitForStep(ITimer ts, IComponentStep<Void> run)
		{
			this.ts = ts;
			this.run = run;
		}

		//-------- methods --------

		/**
		 * Removes the first entry from the {@link ITimer} array from the micro agents
		 * {@link MicroAgent#timers} {@link List} and executes the {@link IComponentStep}.
		 */
		public IFuture<Void> execute(IInternalAccess ia)
		{
			assert ((MicroAgent)ia).timers!=null;
			((MicroAgent)ia).timers.remove(ts);
			run.execute(ia);
			return IFuture.DONE;
		}

		/**
		 * @return "microagent.waitFor_#" plus the hash code of this class
		 */
		public String toString()
		{
			return ts==null? super.toString(): ts.getTimedObject()!=null? ts.getTimedObject().toString(): ts.toString();
		}
		
		/**
		 * Returns the {@link IComponentStep} that is scheduled for execution.
		 * @return The {@link IComponentStep} that is scheduled for execution
		 */
		public IComponentStep<Void> getComponentStep()
		{
			return run;
		}
	}

	/**
	 *  May be overriden to provide a custom service container implementations.
	 */
	// Only needed for ProxyAgent. Todo: remove
	public IServiceContainer createServiceContainer(Map<String, Object> args)
	{
		return null;
	}
	
	/**
	 *  Get the fetcher.
	 *  @return The fetcher.
	 */
	public IValueFetcher getFetcher()
	{
		return interpreter.getFetcher();
	}
	
	/**
	 *  Get the class loader of the component.
	 */
	public ClassLoader	getClassLoader()
	{
		return interpreter.getClassLoader();
	}

	/**
	 *  Get the interpreter.
	 *  @return The interpreter.
	 */
	public MicroAgentInterpreter getInterpreter()
	{
		return interpreter;
	}
	
	/**
	 *  Test if current thread is the component thread.
	 *  @return True if the current thread is the component thread.
	 */
	public boolean isComponentThread()
	{
		return !getComponentAdapter().isExternalThread();
	}
}
