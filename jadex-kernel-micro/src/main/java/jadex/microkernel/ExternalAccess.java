package jadex.microkernel;

import jadex.bridge.MessageType;

import java.util.Map;

/**
 * External access interface.
 */
public class ExternalAccess implements IExternalAccess 
{
	// -------- attributes --------

	/** The agent. */
	protected MicroAgent agent;

	/** The interpreter. */
	protected MicroAgentInterpreter interpreter;

	// -------- constructors --------

	/**
	 *	Create an external access.
	 */
	public ExternalAccess(MicroAgent agent, MicroAgentInterpreter interpreter)
	{
		this.agent = agent;
		this.interpreter = interpreter;
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
		interpreter.invokeLater(new Runnable()
		{
			public void run()
			{
				agent.sendMessage(me, mt);
				// System.out.println("Send message: "+rme);
			}
		});
	}

	/**
	 * Invoke some code on the agent thread. This method queues the runnable in
	 * the agent and immediately return (i.e. probably before the runnable has
	 * been executed).
	 */
	public void invokeLater(Runnable runnable)
	{
		interpreter.invokeLater(runnable);
	}
}
