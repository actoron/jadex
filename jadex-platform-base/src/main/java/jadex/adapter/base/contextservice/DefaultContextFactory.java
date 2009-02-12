package jadex.adapter.base.contextservice;

import java.util.Map;

/**
 *  Factory for default contexts.
 *  No special properties supported, yet.
 */
public class DefaultContextFactory implements IContextFactory
{
	/**
	 *  
	 */
	public IContext createContext(String name, IContext parent, Map properties)
	{
		return new DefaultContext(name, parent, properties);
	}
}
