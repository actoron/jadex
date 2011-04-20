package jadex.micro;

import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentListener;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IMessageService;
import jadex.bridge.IModelInfo;
import jadex.bridge.MessageType;
import jadex.bridge.MessageType.ParameterSpecification;
import jadex.bridge.service.IInternalService;
import jadex.bridge.service.IServiceContainer;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.clock.IClockService;
import jadex.bridge.service.clock.ITimedObject;
import jadex.bridge.service.clock.ITimer;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.bridge.service.component.ComponentServiceContainer;
import jadex.commons.ComposedFilter;
import jadex.commons.IFilter;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 *  Base class for application agents.
 */
public abstract class MicroAgent implements IMicroAgent, IInternalAccess
{
	//-------- attributes --------
	
	/** The agent interpreter. */
	protected MicroAgentInterpreter interpreter;
	
	/** The current timer. */
	protected List timers;
	
	//-------- constructors --------
	
	/**
	 *  Init the micro agent with the interpreter.
	 *  @param interpreter The interpreter.
	 */
	public void init(MicroAgentInterpreter interpreter)
	{
//		System.out.println("Init: "+interpreter);
		this.interpreter = interpreter;
		this.timers = new ArrayList();
	}
	
	//-------- interface methods --------
	
	/**
	 *  Called once after agent creation.
	 */
	public IFuture agentCreated()
	{
		return IFuture.DONE;
	}
	
	/**
	 *  Execute the functional body of the agent.
	 *  Is only called once.
	 */
	public void executeBody()
	{
	}

	/**
	 *  Called, whenever a message is received.
	 *  @param msg The message.
	 *  @param mt The message type.
	 */
	public void messageArrived(Map msg, MessageType mt)
	{
	}

	/**
	 *  Called just before the agent is removed from the platform.
	 *  @return The result of the component.
	 */
	public IFuture agentKilled()
	{
		return IFuture.DONE;
	}

	//-------- methods --------
	
