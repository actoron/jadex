package jadex.bridge;


/**
 *  Thrown when operations are invoked after an component has been terminated.
 */
public class ComponentTerminatedException	extends RuntimeException
{
	/**
	 *	Create an agent termination exception.  
	 */
	public ComponentTerminatedException(String msg)
	{
		super(msg);
	}
}
