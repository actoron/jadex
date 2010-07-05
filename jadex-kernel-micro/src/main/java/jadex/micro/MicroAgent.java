package jadex.micro;

import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IMessageService;
import jadex.bridge.MessageType;
import jadex.commons.concurrent.IResultListener;
import jadex.service.IServiceProvider;
import jadex.service.clock.IClockService;
import jadex.service.clock.ITimedObject;
import jadex.service.clock.ITimer;

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
	protected ITimer timer;
	
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
	public void agentCreated()
	{
	}
	
	/**
	 *  Called when the agent is born and whenever it wants to execute an action
	 *  (e.g. calls wakeup() in one of the other methods).
	 *  The platform guarantees that executeAction() will not be called in parallel. 
	 *  @return True, when there are more actions waiting to be executed. 
	 * /
	public boolean executeAction()
	{
		return false;
	}*/
	
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
	
	//-------- methods --------
	
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
	 *  Get the agent platform.
	 *  @return The agent platform. 
	 */
	public IServiceProvider getServiceProvider()
	{
		return interpreter.getAgentAdapter().getServiceProvider();
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
	public long getTime()
	{
		return ((IClockService)getServiceProvider().getService(IClockService.class)).getTime();
	}
	
	/**
	 *  Wait for an secified amount of time.
	 *  @param time The time.
	 *  @param run The runnable.
	 */
	public void waitFor(final long time, final Runnable run)
	{
		if(timer!=null)
			throw new RuntimeException("timer should be null");
		this.timer = ((IClockService)getServiceProvider().getService(IClockService.class)).createTimer(time, new ITimedObject()
		{
			public void timeEventOccurred(long currenttime)
			{
				interpreter.scheduleStep(new Runnable()
				{
					public void run()
					{
						timer = null;
						run.run();
					}
					
					public String toString()
					{
						return "microagent.waitForDue("+time+")_#"+this.hashCode();
					}
				});
			}
		});
	}
	
	/**
	 *  Wait for the next tick.
	 *  @param time The time.
	 */
	public void waitForTick(final Runnable run)
	{
		if(timer!=null)
			throw new RuntimeException("timer should be null");
		this.timer = ((IClockService)getServiceProvider().getService(IClockService.class)).createTickTimer(new ITimedObject()
		{
			public void timeEventOccurred(final long currenttime)
			{
				interpreter.scheduleStep(new Runnable()
				{
					public void run()
					{
						timer = null;
						run.run();
					}
					
					public String toString()
					{
						return "microagent.waitForTickDue()_#"+this.hashCode();
					}
				});
			}
		});
	}
	
	/**
	 *  Get the logger.
	 *  @return The logger.
	 */
	public Logger getLogger()
	{
		return interpreter.getAgentAdapter().getLogger();
	}	
	
	/**
	 *  Kill the agent.
	 */
	public void killAgent()
	{
		((IComponentManagementService)interpreter.getAgentAdapter().getServiceProvider()
			.getService(IComponentManagementService.class))
			.destroyComponent(interpreter.getAgentAdapter().getComponentIdentifier());
	}
		
	/**
	 *  Send a message.
	 *  @param me	The message content (name value pairs).
	 *  @param mt	The message type describing the content.
	 */
	public void sendMessage(Map me, MessageType mt)
	{
		((IMessageService)getServiceProvider().getService(IMessageService.class)).
			sendMessage(me, mt, interpreter.getAgentAdapter(), interpreter.getAgentModel().getClassLoader());
//			sendMessage(me, mt, getComponentIdentifier(), interpreter.getAgentModel().getClassLoader());
	}
	
	/**
	 *  Create component identifier.
	 *  @param name The name.
	 *  @param local True for local name.
	 *  @param addresses The addresses.
	 *  @return The new component identifier.
	 */
	public IComponentIdentifier createComponentIdentifier(String name)
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
	public IComponentIdentifier createComponentIdentifier(String name, boolean local)
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
	public IComponentIdentifier createComponentIdentifier(final String name, final boolean local, final String[] addresses)
	{
		IComponentManagementService cms = (IComponentManagementService)interpreter.getAgentAdapter().getServiceProvider().getService(IComponentManagementService.class);	
		return cms.createComponentIdentifier(name, local, addresses);
	}
	
	/**
	 *  Create a reply to this message event.
	 *  @param msgeventtype	The message event type.
	 *  @return The reply event.
	 */
	public Map createReply(Map msg, MessageType mt)
	{
		IMessageService ms = (IMessageService)interpreter.getAgentAdapter().getServiceProvider().getService(IMessageService.class);	
		return ms.createReply(msg, mt);
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
	public void	scheduleStep(Runnable step)
	{
		interpreter.scheduleStep(step);
	}
	
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
