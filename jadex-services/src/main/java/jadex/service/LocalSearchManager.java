package jadex.service;

import jadex.commons.Future;
import jadex.commons.IFuture;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 *  Search for services at the local provider and all parents. 
 */
public class LocalSearchManager implements ISearchManager
{
	//-------- attributes --------
	
	/** The result collection. */
	protected Collection	results;
	
	//-------- constructors --------
	
	/**
	 *  Create a new local search manager.
	 */
	public LocalSearchManager()
	{
		this(new ArrayList());
	}
	
	/**
	 *  Create a new local search manager.
	 */
	public LocalSearchManager(Collection results)
	{
		this.results	= results;
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
		if(decider.searchNode(null, provider, results))
		{
			selector.selectServices(services, results);
		}
		return new Future(selector.getResult(results));
	}
}
