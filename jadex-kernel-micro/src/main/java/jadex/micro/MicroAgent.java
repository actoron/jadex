package jadex.micro;

import jadex.bridge.ComponentServiceContainer;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IMessageService;
import jadex.bridge.MessageType;
import jadex.commons.Future;
import jadex.commons.ICommand;
import jadex.commons.IFuture;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.service.BasicService;
import jadex.commons.service.CacheServiceContainer;
import jadex.commons.service.IServiceContainer;
import jadex.commons.service.IServiceProvider;
import jadex.commons.service.SServiceProvider;
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
public abstract class MicroAgent implements IMicroAgent
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
		return new CacheServiceContainer(new ComponentServiceContainer(getAgentAdapter()), 25, 1*30*1000); // 30 secs cache expire
//		return new ComponentServiceContainer(getAgentAdapter());
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
	public IMicroExternalAccess	getExternalAccess()
	{
		return new ExternalAccess(this, interpreter);
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
		return interpreter.getArguments().get(name);
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
			public void resultAvailable(Object source, Object result)
			{
				IClockService cs = (IClockService)result;
				ret.setResult(new Long(cs.getTime()));
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Wait for an secified amount of time.
	 *  @param time The time.
	 *  @param run The runnable.
	 */
	public IFuture waitFor(final long time, final ICommand run)
	{
		final Future ret = new Future();
		
		SServiceProvider.getService(getServiceProvider(), IClockService.class)
			.addResultListener(createResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				IClockService cs = (IClockService)result;
				final ITimer[] ts = new ITimer[1];
				ts[0] = cs.createTimer(time, new ITimedObject()
				{
					public void timeEventOccurred(long currenttime)
					{
						interpreter.scheduleStep(new ICommand()
						{
							public void execute(Object agent)
							{
								timers.remove(ts[0]);
								run.execute(agent);
							}
							
							public String toString()
							{
								return "microagent.waitForDue("+time+")_#"+this.hashCode();
							}
						});
					}
				});
				timers.add(ts[0]);
				ret.setResult(ts[0]);
			}
			
			public void exceptionOccurred(Object source, Exception exception)
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
	public void waitForTick(final ICommand run)
	{
		SServiceProvider.getService(getServiceProvider(), IClockService.class)
			.addResultListener(createResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				IClockService cs = (IClockService)result;
				final ITimer[] ts = new ITimer[1];
				ts[0] = cs.createTickTimer(new ITimedObject()
				{
					public void timeEventOccurred(final long currenttime)
					{
						interpreter.scheduleStep(new ICommand()
						{
							public void execute(Object agent)
							{
								timers.remove(ts[0]);
								run.execute(agent);
							}
							
							public String toString()
							{
								return "microagent.waitForTickDue()_#"+this.hashCode();
							}
						});
					}
				});
				timers.add(ts[0]);
			}
		}));
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
	public void killAgent()
	{
		SServiceProvider.getService(getServiceProvider(), IComponentManagementService.class)
			.addResultListener(createResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				IComponentManagementService cms = (IComponentManagementService)result;
				cms.destroyComponent(getComponentIdentifier());
			}
		}));
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
			public void resultAvailable(Object source, Object result)
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
			public void resultAvailable(Object source, Object result)
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
			public void resultAvailable(Object source, Object result)
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
	 */
	public void	scheduleStep(ICommand step)
	{
		interpreter.scheduleStep(step);
	}
	
	/**
	 *  Add a service to the platform.
	 *  If under the same name and type a service was contained,
	 *  the old one is removed and shutdowned.
	 *  @param service The service.
	 */
	public void addService(BasicService service)
	{
		((IServiceContainer)interpreter.getServiceProvider()).addService(service);
	}

	/**
	 *  Removes a service from the platform (shutdowns also the service).
	 *  @param service The service.
	 */
	public void removeService(BasicService service)
	{
		((IServiceContainer)interpreter.getServiceProvider()).removeService(service);
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
}
