package jadex.service;

import jadex.commons.IFuture;

import java.util.Map;

/**
 *  Search for services at the local provider and all parents. 
 */
public class UpwardsSearchManager implements ISearchManager
{
	/**
	 *  Search for services, starting at the given service provider.
	 *  @param provider	The service provider to start the search at.
	 *  @param visit	The visit decider to select nodes and terminate the search.
	 *  @param result	The result provider to select matching services and produce the final result. 
	 *  @param services	The local services of the provider (class->list of services).
	 */
	public IFuture	searchServices(IServiceProvider provider, IVisitDecider visit, IResultSelector result, Map services)
	{
		
	}
}
