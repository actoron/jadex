package jadex.micro;

import jadex.base.Starter;
import jadex.bridge.IComponentStep;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.util.Map;

/**
 * External access interface.
 */
public class MicroExternalAccess extends jadex.kernelbase.ExternalAccess implements IMicroExternalAccess 
{
	//-------- attributes --------

	/** The agent. */
	protected MicroAgent agent;

	// -------- constructors --------

	/**
	 *	Create an external access.
	 */
	public MicroExternalAccess(MicroAgent agent, MicroAgentInterpreter interpreter)
	{
		super(interpreter);
		this.agent = agent;
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
		catch(final Exception e)
		{
			Starter.scheduleRescueStep(adapter.getComponentIdentifier(), new Runnable()
			{
				public void run()
				{
					ret.setException(e);
				}
			});
		}
		return ret;
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
		catch(final Exception e)
		{
			Starter.scheduleRescueStep(adapter.getComponentIdentifier(), new Runnable()
			{
				public void run()
				{
					ret.setException(e);
				}
			});
		}
		return ret;
	}
}