	/**
	 *  Get the service container.
	 *  @return The service container.
	 */
	public IServiceContainer createServiceContainer()
	{
//		return new CacheServiceContainer(new ComponentServiceContainer(getAgentAdapter()), 25, 1*30*1000); // 30 secs cache expire
		return new ComponentServiceContainer(getAgentAdapter(), MicroAgentFactory.FILETYPE_MICROAGENT,
			interpreter.getAgentModel().getRequiredServices(), interpreter.getRequiredServiceBindings());
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
	public IExternalAccess getParent()
	{
		return interpreter.getParent();
	}
	
	/**
	 *  Get the agent adapter.
	 *  @return The agent adapter.
	 */
	public IComponentAdapter getAgentAdapter()
	{
		return interpreter.getAgentAdapter();
	}
	
	/**
	 *  Get the arguments.
	 *  @return The arguments.
	 */
	public Map getArguments()
	{
		return interpreter.getArguments();
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
	 *  Create a result listener that is executed as an agent step.
	 *  @param listener The listener to be executed as an agent step.
	 */
	public IResultListener createResultListener(IResultListener listener)
	{
		return interpreter.createResultListener(listener);
	}
	
	/**
	 *  Create a result listener that is executed as an agent step.
	 *  @param listener The listener to be executed as an agent step.
	 */
	public IIntermediateResultListener createResultListener(IIntermediateResultListener listener)
	{
		return interpreter.createResultListener(listener);
	}
	
	/**
	 *  Get the current time.
	 *  @return The current time.
	 */
	public IFuture getTime()
	{
		final Future ret = new Future();
		
		SServiceProvider.getService(getServiceContainer(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(createResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				IClockService cs = (IClockService)result;
				ret.setResult(new Long(cs.getTime()));
			}
		}));
		
		return ret;
	}
	
	// for debugging.
	protected long	longtime	= 0;
	
	/**
	 *  Wait for an specified amount of time.
	 *  @param time The time.
	 *  @param run The runnable.
	 */
	public IFuture waitFor(final long time, final IComponentStep run)
	{
		longtime	= Math.max(longtime, time);
		final Future ret = new Future();
		
		SServiceProvider.getService(getServiceContainer(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				IClockService cs = (IClockService)result;
				final ITimer[] ts = new ITimer[1];
				ts[0] = cs.createTimer(time, new ITimedObject()
				{
					public void timeEventOccurred(long currenttime)
					{
						interpreter.scheduleStep(new ExecuteWaitForStep(ts[0], run));
					}
					
					public String toString()
					{
						return getComponentIdentifier().getLocalName()+".waitFor("+time+")_#"+this.hashCode();
					}
				});
				timers.add(ts[0]);
				ret.setResult(new TimerWrapper(ts[0]));
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setException(exception);
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Wait for the next tick.
	 *  @param time The time.
	 */
	public IFuture waitForTick(final IComponentStep run)
	{
		final Future ret = new Future();
		
		SServiceProvider.getService(getServiceContainer(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				IClockService cs = (IClockService)result;
				final ITimer[] ts = new ITimer[1];
				ts[0] = cs.createTickTimer(new ITimedObject()
				{
					public void timeEventOccurred(final long currenttime)
					{
						interpreter.scheduleStep(new ExecuteWaitForStep(ts[0], run));
					}
				});
				timers.add(ts[0]);
				ret.setResult(new TimerWrapper(ts[0]));
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setException(exception);
			}
		}));
		
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
	public IFuture killAgent()
	{
		final Future ret = new Future();
		SServiceProvider.getService(getServiceContainer(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(createResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				IComponentManagementService cms = (IComponentManagementService)result;
				cms.destroyComponent(getComponentIdentifier()).addResultListener(new DelegationResultListener(ret));
			}
		}));
		return ret;
	}
		
	/**
	 *  Send a message.
	 *  @param me	The message content (name value pairs).
	 *  @param mt	The message type describing the content.
	 */
	public IFuture sendMessage(final Map me, final MessageType mt)
	{
		return sendMessage(me, mt, null);
	}
	
	/**
	 *  Send a message.
	 *  @param me	The message content (name value pairs).
	 *  @param mt	The message type describing the content.
	 */
	public IFuture sendMessage(final Map me, final MessageType mt, final byte[] codecids)
	{
		final Future ret = new Future();
		
		SServiceProvider.getService(getServiceContainer(), IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(createResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				IMessageService ms = (IMessageService)result;
				ms.sendMessage(me, mt, interpreter.getAgentAdapter().getComponentIdentifier(),
					interpreter.getAgentModel().getClassLoader(), codecids)
					.addResultListener(createResultListener(new DelegationResultListener(ret)));
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Send a message and wait for a reply.
	 *  @param me	The message content (name value pairs).
	 *  @param mt	The message type describing the content.
	 */
	public IFuture sendMessageAndWait(final Map me, final MessageType mt, final IMessageHandler handler)
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
			
			public void handleMessage(Map msg, MessageType type)
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
	
	/**
	 *  Create component identifier.
	 *  @param name The name.
	 *  @param local True for local name.
	 *  @param addresses The addresses.
	 *  @return The new component identifier.
	 */
	public IFuture createComponentIdentifier(String name)
	{
		return createComponentIdentifier(name, true, null);
	}
	
	/**
	 *  Create component identifier.
	 *  @param name The name.
	 *  @param local True for local name.
	 *  @param addresses The addresses.
	 *  @return The new component identifier.
	 */
	public IFuture createComponentIdentifier(String name, boolean local)
	{
		return createComponentIdentifier(name, local, null);
	}
	
	/**
	 *  Create component identifier.
	 *  @param name The name.
	 *  @param local True for local name.
	 *  @param addresses The addresses.
	 *  @return The new component identifier.
	 */
	public IFuture createComponentIdentifier(final String name, final boolean local, final String[] addresses)
	{
		final Future ret = new Future();
		
		SServiceProvider.getService(getServiceContainer(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(createResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				IComponentManagementService cms = (IComponentManagementService)result;
				ret.setResult(cms.createComponentIdentifier(name, local, addresses));
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Create a reply to this message event.
	 *  @param msgeventtype	The message event type.
	 *  @return The reply event.
	 */
	public IFuture createReply(Map msg, MessageType mt)
	{
		return new Future(mt.createReply(msg));
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
	public IFuture scheduleStep(IComponentStep step)
	{
		return interpreter.scheduleStep(step);		
	}
	
	/**
	 *  Add a service to the platform.
	 *  If under the same name and type a service was contained,
	 *  the old one is removed and shutdowned.
	 *  @param service The service.
	 */
	public void addDirectService(Object service)
	{
		IInternalService proxyser = BasicServiceInvocationHandler.createProvidedServiceProxy(this, getAgentAdapter(), service, true);
		((IServiceContainer)interpreter.getServiceProvider()).addService(proxyser);
	}
	
	/**
	 *  Add a service to the platform. 
	 *  If under the same name and type a service was contained,
	 *  the old one is removed and shutdowned.
	 *  @param service The service.
	 */
	public void addService(Object service)
	{
		IInternalService proxyser = BasicServiceInvocationHandler.createProvidedServiceProxy(this, getAgentAdapter(), service, false);
		((IServiceContainer)interpreter.getServiceProvider()).addService(proxyser);
	}

	/**
	 *  Removes a service from the platform (shutdowns also the service).
	 *  @param service The service.
	 */
	public void removeService(IServiceIdentifier sid)
	{
		((IServiceContainer)interpreter.getServiceProvider()).removeService(sid);
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
		return interpreter.getAgentModel();
	}
	
	/**
	 *  Get the children (if any).
	 *  @return The children.
	 */
	public IFuture getChildren()
	{
		return interpreter.getAgentAdapter().getChildrenAccesses();
	}
	
	/**
	 *  Kill the component.
	 */
	public IFuture killComponent()
	{
		return killAgent();
	}
	
	/**
	 *  Add an component listener.
	 *  @param listener The listener.
	 */
	public IFuture addComponentListener(IComponentListener listener)
	{
		return interpreter.addComponentListener(listener);
	}
	
	/**
	 *  Remove a component listener.
	 *  @param listener The listener.
	 */
	public IFuture removeComponentListener(IComponentListener listener)
	{
		return interpreter.removeComponentListener(listener);
	}
	
	/**
	 *  Get a required service of a given name.
	 *  @param name The service name.
	 *  @return The service.
	 */
	public IFuture getRequiredService(String name)
	{
		return getServiceContainer().getRequiredService(name);
	}
	
	/**
	 *  Get a required services of a given name.
	 *  @param name The services name.
	 *  @return The service.
	 */
	public IIntermediateFuture getRequiredServices(String name)
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
	public static class ExecuteWaitForStep implements IComponentStep
	{
		//-------- attributes --------

		/** The timer. */
		private final ITimer ts;

		/** The component step. */
		private final IComponentStep run;

		//-------- constructors--------

		/**
		 * This class is constructed with an array of {@link ITimer}s and the {@link IComponentStep}
		 * which is scheduled for execution.
		 * @param ts an array of {@link ITimer}s
		 * @param run the {@link IComponentStep} which is scheduled for execution
		 */
		public ExecuteWaitForStep(ITimer ts, IComponentStep run)
		{
			this.ts = ts;
			this.run = run;
		}

		//-------- methods --------

		/**
		 * Removes the first entry from the {@link ITimer} array from the micro agents
		 * {@link MicroAgent#timers} {@link List} and executes the {@link IComponentStep}.
		 */
		public Object execute(IInternalAccess ia)
		{
//			((MicroAgent)ia).timers.remove(ts[0]);
			run.execute(ia);
			return null;
		}

		/**
		 * @return "microagent.waitFor_#" plus the hash code of this class
		 */
		public String toString()
		{
			return ts.getTimedObject().toString();
		}
		
		/**
		 * Returns the {@link IComponentStep} that is scheduled for execution.
		 * @return The {@link IComponentStep} that is scheduled for execution
		 */
		public IComponentStep getComponentStep()
		{
			return run;
		}
	}
}
