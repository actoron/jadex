package jadex.service;

import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.IResultListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

/**
 *  Sequentially searches up and/or down the provider tree. 
 *  If both up and down are activated, the visit decider needs a mechanism
 *  to avoid nodes being checked twice to avoid infinite loops.
 */
public class SequentialSearchManager implements ISearchManager
{
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
		this.up	= true;
		this.down	= true;
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
		Collection	todo	= new HashSet(); // Nodes (children) yet to be processed.
		LocalSearchManager	lsm	= new LocalSearchManager(results);
		processNode(provider, decider, selector, services, ret, results, lsm, todo);
		return ret;
	}
	
	//-------- helper methods --------

	/**
	 *  Process a single node (provider).
	 */
	protected void processNode(final IServiceProvider provider, final IVisitDecider decider, final IResultSelector selector, final Map services,
		final Future ret, final Collection results, final LocalSearchManager lsm, final Collection todo)
	{
		if(decider.searchNode(null, provider, results))
		{
			provider.getServices(lsm, decider, selector).addResultListener(new IResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					if(up)
					{
						processParent(provider, decider, selector, services, ret, results, lsm, todo);
					}
					else if(down)
					{
						processChildren(provider, decider, selector, services, ret, results, lsm, todo);
					}
				}
				
				public void exceptionOccurred(Object source, Exception exception)
				{
					ret.setException(exception);
				}
			});
		}
		else if(todo.isEmpty())
		{
			ret.setResult(selector.getResult(results));
		}
	}

	/**
	 *  Process the parent of a node (provider).
	 */
	protected void processParent(final IServiceProvider provider,
			final IVisitDecider decider, final IResultSelector selector,
			final Map services, final Future ret, final Collection results,
			final LocalSearchManager lsm, final Collection todo)
	{
		provider.getParent().addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				if(result!=null && decider.searchNode(null, provider, results))
				{
					processNode((IServiceProvider)result, decider, selector, services, ret, results, lsm, todo);
				}
				else if(todo.isEmpty())
				{
					ret.setResult(selector.getResult(results));
				}
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				ret.setException(exception);
			}
		});
	}
}
