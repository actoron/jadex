package jadex.micro;

import jadex.bridge.ComponentServiceContainer;
import jadex.bridge.DecouplingServiceInvocationInterceptor;
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
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.IIntermediateFuture;
import jadex.commons.IntermediateFuture;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.service.CacheServiceContainer;
import jadex.commons.service.IInternalService;
import jadex.commons.service.IServiceContainer;
import jadex.commons.service.IServiceIdentifier;
import jadex.commons.service.IServiceProvider;
import jadex.commons.service.RequiredServiceInfo;
import jadex.commons.service.SServiceProvider;
import jadex.commons.service.ServiceNotFoundException;
import jadex.commons.service.clock.IClockService;
import jadex.commons.service.clock.ITimedObject;
import jadex.commons.service.clock.ITimer;

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
	public void agentCreated()
	{
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
	public void agentKilled()
	{
	}

	//-------- methods --------
	
	/**
	 *  Get the service container.
	 *  @return The service container.
	 */
	public IServiceContainer createServiceContainer()
	{
//		return new CacheServiceContainer(new ComponentServiceContainer(getAgentAdapter()), 25, 1*30*1000); // 30 secs cache expire
		return new ComponentServiceContainer(getAgentAdapter());
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
	 *  Get the current time.
	 *  @return The current time.
	 */
	public IFuture getTime()
	{
		final Future ret = new Future();
		
		SServiceProvider.getService(getServiceProvider(), IClockService.class)
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
		
		SServiceProvider.getService(getServiceProvider(), IClockService.class)
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
						interpreter.scheduleStep(new IComponentStep()
						{
							public static final String XML_CLASSNAME = "teo1"; 
							public Object execute(IInternalAccess ia)
							{
								timers.remove(ts[0]);
								run.execute(ia);
								return null;
							}
							
							public String toString()
							{
								return getComponentIdentifier().getLocalName()+".waitForDue("+time+")_#"+this.hashCode();
							}
						});
					}
					
					public String toString()
					{
						return getComponentIdentifier().getLocalName()+".waitForDue("+time+")_#"+this.hashCode();
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
		
		SServiceProvider.getService(getServiceProvider(), IClockService.class)
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
						interpreter.scheduleStep(new IComponentStep()
						{
							public static final String XML_CLASSNAME = "teo2"; 
							public Object execute(IInternalAccess ia)
							{
								timers.remove(ts[0]);
								run.execute(ia);
								return null;
							}
							
							public String toString()
							{
								return "microagent.waitForTickDue()_#"+this.hashCode();
							}
						});
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
		SServiceProvider.getService(getServiceProvider(), IComponentManagementService.class)
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
		final Future ret = new Future();
		
		SServiceProvider.getService(getServiceProvider(), IMessageService.class)
			.addResultListener(createResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				IMessageService ms = (IMessageService)result;
				ms.sendMessage(me, mt, interpreter.getAgentAdapter().getComponentIdentifier(),
					interpreter.getAgentModel().getClassLoader())
					.addResultListener(createResultListener(new DelegationResultListener(ret)));
			}
		}));
		
		return ret;
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
		
		SServiceProvider.getService(getServiceProvider(), IComponentManagementService.class)
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
	public IFuture createReply(final Map msg, final MessageType mt)
	{
		final Future ret = new Future();
		SServiceProvider.getService(getServiceProvider(), IMessageService.class)
			.addResultListener(createResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				IMessageService ms = (IMessageService)result;
				ret.setResult(ms.createReply(msg, mt));
			}
		}));
		
		return ret;
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
	public void addDirectService(IInternalService service)
	{
		((IServiceContainer)interpreter.getServiceProvider()).addService(service);
	}
	
	/**
	 *  Add a service to the platform. 
	 *  If under the same name and type a service was contained,
	 *  the old one is removed and shutdowned.
	 *  @param service The service.
	 */
	public void addService(IInternalService service)
	{
		IInternalService proxyser = DecouplingServiceInvocationInterceptor
			.createServiceProxy(getExternalAccess(), getAgentAdapter(), service);
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
	public void addComponentListener(IComponentListener listener)
	{
		interpreter.addComponentListener(listener);
	}
	
	/**
	 *  Remove a component listener.
	 *  @param listener The listener.
	 */
	public void removeComponentListener(IComponentListener listener)
	{
		interpreter.removeComponentListener(listener);
	}
	
	/**
	 *  Get a required service of a given name.
	 *  @param name The service name.
	 *  @return The service.
	 */
	public IFuture getRequiredService(String name)
	{
		return getRequiredService(name, false);
	}
	
	/**
	 *  Get a required services of a given name.
	 *  @param name The services name.
	 *  @return The service.
	 */
	public IIntermediateFuture getRequiredServices(String name)
	{
		return getRequiredServices(name, false);
	}
	
	/**
	 *  Get a required service.
	 *  @return The service.
	 */
	public IFuture getRequiredService(String name, boolean rebind)
	{
		RequiredServiceInfo info = getModel().getRequiredService(name);
		if(info==null)
		{
			Future ret = new Future();
			ret.setException(new ServiceNotFoundException(name));
			return ret;
		}
		else
		{
			return interpreter.getServiceContainer().getRequiredService(info, rebind);
		}
	}
	
	/**
	 *  Get a required services.
	 *  @return The services.
	 */
	public IIntermediateFuture getRequiredServices(String name, boolean rebind)
	{
		RequiredServiceInfo info = getModel().getRequiredService(name);
		if(info==null)
		{
			IntermediateFuture ret = new IntermediateFuture();
			ret.setException(new ServiceNotFoundException(name));
			return ret;
		}
		else
		{
			return interpreter.getServiceContainer().getRequiredServices(info, rebind);
		}
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

}
