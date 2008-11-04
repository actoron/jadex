package jadex.bridge;


/**
 *  Thrown when operations are invoked after an agent has been terminated.
 */
public class AgentTerminatedException	extends RuntimeException
{
	/**
	 *	Create an agent termination exception.  
	 */
	public AgentTerminatedException(String msg)
	{
		super(msg);
	}
}
