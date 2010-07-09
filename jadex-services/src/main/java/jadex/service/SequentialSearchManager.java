package jadex.service;

import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.IResultListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *  Sequentially searches up and/or down the provider tree. 
 *  If both up and down are activated, the visit decider needs a mechanism
 *  to avoid nodes being checked twice to avoid infinite loops.
 */
public class SequentialSearchManager implements ISearchManager
{
	//-------- constants --------
	
	/** Key for current list of children. */
	protected final String	CURRENT_CHILDREN	= "current-children";
	
	//-------- attributes --------
	
	/** Flag to activate upwards (parent) searching. */
	protected boolean up;
	
	/** Flag to activate downwards (children) searching. */
	protected boolean down;
	
	//-------- constructors --------
	
	/**
	 *  Create a new search manager.
	 */
	public SequentialSearchManager()
	{
		this(true, true);
	}
	
	/**
	 *  Create a new search manager.
	 */
	public SequentialSearchManager(boolean up, boolean down)
	{
		this.up	= up;
		this.down	= down;
	}
	
	//-------- ISearchManager interface --------
	
	/**
	 *  Search for services, starting at the given service provider.
	 *  @param provider	The service provider to start the search at.
	 *  @param decider	The visit decider to select nodes and terminate the search.
	 *  @param selector	The result selector to select matching services and produce the final result. 
	 *  @param services	The local services of the provider (class->list of services).
	 */
	public IFuture	searchServices(IServiceProvider provider, IVisitDecider decider, IResultSelector selector, Map services)
	{
		Future	ret	= new Future();
		Collection	results	= new ArrayList();
		Map	todo	= new LinkedHashMap(); // Nodes of which children still to be processed (id->provider).
		LocalSearchManager	lsm	= new LocalSearchManager(results);
		processNode(provider, decider, selector, services, ret, results, lsm, todo, up);
		return ret;
	}
	
	/**
	 *  Get the cache key.
	 *  Needs to identify this element with respect to its important features so that
	 *  two equal elements should return the same key.
	 */
	public Object getCacheKey()
	{
		return getClass().getName()+up+down;
	}

	//-------- helper methods --------

	/**
	 *  Process a single node (provider).
	 */
	protected void processNode(final IServiceProvider provider, final IVisitDecider decider, final IResultSelector selector, final Map services,
		final Future ret, final Collection results, final LocalSearchManager lsm, final Map todo, final boolean up)
	{
		// If node is to be searched, continue with this node.
		if(provider!=null && decider.searchNode(null, provider, results))
		{
			if(down)
			{
				// Child nodes of this node still have to be searched.
				todo.put(provider.getId(), provider);
			}
			
			provider.getServices(lsm, decider, selector).addResultListener(new IResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					// When searching upwards, continue with parent.
					if(up)
					{
						provider.getParent().addResultListener(new IResultListener()
						{
							public void resultAvailable(Object source, Object result)
							{
								processNode((IServiceProvider)result, decider, selector, services, ret, results, lsm, todo, up);
							}
							
							public void exceptionOccurred(Object source, Exception exception)
							{
								ret.setException(exception);
							}
						});
					}

					// Else continue with child nodes from todo list (if any).
					else
					{
						processChildNodes(decider, selector, services, ret, results, lsm, todo);
					}
				}
				
				public void exceptionOccurred(Object source, Exception exception)
				{
					ret.setException(exception);
				}
			});
		}
		
		// Else continue with child nodes from todo list (if any).
		else
		{
			processChildNodes(decider, selector, services, ret, results, lsm, todo);
		}
	}

	/**
	 *  Process child nodes from the todo list.
	 */
	protected void processChildNodes(
			final IVisitDecider decider, final IResultSelector selector,
			final Map services, final Future ret, final Collection results,
			final LocalSearchManager lsm, final Map todo)
	{
		// Finished, when no more todo nodes.
		if(todo.isEmpty())
		{
			ret.setResult(selector.getResult(results));
		}
		
		// Continue with current list of children (if any)
		else if(todo.containsKey(CURRENT_CHILDREN))
		{
			List	ccs	= (List)todo.get(CURRENT_CHILDREN);
			IServiceProvider	child	= (IServiceProvider)ccs.remove(0);
			if(ccs.isEmpty())
			{
				todo.remove(CURRENT_CHILDREN);
			}
			
			// Set 'up' to false, once traversing children has started.
			processNode(child, decider, selector, services, ret, results, lsm, todo, false);
		}

		// Else pick entry from todo list and continue with its children.
		else
		{
			Object	next	= todo.keySet().iterator().next();
			IServiceProvider	provider	= (IServiceProvider)todo.remove(next);
			provider.getChildren().addResultListener(new IResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					if(result!=null && !((Collection)result).isEmpty())
					{
						List	ccs	= new LinkedList((Collection)result);
						todo.put(CURRENT_CHILDREN, ccs);
					}
					processChildNodes(decider, selector, services, ret, results, lsm, todo);
				}
				
				public void exceptionOccurred(Object source, Exception exception)
				{
					ret.setException(exception);
				}
			});
		}
	}
}
