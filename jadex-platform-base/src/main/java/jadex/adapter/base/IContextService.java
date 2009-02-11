package jadex.adapter.base;

import jadex.bridge.IAgentIdentifier;
import jadex.bridge.IPlatformService;
import jadex.commons.concurrent.IResultListener;

/**
 *  Contexts are an abstract grouping mechanism for agents on a platform,
 *  which is managed using the context service.
 */
public interface IContextService extends IPlatformService
{
	/**
	 *  Get all contexts on the platform (if any).
	 */
	public IContext[]	getContexts();

	/**
	 *  Get all contexts with a given type (if any).
	 */
	public IContext[]	getContexts(String type);

	/**
	 *  Get all direct contexts of an agent (if any).
	 */
	public IContext[]	getContexts(IAgentIdentifier agent);
	
	/**
	 *  Get a context with a given name.
	 */
	public IContext	getContext(String name);

	/**
	 *  Create a context.
	 */
	public IContext	createContext(String name, String type, IContext parent);
	
	/**
	 *  Delete a context.
	 *  @param listener Listener to be called, when the context is deleted
	 *    (e.g. after all contained agents have been terminated).
	 */
	public void	deleteContext(IContext context, IResultListener listener);
}
