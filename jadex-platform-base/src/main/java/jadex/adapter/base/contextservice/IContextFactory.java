package jadex.adapter.base.contextservice;

import jadex.commons.concurrent.IResultListener;

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

	/**
	 *  Delete a context. Called from context service before a context is removed from the platform.
	 *  @param context	The context to be deleted.
	 *  @param listener	The listener to be notified when deletion is finished (if any).
	 */
	public void	deleteContext(IContext context, IResultListener listener);
}
