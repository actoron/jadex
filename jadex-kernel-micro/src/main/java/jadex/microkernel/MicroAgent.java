package jadex.microkernel;

import jadex.bridge.IAgentAdapter;
import jadex.bridge.IAgentIdentifier;
import jadex.bridge.IClockService;
import jadex.bridge.IMessageAdapter;
import jadex.bridge.IPlatform;
import jadex.bridge.ITimedObject;
import jadex.bridge.MessageType;
import jadex.commons.concurrent.IResultListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *  Base class for application agents.
 */
public abstract class MicroAgent implements IMicroAgent
{
	//-------- attributes --------
	
	/** The agent interpreter. */
	protected MicroAgentInterpreter interpreter;
	
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
		return false;
	}

	/**
	 *  Can be called concurrently (also during executeAction()).
	 *  
	 *  Inform the agent that a message has arrived.
	 *  Can be called concurrently (also during executeAction()).
	 *  @param message The message that arrived.
	 */
	public void messageArrived(IMessageAdapter message)
	{
	}

	/**
	 *  Request agent to kill itself.
	 *  The agent might perform arbitrary cleanup activities during which executeAction()
	 *  will still be called as usual.
	 */
	public void killAgent()
	{
	}
	
	/**
	 *  Get the external access for this agent.
	 *  The specific external access interface is kernel specific
	 *  and has to be casted to its corresponding incarnation.
	 */
	public Object getExternalAccess()
	{
		return new ExternalAccess(interpreter);
	}

	//-------- methods --------
	
	/**
	 *  Get the agent adapter.
	 *  @return The agent adapter.
	 */
	public IAgentAdapter getAgentAdapter()
	{
		return interpreter.getAgentAdapter();
	}
	
	/**
	 *  Get the agent platform.
	 *  @return The agent platform. 
	 */
	public IPlatform getPlatform()
	{
		return interpreter.getAgentAdapter().getPlatform();
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
	 *  Create a result listener that is called on the agent thread.
	 *  @param listener The listener to be called on the agent thread.
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
		return ((IClockService)getPlatform().getService(IClockService.class)).getTime();
	}
	
	/**
	 *  Wait for an secified amount of time.
	 *  @param time The time.
	 */
	public void waitFor(long time)
	{
		((IClockService)getPlatform().getService(IClockService.class)).createTimer(time, new ITimedObject()
		{
			public void timeEventOccurred()
			{
				getAgentAdapter().wakeup();
//				interpreter.invokeLater(new Runnable()
//				{
//					public void run()
//					{
//						executeAction();
//					}
//				});
			}
		});
	}
	
	/**
	 *  Get the logger.
	 *  @return The logger.
	 */
	public Logger getLogger()
	{
		return interpreter.getLogger();
	}
	
	/**
	 *  Get the agent name.
	 *  @return The agent name.
	 */
	public String getAgentName()
	{
		return getAgentIdentifier().getLocalName();
	}
	
	/**
	 * Get the agent identifier.
	 * @return The agent identifier.
	 */
	public IAgentIdentifier	getAgentIdentifier()
	{
		return interpreter.getAgentAdapter().getAgentIdentifier();
	}
	
	/**
	 *  Create a reply to this message event.
	 *  @param msgeventtype	The message event type.
	 *  @return The reply event.
	 */
	public Map createReply(Map msg, MessageType mt)
	{
		Map reply = new HashMap();
		
		MessageType.ParameterSpecification[] params	= mt.getParameters();
		for(int i=0; i<params.length; i++)
		{
			String sourcename = params[i].getSource();
			if(sourcename!=null)
			{
				Object sourceval = msg.get(sourcename);
				if(sourceval!=null)
				{
					reply.put(params[i].getName(), sourceval);
				}
			}
		}
		
		MessageType.ParameterSpecification[] paramsets = mt.getParameterSets();
		for(int i=0; i<paramsets.length; i++)
		{
			String sourcename = paramsets[i].getSource();
			if(sourcename!=null)
			{
				Object sourceval = msg.get(sourcename);
				if(sourceval!=null)
				{
					List tmp = new ArrayList();
					tmp.add(sourceval);
					reply.put(paramsets[i].getName(), tmp);	
				}
			}
		}
		
		return reply;
	}
	
}
