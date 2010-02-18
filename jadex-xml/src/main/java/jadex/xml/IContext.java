package jadex.xml;

import java.util.List;

/**
 * 
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
