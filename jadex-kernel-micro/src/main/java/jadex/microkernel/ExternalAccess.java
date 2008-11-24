package jadex.microkernel;

import jadex.bridge.DefaultMessageAdapter;
import jadex.bridge.IMessageAdapter;
import jadex.bridge.MessageType;

import java.util.Map;

/**
 *  External access interface.
 */
public class ExternalAccess implements IExternalAccess
{
	//-------- attributes --------
	
	/** The interpreter. */
	protected MicroAgentInterpreter interpreter;
	
	//-------- constructors --------
	
	/**
	 *  Create a new capability flyweight.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 *  @param adapter	The adapter.
	 */
	public ExternalAccess(MicroAgentInterpreter interpreter)
	{
		this.interpreter = interpreter;
	}

	//-------- eventbase shortcut methods --------

	/**
	 *  Send a message after some delay.
	 *  @param me	The message event.
	 *  @return The filter to wait for an answer.
	 */
	public void	sendMessage(final Map me, final MessageType mt)
	{
		interpreter.invokeLater(new Runnable()
		{
			public void run()
			{
				IMessageAdapter msg = new DefaultMessageAdapter(me, mt);
				interpreter.getAgentAdapter().sendMessage(msg);
//				System.out.println("Send message: "+rme);
			}
		});
	}

	/**
	 *  Invoke some code on the agent thread.
	 *  This method queues the runnable in the agent
	 *  and immediately return (i.e. probably before
	 *  the runnable has been executed).
	 */
	public void invokeLater(Runnable runnable)
	{
		interpreter.invokeLater(runnable);
	}
}
