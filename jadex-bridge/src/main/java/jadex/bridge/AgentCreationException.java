package jadex.bridge;


/**
 *  A problem occurred during the creation of an agent.
 */
public class AgentCreationException	extends RuntimeException
{
	//-------- constructors --------

	/**
	 *  Create a new agent creation exception.
	 *  @param message The message.
	 *  @param cause The parent exception (if any).
	 */
	public AgentCreationException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
