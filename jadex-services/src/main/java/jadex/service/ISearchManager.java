package jadex.service;

import jadex.commons.IFuture;

import java.util.Map;

/**
 *  Manager for doing searches across service providers.
 */
public interface ISearchManager
{
	/**
	 *  Search for services, starting at the given service provider.
	 *  @param provider	The service provider to start the search at.
	 *  @param visit	The visit decider to select nodes and terminate the search.
	 *  @param result	The result provider to select matching services and produce the final result. 
	 *  @param services	The local services of the provider (class->list of services).
	 */
	public IFuture	searchServices(IServiceProvider provider, IVisitDecider visit, IResultDecider result, Map services);
}
