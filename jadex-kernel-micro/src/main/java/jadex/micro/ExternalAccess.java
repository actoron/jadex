package jadex.micro;

import jadex.bridge.IComponentAdapter;
import jadex.bridge.MessageType;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.service.BasicServiceProvider;

import java.util.Map;

/**
 * External access interface.
 */
public class ExternalAccess extends BasicServiceProvider implements IMicroExternalAccess 
{
	// -------- attributes --------

	/** The agent. */
	protected MicroAgent agent;

	/** The interpreter. */
	protected MicroAgentInterpreter interpreter;
	
	/** The agent adapter. */
	protected IComponentAdapter adapter;

	// -------- constructors --------

	/**
	 *	Create an external access.
	 */
	public ExternalAccess(MicroAgent agent, MicroAgentInterpreter interpreter)
	{
		this.agent = agent;
		this.interpreter = interpreter;
		this.adapter = interpreter.getAgentAdapter();
	}

	// -------- eventbase shortcut methods --------

	/**
	 *  Send a message.
	 * 
	 *  @param me	The message.
	 *  @param mt	The message type.
	 */
	public void sendMessage(final Map me, final MessageType mt)
	{
		adapter.invokeLater(new Runnable()
		{
			public void run()
			{
				agent.sendMessage(me, mt);
				// System.out.println("Send message: "+rme);
			}
		});
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
	 *  Get the agent implementation.
	 *  Operations on the agent object
	 *  should be properly synchronized with invokeLater()!
	 */
	public IFuture getAgent()
	{
		final Future ret = new Future();
		adapter.invokeLater(new Runnable() 
		{
			public void run() 
			{
				ret.setResult(agent);
			}
		});
		return ret;
	}
	
	/**
	 *  Get the model of the component.
	 */
	public IFuture getModel()
	{
		final Future ret = new Future();
		adapter.invokeLater(new Runnable() 
		{
			public void run() 
			{
				ret.setResult(interpreter.getAgentModel());
			}
		});
		return ret;
	}
	
	/**
	 *  Get the id of the component.
	 *  @return	The component id.
	 */
	public IFuture getComponentIdentifier()
	{
		final Future ret = new Future();
		adapter.invokeLater(new Runnable() 
		{
			public void run() 
			{
				ret.setResult(interpreter.getAgentAdapter().getComponentIdentifier());
			}
		});
		return ret;
	}
	
	/**
	 *  Get the parent component.
	 *  @return The parent component.
	 */
	public IFuture getParent()
	{
		final Future ret = new Future();
		adapter.invokeLater(new Runnable() 
		{
			public void run() 
			{
				ret.setResult(interpreter.getParent());
			}
		});
		return ret;
	}

	/**
	 *  Get the interpreter.
	 *  @return The interpreter.
	 */
	public MicroAgentInterpreter getInterpreter()
	{
		return this.interpreter;
	}
}
