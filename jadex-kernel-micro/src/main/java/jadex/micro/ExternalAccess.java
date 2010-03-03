package jadex.micro;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.ILoadableComponentModel;
import jadex.bridge.MessageType;

import java.util.Map;

/**
 * External access interface.
 */
public class ExternalAccess implements IMicroExternalAccess 
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
		invokeLater(new Runnable()
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
		interpreter.getAgentAdapter().invokeLater(runnable);
	}

	/**
	 *  Get the agent implementation.
	 *  Operations on the agent object
	 *  should be properly synchronized with invokeLater()!
	 */
	public IMicroAgent	getAgent()
	{
		return agent;
	}
	
	/**
	 *  Get the model of the component.
	 */
	public ILoadableComponentModel getModel()
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
	 *  Get the interpreter.
	 *  @return The interpreter.
	 */
	public MicroAgentInterpreter getInterpreter()
	{
		return this.interpreter;
	}
	
}
