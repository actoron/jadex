package jadex.adapter.base.contextservice;

import java.util.Map;

/**
 *  Factory for creating new instances of contexts.
 */
public interface IContextFactory
{
	/**
	 *  Create a new context.
	 *  @param name	The name of the context.
	 *  @param parent	The parent of the context (if any).
	 *  @param properties	Initialization properties (if any).
	 */
	public IContext	createContext(String name, IContext parent, Map properties);
}
