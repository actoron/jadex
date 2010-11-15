package jadex.micro;

import jadex.bridge.ComponentResultListener;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IModelInfo;
import jadex.bridge.MessageType;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.service.IServiceProvider;

import java.util.Map;

/**
 * External access interface.
 */
public class ExternalAccess implements IMicroExternalAccess 
{
	//-------- attributes --------

	/** The agent. */
	protected MicroAgent agent;

	/** The interpreter. */
	protected MicroAgentInterpreter interpreter;
	
	/** The agent adapter. */
	protected IComponentAdapter adapter;
	
	/** The provider. */
	protected IServiceProvider provider;
	
	// -------- constructors --------

	/**
	 *	Create an external access.
	 */
	public ExternalAccess(MicroAgent agent, MicroAgentInterpreter interpreter)
	{
		this.agent = agent;
		this.interpreter = interpreter;
		this.adapter = interpreter.getAgentAdapter();
		this.provider = interpreter.getServiceProvider();
	}

	// -------- eventbase shortcut methods --------

	/**
	 *  Send a message.
	 * 
	 *  @param me	The message.
	 *  @param mt	The message type.
	 */
	public IFuture sendMessage(final Map me, final MessageType mt)
	{
		final Future ret = new Future();
		try
		{
			adapter.invokeLater(new Runnable()
			{
				public void run()
				{
					agent.sendMessage(me, mt).addResultListener(new DelegationResultListener(ret));
					// System.out.println("Send message: "+rme);
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
//	 *  Schedule a step of the agent.
//	 *  May safely be called from external threads.
//	 *  @param step	Code to be executed as a step of the agent.
//	 */
//	public IFuture scheduleStep(ICommand step)
//	{
//		return interpreter.scheduleStep(step);
//	}
	
	/**
	 *  Schedule a step of the agent.
	 *  May safely be called from external threads.
	 *  @param step	Code to be executed as a step of the agent.
	 *  @return The result of the step.
	 */
	public IFuture scheduleStep(IComponentStep step)
	{
		return interpreter.scheduleStep(step);
	}
	
	// todo: support with IResultCommand also?!
	/**
	 *  Wait for an specified amount of time.
	 *  @param time The time.
	 *  @param run The runnable.
	 */
	public IFuture waitFor(long time, IComponentStep run)
	{
		return agent.waitFor(time, run);
	}
	
	// todo: support with IResultCommand also?!
	/**
	 *  Wait for the next tick.
	 *  @param time The time.
	 */
	public IFuture waitForTick(IComponentStep run)
	{
		return agent.waitForTick(run);
	}

	/**
	 *  Get the agent implementation.
	 *  Operations on the agent object
	 *  should be properly synchronized with invokeLater()!
	 */
	public IFuture getAgent()
	{
		final Future ret = new Future();
		try
		{
			adapter.invokeLater(new Runnable() 
			{
				public void run() 
				{
					ret.setResult(agent);
				}
			});
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
		return ret;
	}
	
	/**
	 *  Get the model of the component.
	 */
	public IModelInfo	getModel()
	{
		return interpreter.getAgentModel();
	}
	
	/**
	 *  Get the id of the component.
	 *  @return	The component id.
	 */
	public IComponentIdentifier	getComponentIdentifier()
	{
		return interpreter.getAgentAdapter().getComponentIdentifier();
	}
	
	/**
	 *  Get the parent component.
	 *  @return The parent component.
	 */
	public IComponentIdentifier	getParent()
	{
		return interpreter.getParent().getComponentIdentifier();
	}
	
	/**
	 *  Get the children (if any).
	 *  @return The children.
	 */
	public IFuture getChildren()
	{
		return adapter.getChildrenIdentifiers();
	}

	/**
	 *  Kill the component.
	 */
	public IFuture killComponent()
	{
		final Future ret = new Future();
		
		if(adapter.isExternalThread())
		{
			adapter.invokeLater(new Runnable() 
			{
				public void run() 
				{
					interpreter.cleanupComponent().addResultListener(new DelegationResultListener(ret));
				}
			});
		}
		else
		{
			interpreter.cleanupComponent().addResultListener(new DelegationResultListener(ret));
		}
		
		return ret;
	}
	
	/**
	 *  Get the application component.
	 */
	public IServiceProvider getServiceProvider()
	{
		return provider;
	}
	
	/**
	 *  Get the interpreter.
	 *  @return The interpreter.
	 */
	public MicroAgentInterpreter getInterpreter()
	{
		return this.interpreter;
	}
	
	/**
	 *  Create a result listener that will be 
	 *  executed on the component thread.
	 *  @param listener The result listener.
	 *  @return A result listener that is called on component thread.
	 */
	public IResultListener createResultListener(IResultListener listener)
	{
		return new ComponentResultListener(listener, adapter);
	}
	
}
