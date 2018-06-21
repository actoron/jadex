package jadex.xml;


/**
 *  Common interface for read and write contexts.
 */
public interface IContext
{
	//-------- methods --------
	
	/**
	 *  Get the root object.
	 *  @return The root object.
	 */
	public Object getRootObject();
	
	/**
	 *  Get the current object.
	 *  @return The current object.
	 * /
	public Object getCurrentObject();*/
	
	/**
	 *  Get the stack.
	 *  @return The stack.
	 * /
	public List getStack();*/

	/**
	 *  Get the usercontext.
	 *  @return The usercontext.
	 */
	public Object getUserContext();

	/**
	 *  Get the classloader.
	 *  @return The classloader.
	 */
	public ClassLoader getClassLoader();
}
