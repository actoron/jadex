package jadex.adapter.base.contextservice;

import java.util.Map;

/**
 *  Factory for default contexts.
 *  No special properties supported, yet.
 */
public class DefaultContextFactory implements IContextFactory
{
	//-------- IContextFactory interface --------
	
	/**
	 *  Create a new context.
	 *  @param name	The name of the context.
	 *  @param parent	The parent of the context (if any).
	 *  @param properties	Initialization properties (if any).
	 */
	public BaseContext createContext(String name, /*IContext parent,*/ Map properties)
	{
		return new BaseContext(name, /*parent,*/ properties);
	}
}
